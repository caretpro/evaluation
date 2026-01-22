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

	public void GameController(final GameState gameState) {
		this.gameState = Objects.requireNonNull(gameState);
	}

	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "Direction cannot be null");
		GameBoard gameBoard = gameState.getGameBoard();
		GameBoardController boardController = gameState.getGameBoardController();
		MoveStack moveStack = gameState.getMoveStack();
		Position currentPos = gameBoard.getPlayerPosition();
		PositionOffset offset = direction.getOffset();
		Position newPos = currentPos.offset(offset.getRowOffset(), offset.getColOffset());
		if (!gameBoard.isWithinBounds(newPos)) {
			return MoveResult.INVALID_MOVE;
		}
		if (gameBoard.isBlocked(newPos)) {
			return MoveResult.INVALID_MOVE;
		}
		moveStack.push(currentPos);
		gameBoard.setPlayerPosition(newPos);
		boolean collectedGem = false;
		if (gameBoard.hasGem(newPos)) {
			gameBoard.removeGem(newPos);
			collectedGem = true;
		}
		gameState.incrementNumMoves();
		if (gameState.hasWon()) {
			return MoveResult.WON;
		}
		if (gameBoard.isHazard(newPos)) {
			gameState.incrementNumDeaths();
			if (!gameState.hasUnlimitedLives()) {
				gameState.decrementNumLives();
			}
			if (gameState.hasLost()) {
				return MoveResult.LOST;
			}
			return MoveResult.DEATH;
		}
		if (collectedGem) {
			return MoveResult.GEM_COLLECTED;
		}
		return MoveResult.MOVED;
	}

	public boolean processUndo() {
		MoveStack moveStack = gameState.getMoveStack();
		if (moveStack.isEmpty()) {
			return false;
		}
		MoveResult lastMove = moveStack.pop();
		GameBoardController boardController = gameState.getGameBoardController();
		boardController.undoMove(lastMove);
		if (lastMove instanceof MoveResult.Valid.Dead) {
			gameState.decrementNumDeaths();
			if (gameState.getNumLives() != Integer.MAX_VALUE) {
				gameState.increaseNumLives(1);
			}
		}
		return true;
	}
}
