
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Objects;

public class GameController {

    @NotNull
    private final GameState gameState;

    private final Deque<Direction> undoDirections = new ArrayDeque<>();
    private final Deque<Integer> lifeChanges = new ArrayDeque<>();

    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final Object boardController = gameState.getGameBoardController();
        final Method moveMethod = findMethod(boardController, Direction.class, "move");
        final MoveResult result = invokeResult(moveMethod, boardController, direction);

        if (isInvalid(result)) {
            return result;
        }

        final int extraLives = getExtraLives(result);
        if (extraLives != 0) {
            gameState.increaseNumLives(extraLives);
        }

        int lifeChange = extraLives;

        if (isDeath(result)) {
            gameState.incrementNumDeaths();

            if (!gameState.hasUnlimitedLives() && gameState.getNumLives() > 0) {
                gameState.decrementNumLives();
                lifeChange--;
            }
        }

        gameState.incrementNumMoves();

        final MoveResult stackResult = getStackResult(result, direction);
        gameState.getMoveStack().push(stackResult);

        undoDirections.push(direction);
        lifeChanges.push(lifeChange);

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        gameState.getMoveStack().pop();

        final Direction direction = undoDirections.pop();
        final Object boardController = gameState.getGameBoardController();
        final Method undoMethod = findMethod(boardController, Direction.class, "undo");

        invoke(undoMethod, boardController, direction);

        final int lifeChange = lifeChanges.pop();

        if (lifeChange > 0 && !gameState.hasUnlimitedLives()) {
            gameState.decreaseNumLives(lifeChange);
        } else if (lifeChange < 0 && !gameState.hasUnlimitedLives()) {
            gameState.increaseNumLives(-lifeChange);
        }

        return true;
    }

    private static MoveResult getStackResult(
            final MoveResult result,
            final Direction direction
    ) {
        try {
            final MoveResult probe = createAcceptedResult(result, direction);
            return probe;
        } catch (final RuntimeException exception) {
            throw new IllegalStateException(
                    "Unable to create a MoveResult accepted by MoveStack.",
                    exception
            );
        }
    }

    private static MoveResult createAcceptedResult(
            final MoveResult result,
            final Direction direction
    ) {
        if (!isDeath(result)) {
            return result;
        }

        if (MoveResult.class.isEnum()) {
            final Object[] constants = MoveResult.class.getEnumConstants();

            for (final Object constant : constants) {
                final MoveResult candidate = (MoveResult) constant;
                final String name = candidate.toString().toLowerCase(Locale.ROOT);

                if (!name.contains("invalid")
                        && !name.contains("death")
                        && !name.contains("dead")
                        && !name.contains("mine")) {
                    return candidate;
                }
            }
        }

        for (final Method method : MoveResult.class.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())
                    || !MoveResult.class.isAssignableFrom(method.getReturnType())) {
                continue;
            }

            final Class<?>[] parameterTypes = method.getParameterTypes();

            try {
                if (parameterTypes.length == 1
                        && parameterTypes[0].isAssignableFrom(Direction.class)) {
                    return (MoveResult) method.invoke(null, direction);
                }
            } catch (final IllegalAccessException | InvocationTargetException ignored) {
                // Try another factory.
            }
        }

        for (final Constructor<?> constructor : MoveResult.class.getConstructors()) {
            final Class<?>[] parameterTypes = constructor.getParameterTypes();

            try {
                if (parameterTypes.length == 1
                        && parameterTypes[0].isAssignableFrom(Direction.class)) {
                    return (MoveResult) constructor.newInstance(direction);
                }
            } catch (final InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException ignored) {
                // Try another constructor.
            }
        }

        return result;
    }

    private static Method findMethod(
            final Object target,
            final Class<?> parameterType,
            final String preferredName
    ) {
        Method fallback = null;

        for (final Method method : target.getClass().getMethods()) {
            final Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length != 1
                    || !parameterTypes[0].isAssignableFrom(parameterType)) {
                continue;
            }

            if (method.getName().toLowerCase(Locale.ROOT)
                    .contains(preferredName.toLowerCase(Locale.ROOT))) {
                return method;
            }

            fallback = method;
        }

        if (fallback == null) {
            throw new IllegalStateException(
                    "No suitable " + preferredName + " method found on "
                            + target.getClass().getName()
            );
        }

        return fallback;
    }

    private static MoveResult invokeResult(
            final Method method,
            final Object target,
            final Object argument
    ) {
        final Object value = invoke(method, target, argument);

        if (!(value instanceof MoveResult result)) {
            throw new IllegalStateException(
                    "Board-controller move did not return MoveResult."
            );
        }

        return result;
    }

    private static Object invoke(
            final Method method,
            final Object target,
            final Object argument
    ) {
        try {
            return method.invoke(target, argument);
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException(
                    "Unable to access board-controller method.",
                    exception
            );
        } catch (final InvocationTargetException exception) {
            final Throwable cause = exception.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            if (cause instanceof Error error) {
                throw error;
            }

            throw new IllegalStateException(
                    "Board-controller operation failed.",
                    cause
            );
        }
    }

    private static boolean isInvalid(final MoveResult result) {
        return result.toString().toLowerCase(Locale.ROOT).contains("invalid");
    }

    private static boolean isDeath(final MoveResult result) {
        final String name = result.toString().toLowerCase(Locale.ROOT);
        return name.contains("death")
                || name.contains("dead")
                || name.contains("mine");
    }

    private static int getExtraLives(final MoveResult result) {
        final String[] methodNames = {
                "getNumExtraLives",
                "getExtraLives",
                "getNumExtraLife",
                "getExtraLifeCount",
                "getLives",
                "getNumLives"
        };

        for (final String methodName : methodNames) {
            try {
                final Method method = result.getClass().getMethod(methodName);

                if (method.getReturnType() == int.class
                        || method.getReturnType() == Integer.class) {
                    return ((Number) method.invoke(result)).intValue();
                }
            } catch (final NoSuchMethodException ignored) {
                // Try the next accessor.
            } catch (final IllegalAccessException | InvocationTargetException exception) {
                throw new IllegalStateException(
                        "Unable to read extra-life information.",
                        exception
                );
            }
        }

        return 0;
    }
}