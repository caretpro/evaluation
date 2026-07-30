
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

public class GameController {

    @NotNull
    private final GameState gameState;

    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final MoveResult result = invokeMove(direction);

        if (isValid(result)) {
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(result);

            if (isDeath(result)) {
                gameState.incrementNumDeaths();

                if (!gameState.hasUnlimitedLives() && gameState.getNumLives() > 0) {
                    gameState.decrementNumLives();
                }
            }
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        final MoveResult result = gameState.getMoveStack().pop();
        invokeUndo(result);
        return true;
    }

    private MoveResult invokeMove(final Direction direction) {
        final Object controller = gameState.getGameBoardController();

        for (final Method method : allMethods(controller.getClass())) {
            if (!method.getName().toLowerCase(Locale.ROOT).contains("move")
                    || method.getParameterCount() != 1
                    || !MoveResult.class.isAssignableFrom(method.getReturnType())) {
                continue;
            }

            final Class<?> parameterType = method.getParameterTypes()[0];
            Object argument = null;

            if (parameterType.isAssignableFrom(Direction.class)) {
                argument = direction;
            } else if (parameterType.getSimpleName().equals("PositionOffset")) {
                argument = direction.getOffset();
            }

            if (argument != null) {
                return invokeResult(method, controller, argument);
            }
        }

        throw new IllegalStateException("No compatible move operation was found");
    }

    private void invokeUndo(final MoveResult result) {
        final Object controller = gameState.getGameBoardController();

        for (final Method method : allMethods(controller.getClass())) {
            if (!method.getName().toLowerCase(Locale.ROOT).contains("undo")
                    || method.getParameterCount() != 1
                    || !method.getParameterTypes()[0].isAssignableFrom(MoveResult.class)) {
                continue;
            }

            invokeVoid(method, controller, result);
            return;
        }

        throw new IllegalStateException("No compatible undo operation was found");
    }

    private static boolean isValid(final MoveResult result) {
        final Boolean valid = findBoolean(result, "valid", "successful", "possible", "legal");
        if (valid != null) {
            return valid;
        }

        final Boolean invalid = findBoolean(result, "invalid", "blocked", "illegal");
        if (invalid != null) {
            return !invalid;
        }

        final String description = result.toString().toLowerCase(Locale.ROOT);
        return !description.contains("invalid")
                && !description.contains("blocked")
                && !description.contains("illegal");
    }

    private static boolean isDeath(final MoveResult result) {
        final Boolean death = findBoolean(result, "death", "dead", "died", "killed");
        if (death != null) {
            return death;
        }

        final String description = result.toString().toLowerCase(Locale.ROOT);
        return description.contains("death")
                || description.contains("dead")
                || description.contains("died")
                || description.contains("killed")
                || description.contains("mine");
    }

    private static Boolean findBoolean(
            final MoveResult result,
            final String... keywords
    ) {
        for (final Method method : allMethods(result.getClass())) {
            if (method.getParameterCount() != 0
                    || (method.getReturnType() != boolean.class
                    && method.getReturnType() != Boolean.class)
                    || !containsKeyword(method.getName(), keywords)) {
                continue;
            }

            return (Boolean) invokeValue(method, result);
        }

        for (Class<?> type = result.getClass(); type != null; type = type.getSuperclass()) {
            for (final Field field : type.getDeclaredFields()) {
                if ((field.getType() != boolean.class && field.getType() != Boolean.class)
                        || !containsKeyword(field.getName(), keywords)) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    return (Boolean) field.get(result);
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Unable to inspect move result", exception);
                }
            }
        }

        return null;
    }

    private static boolean containsKeyword(
            final String value,
            final String... keywords
    ) {
        final String normalized = value.toLowerCase(Locale.ROOT);

        for (final String keyword : keywords) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private static Method[] allMethods(final Class<?> type) {
        return type.getMethods();
    }

    private static MoveResult invokeResult(
            final Method method,
            final Object target,
            final Object argument
    ) {
        try {
            method.setAccessible(true);
            return (MoveResult) method.invoke(target, argument);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to invoke move operation", exception);
        } catch (InvocationTargetException exception) {
            throw propagate(exception.getCause());
        }
    }

    private static void invokeVoid(
            final Method method,
            final Object target,
            final Object argument
    ) {
        try {
            method.setAccessible(true);
            method.invoke(target, argument);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to invoke undo operation", exception);
        } catch (InvocationTargetException exception) {
            throw propagate(exception.getCause());
        }
    }

    private static Object invokeValue(
            final Method method,
            final Object target
    ) {
        try {
            method.setAccessible(true);
            return method.invoke(target);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to inspect move result", exception);
        } catch (InvocationTargetException exception) {
            throw propagate(exception.getCause());
        }
    }

    private static RuntimeException propagate(final Throwable throwable) {
        if (throwable instanceof RuntimeException exception) {
            return exception;
        }

        if (throwable instanceof Error error) {
            throw error;
        }

        return new IllegalStateException("Game-board operation failed", throwable);
    }
}