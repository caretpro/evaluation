
package game;

import game.map.Map;
import game.map.cells.FillableCell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

import java.util.List;

/**
 * Encapsulates a single play‐through of the pipe‐routing game.
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
        // Default delay of 10 rounds, no pre‐generated pipes.
        // Build an empty grid via Deserializer.parseString (empty string ⇒ all FillableCells).
        this(rows, cols, 10, Deserializer.parseString(rows, cols, ""), null);
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
    public Game(int rows, int cols, int delay, FillableCell[][] cells, @Nullable List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    /**
     * Convenience factory for tests.
     */
    @NotNull
    static Game fromString(int rows,
                           int cols,
                           int delay,
                           @NotNull String cellsRep,
                           @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     *
     * @param row Row number, 1-based.
     * @param col Column letter.
     * @return {@code true} if placed successfully.
     */
    public boolean placePipe(int row, char col) {
        Pipe nextPipe = pipeQueue.peek();
        Coordinate coord = new Coordinate(row, col);
        if (!map.tryPlacePipe(coord, nextPipe)) {
            return false;
        }
        // commit placement
        pipeQueue.consume();
        // record for undo: coordinate + pipe
        cellStack.push(coord, nextPipe);
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Skips the current pipe.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last placement step (skips are not undoable).
     *
     * @return false if nothing to undo.
     */
    public boolean undoStep() {
        CellStack.CoordPipe entry = cellStack.pop();
        if (entry == null) {
            return false;
        }
        numOfSteps++;
        // revert the pipe placement on the map
        map.undo(entry.coord());
        // put the pipe back into the front of the queue
        pipeQueue.undo(entry.pipe());
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
     * Fills pipes if the delay has passed.
     */
    public void updateState() {
        if (delayBar.distance() > 0) {
            map.fillBeginTile();
            map.fillTiles(delayBar.distance());
        }
    }

    /**
     * @return true if source→sink path exists.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * @return true if no pipes filled during the last round after the delay.
     */
    public boolean hasLost() {
        return delayBar.distance() > 0 && map.hasLost();
    }

    /**
     * @return steps taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}