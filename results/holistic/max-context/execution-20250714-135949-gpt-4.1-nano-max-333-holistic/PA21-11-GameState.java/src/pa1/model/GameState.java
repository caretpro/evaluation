
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;
import org.jetbrains.annotations.NotNull;

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
     * Creates an instance with unlimited lives.
     *
     * @param gameBoard The game board to be managed by this instance.
     */
    public GameState(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = UNLIMITED_LIVES; // Unlimited lives by default
    }

    /**
     * Creates an instance with specified number of lives.
     *
     * @param gameBoard The game board to be managed by this instance.
     * @param numLives  Number of lives the player has. Negative for unlimited.
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = gameBoard;
        this.initialNumOfGems = gameBoard.getNumGems();
        if (numLives <= 0) {
            this.numLives = UNLIMITED_LIVES;
        } else {
            this.numLives = numLives;
        }
    }

    /**
     * Checks whether the game has been won (no gems left).
     */
    public boolean hasWon() {
        return getNumGems() == 0;
    }

    /**
     * Checks whether the game has been lost (no lives remaining unless unlimited).
     */
    public boolean hasLost() {
        return !hasUnlimitedLives() && getNumLives() <= 0;
    }

    /**
     * Increases the player's number of lives by delta.
     */
    public int increaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        if (delta < 0) {
            // Negative delta, treat as decrease
            return decreaseNumLives(-delta);
        }
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the player's number of lives by delta.
     */
    public int decreaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        if (delta < 0) {
            // Negative delta, treat as increase
            return increaseNumLives(-delta);
        }
        numLives -= delta;
        if (numLives < 0) {
            numLives = 0;
        }
        return numLives;
    }

    /**
     * Decrements the number of lives by one.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves.
     */
    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    /**
     * Increments the number of deaths.
     */
    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    /**
     * Gets the current number of deaths.
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * Gets the current number of moves.
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * Checks if the player has unlimited lives.
     */
    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    /**
     * Gets the current number of lives.
     */
    public int getNumLives() {
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
    }

    /**
     * Gets the number of gems remaining.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Calculates the current score based on game rules.
     */
    public int getScore() {
        int initialSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int score = initialSize; // initial score
        score += getNumGems() * 10; // each gem worth 10
        score -= getNumMoves(); // each move deducts 1
        score -= getMoveStack().getPopCount() * 2; // each undo deducts 2
        score -= getNumDeaths() * 4; // each death deducts 4
        return Math.max(score, 0);
    }

    /**
     * Returns the game board controller for mutation.
     */
    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    /**
     * Returns a read-only view of the game board.
     */
    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    /**
     * Returns the managed game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Returns the move stack.
     */
    public MoveStack getMoveStack() {
        return moveStack;
    }
}