
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
 * Main class managing the game state.
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
        // Initialize delay bar with default delay, e.g., 10 rounds
        this.delayBar = new DelayBar(10);
        // Create a new map with specified size (+2 for walls)
        this.map = new Map(rows + 2, cols + 2);
        // Initialize pipe queue with no pre-existing pipes
        this.pipeQueue = new PipeQueue();
        // Fill the begin tile (source) to start water flow
        this.map.fillBeginTile();
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
        this.delayBar = new DelayBar(delay);
        this.map = new Map(rows + 2, cols + 2, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.map.fillBeginTile();
    }

    /**
     * Places a pipe at (row, col).
     *
     * @param row Row number, 1-based.
     * @param col Column character.
     * @return {@code true} if the pipe is placed.
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1; // Convert char to 1-based index
        Coordinate coord = new Coordinate(row, colIndex);
        Pipe pipe = pipeQueue.peek();

        boolean success = map.tryPlacePipe(coord, pipe);
        if (success) {
            // Access the cell via the new getter method
            Cell cell = map.getCellAt(coord);
            if (cell instanceof FillableCell) {
                cellStack.push((FillableCell) cell);
            }
            pipeQueue.consume();
            numOfSteps++;
        }
        return success;
    }

    /**
     * Directly skips the current pipe and use the next pipe.
     */
    public void skipPipe() {
        pipeQueue.consume();
    }

    /**
     * Undos a step from the game.
     *
     * @return {@code false} if there are no steps to undo, otherwise {@code true}.
     */
    public boolean undoStep() {
        if (cellStack.getUndoCount() == 0) {
            return false;
        }
        FillableCell lastCell = cellStack.pop();
        if (lastCell != null) {
            // Reset the cell to empty
            map.undo(lastCell.coord);
            // Reinsert the pipe into the queue
            Pipe pipe = lastCell.getPipe().orElse(null);
            if (pipe != null) {
                pipeQueue.undo(pipe);
            }
            numOfSteps++;
            return true;
        }
        return false;
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
     * Updates the game state. Fills pipes according to the delay.
     */
    public void updateState() {
        delayBar.countdown();
        int distance = delayBar.distance();
        map.fillTiles(distance);
    }

    /**
     * Checks whether the game is won.
     *
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks whether the game is lost.
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}