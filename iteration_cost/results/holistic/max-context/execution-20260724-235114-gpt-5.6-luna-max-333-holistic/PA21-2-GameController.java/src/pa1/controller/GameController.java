
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Controller for {@link pa1.InertiaTextGame}.
 *
 * <p>
 * All game state mutations should be performed by this class.
 * </p>
 */
public class GameController {

    @NotNull
    private final GameState gameState;

    @NotNull
    private final Deque<Direction> moveDirections = new ArrayDeque<>();

    @NotNull
    private final Deque<Boolean> moveDeaths = new ArrayDeque<>();

    /**
     * Creates an instance.
     *
     * @param gameState The instance of {@link GameState} to control.
     */
    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final MoveResult move = createMove(direction);
        final MoveResult result = invokeBoardMove(move);

        if (isValid(result)) {
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(move);
            moveDirections.push(direction);

            final boolean death = isDeath(result);
            moveDeaths.push(death);

            if (death) {
                gameState.incrementNumDeaths();

                if (!gameState.hasUnlimitedLives() && gameState.getNumLives() > 0) {
                    gameState.decrementNumLives();
                }
            }
        }

        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        final MoveResult previousMove = gameState.getMoveStack().pop();
        final Direction direction = moveDirections.isEmpty()
                ? getDirection(previousMove)
                : moveDirections.pop();

        invokeBoardMove(createMove(getOpposite(direction)));

        final boolean death = !moveDeaths.isEmpty() && moveDeaths.pop();
        if (death && !gameState.hasUnlimitedLives()) {
            gameState.increaseNumLives(1);
        }

        return true;
    }

    @NotNull
    private MoveResult createMove(final Direction direction) {
        for (final Constructor<?> constructor : MoveResult.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 1
                    && constructor.getParameterTypes()[0].isAssignableFrom(Direction.class)) {
                try {
                    constructor.setAccessible(true);
                    return (MoveResult) constructor.newInstance(direction);
                } catch (final ReflectiveOperationException exception) {
                    throw new IllegalStateException("Unable to create a move.", exception);
                }
            }
        }

        throw new IllegalStateException("MoveResult must provide a Direction constructor.");
    }

    @NotNull
    private MoveResult invokeBoardMove(final MoveResult move) {
        final Object boardController = gameState.getGameBoardController();

        for (final Method method : boardController.getClass().getMethods()) {
            if (!method.getName().equals("processMove")
                    || method.getParameterCount() != 1
                    || !method.getParameterTypes()[0].isAssignableFrom(MoveResult.class)
                    || !MoveResult.class.isAssignableFrom(method.getReturnType())) {
                continue;
            }

            try {
                method.setAccessible(true);
                return (MoveResult) method.invoke(boardController, move);
            } catch (final InvocationTargetException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (cause instanceof Error error) {
                    throw error;
                }
                throw new IllegalStateException("Board movement failed.", cause);
            } catch (final ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to invoke board movement.", exception);
            }
        }

        throw new IllegalStateException("No MoveResult-based processMove method was found.");
    }

    private boolean isValid(final MoveResult result) {
        final Boolean invalid = invokeBoolean(result, "isInvalid", "isInvalidMove");
        if (invalid != null) {
            return !invalid;
        }

        final Boolean valid = invokeBoolean(result, "isValid", "isValidMove", "isSuccessful");
        if (valid != null) {
            return valid;
        }

        return !result.toString().toUpperCase().contains("INVALID");
    }

    private boolean isDeath(final MoveResult result) {
        final Boolean death = invokeBoolean(
                result,
                "isDeath",
                "isDead",
                "hasDied",
                "isPlayerDead");

        return Boolean.TRUE.equals(death)
                || result.toString().toUpperCase().contains("DEATH")
                || result.toString().toUpperCase().contains("DIED");
    }

    private Boolean invokeBoolean(final MoveResult result, final String... methodNames) {
        for (final String methodName : methodNames) {
            try {
                final Method method = result.getClass().getMethod(methodName);
                if (method.getReturnType() == boolean.class
                        || method.getReturnType() == Boolean.class) {
                    return (Boolean) method.invoke(result);
                }
            } catch (final ReflectiveOperationException ignored) {
                // Try the next method name.
            }
        }

        return null;
    }

    private Direction getDirection(final MoveResult move) {
        for (final Method method : move.getClass().getMethods()) {
            if (method.getParameterCount() == 0
                    && method.getReturnType() == Direction.class) {
                try {
                    return (Direction) method.invoke(move);
                } catch (final ReflectiveOperationException exception) {
                    throw new IllegalStateException("Unable to determine move direction.", exception);
                }
            }
        }

        throw new IllegalStateException("MoveResult does not expose its direction.");
    }

    private Direction getOpposite(final Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }
}