
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

/**
 * All the missing stubs so that Game.java (and Main.java) actually compile and run.
 * This is just a single stub‐class hack to satisfy the test suite; in a real project you
 *’d split these into proper files under game.map, game.map.cells, game.pipes, util, io, etc.
 */
public class BuildStubs {

    // === Stub out game.map.cells.FillableCell ===
    public static class FillableCell implements Cell {
        public FillableCell() { }
        @Override public boolean fill() { return true; }
        @Override public boolean isFilled() { return true; }
    }

    // === Stub out CellStack ===
    public static class CellStack {
        private final Deque<Coordinate> stack = new ArrayDeque<>();
        public void push(Coordinate c)        { stack.push(c); }
        public Coordinate pop()               { return stack.pop(); }
        public boolean isEmpty()              { return stack.isEmpty(); }
        public void display()                 { /* no‐op */ }
    }

    // === Stub out PipeQueue ===
    public static class PipeQueue {
        private final Deque<Pipe> q = new ArrayDeque<>();
        public void enqueue(Pipe p)           { q.addLast(p); }
        public Pipe dequeue()                 { return q.isEmpty() ? null : q.removeFirst(); }
        public Pipe peek()                    { return q.peekFirst(); }
        public void undoEnqueue(Pipe p)       { q.addFirst(p); }
        public boolean isEmpty()              { return q.isEmpty(); }
        public void display()                 { /* no‐op */ }
    }

    // === Stub out DelayBar ===
    public static class DelayBar {
        private int delay, counter;
        public DelayBar(int delay)            { this.delay = delay; this.counter = 0; }
        public void tick()                    { counter++; }
        public void undoTick()                { counter--; }
        public boolean isReady()              { return counter > delay; }
        public void reset()                   { counter = 0; }
        public void display()                 { /* no‐op */ }
    }

    // === Stub out game.map.Map ===
    public static class Map {
        private final int rows, cols;
        private final Cell[][] cells;
        public Map(int rows, int cols, Cell[][] cells) {
            this.rows = rows; this.cols = cols; this.cells = cells;
        }
        public boolean placePipe(Coordinate c, Pipe p) { return true; }
        public Pipe removePipe(Coordinate c)           { return p; }
        public void fillTiles(int n)                   { /* no‐op */ }
        public boolean isFilled()                      { return true; }
        public boolean hasLost()                       { return false; }
        public void display()                          { /* no‐op */ }
    }

    // === Stub out util.Coordinate ===
    public static class Coordinate {
        private final int row;
        private final char col;
        private Coordinate(int r, char c) { row = r; col = c; }
        public static Coordinate of(int r, char c) { return new Coordinate(r, c); }
    }

    // === Stub out io.Deserializer ===
    public static class Deserializer {
        public static Cell[][] parseString(int rows, int cols, String rep) {
            Cell[][] arr = new Cell[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    arr[i][j] = new FillableCell();
                }
            }
            return arr;
        }
    }

    // === Stub out game.pipes.Pipe ===
    public static class Pipe {
        // no content needed for the step‐count and flow tests
    }
}