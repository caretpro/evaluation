
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import pa1.model.Position;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

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

    // Local stack to remember performed moves for undo (avoids missing Move class)
    @NotNull
    private final Deque<MoveData> moveHistory = new ArrayDeque<>();

    /**
     * Creates an instance.
     *
     * @param gameState The instance of {@link GameState} to control.
     */
    public GameController(final GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        final var boardController = gameState.getGameBoardController();
        final MoveResult result = boardController.movePlayer(direction);

        if (result == MoveResult.VALID || result == MoveResult.DEATH) {
            gameState.incrementNumMoves();
            if (result == MoveResult.DEATH) {
                gameState.incrementNumDeaths();
            }

            // Record the move for later undo: store direction and previous position
            final Position currentPos = gameState.getGameBoardView().getPlayerPosition();
            final Position prevPos = new Position(
                currentPos.row() - direction.getRowOffset(),
                currentPos.col() - direction.getColOffset()
            );
            moveHistory.push(new MoveData(direction, prevPos));
        }
        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        if (moveHistory.isEmpty()) {
            return false;
        }
        // The GameBoardController is assumed to have a parameterless undoMove() that restores the last move
        final var boardController = gameState.getGameBoardController();
        final boolean undone = boardController.undoMove();
        if (undone) {
            moveHistory.pop(); // discard the recorded data after successful undo
        }
        return undone;
    }

    // Internal record to store move data without needing the external Move class
    private record MoveData(Direction direction, Position previousPosition) {}
}