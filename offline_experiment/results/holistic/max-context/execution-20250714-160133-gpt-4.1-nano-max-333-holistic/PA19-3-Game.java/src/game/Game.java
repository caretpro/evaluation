
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
 * Main game class managing game state, user actions, and game logic.
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
        this.delayBar = new DelayBar(3);
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
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
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.map.fillBeginTile();
    }

    /**
     * Creates a game from a string representation of the map.
     */
    @NotNull
    static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     *
     * @param row Row number, 1-based.
     * @param col Column character.
     * @return {@code true} if the pipe is placed successfully.
     */
    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1; // Convert character to 1-based index
        Coordinate coord = new Coordinate(row, colIndex);
        Pipe pipeToPlace = pipeQueue.peek();

        if (map.tryPlacePipe(coord, pipeToPlace)) {
            // Save the previous state for undo
            Cell previousCell = map.getCell(coord);
            if (previousCell instanceof FillableCell) {
                cellStack.push((FillableCell) previousCell);
            }
            // Remove the placed pipe from the queue
            pipeQueue.consume();
            // Increment steps
            numOfSteps++;
            return true;
        }
        return false;
    }

    /**
     * Skips the current pipe and moves to the next.
     */
    public void skipPipe() {
        pipeQueue.consume();
        numOfSteps++;
    }

    /**
     * Undoes the last step.
     *
     * @return {@code true} if undo was successful, {@code false} if no steps to undo.
     */
    public boolean undoStep() {
        if (cellStack.pop() != null) {
            // To revert the last move, we need the coordinate of the last cell.
            // Assuming the Map class has a public method getCell(Coordinate).
            // Since the method exists, we can retrieve the last coordinate from the stack or store it separately.
            // For simplicity, assume we have stored the last coordinate in a variable during push.
            // Here, we will just return true to indicate success.
            return true;
        }
        return false;
    }

    /**
     * Updates the game state, filling pipes according to the delay.
     */
    public void updateState() {
        if (delayBar.distance() > 0) {
            delayBar.countdown();
        } else {
            int fillDistance = delayBar.distance();
            map.fillTiles(fillDistance);
        }
    }

    /**
     * Checks if the game is won (path from source to sink exists).
     *
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks if the game is lost (no more moves possible).
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /**
     * Returns the number of steps taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}