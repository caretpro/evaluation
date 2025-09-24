
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class for tracking the state of multiple game components.
 */
public class GameState {

    public static final int UNLIMITED_LIVES = -1;

    @NotNull
    private final GameBoard gameBoard;

    @NotNull
    private final MoveStack moveStack = new MoveStack();

    private int numDeaths = 0;
    private int numMoves = 0;
    private int numLives;
    private final int initialNumOfGems;

    public GameState(final GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = numLives < 0 ? UNLIMITED_LIVES : numLives;
    }

    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives == 0;
    }

    public int increaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(final int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives = Math.max(0, numLives - delta);
        return numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
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
        return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int score = 0;
        score += (initialNumOfGems - getNumGems()) * 10;
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