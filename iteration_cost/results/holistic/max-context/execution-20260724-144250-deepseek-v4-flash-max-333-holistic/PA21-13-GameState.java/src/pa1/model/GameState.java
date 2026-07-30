
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = (numLives < 0) ? UNLIMITED_LIVES : numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        return !hasUnlimitedLives() && numLives <= 0;
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
        numLives -= delta;
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
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        int gameBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        int gemPoints = collectedGems * 10;
        int movePenalty = numMoves;
        int undoPenalty = 2 * moveStack.getPopCount();
        int deathPenalty = 4 * numDeaths;
        return gameBoardSize + gemPoints - movePenalty - undoPenalty - deathPenalty;
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