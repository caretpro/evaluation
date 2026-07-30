
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
    public GameState(@NotNull final GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    /**
     * Creates an instance.
     *
     * @param gameBoard The game board to be managed by this instance.
     * @param numLives  Number of lives the player has. Negative values represent unlimited lives.
     */
    public GameState(@NotNull final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = numLives < 0 ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    /**
     * Checks whether the game has been won.
     *
     * @return Whether no gems remain on the game board.
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
     * @param delta The number of lives to give the player.
     * @return The new number of lives, or {@link Integer#MAX_VALUE} for unlimited lives.
     */
    public int increaseNumLives(final int delta) {
        if (!hasUnlimitedLives()) {
            numLives += delta;
        }
        return getNumLives();
    }

    /**
     * Decreases the player's number of lives.
     *
     * @param delta The number of lives to take from the player.
     * @return The new number of lives, or {@link Integer#MAX_VALUE} for unlimited lives.
     */
    public int decreaseNumLives(final int delta) {
        if (!hasUnlimitedLives()) {
            numLives -= delta;
        }
        return getNumLives();
    }

    /**
     * Decrements the player's number of lives by one.
     *
     * @return The new number of lives, or {@link Integer#MAX_VALUE} for unlimited lives.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves taken by the player.
     *
     * @return The new number of moves taken by the player.
     */
    public int incrementNumMoves() {
        return ++numMoves;
    }

    /**
     * Increments the number of deaths of the player.
     *
     * @return The new number of deaths of the player.
     */
    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    /**
     * @return The current number of deaths of the player.
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * @return The current number of moves taken by the player.
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
     * @return The number of gems still present on the game board.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Computes the current game score.
     *
     * @return The current score.
     */
    public int getScore() {
        final int gameBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        final int collectedGems = initialNumOfGems - getNumGems();

        return gameBoardSize
                + collectedGems * 10
                - numMoves
                - moveStack.getPopCount() * 2
                - numDeaths * 4;
    }

    /**
     * @return A controller of the managed game board for mutation.
     */
    @NotNull
    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    /**
     * @return A read-only view of the managed game board.
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
     * @return The managed move stack.
     */
    @NotNull
    public MoveStack getMoveStack() {
        return moveStack;
    }
}