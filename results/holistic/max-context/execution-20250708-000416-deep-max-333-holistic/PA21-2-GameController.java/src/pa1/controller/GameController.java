
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
        this.gameState = Objects.requireNonNull(gameState);
    }

    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);
        
        var boardController = gameState.getGameBoardController();
        var result = boardController.movePlayer(direction);
        
        if (result.isSuccess()) {
            gameState.incrementNumMoves();
            
            if (result.isDeath()) {
                gameState.incrementNumDeaths();
                gameState.decrementNumLives();
            }
            
            gameState.getMoveStack().push(result);
        }
        
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        
        MoveResult lastMove = gameState.getMoveStack().pop();
        Direction reverseDirection = getReverseDirection(lastMove.getDirection());
        
        var boardController = gameState.getGameBoardController();
        boardController.movePlayer(reverseDirection);
        
        return true;
    }
    
    private Direction getReverseDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }
}