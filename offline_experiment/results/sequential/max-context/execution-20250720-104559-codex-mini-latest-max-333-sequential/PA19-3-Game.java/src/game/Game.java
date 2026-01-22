
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
 * Main game‐state class.
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
     * Factory for tests: parse a textual map and optional pre‑generated pipes.
     *
     * @param rows    number of rows (including walls)
     * @param cols    number of cols (including walls)
     * @param delay   initial delay before water flows
     * @param cellsRep newline‑delimited char grid for the map
     * @param pipes   optional initial pipes for the queue
     * @return a fresh Game
     */
    @NotNull
    static Game fromString(int rows,
                           int cols,
                           int delay,
                           @NotNull String cellsRep,
                           @Nullable List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Constructor used by fromString(...) above.
     *
     * @param rows   number of rows (including walls)
     * @param cols   number of cols (including walls)
     * @param delay  initial delay before water flows
     * @param cells  prebuilt cell grid
     * @param pipes  optional initial pipes for the queue
     */
    public Game(int rows,
                int cols,
                int delay,
                @NotNull Cell[][] cells,
                @Nullable List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.map.fillBeginTile();
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    /**
     * “Main” constructor for interactive play: size and config‑loaded delay.
     *
     * @param rows number of rows (excluding walls)
     * @param cols number of cols (excluding walls)
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows, cols);
        this.map.fillBeginTile();
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(Deserializer.loadConfig().delay());
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }

    /**
     * Display the game board, pipe queue, undo count, and delay bar.
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
     * @return true if there is a complete source→sink path.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * @return true if the game is lost.
     */
    public boolean hasLost() {
        return delayBar.distance() > 0 && map.hasLost();
    }

    /**
     * Place the next pipe at (row, col). 1‑based columns 'A'.., rows 1‑based.
     *
     * @return true if placement succeeded (and advances pipeQueue, delayBar, undo stack, stepCount)
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Coordinate coord = new Coordinate(row, colIndex);
        Pipe next = pipeQueue.peek();
        if (!map.tryPlacePipe(coord, next)) {
            return false;
        }
        pipeQueue.consume();
        cellStack.push((FillableCell) map.cells[coord.row][coord.col]);
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Skip the next pipe.
     */
    public void skipPipe() {
        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
    }

    /**
     * Undo the last placement (or skip). Counts as a step.
     *
     * @return false if nothing to undo
     */
    public boolean undoStep() {
        FillableCell last = cellStack.pop();
        if (last == null) {
            return false;
        }
        map.undo(last.coord);
        last.getPipe().ifPresent(pipeQueue::undo);
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Advance water flow by the current delay distance. Counts as a step.
     */
    public void updateState() {
        int distance = delayBar.distance();
        map.fillTiles(distance);
        numOfSteps++;
    }

    /**
     * Expose the map’s path‐check for tests or external logic.
     */
    public boolean checkPath() {
        return map.checkPath();
    }
}