
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
     * Constructs a GameState with unlimited lives.
     * @param gameBoard the game board to manage
     */
    public GameState(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    /**
     * Constructs a GameState with specified number of lives.
     * If numLives < 0, unlimited lives are set.
     * @param gameBoard the game board to manage
     * @param numLives the number of lives for the player
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.numLives = (numLives < 0) ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    /**
     * Checks whether the game has been won.
     * The game is won when there are no gems left in the game board.
     * @return Whether the player has won the game.
     */
    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return numLives != UNLIMITED_LIVES && numLives <= 0;
    }

    public int increaseNumLives(final int delta) {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(final int delta) {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        if (numLives < 0) {
            numLives = 0;
        }
        return numLives;
    }

    /**
     * Decrements the player's number of lives by one.
     * @return The new number of lives of the player. If the player has unlimited number of lives, returns {@link Integer#MAX_VALUE}.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves taken by the player.
     * @return The new number of moves taken by the player.
     */
    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    /**
     * Increments the number of deaths of the player.
     * @return The new number of deaths of the player.
     */
    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
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
     * @return The number of lives a player has. If the player has an unlimited number of lives, returns {@link Integer#MAX_VALUE}.
     */
    public int getNumLives() {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    /**
     * @return The number of gems that is still present on the game board.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int initialScore = gameBoard.getSize();
        int gemPoints = initialNumOfGems * 10;
        int movePenalty = numMoves;
        int undoPenalty = moveStack.getNumUndos() * 2;
        int deathPenalty = numDeaths * 4;
        return initialScore + gemPoints - movePenalty - undoPenalty - deathPenalty;
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
        return gameBoard;
    }
}