
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

/**
 * Core game logic: holds the map, the upcoming‑pipe queue, the delay bar,
 * and an undo stack of placed cells.
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
        this.map = new Map(rows, cols);
        this.pipeQueue = new PipeQueue(/* no pre‑generated pipes */);
        this.delayBar = new DelayBar(/* default delay = 0 */ 0);
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
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    /**
     * Convenience constructor for unit tests.
     *
     * @param rows     Number of rows of the given map.
     * @param cols     Number of columns of the given map.
     * @param delay    Delay in number of rounds before filling the pipes.
     * @param cellsRep String repr. of the map, with rows separated by '\n'.
     * @param pipes    Optional pre-generated pipes.
     * @return A new Game instance.
     */
    @NotNull
    static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, colChar).
     *
     * <p>
     * Converts the 1‑based char column (e.g. 'A') into a Coordinate, tries to place
     * the next pipe, and on success records everything for undo/redo.
     * </p>
     *
     * @param row Row number, 1‑based.
     * @param col Column letter, 'A'.. etc.
     * @return true if the pipe was validly placed.
     */
    public boolean placePipe(int row, char col) {
        // Convert 1‑based column char into 1‑based index
        int colIdx = col - 'A' + 1;
        Coordinate coord = new Coordinate(row, colIdx);
        Pipe next = pipeQueue.peek();

        // use public API: tryPlacePipe(Coordinate, Pipe)
        boolean ok = map.tryPlacePipe(coord, next);
        if (!ok) {
            return false;
        }

        // record this placement for undo (we build our own FillableCell)
        FillableCell placedCell = new FillableCell(coord, next);
        cellStack.push(placedCell);

        // consume and advance
        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Skip the current pipe (counts as a step).
     */
    public void skipPipe() {
        Pipe skipped = pipeQueue.peek();
        pipeQueue.consume();
        // record a skip marker by pushing null
        cellStack.push(null);
        numOfSteps++;
    }

    /**
     * Undo one step (either a placement or a skip).
     *
     * <p>Undoing itself counts as a step.</p>
     *
     * @return false if there’s nothing left to undo.
     */
    public boolean undoStep() {
        // if we’ve undone as many times as steps taken, nothing left
        if (cellStack.getUndoCount() >= numOfSteps) {
            return false;
        }

        FillableCell last = cellStack.pop();
        if (last != null) {
            // was a placement: remove it from map and restore pipeQueue
            map.undo(last.coord);
            pipeQueue.undo(last.getPipe().orElseThrow());
        }
        // undo counts as a step
        numOfSteps++;
        return true;
    }

    /**
     * Print the board, upcoming pipes, undo‑count, and delay bar.
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
     * Advance the game state: decrement the delay, then fill pipes up to the
     * current water‑flow distance.
     */
    public void updateState() {
        delayBar.countdown();
        int dist = delayBar.distance();
        if (dist >= 0) {
            if (dist == 0) {
                map.fillBeginTile();
            }
            map.fillTiles(dist);
        }
    }

    /**
     * True if a continuous path from source to sink exists.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * True if we failed to fill any new pipe tiles last round and haven’t won.
     */
    public boolean hasLost() {
        return map.hasLost() && !hasWon();
    }

    /**
     * How many actions (places, skips, undos) have been taken so far.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}