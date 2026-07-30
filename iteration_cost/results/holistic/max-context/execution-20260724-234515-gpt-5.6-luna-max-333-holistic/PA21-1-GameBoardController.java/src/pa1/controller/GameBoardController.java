
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.Cell;
import pa1.model.Direction;
import pa1.model.Entity;
import pa1.model.EntityCell;
import pa1.model.ExtraLife;
import pa1.model.GameBoard;
import pa1.model.Gem;
import pa1.model.Mine;
import pa1.model.MoveResult;
import pa1.model.Player;
import pa1.model.Position;
import pa1.model.StopCell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    @NotNull
    private final Map<MoveResult, MoveState> moveStates = new HashMap<>();

    public GameBoardController(@NotNull final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    @NotNull
    public MoveResult makeMove(@NotNull final Direction direction) {
        Objects.requireNonNull(direction);

        final Player player = gameBoard.getPlayer();
        final EntityCell owner = player.getOwner();
        if (owner == null) {
            throw new IllegalStateException("The player is not placed on the game board.");
        }

        final MoveResult result = tryMove(owner.getPosition(), direction);
        if (!(result instanceof MoveResult.Valid.Alive alive)) {
            return result;
        }

        final MoveState state = new MoveState(
                alive.origPosition,
                alive.newPosition,
                readLives(player)
        );

        addEntityState(state, alive.origPosition);
        addEntityState(state, alive.newPosition);

        for (final Position position : alive.collectedGems) {
            addEntityState(state, position);
        }
        for (final Position position : alive.collectedExtraLives) {
            addEntityState(state, position);
        }

        clearEntityCell(gameBoard.getEntityCell(alive.origPosition));
        clearEntityCell(gameBoard.getEntityCell(alive.newPosition));

        for (final Position position : state.entities.keySet()) {
            clearEntityCell(gameBoard.getEntityCell(position));
        }

        setEntity(gameBoard.getEntityCell(alive.newPosition), player);
        setOwner(player, gameBoard.getEntityCell(alive.newPosition));

        writeLives(player, state.previousLives + alive.collectedExtraLives.size());
        moveStates.put(result, state);

        return result;
    }

    public void undoMove(@NotNull final MoveResult previousMove) {
        Objects.requireNonNull(previousMove);

        if (!(previousMove instanceof MoveResult.Valid.Alive)) {
            return;
        }

        final MoveState state = moveStates.remove(previousMove);
        if (state == null) {
            return;
        }

        final Player player = gameBoard.getPlayer();

        setOwner(player, null);

        for (final Entity entity : state.entities.values()) {
            if (entity != null) {
                setOwner(entity, null);
            }
        }

        for (final Position position : state.entities.keySet()) {
            clearEntityCell(gameBoard.getEntityCell(position));
        }

        for (final Position position : state.entities.keySet()) {
            final Entity entity = state.entities.get(position);
            if (entity != null) {
                setEntity(gameBoard.getEntityCell(position), entity);
                setOwner(entity, gameBoard.getEntityCell(position));
            }
        }

        writeLives(player, state.previousLives);
    }

    private void addEntityState(@NotNull final MoveState state, @NotNull final Position position) {
        state.entities.putIfAbsent(position, gameBoard.getEntityCell(position).getEntity());
    }

    private void clearEntityCell(@NotNull final EntityCell cell) {
        final Entity entity = cell.getEntity();
        if (entity != null) {
            setOwner(entity, null);
        }
        setEntity(cell, null);
    }

    @NotNull
    private MoveResult tryMove(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final List<Position> collectedGems = new ArrayList<>();
        final List<Position> collectedExtraLives = new ArrayList<>();
        Position lastValidPosition = position;

        while (true) {
            final Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;
            final Cell cell = gameBoard.getCell(newPosition);

            if (cell instanceof StopCell) {
                break;
            }

            if (cell instanceof EntityCell entityCell) {
                final Entity entity = entityCell.getEntity();

                if (entity instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }
                if (entity instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entity instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
                }
            }
        }

        if (lastValidPosition.equals(position)) {
            return new MoveResult.Invalid(position);
        }

        return new MoveResult.Valid.Alive(
                lastValidPosition,
                position,
                collectedGems,
                collectedExtraLives
        );
    }

    @Nullable
    private Position offsetPosition(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final Position newPosition = position.offsetByOrNull(
                direction.getOffset(),
                gameBoard.getNumRows(),
                gameBoard.getNumCols()
        );

        return newPosition != null && gameBoard.getCell(newPosition) instanceof EntityCell
                ? newPosition
                : null;
    }

    private static void setEntity(
            @NotNull final EntityCell cell,
            @Nullable final Entity entity
    ) {
        if (!invokeOneArgument(cell, "setEntity", entity)) {
            setField(cell, "entity", entity);
        }
    }

    private static void setOwner(
            @NotNull final Entity entity,
            @Nullable final EntityCell owner
    ) {
        if (!invokeOneArgument(entity, "setOwner", owner)) {
            setField(entity, "owner", owner);
        }
    }

    private static int readLives(@NotNull final Player player) {
        for (final String name : List.of("getLives", "getNumLives", "getExtraLives", "lives")) {
            final Object value = invokeNoArgument(player, name);
            if (value instanceof Number number) {
                return number.intValue();
            }
        }

        final Field field = findLifeField(player.getClass());
        if (field == null) {
            return 0;
        }

        try {
            field.setAccessible(true);
            return ((Number) field.get(player)).intValue();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void writeLives(@NotNull final Player player, final int lives) {
        for (final String name : List.of("setLives", "setNumLives", "setExtraLives", "lives")) {
            if (invokeOneArgument(player, name, lives)) {
                return;
            }
        }

        final Field field = findLifeField(player.getClass());
        if (field == null || Modifier.isFinal(field.getModifiers())) {
            return;
        }

        try {
            field.setAccessible(true);
            if (field.getType() == long.class || field.getType() == Long.class) {
                field.set(player, (long) lives);
            } else {
                field.set(player, lives);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Nullable
    private static Field findLifeField(@NotNull final Class<?> type) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (final Field field : current.getDeclaredFields()) {
                final String name = field.getName().toLowerCase();
                final Class<?> fieldType = field.getType();

                if (name.contains("life")
                        && (fieldType == int.class
                        || fieldType == Integer.class
                        || fieldType == long.class
                        || fieldType == Long.class)) {
                    return field;
                }
            }
        }
        return null;
    }

    private static boolean invokeOneArgument(
            @NotNull final Object target,
            @NotNull final String name,
            @Nullable final Object argument
    ) {
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            for (final Method method : current.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())
                        || !method.getName().equals(name)
                        || method.getParameterCount() != 1) {
                    continue;
                }

                final Class<?> parameterType = method.getParameterTypes()[0];
                if (argument == null
                        ? parameterType.isPrimitive()
                        : !isCompatible(parameterType, argument.getClass())) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    method.invoke(target, argument);
                    return true;
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        }
        return false;
    }

    @Nullable
    private static Object invokeNoArgument(
            @NotNull final Object target,
            @NotNull final String name
    ) {
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            for (final Method method : current.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())
                        || !method.getName().equals(name)
                        || method.getParameterCount() != 0) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        }
        return null;
    }

    private static void setField(
            @NotNull final Object target,
            @NotNull final String name,
            @Nullable final Object value
    ) {
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            try {
                final Field field = current.getDeclaredField(name);
                if (Modifier.isFinal(field.getModifiers())) {
                    return;
                }

                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                // Continue searching parent classes.
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException(exception);
            }
        }
    }

    private static boolean isCompatible(
            @NotNull final Class<?> parameterType,
            @NotNull final Class<?> valueType
    ) {
        if (parameterType.isAssignableFrom(valueType)) {
            return true;
        }

        return parameterType == int.class && valueType == Integer.class
                || parameterType == long.class && valueType == Long.class
                || parameterType == short.class && valueType == Short.class
                || parameterType == byte.class && valueType == Byte.class
                || parameterType == double.class && valueType == Double.class
                || parameterType == float.class && valueType == Float.class
                || parameterType == boolean.class && valueType == Boolean.class
                || parameterType == char.class && valueType == Character.class;
    }

    private static final class MoveState {
        private final Position originalPosition;
        private final Position newPosition;
        private final int previousLives;
        private final Map<Position, Entity> entities = new HashMap<>();

        private MoveState(
                @NotNull final Position originalPosition,
                @NotNull final Position newPosition,
                final int previousLives
        ) {
            this.originalPosition = originalPosition;
            this.newPosition = newPosition;
            this.previousLives = previousLives;
        }
    }
}