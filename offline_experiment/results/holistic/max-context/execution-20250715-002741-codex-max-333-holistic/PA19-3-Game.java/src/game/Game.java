
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
    private boolean waterStarted = false;

    /**
     * Creates a game with a map of rows x cols.
     *
     * @param rows Number of rows to generate, not counting the surrounding walls.
     * @param cols Number of columns to generate, not counting the surrounding walls.
     */
    public Game(int rows, int cols) {
        this.map      = new Map(rows + 2, cols + 2);
        this.pipeQueue= new PipeQueue();
        this.delayBar = new DelayBar((rows + cols) / 2);
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
        this.map       = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar  = new DelayBar(delay);
    }

    /**
     * Convenience constructor for unit testing.
     */
    @NotNull
    static Game fromString(int rows, int cols, int delay,
                           @NotNull String cellsRep,
                           @Nullable List<Pipe> pipes) {
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    /**
     * Places a pipe at (row, col).
     */
    public boolean placePipe(int row, char col) {
        // convert 1-based row/col-letter -> 1-based indices into the Map
        int r = row;
        int c = col - 'A' + 1;

        Pipe next = pipeQueue.peek();
        if (!map.tryPlacePipe(new Coordinate(r, c), next)) {
            return false;
        }

        // Push a fresh FillableCell carrying the coord (undo only needs coord)
        cellStack.push(new FillableCell(new Coordinate(r, c), next));

        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
        return true;
    }

    /**
     * Skips the current pipe.
     */
    public void skipPipe() {
        Pipe skipped = pipeQueue.peek();
        pipeQueue.consume();
        pipeQueue.undo(skipped);
        numOfSteps++;
    }

    /**
     * Undoes the last placement.
     */
    public boolean undoStep() {
        FillableCell undone = cellStack.pop();
        if (undone == null) {
            return false;
        }
        map.undo(undone.getCoord());
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
     * Updates the game state by filling pipes according to the delay.
     */
    public void updateState() {
        int dist = delayBar.distance();
        if (dist >= 0) {
            if (!waterStarted && dist == 0) {
                map.fillBeginTile();
                waterStarted = true;
            }
            map.fillTiles(dist);
        }
    }

    /**
     * @return true if a valid path exists from source to sink.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * @return true if no pipes were filled on the last round after the Nth delay-round.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /** @return number of steps the player has taken. */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}