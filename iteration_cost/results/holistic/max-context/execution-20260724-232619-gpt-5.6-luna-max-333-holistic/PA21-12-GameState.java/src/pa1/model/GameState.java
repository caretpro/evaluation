
package pa1.model;

import org.jetbrains.annotations.NotNull;
import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;

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
     * Instance of all moves performed by the player.
     */
    @NotNull
    private final MoveStack moveStack = new MoveStack();

    /**
     * The number of deaths of the player.
     */
    private int numDeaths;

    /**
     * The number of valid moves performed by the player.
     */
    private int numMoves;

    /**
     * The number of lives the player has.
     */
    private int numLives;

    /**
     * The number of gems initially on the game board.
     */
    private final int initialNumOfGems;

    /**
     * Creates an instance with unlimited lives.
     *
     * @param gameBoard The game board to be managed.
     */
    public GameState(@NotNull final GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    /**
     * Creates an instance.
     *
     * @param gameBoard The game board to be managed.
     * @param numLives The number of lives. Negative values and
     *                 {@link Integer#MAX_VALUE} represent unlimited lives.
     */
    public GameState(@NotNull final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = numLives < 0 || numLives == Integer.MAX_VALUE
                ? UNLIMITED_LIVES
                : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    /**
     * Checks whether the game has been won.
     *
     * @return Whether no gems remain.
     */
    public boolean hasWon() {
        return getNumGems() == 0;
    }

    /**
     * Checks whether the game has been lost.
     *
     * @return Whether the player has no lives remaining.
     */
    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives <= 0;
    }

    /**
     * Increases the player's number of lives.
     *
     * @param delta The number of lives to add.
     * @return The resulting number of lives, or {@link Integer#MAX_VALUE}
     *         for unlimited lives.
     */
    public int increaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }

        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the player's number of lives.
     *
     * @param delta The number of lives to remove.
     * @return The resulting number of lives, or {@link Integer#MAX_VALUE}
     *         for unlimited lives.
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
     *
     * @return The resulting number of lives, or {@link Integer#MAX_VALUE}
     *         for unlimited lives.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of valid moves.
     *
     * @return The resulting number of moves.
     */
    public int incrementNumMoves() {
        return ++numMoves;
    }

    /**
     * Increments the number of deaths.
     *
     * @return The resulting number of deaths.
     */
    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    /**
     * @return The current number of deaths.
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * @return The current number of valid moves.
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * @return Whether the player has unlimited lives.
     */
    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    /**
     * @return The number of lives, or {@link Integer#MAX_VALUE} for unlimited lives.
     */
    public int getNumLives() {
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
    }

    /**
     * @return The number of gems remaining on the board.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Computes the current score.
     *
     * @return The current game score.
     */
    public int getScore() {
        final int initialScore = gameBoard.getNumRows() * gameBoard.getNumCols();

        return initialScore
                + initialNumOfGems * 10
                - numMoves
                - moveStack.getPopCount() * 2
                - numDeaths * 4;
    }

    /**
     * @return A controller for mutating the game board.
     */
    @NotNull
    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    /**
     * @return A read-only view of the game board.
     */
    @NotNull
    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    /**
     * @return The managed game board.
     */
    @NotNull
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * @return The move stack containing all valid moves.
     */
    @NotNull
    public MoveStack getMoveStack() {
        return moveStack;
    }
}