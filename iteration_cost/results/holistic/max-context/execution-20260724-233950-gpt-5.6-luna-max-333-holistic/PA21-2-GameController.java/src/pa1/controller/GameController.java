
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class GameController {

    @NotNull
    private final GameState gameState;

    private final Deque<MoveInfo> moveInfoStack = new ArrayDeque<>();

    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final MoveResult result = invokeMove(direction);
        final boolean valid = isValidMove(result);
        final boolean death = hasProperty(result, "death", "dead", "mine");
        final boolean extraLife = hasProperty(
                result,
                "extralife",
                "extr alife",
                "life"
        );

        if (valid) {
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(result);

            if (death) {
                gameState.incrementNumDeaths();

                if (!gameState.hasUnlimitedLives()) {
                    gameState.decrementNumLives();
                }
            }

            if (extraLife && !gameState.hasUnlimitedLives()) {
                gameState.increaseNumLives(1);
            }

            moveInfoStack.push(new MoveInfo(death, extraLife));
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        final MoveResult result = gameState.getMoveStack().pop();
        invokeRestore(result);

        if (!moveInfoStack.isEmpty()) {
            final MoveInfo moveInfo = moveInfoStack.pop();

            if (!gameState.hasUnlimitedLives()) {
                if (moveInfo.death()) {
                    gameState.increaseNumLives(1);
                }
                if (moveInfo.extraLife() && gameState.getNumLives() > 0) {
                    gameState.decreaseNumLives(1);
                }
            }
        }

        return true;
    }

    private MoveResult invokeMove(final Direction direction) {
        final Object boardController = gameState.getGameBoardController();

        for (final Method method : boardController.getClass().getMethods()) {
            if (method.getParameterCount() != 1
                    || !method.getParameterTypes()[0].isAssignableFrom(Direction.class)
                    || !MoveResult.class.isAssignableFrom(method.getReturnType())) {
                continue;
            }

            try {
                return (MoveResult) method.invoke(boardController, direction);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Unable to process move.", exception);
            } catch (InvocationTargetException exception) {
                throw new IllegalStateException(
                        "Unable to process move.",
                        exception.getCause()
                );
            }
        }

        throw new IllegalStateException("No compatible move operation was found.");
    }

    private void invokeRestore(final MoveResult result) {
        final Object boardController = gameState.getGameBoardController();

        for (final Method method : boardController.getClass().getMethods()) {
            if (method.getParameterCount() != 1
                    || !method.getParameterTypes()[0].isAssignableFrom(result.getClass())) {
                continue;
            }

            final String methodName = method.getName().toLowerCase();
            if (!methodName.contains("restore")
                    && !methodName.contains("undo")
                    && !methodName.contains("revert")) {
                continue;
            }

            try {
                method.invoke(boardController, result);
                return;
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Unable to restore move.", exception);
            } catch (InvocationTargetException exception) {
                throw new IllegalStateException(
                        "Unable to restore move.",
                        exception.getCause()
                );
            }
        }

        throw new IllegalStateException("No compatible restore operation was found.");
    }

    private boolean isValidMove(final MoveResult result) {
        final Boolean explicitValue = readBoolean(result, "valid", "successful", "success");
        if (explicitValue != null) {
            return explicitValue;
        }

        final String description = result.toString().toLowerCase();
        return !description.contains("invalid")
                && !description.contains("illegal")
                && !description.contains("blocked")
                && !description.contains("cannot");
    }

    private boolean hasProperty(
            final Object object,
            final String... propertyNames
    ) {
        final Boolean value = readBoolean(object, propertyNames);
        if (value != null) {
            return value;
        }

        final String description = object.toString().toLowerCase();
        for (final String propertyName : propertyNames) {
            final String normalized = propertyName.replace(" ", "").toLowerCase();
            if (description.contains(normalized)) {
                return true;
            }
        }

        return false;
    }

    private Boolean readBoolean(
            final Object object,
            final String... propertyNames
    ) {
        for (final Method method : object.getClass().getMethods()) {
            if (method.getParameterCount() != 0
                    || (method.getReturnType() != boolean.class
                    && method.getReturnType() != Boolean.class)) {
                continue;
            }

            if (!matches(method.getName(), propertyNames)) {
                continue;
            }

            try {
                return (Boolean) method.invoke(object);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Unable to inspect move result.", exception);
            } catch (InvocationTargetException exception) {
                throw new IllegalStateException(
                        "Unable to inspect move result.",
                        exception.getCause()
                );
            }
        }

        for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
            for (final Field field : type.getDeclaredFields()) {
                if (field.getType() != boolean.class
                        && field.getType() != Boolean.class) {
                    continue;
                }

                if (!matches(field.getName(), propertyNames)) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    return (Boolean) field.get(object);
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Unable to inspect move result.", exception);
                }
            }
        }

        return null;
    }

    private boolean matches(
            final String name,
            final String... propertyNames
    ) {
        final String normalizedName = name.replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .toLowerCase();

        for (final String propertyName : propertyNames) {
            final String normalizedProperty = propertyName.replace("_", "")
                    .replace("-", "")
                    .replace(" ", "")
                    .toLowerCase();

            if (normalizedName.contains(normalizedProperty)) {
                return true;
            }
        }

        return false;
    }

    private record MoveInfo(boolean death, boolean extraLife) {
    }
}