
package pa1.model;

import java.util.Stack;

/**
 * Represents the state of the game, including position and history for undo functionality.
 */
public class GameState {
    private int playerX;
    private int playerY;
    private final Stack<int[]> history;

    public GameState(int startX, int startY) {
        this.playerX = startX;
        this.playerY = startY;
        this.history = new Stack<>();
    }

    /**
     * Moves the player in the specified direction.
     *
     * @param direction The direction to move.
     * @return A MoveResult indicating success or failure.
     */
    public MoveResult move(Direction direction) {
        // Save current state for undo
        history.push(new int[]{playerX, playerY});
        switch (direction) {
            case UP:
                playerY--;
                break;
            case DOWN:
                playerY++;
                break;
            case LEFT:
                playerX--;
                break;
            case RIGHT:
                playerX++;
                break;
            default:
                return new FailureMoveResult("Invalid direction");
        }
        return new SuccessMoveResult("Moved " + direction);
    }

    /**
     * Undoes the last move.
     *
     * @return true if undo was successful, false if no moves to undo.
     */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        int[] previousState = history.pop();
        this.playerX = previousState[0];
        this.playerY = previousState[1];
        return true;
    }

    // Getters for position (optional, if needed elsewhere)
    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    /**
     * Concrete class representing a successful move result.
     */
    public static class SuccessMoveResult extends MoveResult {
        public SuccessMoveResult(String message) {
            super(true, message);
        }
    }

    /**
     * Concrete class representing a failed move result.
     */
    public static class FailureMoveResult extends MoveResult {
        public FailureMoveResult(String message) {
            super(false, message);
        }
    }
}