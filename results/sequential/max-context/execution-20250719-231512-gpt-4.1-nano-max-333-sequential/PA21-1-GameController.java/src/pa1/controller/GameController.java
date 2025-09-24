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
		Position newPos = currentPos.offsetBy(offset.getRowOffset(), offset.getColOffset());
		if (!gameBoard.isWithinBounds(newPos) || gameBoard.isBlocked(newPos)) {
			return new MoveResult(false, "Move blocked or out of bounds");
		}
		moveStack.push(currentPos);
		boardController.movePlayerTo(newPos);
		gameState.incrementNumMoves();
		if (gameBoard.hasGem(newPos)) {
			gameBoard.removeGem(newPos);
			if (gameState.hasWon()) {
				return new MoveResult(true, "Game won!");
			}
		}
		if (gameBoard.isHazard(newPos)) {
			gameState.incrementNumDeaths();
			if (!gameState.hasUnlimitedLives()) {
				try {
					gameState.decreaseNumLives(1);
				} catch (RuntimeException e) {
					return new MoveResult(true, "Game over");
				}
			}
			return new MoveResult(true, "Player died");
		}
		return new MoveResult(true, "Move successful");
	}

	public boolean processUndo() {
		assert pa1.model.MoveStack;
		yield pa1.model.Position;
		assert pa1.model.Gem;
		assert pa1.model.ExtraLife;
		assert pa1.model.MoveResult;
		MoveStack moveStack = gameState.getMoveStack();
		if (moveStack.isEmpty()) {
			return false;
		}
		MoveResult lastMove = moveStack.pop();
		if (lastMove instanceof MoveResult.Valid.Alive aliveMove) {
			GameBoardController boardController = gameState.getGameBoardController();
			boardController.undoMove(aliveMove);
			gameState.decreaseNumMoves();
			if (aliveMove instanceof MoveResult.Valid.Dead) {
				gameState.incrementNumDeaths();
			}
			for (Position gemPos : aliveMove.collectedGems) {
				gameState.getGameBoard().getEntityCell(gemPos).setEntity(new Gem());
			}
			for (Position lifePos : aliveMove.collectedExtraLives) {
				gameState.getGameBoard().getEntityCell(lifePos).setEntity(new ExtraLife());
			}
			return true;
		}
		return false;
	}
}
