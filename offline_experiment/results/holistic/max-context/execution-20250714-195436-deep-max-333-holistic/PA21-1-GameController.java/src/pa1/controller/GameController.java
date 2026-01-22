
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
        if (gameState.hasWon() || gameState.hasLost()) {
            return MoveResult.INVALID;
        }

        var boardController = gameState.getGameBoardController();
        MoveResult result = boardController.processMove(direction);

        if (result == MoveResult.SUCCESS || result == MoveResult.DEATH) {
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(direction);
            
            if (result == MoveResult.DEATH) {
                gameState.incrementNumDeaths();
                gameState.decrementNumLives();
            }
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        var lastDirection = gameState.getMoveStack().pop();
        var boardController = gameState.getGameBoardController();
        boardController.processUndo(lastDirection);
        return true;
    }
}