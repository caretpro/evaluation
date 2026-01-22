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
		this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
	}

	public MoveResult processMove(final Direction direction) {
		var boardController = gameState.getGameBoardController();
		MoveResult result = boardController.movePlayer(direction);
		if (!result.isValid()) {
			return result;
		}
		gameState.incrementNumMoves();
		if (result.isDeath()) {
			gameState.decrementNumLives();
			gameState.incrementNumDeaths();
		}
		gameState.getMoveStack().push(result);
		return result;
	}

	public boolean processUndo() {
		var moveStack = gameState.getMoveStack();
		if (moveStack.isEmpty()) {
			return false;
		}
		MoveResult lastMove = moveStack.pop();
		var boardController = gameState.getGameBoardController();
		if (lastMove instanceof MoveResult.Valid validMove) {
			boardController.setPlayerPosition(validMove.origPosition);
			if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
				for (var gemPos : aliveMove.collectedGems) {
					boardController.placeGem(gemPos);
				}
				for (var extraLifePos : aliveMove.collectedExtraLives) {
					boardController.placeExtraLife(extraLifePos);
				}
			}
			try {
				var numMovesField = GameState.class.getDeclaredField("numMoves");
				numMovesField.setAccessible(true);
				int currentMoves = (int) numMovesField.get(gameState);
				if (currentMoves > 0) {
					numMovesField.set(gameState, currentMoves - 1);
				}
				if (validMove instanceof MoveResult.Valid.Dead) {
					var numDeathsField = GameState.class.getDeclaredField("numDeaths");
					numDeathsField.setAccessible(true);
					int currentDeaths = (int) numDeathsField.get(gameState);
					if (currentDeaths > 0) {
						numDeathsField.set(gameState, currentDeaths - 1);
					}
					gameState.increaseNumLives(1);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Failed to adjust game state counters during undo", e);
			}
		} else {
			try {
				var numMovesField = GameState.class.getDeclaredField("numMoves");
				numMovesField.setAccessible(true);
				int currentMoves = (int) numMovesField.get(gameState);
				if (currentMoves > 0) {
					numMovesField.set(gameState, currentMoves - 1);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Failed to adjust game state counters during undo", e);
			}
		}
		return true;
	}
}
