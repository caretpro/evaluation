
package pa1.model;

import java.util.Optional;

/**
 * Represents the state of a running InertiaTextGame.
 */
public class GameState {
    // Example fields (replace with your actual fields)
    private Direction lastDirection;
    private MoveResult lastMoveResult;

    /**
     * Attempts to move in the given direction.
     *
     * @param direction the direction to move
     * @return result of trying to move (e.g. {@link MoveResult#SUCCESS}, etc.)
     */
    public MoveResult movePlayer(Direction direction) {
        // TODO: implement the real move logic
        // For now, just record and return a dummy result
        this.lastDirection = direction;
        this.lastMoveResult = MoveResult.SUCCESS;
        return this.lastMoveResult;
    }

    /**
     * Undoes the last successful move, if any.
     *
     * @return {@code true} if a move was undone, {@code false} otherwise
     */
    public boolean undoMove() {
        // TODO: implement the real undo logic
        if (lastMoveResult == MoveResult.SUCCESS) {
            lastMoveResult = MoveResult.NONE;
            return true;
        }
        return false;
    }

    /**
     * @return an Optional containing the last move’s result, if any
     */
    public Optional<MoveResult> lastResult() {
        return Optional.ofNullable(lastMoveResult);
    }
}