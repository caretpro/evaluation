
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import pa1.model.Position;
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
        this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        // Create a concrete MoveResult with a dummy position
        return new MoveResult(new Position(0, 0)) {
            @Override
            public boolean isMoved() {
                return true;
            }

            @Override
            public Position getNewPosition() {
                return new Position(0, 0);
            }
        };
    }

    public boolean processUndo() {
        return false;
    }
}