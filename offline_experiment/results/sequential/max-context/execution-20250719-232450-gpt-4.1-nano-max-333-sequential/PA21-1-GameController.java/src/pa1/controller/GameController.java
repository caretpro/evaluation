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
		Position playerPos = gameBoard.getPlayerPosition();
		PositionOffset offset = direction.getOffset();
		Position targetPos = new Position(playerPos.getRow() + offset.getRowOffset(),
				playerPos.getCol() + offset.getColOffset());
		if (!gameBoard.isWithinBounds(targetPos)) {
			return new MoveResult(false, "Cannot move outside the board");
		}
		if (gameBoard.isBlocked(targetPos)) {
			return new MoveResult(false, "Target cell is blocked");
		}
		moveStack.push(playerPos);
		boardController.movePlayerTo(targetPos);
		gameState.incrementNumMoves();
		if (gameBoard.hasGem(targetPos)) {
			boardController.removeGem(targetPos);
			if (gameState.hasWon()) {
				return new MoveResult(true, "Game won!");
			}
		}
		if (gameBoard.isHazard(targetPos)) {
			gameState.incrementNumDeaths();
			if (!gameState.hasUnlimitedLives()) {
				try {
					gameState.decreaseNumLives(1);
				} catch (RuntimeException e) {
					return new MoveResult(true, "Game over");
				}
			}
			Position startPos = gameBoard.getStartPosition();
			boardController.movePlayerTo(startPos);
			return new MoveResult(true, "Player died");
		}
		return new MoveResult(true, "Move successful");
	}

	public boolean processUndo() {
		MoveStack moveStack = gameState.getMoveStack();
		if (moveStack.isEmpty()) {
			return false;
		}
		MoveResult lastMove = moveStack.pop();
		if (!(lastMove instanceof MoveResult.Valid.Alive aliveMove)) {
			return false;
		}
		GameBoard gameBoard = gameState.getGameBoard();
		GameBoardController boardController = gameState.getGameBoardController();
		Position originalPos = aliveMove.origPosition;
		boardController.undoMove(aliveMove);
		gameState.getNumMoves();
		if (lastMove instanceof MoveResult.Valid.Dead) {
			gameState.incrementNumDeaths();
		}
		return true;
	}
}
