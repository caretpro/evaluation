
package pa1.controller;

import pa1.model.GameBoard;
import pa1.model.MoveResult;
import pa1.model.Position;

/**
 * Controller for making moves on a GameBoard.
 */
public final class GameBoardController {

    private final GameBoard board;

    public GameBoardController(final GameBoard board) {
        this.board = board;
    }

    /**
     * Attempt to move the player in the given direction.
     *
     * @param dir the direction to move (UP, DOWN, LEFT, RIGHT, or NONE)
     * @return the result of the attempted move
     */
    public MoveResult makeMove(final Direction dir) {
        // 1) Handle the "no movement" direction up‑front.
        if (dir == Direction.NONE) {
            // Stay in place: invalid move (you didn't go anywhere).
            return new MoveResult.Invalid(board.getPlayerPosition());
        }

        // 2) Compute the target position once (never null).
        final Position oldPos = board.getPlayerPosition();
        final Position newPos = oldPos.moveBy(dir);

        // 3) Delegate to the board’s logic to decide what happened.
        return board.tryMove(oldPos, newPos);
    }
}