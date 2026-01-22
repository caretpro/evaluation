
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
     * Creates an instance with unlimited lives.
     *
     * @param gameBoard The game board to be managed by this instance.
     */
    public GameState(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.countGems();
        this.numLives = UNLIMITED_LIVES; // Unlimited lives by default
    }

    /**
     * Creates an instance with specified number of lives.
     *
     * @param gameBoard The game board to be managed by this instance.
     * @param numLives  Number of lives the player has. Negative value indicates unlimited lives.
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.countGems();
        if (numLives < 0) {
            this.numLives = UNLIMITED_LIVES; // Unlimited lives
        } else {
            this.numLives = numLives;
        }
    }

    /**
     * Checks whether the game has been won.
     *
     * @return Whether the player has won the game.
     */
    public boolean hasWon() {
        return getNumGems() == 0;
    }

    /**
     * Checks whether the game has been lost.
     *
     * @return Whether the player has lost the game.
     */
    public boolean hasLost() {
        return (numLives != UNLIMITED_LIVES) && (numLives <= 0);
    }

    /**
     * Increases the player's number of lives by the specified amount.
     *
     * @param delta The number of lives to give the player.
     * @return The new number of lives of the player. If unlimited, returns Integer.MAX_VALUE.
     */
    public int increaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        if (delta < 0) {
            return numLives;
        }
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the player's number of lives by the specified amount.
     *
     * @param delta The number of lives to take from the player.
     * @return The new number of lives of the player. If unlimited, returns Integer.MAX_VALUE.
     */
    public int decreaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        if (delta < 0) {
            return numLives;
        }
        numLives -= delta;
        if (numLives < 0) {
            numLives = 0;
        }
        return numLives;
    }

    /**
     * Decrements the player's number of lives by one.
     *
     * @return The new number of lives of the player. If unlimited, returns Integer.MAX_VALUE.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves taken by the player.
     *
     * @return The new number of moves taken.
     */
    public int incrementNumMoves() {
        this.numMoves++;
        return this.numMoves;
    }

    /**
     * Increments the number of deaths of the player.
     *
     * @return The new number of deaths.
     */
    public int incrementNumDeaths() {
        this.numDeaths++;
        return this.numDeaths;
    }

    /**
     * @return The current number of deaths of the player.
     */
    public int getNumDeaths() {
        return this.numDeaths;
    }

    /**
     * @return The current number of moves taken by the player.
     */
    public int getNumMoves() {
        return this.numMoves;
    }

    /**
     * @return Whether the player has unlimited lives.
     */
    public boolean hasUnlimitedLives() {
        return this.numLives == UNLIMITED_LIVES;
    }

    /**
     * @return The number of lives a player has. If unlimited, returns Integer.MAX_VALUE.
     */
    public int getNumLives() {
        return hasUnlimitedLives() ? Integer.MAX_VALUE : this.numLives;
    }

    /**
     * @return The number of gems that is still present on the game board.
     */
    public int getNumGems() {
        return gameBoard.countGems();
    }

    /**
     * Calculates the current score based on game metrics.
     *
     * @return The current score of this game.
     */
    public int getScore() {
        int score = gameBoard.getSize();
        score += getNumGems() * 10;
        score -= getNumMoves();
        score -= moveStack.getUndoCount();
        score -= getNumDeaths() * 4;
        return Math.max(score, 0);
    }

    /**
     * @return A controller of the managed game board for mutation.
     */
    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    /**
     * @return A read-only view of the managed game board.
     */
    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    /**
     * @return The instance of the managed {@link GameBoard}.
     */
    public GameBoard getGameBoard() {
        return this.gameBoard;
    }

    /**
     * @return The instance of the managed {@link MoveStack}.
     */
    public MoveStack getMoveStack() {
        return this.moveStack;
    }
}