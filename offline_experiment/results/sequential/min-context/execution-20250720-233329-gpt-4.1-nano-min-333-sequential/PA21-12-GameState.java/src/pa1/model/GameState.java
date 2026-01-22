
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
     * The number of gems initially on the game board when this instance was created.
     */
    private final int initialNumOfGems;

    /**
     * Constructor for serialization/deserialization and default state.
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
     * Constructor with specified number of lives.
     */
    public GameState(@NotNull GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = numLives;
        this.moveStack = new MoveStack();
        this.numDeaths = 0;
        this.numMoves = 0;
    }

    // Existing getters and methods...
    public MoveStack getMoveStack() {
        return this.moveStack;
    }

    // ... other methods remain unchanged
}