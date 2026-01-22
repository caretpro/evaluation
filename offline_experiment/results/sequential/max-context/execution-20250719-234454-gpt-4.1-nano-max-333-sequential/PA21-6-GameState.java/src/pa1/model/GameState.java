
package pa1.model;

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
     * Constructor for unlimited lives.
     * @param gameBoard The game board instance.
     */
    public GameState(@NotNull GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numLives = UNLIMITED_LIVES;
        this.numDeaths = 0;
        this.numMoves = 0;
    }

    /**
     * Constructor for limited lives.
     * @param gameBoard The game board instance.
     * @param numLives The number of lives (negative or zero for unlimited).
     */
    public GameState(@NotNull GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialNumOfGems = gameBoard.getNumGems();
        if (numLives <= 0) {
            this.numLives = UNLIMITED_LIVES;
        } else {
            this.numLives = numLives;
        }
        this.numDeaths = 0;
        this.numMoves = 0;
    }

    // Existing methods...
    public MoveStack getMoveStack() {
        return this.moveStack;
    }

    // ... other methods remain unchanged
}