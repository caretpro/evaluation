
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
    private final MoveStack moveStack;

    /**
     * The number of deaths of the player.
     */
    private int numDeaths;

    /**
     * The number of moves performed by the player (excluding invalid moves).
     */
    private int numMoves;

    /**
     * The number of lives the player has.
     */
    private int numLives;

    /**
     * The initial number of gems on the game board.
     */
    private final int initialNumOfGems;

    /**
     * Constructor for creating a GameState with unlimited lives.
     * @param gameBoard the current game board
     */
    public GameState(@NotNull GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = UNLIMITED_LIVES;
        this.moveStack = new MoveStack();
        this.numDeaths = 0;
        this.numMoves = 0;
    }

    /**
     * Constructor for creating a GameState with specified number of lives.
     * @param gameBoard the current game board
     * @param numLives the number of lives (negative for unlimited)
     */
    public GameState(@NotNull GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.getNumGems();
        if (numLives < 0) {
            this.numLives = UNLIMITED_LIVES;
        } else {
            this.numLives = numLives;
        }
        this.moveStack = new MoveStack();
        this.numDeaths = 0;
        this.numMoves = 0;
    }

    // Getters and other methods as needed...
}