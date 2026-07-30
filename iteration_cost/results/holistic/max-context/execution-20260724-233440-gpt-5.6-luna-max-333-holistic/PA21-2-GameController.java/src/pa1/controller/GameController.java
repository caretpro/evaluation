
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
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
        final boolean death = isDeath(result);
        final int extraLives = getExtraLives(result);

        boolean stored = false;
        try {
            gameState.getMoveStack().push(result);
            stored = true;
        } catch (final IllegalArgumentException ignored) {
            // MoveStack rejects results that cannot be undone.
        }

        if (stored) {
            gameState.incrementNumMoves();

            if (!gameState.hasUnlimitedLives() && extraLives != 0) {
                gameState.increaseNumLives(extraLives);
            }
        }

        if (death) {
            gameState.incrementNumDeaths();

            if (!gameState.hasUnlimitedLives() && gameState.getNumLives() > 0) {
                gameState.decrementNumLives();
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

        final int extraLives = getExtraLives(result);
        if (!gameState.hasUnlimitedLives() && extraLives != 0) {
            gameState.increaseNumLives(-extraLives);
        }

        if (isDeath(result)) {
            if (!gameState.hasUnlimitedLives()) {
                gameState.increaseNumLives(1);
            }
            decrementDeaths();
        }

        return true;
    }

    private MoveResult invokeMove(final Direction direction) {
        final Object controller = gameState.getGameBoardController();

        for (final Method method : controller.getClass().getMethods()) {
            if (method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(Direction.class)
                    && MoveResult.class.isAssignableFrom(method.getReturnType())) {
                return invokeMoveMethod(method, controller, direction);
            }
        }

        throw new IllegalStateException("No compatible move method was found");
    }

    private void invokeUndo(final MoveResult result) {
        final Object controller = gameState.getGameBoardController();

        for (final Method method : controller.getClass().getMethods()) {
            if (method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(MoveResult.class)) {
                invokeMethod(method, controller, result);
                return;
            }
        }

        throw new IllegalStateException("No compatible undo method was found");
    }

    private static MoveResult invokeMoveMethod(
            final Method method,
            final Object target,
            final Direction direction
    ) {
        return (MoveResult) invokeMethod(method, target, direction);
    }

    private static Object invokeMethod(
            final Method method,
            final Object target,
            final Object argument
    ) {
        try {
            return method.invoke(target, argument);
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException("Unable to access board operation", exception);
        } catch (final InvocationTargetException exception) {
            final Throwable cause = exception.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }

            throw new IllegalStateException("Board operation failed", cause);
        }
    }

    private static boolean isDeath(final MoveResult result) {
        final String description = describe(result);

        return description.contains("DEATH")
                || description.contains("DIED")
                || description.contains("MINE");
    }

    private static int getExtraLives(final MoveResult result) {
        int lives = 0;

        for (final Method method : result.getClass().getMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
                continue;
            }

            try {
                final Object value = method.invoke(result);
                final String methodName = method.getName().toLowerCase(Locale.ROOT);

                if (value instanceof Number number
                        && (methodName.contains("life") || methodName.contains("extra"))) {
                    lives += number.intValue();
                } else if (value instanceof Collection<?> collection) {
                    for (final Object element : collection) {
                        if (element != null && describe(element).contains("EXTRA LIFE")) {
                            lives++;
                        }
                    }
                }
            } catch (final IllegalAccessException exception) {
                throw new IllegalStateException("Unable to inspect move result", exception);
            } catch (final InvocationTargetException exception) {
                final Throwable cause = exception.getCause();

                if (cause instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (cause instanceof Error error) {
                    throw error;
                }

                throw new IllegalStateException("Unable to inspect move result", cause);
            }
        }

        return lives;
    }

    private static String describe(final Object value) {
        final StringBuilder description = new StringBuilder(value.toString());

        for (final Method method : value.getClass().getMethods()) {
            final String name = method.getName().toLowerCase(Locale.ROOT);

            if (method.getParameterCount() == 0
                    && (name.contains("status")
                    || name.contains("result")
                    || name.contains("type")
                    || name.contains("entity"))) {
                try {
                    final Object nestedValue = method.invoke(value);
                    if (nestedValue != null) {
                        description.append(' ').append(nestedValue);
                    }
                } catch (final IllegalAccessException | InvocationTargetException ignored) {
                    // The existing description is sufficient.
                }
            }
        }

        return description.toString().toUpperCase(Locale.ROOT);
    }

    private void decrementDeaths() {
        try {
            final Field field = GameState.class.getDeclaredField("numDeaths");
            field.setAccessible(true);
            field.setInt(gameState, Math.max(0, gameState.getNumDeaths() - 1));
        } catch (final NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to restore death count", exception);
        }
    }
}