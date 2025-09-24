
package game;

import game.map.Map;
import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Game {

    @NotNull
    private final Map map;
    @NotNull
    private final Deque<Pipe> pipeQueue;
    @NotNull
    private int currentDelay;
    private final int maxDelay;
    @NotNull
    private final Deque<FillableCell> cellStack = new ArrayDeque<>();

    private int numOfSteps = 0;

    public Game(int rows, int cols) {
        this.map = new Map(rows, cols);
        this.pipeQueue = new ArrayDeque<>();
        this.currentDelay = 0;
        this.maxDelay = 3;
    }

    public Game(int rows, int cols, int delay, Cell[][] cells, List<Pipe> pipes) {
        this.map = new Map(rows, cols, cells);
        this.pipeQueue = new ArrayDeque<>(pipes);
        this.currentDelay = 0;
        this.maxDelay = delay;
    }

    public boolean placePipe(int row, char col) {
        Coordinate coord = new Coordinate(row, col);
        if (pipeQueue.isEmpty()) return false;
        
        Pipe currentPipe = pipeQueue.peek();
        FillableCell newCell = new FillableCell(coord, currentPipe);
        
        if (map.tryPlaceCell(coord, newCell)) {
            cellStack.push(newCell);
            pipeQueue.pop();
            currentDelay++;
            numOfSteps++;
            return true;
        }
        return false;
    }

    public void skipPipe() {
        if (!pipeQueue.isEmpty()) {
            pipeQueue.pop();
            numOfSteps++;
        }
    }

    public boolean undoStep() {
        if (cellStack.isEmpty()) {
            return false;
        }
        
        FillableCell cell = cellStack.pop();
        map.tryPlaceCell(cell.coord, null);
        pipeQueue.addFirst(cell.pipe);
        currentDelay--;
        numOfSteps++;
        return true;
    }

    public void display() {
        map.display();
        System.out.println("\nPipe Queue: " + pipeQueue);
        System.out.println("Cell Stack: " + cellStack);
        System.out.println("Delay: " + currentDelay + "/" + maxDelay);
    }

    public void updateState() {
        if (currentDelay >= maxDelay) {
            map.fillTiles(currentDelay);
            currentDelay = 0;
        }
    }

    public boolean hasWon() {
        return map.checkCompletion();
    }

    public boolean hasLost() {
        return map.checkLeak() || pipeQueue.isEmpty();
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }
}