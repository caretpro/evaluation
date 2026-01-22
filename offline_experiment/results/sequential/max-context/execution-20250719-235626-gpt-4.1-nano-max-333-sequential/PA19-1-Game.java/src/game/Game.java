
package game;

import game.map.Map;
import game.map.cells.Cell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
     * Constructor with only rows and columns, initializes with default map, pipes, and delay.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows, cols);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(10); // default delay
        this.numOfSteps = 0;
    }

    /**
     * Creates a game with a given map and various properties.
     *
     * @param rows Number of rows of the map.
     * @param cols Number of columns of the map.
     * @param delay Delay in number of rounds before filling the pipes.
     * @param cells Map cells.
     * @param pipes List of pre-generated pipes, if any.
     * @return A game constructed with the given parameters.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    // Existing methods...

    @NotNull
    static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return this.numOfSteps;
    }

    // Other methods...
}