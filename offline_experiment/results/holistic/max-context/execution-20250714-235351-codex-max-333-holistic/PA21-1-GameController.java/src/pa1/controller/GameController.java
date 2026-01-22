
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

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

    /**
     * Creates an instance.
     *
     * @param gameState The instance of {@link GameState} to control.
     */
    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");

        // Perform the move on the board
        final var boardCtrl = gameState.getGameBoardController();
        final MoveResult result = boardCtrl.move(direction);

        // If the move was valid, record it and update stats
        if (result.isValidMove()) {
            gameState.getMoveStack().push(direction);
            gameState.incrementNumMoves();

            // Handle death case
            if (result.isDeath()) {
                gameState.decrementNumLives();
                gameState.incrementNumDeaths();
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
        // Pop last direction and undo it on the board
        final Direction last = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undo(last);
        gameState.incrementNumMoves(); // count undo as a move (and affects score)
        return true;
    }
}