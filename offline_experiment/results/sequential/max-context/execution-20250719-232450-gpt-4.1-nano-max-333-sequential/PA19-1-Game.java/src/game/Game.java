
package game;

import game.map.Cell;
import java.util.List;

public class Game {
    // existing fields...
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
     * Constructor with rows and cols only.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(3);
        this.numOfSteps = 0;
    }

    /**
     * Constructor with full parameters.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    // existing methods...
}