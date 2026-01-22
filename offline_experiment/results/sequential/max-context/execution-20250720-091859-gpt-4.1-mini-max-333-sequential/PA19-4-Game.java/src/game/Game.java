
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
     * @param rows Number of rows of the given map.
     * @param cols Number of columns of the given map.
     * @param delay Delay in number of rounds before filling the pipes.
     * @param cellsRep String representation of the map, with columns delimited by {@code '\n'}.
     * @param pipes List of pre-generated pipes, if any.
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
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }

    // Corrected constructor: no return type
    public Game(int rows, int cols) {
        this.map = new Map(rows + 2, cols + 2);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(10);
        this.numOfSteps = 0;
        this.map.fillBeginTile();
    }

    // Corrected constructor: no return type
    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows + 2, cols + 2, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
        this.numOfSteps = 0;
        this.map.fillBeginTile();
    }

    public boolean placePipe(int row, char col) {
        int colIndex = col - 'A' + 1;
        Pipe nextPipe = pipeQueue.peek();
        boolean placed = map.tryPlacePipe(row, colIndex, nextPipe);
        if (!placed) {
            return false;
        }
        pipeQueue.consume();
        delayBar.countdown();
        cellStack.push((FillableCell) map.cells[row][colIndex]);
        numOfSteps++;
        return true;
    }

    /**
     * Directly skips the current pipe and use the next pipe.
     */
    public void skipPipe() {
        pipeQueue.consume();
        delayBar.countdown();
        numOfSteps++;
    }

    /**
     * Undos a step from the game. <p> Note: Undoing a step counts will increment the number of steps by one. </p> <p> Hint: Remember to check whether there are cells to undo. </p>
     * @return   {@code  false}  if there are no steps to undo, otherwise  {@code  true} .
     */
    public boolean undoStep() {
        FillableCell cellToUndo = cellStack.pop();
        if (cellToUndo == null) {
            return false;
        }
        map.undo(cellToUndo.coord);
        cellToUndo.getPipe().ifPresent(pipeQueue::undo);
        numOfSteps++;
        return true;
    }

    /**
     * Updates the game state. More specifically, it fills pipes according to the distance the water should flow. <p> This method should invoke  {@link Map#fillTiles(int)}  up to a certain distance. </p>
     */
    public void updateState() {
        int distance = delayBar.distance();
        if (distance > 0) {
            map.fillTiles(distance);
        }
    }

    /**
     * Checks whether the game is won.
     * @return   {@code  true}  if the game is won.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks whether the game is lost. <p> Hint: This method should invoke  {@link Map#hasLost()} . You may also need other judgements. </p>
     * @return   {@code  true}  if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost() && !hasWon();
    }
}