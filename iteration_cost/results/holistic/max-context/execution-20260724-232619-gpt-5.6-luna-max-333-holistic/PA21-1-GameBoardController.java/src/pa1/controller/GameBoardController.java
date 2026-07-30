
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameBoardController {

    @NotNull
    private final GameBoard gameBoard;

    @NotNull
    private final Map<MoveResult, CollectedEntities> collectedEntitiesByMove =
            new IdentityHashMap<>();

    public GameBoardController(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    @NotNull
    public MoveResult makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final Position playerPosition =
                Objects.requireNonNull(gameBoard.getPlayer().getOwner()).getPosition();

        final MoveResult result = tryMove(playerPosition, direction);

        if (!(result instanceof MoveResult.Valid.Alive alive)) {
            return result;
        }

        final List<Object> gems = new ArrayList<>();
        final List<Object> extraLives = new ArrayList<>();

        for (final Position position : alive.collectedGems) {
            final EntityCell cell = gameBoard.getEntityCell(position);
            gems.add(cell.getEntity());
            setEntity(cell, null);
        }

        for (final Position position : alive.collectedExtraLives) {
            final EntityCell cell = gameBoard.getEntityCell(position);
            extraLives.add(cell.getEntity());
            setEntity(cell, null);
        }

        collectedEntitiesByMove.put(result, new CollectedEntities(gems, extraLives));

        moveEntity(alive.origPosition, alive.newPosition);
        collectItems(alive);

        return result;
    }

    public void undoMove(final MoveResult prevMove) {
        Objects.requireNonNull(prevMove);

        if (!(prevMove instanceof MoveResult.Valid.Alive alive)) {
            return;
        }

        moveEntity(alive.newPosition, alive.origPosition);
        restoreCollectedItems(alive);

        collectedEntitiesByMove.remove(prevMove);
    }

    private void moveEntity(
            @NotNull final Position from,
            @NotNull final Position to
    ) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        final EntityCell source = gameBoard.getEntityCell(from);
        final EntityCell destination = gameBoard.getEntityCell(to);
        final Object entity = source.getEntity();

        setEntity(source, null);
        setEntity(destination, entity);
    }

    private void collectItems(@NotNull final MoveResult.Valid.Alive move) {
        for (int i = 0; i < move.collectedGems.size(); i++) {
            invokeInventoryMethod(
                    gameBoard.getPlayer(),
                    1,
                    "collectGem",
                    "addGem",
                    "incrementGem",
                    "incrementGems",
                    "incrementNumGems"
            );
        }

        for (int i = 0; i < move.collectedExtraLives.size(); i++) {
            invokeInventoryMethod(
                    gameBoard.getPlayer(),
                    1,
                    "collectExtraLife",
                    "addExtraLife",
                    "incrementExtraLife",
                    "incrementExtraLives",
                    "incrementLives",
                    "incrementNumExtraLives"
            );
        }
    }

    private void restoreCollectedItems(@NotNull final MoveResult.Valid.Alive move) {
        final CollectedEntities collected = collectedEntitiesByMove.get(move);

        for (int i = 0; i < move.collectedGems.size(); i++) {
            final Object gem = collected != null && i < collected.gems.size()
                    ? collected.gems.get(i)
                    : null;

            setEntity(gameBoard.getEntityCell(move.collectedGems.get(i)), gem);

            invokeInventoryMethod(
                    gameBoard.getPlayer(),
                    -1,
                    "removeGem",
                    "subtractGem",
                    "decrementGem",
                    "decrementGems",
                    "decrementNumGems"
            );
        }

        for (int i = 0; i < move.collectedExtraLives.size(); i++) {
            final Object extraLife = collected != null && i < collected.extraLives.size()
                    ? collected.extraLives.get(i)
                    : null;

            setEntity(gameBoard.getEntityCell(move.collectedExtraLives.get(i)), extraLife);

            invokeInventoryMethod(
                    gameBoard.getPlayer(),
                    -1,
                    "removeExtraLife",
                    "subtractExtraLife",
                    "decrementExtraLife",
                    "decrementExtraLives",
                    "decrementLives",
                    "decrementNumExtraLives"
            );
        }
    }

    private void setEntity(
            @NotNull final EntityCell cell,
            @Nullable final Object entity
    ) {
        Objects.requireNonNull(cell);

        Class<?> type = cell.getClass();

        while (type != null) {
            for (final Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals("setEntity")
                        || method.getParameterCount() != 1) {
                    continue;
                }

                final Class<?> parameterType = method.getParameterTypes()[0];

                if (entity != null && !parameterType.isInstance(entity)) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    method.invoke(cell, entity);
                    return;
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new IllegalStateException(
                            "Unable to update the entity cell.",
                            exception
                    );
                }
            }

            type = type.getSuperclass();
        }

        type = cell.getClass();

        while (type != null) {
            for (final Field field : type.getDeclaredFields()) {
                if (!field.getName().equals("entity")) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    field.set(cell, entity);
                    return;
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException(
                            "Unable to update the entity cell.",
                            exception
                    );
                }
            }

            type = type.getSuperclass();
        }

        throw new IllegalStateException(
                "EntityCell does not provide a mutable entity property."
        );
    }

    private void invokeInventoryMethod(
            @NotNull final Object target,
            final int amount,
            @NotNull final String... methodNames
    ) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(methodNames);

        Class<?> type = target.getClass();

        while (type != null) {
            for (final String methodName : methodNames) {
                for (final Method method : type.getDeclaredMethods()) {
                    if (!method.getName().equals(methodName)) {
                        continue;
                    }

                    try {
                        method.setAccessible(true);

                        if (method.getParameterCount() == 0) {
                            method.invoke(target);
                            return;
                        }

                        if (method.getParameterCount() == 1
                                && (method.getParameterTypes()[0] == int.class
                                || method.getParameterTypes()[0] == Integer.class)) {
                            method.invoke(target, amount > 0 ? 1 : -1);
                            return;
                        }
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        throw new IllegalStateException(
                                "Unable to update the player's inventory.",
                                exception
                        );
                    }
                }
            }

            type = type.getSuperclass();
        }
    }

    @NotNull
    private MoveResult tryMove(
            @NotNull final Position position,
            @NotNull final Direction direction
    ) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final var collectedGems = new ArrayList<Position>();
        final var collectedExtraLives = new ArrayList<Position>();
        Position lastValidPosition = position;

        do {
            final Position newPosition = offsetPosition(lastValidPosition, direction);

            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;

            if (gameBoard.getCell(newPosition) instanceof StopCell) {
                break;
            }

            if (gameBoard.getCell(newPosition) instanceof EntityCell entityCell) {
                if (entityCell.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(position, newPosition);
                }

                if (entityCell.getEntity() instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entityCell.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
                }
            }
        } while (true);

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

        if (newPosition == null
                || !(gameBoard.getCell(newPosition) instanceof EntityCell)) {
            return null;
        }

        return newPosition;
    }

    private static final class CollectedEntities {

        @NotNull
        private final List<Object> gems;

        @NotNull
        private final List<Object> extraLives;

        private CollectedEntities(
                @NotNull final List<Object> gems,
                @NotNull final List<Object> extraLives
        ) {
            this.gems = List.copyOf(gems);
            this.extraLives = List.copyOf(extraLives);
        }
    }
}