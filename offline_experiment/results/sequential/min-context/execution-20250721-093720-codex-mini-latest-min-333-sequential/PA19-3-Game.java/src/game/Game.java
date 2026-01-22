
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
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
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
     *
     * <p>
     * This method should invoke {@link Map#fillTiles(int)} up to a certain distance.
     * </p>
     */
    public void updateState() {
        // TODO
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }

    /**
     * Creates a game with a map of rows x cols.
     *
     * @param rows Number of rows to generate, not counting the surrounding walls.
     * @param cols Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this(rows, cols, 1, Map.makeEmpty(rows, cols), List.of());
    }

    /**
     * Creates a game with a given map and various properties.
     *
     * @param rows   Number of rows of the given map.
     * @param cols   Number of columns of the given map.
     * @param delay  Delay in number of rounds before filling the pipes.
     * @param cells  Cells of the map.
     * @param pipes  List of pre-generated pipes, if any.
     */
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        for (Coordinate src : map.getSourceCoordinates()) {
            map.fillTiles(src);
        }
    }

    /**
     * Places a pipe at (row, col).
     *
     * <p>
     * This method should convert the row and column into {@link Coordinate} and try to place the pipe on the map.
     * If this succeeds, also update the pipe queue, delay bar, cell stack, and the number of steps.
     * </p>
     *
     * @param row Row number, 1-based.
     * @param col Column character.
     * @return {@code true} if the pipe is placed.
     */
    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row - 1, col - 'A');
        Pipe pipe = pipeQueue.peek();
        boolean placed = map.placePipe(coord, pipe);
        if (!placed) {
            return false;
        }
        pipeQueue.pop();
        cellStack.push(coord);
        delayBar.tick();
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and use the next pipe.
     */
    public void skipPipe() {
        pipeQueue.pop();
        numOfSteps++;
    }

    /**
     * Undos a step from the game.
     *
     * <p>
     * Note: Undoing a step counts will increment the number of steps by one.
     * Hint: Remember to check whether there are cells to undo.
     * </p>
     *
     * @return {@code false} if there are no steps to undo, otherwise {@code true}.
     */
    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        Coordinate last = cellStack.pop();
        map.resetCell(last);
        delayBar.tick();
        numOfSteps++;
        return true;
    }

    /**
     * Checks whether the game is won.
     *
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.getCells().stream()
                      .filter(cell -> cell instanceof FillableCell)
                      .allMatch(cell -> ((FillableCell) cell).isFilled());
    }

    /**
     * Checks whether the game is lost.
     *
     * <p>
     * Hint: This method should invoke {@link Map#hasLost()}. You may also need other judgements.
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost() || pipeQueue.isEmpty();
    }
}