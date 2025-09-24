
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import org.jetbrains.annotations.NotNull;

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

    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState, "gameState cannot be null");
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction, "direction cannot be null");
        // Basic implementation assuming GameState has position tracking
        int[] newPosition = calculateNewPosition(direction);
        boolean valid = isValidPosition(newPosition);
        if (valid) {
            updatePosition(newPosition);
        }
        return MoveResult.create(valid, newPosition);
    }

    public boolean processUndo() {
        // Basic undo implementation
        return false; // No undo functionality in this minimal version
    }

    private int[] calculateNewPosition(Direction direction) {
        int[] current = gameState.getCurrentPosition();
        return new int[]{
            current[0] + direction.getXDelta(),
            current[1] + direction.getYDelta()
        };
    }

    private boolean isValidPosition(int[] position) {
        // Basic boundary check
        return position[0] >= 0 && position[1] >= 0;
    }

    private void updatePosition(int[] newPosition) {
        // Position update logic would go here
    }
}