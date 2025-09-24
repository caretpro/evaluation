
package game;

import game.map.Map;
import game.map.cells.FillableCell;
import game.map.cells.Cell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

import java.util.List;

/**
 * Main game logic: placing pipes, undo/skip steps, tracking steps, etc.
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
     * Convenience factory for testing: build a Game from a map‐string.
     *
     * @param rows     number of rows
     * @param cols     number of cols
     * @param delay    initial delay before flow
     * @param cellsRep textual map representation
     * @param pipes    pre‐generated pipes (may be null)
     * @return constructed Game
     */
    @NotNull
    public static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * True constructor: generates a blank map of (rows + 2)x(cols + 2).
     *
     * @param rows inner rows (excluding walls)
     * @param cols inner cols (excluding walls)
     */
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(rows + cols);
        this.numOfSteps = 0;
    }

    /**
     * True constructor: for testing/deserialization.
     *
     * @param rows   number of rows (including walls)
     * @param cols   number of cols (including walls)
     * @param delay  flow delay
     * @param cells  prebuilt Cell[][]
     * @param pipes  initial pipe list (may be null)
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.map.fillBeginTile();
        this.numOfSteps = 0;
    }

    /**
     * Display entire game state to stdout.
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
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }

    /**
     * Place the next pipe at (row, col).
     *
     * @param row  1-based row
     * @param col  column letter
     * @return true if placement succeeded
     */
    public boolean placePipe(int row, char col) {
        int colIndex = Character.toUpperCase(col) - 'A' + 1;
        Coordinate coord = new Coordinate(row, colIndex);
        Pipe next = pipeQueue.peek();
        boolean placed = map.tryPlacePipe(coord, next);
        if (placed) {
            cellStack.push(new FillableCell(coord, next));
            pipeQueue.consume();
            delayBar.countdown();
            numOfSteps++;
        }
        return placed;
    }

    /**
     * Skip the current pipe (counts as a step).
     */
    public void skipPipe() {
        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
    }

    /**
     * Undo last placement.  Counts as a step if successful.
     *
     * @return false if nothing to undo; true otherwise
     */
    public boolean undoStep() {
        FillableCell last = cellStack.pop();
        if (last == null) {
            return false;
        }
        map.undo(last.coord());
        // reinsert the undone pipe at the front of the queue
        last.getPipe().ifPresent(pipeQueue::undo);
        numOfSteps++;
        return true;
    }

    /**
     * Advance game state by one round (delay countdown + water fill).
     */
    public void updateState() {
        delayBar.countdown();
        map.fillTiles(delayBar.distance());
    }

    /**
     * @return true if a path from source to sink exists
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * @return true if the game is lost: flow has started and no new tiles fill
     */
    public boolean hasLost() {
        return delayBar.distance() > 0 && map.hasLost();
    }
}