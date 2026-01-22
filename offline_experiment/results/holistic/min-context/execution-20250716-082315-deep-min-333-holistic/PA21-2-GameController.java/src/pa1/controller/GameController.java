
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GameController {
    @NotNull
    private final GameState gameState;

    public GameController(final GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState, "gameState cannot be null");
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction, "direction cannot be null");
        
        // Basic move implementation without specific GameState methods
        try {
            // Assume GameState has a move method that throws IllegalArgumentException for invalid moves
            gameState.move(direction);
            
            // Assume GameState has a simple isGoalReached check
            if (gameState.isGoalReached()) {
                return new MoveResult("GOAL_REACHED");
            }
            return new MoveResult("VALID");
        } catch (IllegalArgumentException e) {
            return new MoveResult("INVALID");
        }
    }

    public boolean processUndo() {
        // Basic undo implementation
        return gameState.undo();
    }
}