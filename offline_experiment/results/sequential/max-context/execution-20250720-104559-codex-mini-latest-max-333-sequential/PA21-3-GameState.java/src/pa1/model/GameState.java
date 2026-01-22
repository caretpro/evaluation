
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class for tracking the state of multiple game components.
 */
public class GameState {

    /** Number representing unlimited number of lives for a player. */
    public static final int UNLIMITED_LIVES = -1;

    /** The game board managed by this instance. */
    @NotNull
    private final GameBoard gameBoard;

    /** {@link MoveStack} instance of all moves performed by the player. */
    @NotNull
    private final MoveStack moveStack = new MoveStack();

    private int numDeaths = 0;
    private int numMoves = 0;
    private int numLives;
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
        this(gameBoard, UNLIMITED_LIVES);
    }

    /**
     * Creates an instance. <p> Note: Do NOT throw an exception when {@code numLives} is zero. This will be used in our testing. </p>
     * @param gameBoard  The game board to be managed by this instance.
     * @param numLives   Number of lives the player has. If the value is negative, treat as if the player has an unlimited number of lives.
     */
    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
        this.numLives = (numLives < 0) ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = this.gameBoard.getNumGems();
    }

    // … the rest of the class remains unchanged …

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
        return numLives;
    }

    public int incrementNumMoves() {
        return ++numMoves;
    }

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

    public int getNumLives() {
        return (numLives == UNLIMITED_LIVES) ? Integer.MAX_VALUE : numLives;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    @Override
    public int getScore() {
        int score = gameBoard.getNumRows() * gameBoard.getNumCols();
        int gemsCollected = initialNumOfGems - gameBoard.getNumGems();
        score += gemsCollected * 10;
        score -= numMoves;
        score -= moveStack.getPopCount() * 2;
        score -= numDeaths * 4;
        return score;
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard, moveStack);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}