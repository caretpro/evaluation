
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
     * Constructor for creating a game with specified parameters.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        int totalRows = rows + 2;
        int totalCols = cols + 2;
        this.map = new Map(totalRows, totalCols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.map.setCell(r + 1, c + 1, cells[r][c]);
            }
        }
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    /**
     * Constructor for creating a game with just rows and columns, initializes empty map.
     */
    public Game(int rows, int cols) {
        int totalRows = rows + 2;
        int totalCols = cols + 2;
        this.map = new Map(totalRows, totalCols);
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < totalCols; c++) {
                if (r == 0 || r == totalRows - 1 || c == 0 || c == totalCols - 1) {
                    this.map.setCell(r, c, new Cell());
                } else {
                    this.map.setCell(r, c, new FillableCell());
                }
            }
        }
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar();
        this.numOfSteps = 0;
    }

    /**
     * Constructor matching the signature used in tests and main methods.
     */
    public Game(int rows, int cols, int delay, @NotNull Cell[][] cells, @Nullable List<Pipe> pipes) {
        int totalRows = rows + 2;
        int totalCols = cols + 2;
        this.map = new Map(totalRows, totalCols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.map.setCell(r + 1, c + 1, cells[r][c]);
            }
        }
        this.pipeQueue = new PipeQueue(pipes != null ? pipes : List.of());
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
    }

    // ... rest of the class remains unchanged ...
}