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
		final var gameBoard = gameState.getGameBoard();
		final var currentPosition = gameBoard.getPlayerPosition();
		final var offset = direction.getOffset();
		final var newRow = currentPosition.getRow() + offset.getRowOffset();
		final var newCol = currentPosition.getCol() + offset.getColOffset();
		if (!gameBoard.isWithinBounds(newRow, newCol) || gameBoard.isBlocked(newRow, newCol)) {
			return MoveResult.INVALID;
		}
		boolean moved = gameBoard.movePlayerTo(newRow, newCol);
		if (!moved) {
			return MoveResult.FAIL;
		}
		gameState.incrementNumMoves();
		if (gameBoard.isGemAt(newRow, newCol)) {
			gameBoard.collectGemAt(newRow, newCol);
			if (gameState.hasWon()) {
				return MoveResult.WIN;
			}
		}
		if (gameBoard.isHazardAt(newRow, newCol)) {
			gameState.incrementNumDeaths();
			gameState.decrementNumLives();
			if (gameState.hasLost()) {
				return MoveResult.LOSE;
			} else {
				return MoveResult.DEATH;
			}
		}
		return MoveResult.SUCCESS;
	}

	public boolean processUndo() {
		if (gameState.getMoveStack().isEmpty()) {
			return false;
		}
		pa1.model.Move lastMove = gameState.getMoveStack().pop();
		pa1.model.GameBoard gameBoard = gameState.getGameBoard();
		var currentPosition = gameBoard.getPlayerPosition();
		int prevRow = currentPosition.getRow() - lastMove.getDirection().getOffset().getRowOffset();
		int prevCol = currentPosition.getCol() - lastMove.getDirection().getOffset().getColOffset();
		boolean movedBack = gameBoard.movePlayerTo(prevRow, prevCol);
		if (!movedBack) {
			gameState.getMoveStack().push(lastMove);
			return false;
		}
		gameState.decrementNumMoves();
		if (gameBoard.isGemAt(currentPosition.getRow(), currentPosition.getCol())) {
			gameBoard.restoreGemAt(currentPosition.getRow(), currentPosition.getCol());
		}
		if (gameBoard.isHazardAt(prevRow, prevCol)) {
			gameState.decrementNumDeaths();
			gameState.increaseNumLives(1);
		}
		return true;
	}
}
