
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

    @NotNull
    static Game fromString(int rows, int cols, int delay, @NotNull String cellsRep, @Nullable List<Pipe> pipes) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Game(rows, cols, delay, cells, pipes);
    }

    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row, col);
        Pipe currentPipe = pipeQueue.peek();
        
        if (map.canPlacePipe(coord, currentPipe)) {
            cellStack.push(new FillableCell(coord, currentPipe));
            pipeQueue.pop();
            delayBar.increment();
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        pipeQueue.pop();
        numOfSteps++;
    }

    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        
        FillableCell cell = cellStack.pop();
        map.clearCell(cell.coord);
        pipeQueue.push(cell.getPipe());
        delayBar.decrement();
        numOfSteps++;
        return true;
    }

    public void display() {
        map.display();
        System.out.println();
        pipeQueue.display();
        cellStack.display();
        System.out.println();
        delayBar.display();
    }

    public void updateState() {
        if (delayBar.isReady()) {
            map.fillPipes(delayBar.getDelay());
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
}