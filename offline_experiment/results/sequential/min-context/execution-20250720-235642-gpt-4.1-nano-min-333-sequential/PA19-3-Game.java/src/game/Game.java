
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
     * Creates a game with a given map and various properties.
     *
     * <p>
     * This constructor is a convenience method for unit testing purposes.
     * </p>
     *
     * @param rows Number of rows of the given map.
     * @param cols Number of columns of the given map.
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(0);
        for (int r = 0; r < this.map.getRows(); r++) {
            for (int c = 0; c < this.map.getCols(); c++) {
                if (r == 0 || r == this.map.getRows() - 1 || c == 0 || c == this.map.getCols() - 1) {
                    this.map.setCell(r, c, new Cell(Cell.Type.WALL));
                } else {
                    this.map.setCell(r, c, new FillableCell());
                }
            }
        }
        this.numOfSteps = 0;
    }

    /**
     * Creates a game with specified parameters.
     *
     * @param rows Number of rows.
     * @param cols Number of columns.
     * @param delay Delay in rounds.
     * @param cells 2D array of cells representing the map.
     * @param pipes List of pipes.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
        for (int r = 0; r < this.map.getRows(); r++) {
            for (int c = 0; c < this.map.getCols(); c++) {
                if (r == 0 || r == this.map.getRows() - 1 || c == 0 || c == this.map.getCols() - 1) {
                    this.map.setCell(r, c, new Cell(Cell.Type.WALL));
                } else {
                    this.map.setCell(r, c, cells[r - 1][c - 1]);
                }
            }
        }
        if (pipes != null) {
            for (Pipe pipe : pipes) {
                this.pipeQueue.enqueue(pipe);
            }
        }
    }

    // Existing methods...

    @NotNull
    public static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public int getNumOfSteps() {
        return this.numOfSteps;
    }
}