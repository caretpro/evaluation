
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

    // Correct constructors without void return type

    public GameState(final GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.numLives = (numLives < 0) ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

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

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
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

    public int getNumLives() {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int gameBoardSize = gameBoard.getSize();
        int score = gameBoardSize;
        score += initialNumOfGems * 10;
        score -= numMoves;
        score -= moveStack.getNumUndos() * 2;
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
}