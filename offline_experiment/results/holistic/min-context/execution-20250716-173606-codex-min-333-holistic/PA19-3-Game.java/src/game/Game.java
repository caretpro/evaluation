
package game;

import game.map.Map;
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
     * Creates a game with a map of rows x cols.
     *
     * @param rows Number of rows to generate, not counting the surrounding walls.
     * @param cols Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this(rows, cols, /*delay=*/0, /*cells=*/null, /*pipes=*/null);
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
    public Game(int rows, int cols, int delay, FillableCell[][] cells, List<Pipe> pipes) {
        if (cells == null) {
            this.map = new Map(rows, cols);
        } else {
            this.map = new Map(rows, cols, cells);
        }
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    /**
     * Creates a game with a given map and various properties.
     *
     * <p>
     * This constructor is a convenience method for unit testing purposes.
     * </p>
     *
     * @param rows     Number of rows of the given map.
     * @param cols     Number of columns of the given map.
     * @param delay    Delay in number of rounds before filling the pipes.
     * @param cellsRep String representation of the map, with columns delimited by {@code '\n'}.
     * @param pipes    List of pre-generated pipes, if any.
     * @return A game constructed with the given parameters.
     */
    @NotNull
    static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        FillableCell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     *
     * <p>
     * This method converts (row,col) to a Coordinate, peeks the current pipe,
     * and tries to place it on the map. If successful, advances the pipe queue,
     * ticks down the delay bar, records the placement in the cell stack, and
     * increments the step count.
     * </p>
     *
     * @param row Row number, 1-based.
     * @param col Column character.
     * @return {@code true} if the pipe is placed.
     */
    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row, col);
        FillableCell cell = map.getFillableCell(coord);
        Pipe current = pipeQueue.peek();
        if (!map.placePipe(cell, current)) {
            return false;
        }
        pipeQueue.pop();
        delayBar.tickDown();
        cellStack.push(cell);
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and uses the next pipe.
     */
    public void skipPipe() {
        pipeQueue.pop();
        numOfSteps++;
    }

    /**
     * Undoes a step from the game.
     *
     * <p>
     * Note: Undoing a step counts will increment the number of steps by one.
     * </p>
     * <p>
     * Hint: Remember to check whether there are cells to undo.
     * </p>
     *
     * @return {@code false} if there are no steps to undo, otherwise {@code true}.
     */
    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        numOfSteps++;
        FillableCell last = cellStack.pop();
        map.removePipe(last);
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
     * Updates the game state. More specifically, it fills pipes according
     * to the delay bar’s current value.
     *
     * <p>
     * This method invokes {@link Map#fillTiles(int)} with the delay bar’s value.
     * </p>
     */
    public void updateState() {
        int distance = delayBar.getCurrent();
        map.fillTiles(distance);
    }

    /**
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.hasWon();
    }

    /**
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