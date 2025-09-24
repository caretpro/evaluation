
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class for tracking the state of multiple game components.
 */
public class GameState {

	/**
	 * Number representing unlimited number of lives for a player.
	 */
	public static final int UNLIMITED_LIVES = -1;

	/**
	 * The game board managed by this instance.
	 */
	@NotNull
	private final GameBoard gameBoard;

	/**
	 * {@link MoveStack} instance of all moves performed by the player.
	 */
	@NotNull
	private final MoveStack moveStack = new MoveStack();

	/**
	 * The number of deaths of the player.
	 */
	private int numDeaths = 0;

	/**
	 * The number of moves performed by the player (excluding invalid moves).
	 */
	private int numMoves = 0;

	/**
	 * The number of lives the player has.
	 */
	private int numLives;

	/**
	 * The number of gems initially on the game board when this instance was created.
	 */
	private final int initialNumOfGems;

	/**
	 * @return The instance of the managed {@link MoveStack}.
	 */
	public MoveStack getMoveStack() {
		return moveStack;
	}

	/**
	 * Creates an instance. <p> The player will have an unlimited number of lives by default. </p>
	 * @param gameBoard  The game board to be managed by this instance.
	 */
	public GameState(final GameBoard gameBoard) {
		this.gameBoard = Objects.requireNonNull(gameBoard);
		this.numLives = UNLIMITED_LIVES;
		this.initialNumOfGems = gameBoard.getNumGems();
	}

	/**
	 * Creates an instance. <p> Note: Do NOT throw an exception when  {@code  numLives}  is zero. This will be used in our testing. </p>
	 * @param gameBoard  The game board to be managed by this instance.
	 * @param numLives   Number of lives the player has. If the value is negative, treat as if the player has an unlimited number of lives.
	 */
	public GameState(final GameBoard gameBoard, final int numLives) {
		this.gameBoard = Objects.requireNonNull(gameBoard);
		this.numLives = numLives < 0 ? UNLIMITED_LIVES : numLives;
		this.initialNumOfGems = gameBoard.getNumGems();
	}

	/**
	 * Checks whether the game has been won. <p> The game is won when there are no gems left in the game board. </p>
	 * @return  Whether the player has won the game.
	 */
	public boolean hasWon() {
		return gameBoard.getNumGems() == 0;
	}

	/**
	 * Checks whether the game has been lost. <p> The game is lost when the player has no lives remaining. For games which the player has unlimited lives, this condition should never trigger. </p>
	 * @return  Whether the player has lost the game.
	 */
	public boolean hasLost() {
		return numLives != UNLIMITED_LIVES && numLives <= 0;
	}

	/**
	 * Increases the player's number of lives by the specified amount.
	 * @param delta  The number of lives to give the player.
	 * @return  The new number of lives of the player. If the player has unlimited number of lives, returns {@link Integer#MAX_VALUE} .
	 */
	public int increaseNumLives(final int delta) {
		if (numLives == UNLIMITED_LIVES) {
			return Integer.MAX_VALUE;
		}
		numLives += delta;
		return numLives;
	}

	/**
	 * Decreases the player's number of lives by the specified amount.
	 * @param delta  The number of lives to take from the player.
	 * @return  The new number of lives of the player. If the player has unlimited number of lives, returns {@link Integer#MAX_VALUE} .
	 */
	public int decreaseNumLives(final int delta) {
		if (numLives == UNLIMITED_LIVES) {
			return Integer.MAX_VALUE;
		}
		numLives -= delta;
		return numLives;
	}

	/**
	 * Decrements the player's number of lives by one.
	 * @return  The new number of lives of the player. If the player has unlimited number of lives, returns {@link Integer#MAX_VALUE} .
	 */
	public int decrementNumLives() {
		if (numLives == UNLIMITED_LIVES) {
			return Integer.MAX_VALUE;
		}
		numLives--;
		return numLives;
	}

	/**
	 * Increments the number of moves taken by the player.
	 * @return  The new number of moves taken by the player.
	 */
	public int incrementNumMoves() {
		numMoves++;
		return numMoves;
	}

	/**
	 * Increments the number of deaths of the player.
	 * @return  The new number of deaths of the player.
	 */
	public int incrementNumDeaths() {
		numDeaths++;
		return numDeaths;
	}

	/**
	 * @return  The current number of deaths of the player.
	 */
	public int getNumDeaths() {
		return numDeaths;
	}

	/**
	 * @return  The current number of moves taken by the player.
	 */
	public int getNumMoves() {
		return numMoves;
	}

	/**
	 * @return  Whether the player has unlimited lives.
	 */
	public boolean hasUnlimitedLives() {
		return numLives == UNLIMITED_LIVES;
	}

	/**
	 * @return  The number of lives a player has. If the player has an unlimited number of lives, returns {@link Integer#MAX_VALUE} .
	 */
	public int getNumLives() {
		if (numLives == UNLIMITED_LIVES) {
			return Integer.MAX_VALUE;
		}
		return numLives;
	}

	/**
	 * @return  The number of gems that is still present on the game board.
	 */
	public int getNumGems() {
		return gameBoard.getNumGems();
	}

	public int getScore() {
		int score = gameBoard.getNumRows() * gameBoard.getNumCols();
		score += gameBoard.getNumGems() * 10;
		score -= numMoves;
		score -= moveStack.getPopCount() * 2;
		score -= numDeaths * 4;
		return score;
	}

	/**
	 * @return  A controller of the managed game board for mutation.
	 */
	public GameBoardController getGameBoardController() {
		return new GameBoardController(this.gameBoard);
	}

	/**
	 * @return  A read-only view of the managed game board.
	 */
	public GameBoardView getGameBoardView() {
		return new GameBoardView(this.gameBoard);
	}

	/**
	 * @return  The instance of the managed  {@link GameBoard} .
	 */
	public GameBoard getGameBoard() {
		return this.gameBoard;
	}
}