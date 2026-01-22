
package game;

import game.map.Map;
import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

import java.util.List;

/**
 * Main game logic.
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
     * Creates a game with a map of rows x cols.
     *
     * @param rows Number of rows to generate, not counting the surrounding walls.
     * @param cols Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this.map       = new Map(rows, cols);
        this.pipeQueue = new PipeQueue();
        this.delayBar  = new DelayBar(0);
    }

    /**
     * Creates a game with a given map and various properties.
     *
     * @param rows  Number of rows of the given map.
     * @param cols  Number of columns of the given map.
     * @param delay Delay in number of rounds before filling the pipes.
     * @param cells Cells of the map.
     * @param pipes List of pre-generated pipes, if any.
     */
    public Game(int rows,
                int cols,
                int delay,
                @NotNull Cell[][] cells,
                @Nullable List<Pipe> pipes) {
        this.map       = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar  = new DelayBar(delay);
    }

    /**
     * Convenience for unit tests.
     */
    @NotNull
    static Game fromString(int rows,
                           int cols,
                           int delay,
                           @NotNull String cellsRep,
                           @Nullable List<Pipe> pipes) {
        var cells = io.Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     *
     * <p>This method converts (row, col) into a {@link Coordinate}, attempts to place the pipe on the map,
     * and if successful updates the pipe queue, delay bar, undo stack, and step count.</p>
     *
     * @param row  1‑based row index.
     * @param col  column letter ('A', 'B', …).
     * @return true if the pipe was placed.
     */
    public boolean placePipe(int row, char col) {
        int colIdx = col - 'A' + 1;
        Pipe next  = pipeQueue.peek();

        Coordinate coord = new Coordinate(row, colIdx);
        boolean ok = map.tryPlacePipe(coord, next);
        if (!ok) {
            return false;
        }

        // Record for undo
        FillableCell placed = new FillableCell(coord, next);
        cellStack.push(placed);

        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and uses the next one.
     * Counts as a step but is not undoable.
     */
    public void skipPipe() {
        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
    }

    /**
     * Undoes the last pipe placement step.
     *
     * <p>Undoing still increments the step‑count.</p>
     *
     * @return false if no placements remain to undo.
     */
    public boolean undoStep() {
        FillableCell last = cellStack.pop();
        if (last == null) {
            return false;
        }
        // Restore the map cell and re‑insert its pipe into the queue
        map.undo(last.coord);
        Pipe p = last.getPipe().orElseThrow();
        pipeQueue.undo(p);

        numOfSteps++;
        return true;
    }

    /**
     * Displays the current game state.
     */
    public void display() {
        map.display();
        System.out.println();
        pipeQueue.display();
        cellStack.display();
        System.out.println();
        delayBar.display();
    }

    /**
     * Updates the game state by filling pipes up to the current water‑flow distance.
     */
    public void updateState() {
        if (delayBar.distance() >= 0) {
            map.fillBeginTile();
            map.fillTiles(delayBar.distance());
        }
    }

    /**
     * @return true if a path from source to sink exists.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * @return true if no pipes were filled in the last round.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /**
     * @return total number of player moves (placements + skips + undos).
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}