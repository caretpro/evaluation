
package game;

import game.map.Map;
import game.map.cells.FillableCell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

import java.util.ArrayList;
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
     * Creates a game with a map of rows x cols, using the default delay.
     *
     * @param rows Number of rows to generate, not counting the surrounding walls.
     * @param cols Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this(rows, cols, DelayBar.DEFAULT_DELAY, null, null);
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
        this.pipeQueue = (pipes != null)
                ? new PipeQueue(new ArrayList<>(pipes))
                : new PipeQueue();
        this.delayBar = new DelayBar(delay);
    }

    /**
     * Creates a game with a given map and various properties.
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
    public static Game fromString(int rows, int cols, int delay,
                                  @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     * <p>
     * This method converts the row and column into {@link Coordinate}
     * and tries to place the pipe on the map. If this succeeds, also update
     * the pipe queue, delay bar, cell stack, and the number of steps.
     * </p>
     *
     * @param row Row number, 1-based.
     * @param col Column character.
     * @return {@code true} if the pipe is placed.
     */
    public boolean placePipe(int row, char col) {
        Pipe next = pipeQueue.peek();
        Coordinate coord = new Coordinate(row, col);
        if (!map.placePipe(coord, next)) {
            return false;
        }
        pipeQueue.pop();
        FillableCell placed = map.getCell(coord);
        cellStack.push(placed);
        delayBar.tick();
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and uses the next pipe.
     */
    public void skipPipe() {
        pipeQueue.skip();
        delayBar.reset();
        numOfSteps++;
    }

    /**
     * Undoes a step from the game.
     * <p>
     * Note: Undoing a step counts; it will increment the number of steps by one.
     * Hint: Remember to check whether there are cells to undo.
     * </p>
     *
     * @return {@code false} if there are no steps to undo, otherwise {@code true}.
     */
    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        FillableCell cell = cellStack.pop();
        Coordinate coord = cell.getCoordinate();
        Pipe pipe = cell.getPipe();
        map.removePipe(coord);
        pipeQueue.unshift(pipe);
        delayBar.rollback();
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
     * Updates the game state. More specifically, it fills pipes according to the distance the water should flow.
     * <p>
     * This method invokes {@link Map#fillTiles(int)} up to a certain distance.
     * </p>
     */
    public void updateState() {
        int flow = delayBar.getDelay();
        map.fillTiles(flow);
    }

    /**
     * Checks whether the game is won.
     *
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.hasFilledAll();
    }

    /**
     * Checks whether the game is lost.
     * <p>
     * This method invokes {@link Map#hasLost()} and also checks delay overflow.
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost() || delayBar.isOverflowed();
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}