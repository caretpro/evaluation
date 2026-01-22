
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
 * Core game logic for placing pipes, skipping, undoing, and tracking steps.
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
     * Convenience factory for tests. Parses a string representation of cells.
     */
    @NotNull
    static Game fromString(int rows, int cols, int delay,
                           @NotNull String cellsRep,
                           @Nullable List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Two‑argument constructor used by Main (rows, cols).
     */
    public Game(int rows, int cols) {
        this(rows, cols, DelayBar.DEFAULT_DELAY, Deserializer.emptyCells(rows, cols), null);
    }

    /**
     * Full constructor.
     */
    public Game(int rows,
                int cols,
                int delay,
                Cell[][] cells,
                @Nullable List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.delayBar = new DelayBar(delay);
        this.pipeQueue = new PipeQueue(delayBar);
        if (pipes != null) {
            for (Pipe p : pipes) {
                pipeQueue.enqueue(p);
            }
        }
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
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }

    /**
     * Attempts to place the next pipe at the given 1‑based row, A‑based column.
     * If successful, updates queue, delay bar, history stack, and step count.
     *
     * @return true if placement succeeded
     */
    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row - 1, col - 'A');
        Pipe nextPipe = pipeQueue.peek();
        if (nextPipe == null) {
            return false;
        }
        boolean placed = map.placePipe(nextPipe, coord);
        if (!placed) {
            return false;
        }
        pipeQueue.dequeue();
        delayBar.advance();
        cellStack.push(coord);
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and use the next pipe.
     */
    public void skipPipe() {
        if (pipeQueue.peek() != null) {
            pipeQueue.dequeue();
            delayBar.advance();
            numOfSteps++;
        }
    }

    /**
     * Undos a step from the game. Note: Undoing a step counts as a step.
     * @return false if there are no steps to undo, otherwise true.
     */
    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        Coordinate last = cellStack.pop();
        map.clearCell(last);
        numOfSteps++;
        return true;
    }

    /**
     * Updates the game state. More specifically, it fills pipes according to
     * the distance the water should flow.
     */
    public void updateState() {
        int flowDistance = delayBar.getDistance();
        map.fillTiles(flowDistance);
    }

    /**
     * Checks whether the game is won.
     * @return true if the game is won.
     */
    public boolean hasWon() {
        return map.hasWon();
    }

    /**
     * Checks whether water has reached a wrong cell (i.e., the game is lost).
     */
    public boolean hasLost() {
        return map.hasLost();
    }
}