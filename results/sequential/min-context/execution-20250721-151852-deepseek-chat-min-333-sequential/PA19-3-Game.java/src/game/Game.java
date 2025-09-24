
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
     * Creates a game with a map of rows x cols.
     * @param rows  Number of rows to generate, not counting the surrounding walls.
     * @param cols  Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows, cols);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
    }

    /**
     * Creates a game with a given map and various properties.
     * @param rows  Number of rows of the given map.
     * @param cols  Number of columns of the given map.
     * @param delay  Delay in number of rounds before filling the pipes.
     * @param cells  Cells of the map.
     * @param pipes  List of pre-generated pipes, if any.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j] instanceof FillableCell) {
                    cellStack.push((FillableCell) cells[i][j]);
                }
            }
        }
    }

    // ... rest of the class remains unchanged ...
}