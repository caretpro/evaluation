
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
     * Stack of all valid moves performed by the player.
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
     * The number of gems initially present on the game board.
     */
    private final int initialNumOfGems;

    /**
     * Creates a game state with unlimited lives.
     *
     * @param gameBoard the game board to manage
     */
    public GameState(final GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    /**
     * Creates a game state.
     *
     * @param gameBoard the game board to manage
     * @param numLives the number of lives; negative values represent unlimited lives
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = numLives < 0 ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    /**
     * @return whether no gems remain on the board
     */
    public boolean hasWon() {
        return getNumGems() == 0;
    }

    /**
     * @return whether the player has no lives remaining
     */
    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives <= 0;
    }

    /**
     * Increases the player's number of lives.
     *
     * @param delta the number of lives to add
     * @return the resulting number of lives, or {@link Integer#MAX_VALUE} for unlimited lives
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
     * @param delta the number of lives to remove
     * @return the resulting number of lives, or {@link Integer#MAX_VALUE} for unlimited lives
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
     * @return the resulting number of lives, or {@link Integer#MAX_VALUE} for unlimited lives
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of valid moves.
     *
     * @return the resulting number of moves
     */
    public int incrementNumMoves() {
        return ++numMoves;
    }

    /**
     * Increments the number of deaths.
     *
     * @return the resulting number of deaths
     */
    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    /**
     * @return the current number of deaths
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * @return the current number of valid moves
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * @return whether the player has unlimited lives
     */
    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    /**
     * @return the number of lives, or {@link Integer#MAX_VALUE} for unlimited lives
     */
    public int getNumLives() {
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
    }

    /**
     * @return the number of gems remaining on the board
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Computes the current score.
     *
     * @return the current score
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
     * @return a controller for mutating the managed game board
     */
    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    /**
     * @return a read-only view of the managed game board
     */
    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    /**
     * @return the managed game board
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * @return the stack of valid moves
     */
    public MoveStack getMoveStack() {
        return moveStack;
    }
}