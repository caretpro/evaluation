
package game;

import game.map.Cell;
import game.map.cells.FillableCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the game state.
 */
public class Game {

    @NotNull
    private final Map map;
    @NotNull
    private final PipeQueue pipeQueue;
    @NotNull
    private final DelayBar delayBar;
    @NotNull
    private final CellStack cellStack = new CellStack();

    private int numOfSteps = 0;

    /**
     * Creates a game with a given map and various properties.
     *
     * <p>
     * This constructor is a convenience method for unit testing purposes.
     * </p>
     *
     * @param rows Number of rows of the map.
     * @param cols Number of columns of the map.
     * @param delay Delay in number of rounds.
     * @param cells Map cells.
     * @param pipes List of pre-generated pipes.
     */
    public Game(int rows, int cols, int delay, @NotNull Cell[][] cells, @Nullable List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.cellStack = new CellStack();
        this.numOfSteps = 0;
    }

    /**
     * Additional constructor to initialize game with only rows and cols.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(10);
        this.cellStack = new CellStack();
        this.numOfSteps = 0;
    }

    @NotNull
    public int getNumOfSteps() {
        return this.numOfSteps;
    }

    // Existing methods...
}