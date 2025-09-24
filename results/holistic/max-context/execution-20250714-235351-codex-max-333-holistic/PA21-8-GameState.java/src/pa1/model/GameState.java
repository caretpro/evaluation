
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
     * The number of lives the player has internally (UNLIMITED_LIVES for unlimited).
     */
    private int numLives;

    /**
     * The number of gems initially on the game board when this instance was created.
     */
    private final int initialNumOfGems;

    /**
     * Creates an instance.
     *
     * <p>
     * The player will have a number of lives equal to the initial number of gems.
     * </p>
     *
     * @param gameBoard The game board to be managed by this instance.
     */
    public GameState(final GameBoard gameBoard) {
        this(gameBoard, gameBoard.getNumGems());
    }

    /**
     * Creates an instance.
     *
     * <p>
     * Note: Do NOT throw an exception when {@code numLives} is zero. This will be used in our testing.
     * If {@code numLives} is negative, treats as unlimited lives.
     * </p>
     *
     * @param gameBoard The game board to be managed by this instance.
     * @param numLives  Number of lives the player has.
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = (numLives < 0) ? UNLIMITED_LIVES : numLives;
    }

    /**
     * The game is won when there are no gems left in the game board.
     */
    public boolean hasWon() {
        return getNumGems() == 0;
    }

    /**
     * The game is lost when the player has no lives remaining.
     */
    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives <= 0;
    }

    /**
     * Increases the player's number of lives by the specified amount.
     */
    public int increaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the player's number of lives by the specified amount.
     */
    public int decreaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        return numLives;
    }

    /**
     * Decrements the player's number of lives by one.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves taken by the player.
     */
    public int incrementNumMoves() {
        return ++numMoves;
    }

    /**
     * Increments the number of deaths of the player.
     */
    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    /**
     * Returns Integer.MAX_VALUE if unlimited, else remaining lives.
     */
    public int getNumLives() {
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Score rules:
     * • start at 0
     * • gain +10 per gem collected (initialNumOfGems – current gems)
     * • lose –1 per move
     * • lose –2 per undo
     * • lose –4 per death
     */
    public int getScore() {
        int gemsCollected = initialNumOfGems - getNumGems();
        int score = gemsCollected * 10;
        score -= numMoves;
        score -= moveStack.getPopCount() * 2;
        score -= numDeaths * 4;
        return score;
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }
}