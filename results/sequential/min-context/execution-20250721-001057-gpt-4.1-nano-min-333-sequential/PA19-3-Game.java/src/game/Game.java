
package game;

import game.map.Map;
import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

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
     * Constructor for minimal game setup with rows and columns.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        for (int r = 0; r < rows + 2; r++) {
            for (int c = 0; c < cols + 2; c++) {
                if (r == 0 || r == rows + 1 || c == 0 || c == cols + 1) {
                    map.setCell(r, c, new FillableCell());
                } else {
                    map.setCell(r, c, new Cell());
                }
            }
        }
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
        this.numOfSteps = 0;
    }

    /**
     * Constructor for full game setup with map cells, pipes, and delay.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows + 2, cols + 2);
        for (int r = 0; r < rows + 2; r++) {
            for (int c = 0; c < cols + 2; c++) {
                if (r == 0 || r == rows + 1 || c == 0 || c == cols + 1) {
                    this.map.setCell(r, c, new FillableCell());
                } else {
                    this.map.setCell(r, c, cells[r - 1][c - 1]);
                }
            }
        }
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    // Existing methods...

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return this.numOfSteps;
    }
}