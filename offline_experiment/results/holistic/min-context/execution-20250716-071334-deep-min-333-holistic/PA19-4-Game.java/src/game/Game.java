
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

    public Game(int rows, int cols) {
        this.map = new Map(rows, cols);
        this.pipeQueue = new PipeQueue();
        this.delayBar = new DelayBar(0);
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new PipeQueue(pipes);
        this.delayBar = new DelayBar(delay);
    }

    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row, col);
        Pipe currentPipe = pipeQueue.peekCurrent();
        
        if (map.placeCell(coord, new FillableCell(coord, currentPipe))) {
            cellStack.push(new FillableCell(coord, currentPipe));
            pipeQueue.removeCurrent();
            delayBar.increment();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.removeCurrent();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        
        FillableCell cell = cellStack.pop();
        map.clearCell(cell.coord);
        numOfSteps++;
        return true;
    }

    public void updateState() {
        if (delayBar.isReady()) {
            map.fillCells(delayBar.getCurrentValue());
            delayBar.reset();
        }
    }

    public boolean hasWon() {
        return map.isComplete();
    }

    public boolean hasLost() {
        return map.hasLeak() || pipeQueue.isEmpty();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void display() {
        map.display();
        System.out.println();
        pipeQueue.display();
        cellStack.display();
        System.out.println();
        delayBar.display();
    }
}