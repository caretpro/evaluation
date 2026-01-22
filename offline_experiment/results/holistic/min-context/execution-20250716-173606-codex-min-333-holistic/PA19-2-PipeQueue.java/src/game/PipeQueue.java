
package game;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Core Game logic: parses a level, applies moves (place/skip/undo),
 * counts steps, and exposes serialization via fromString().
 */
public class Game {

    private final int width;
    private final int height;
    private final char[][] board;
    private final PipeQueue pipeQueue;
    private int steps;
    private final Deque<PlacedPipe> history = new ArrayDeque<>();

    /**
     * Parse game state from its string representation.
     * Format:
     *   First line: width,height
     *   Next height lines: board rows ('.' empty, '#' wall)
     *   One line: pipe‐queue chars (no commas, each char is one pipe)
     *   One line: step count
     */
    @NotNull
    public static Game fromString(@NotNull String input) {
        Objects.requireNonNull(input, "input must not be null");
        String[] lines = input.split("\\R");
        if (lines.length < 3) {
            throw new IllegalArgumentException("Invalid input");
        }

        // parse dimensions
        String[] dims = lines[0].split(",");
        int w = Integer.parseInt(dims[0]);
        int h = Integer.parseInt(dims[1]);

        // parse board
        char[][] bd = new char[h][w];
        for (int r = 0; r < h; r++) {
            String row = lines[1 + r];
            if (row.length() != w) {
                throw new IllegalArgumentException("Row length mismatch");
            }
            bd[r] = row.toCharArray();
        }

        // parse pipe‐queue: each character is one pipe
        String pipeCharsLine = lines[1 + h];
        String[] pipeCharTokens = pipeCharsLine.split("");
        List<Pipe> pipes = Pipe.fromChars(pipeCharTokens);

        // parse steps
        int st = Integer.parseInt(lines[2 + h]);

        // assemble
        return new Game(bd, new PipeQueue(pipes), st);
    }

    private Game(char[][] board, PipeQueue pipeQueue, int steps) {
        this.width = board[0].length;
        this.height = board.length;
        this.board = board;
        this.pipeQueue = pipeQueue;
        this.steps = steps;
    }

    /** Attempts to place the next pipe at (r,c). Returns true if successful. */
    public boolean placePipe(int r, int c) {
        Pipe next = pipeQueue.peek();
        if (!Pipe.canPlace(board, next, r, c)) {
            return false;
        }
        pipeQueue.consume();
        Pipe.place(board, next, r, c);
        history.push(new PlacedPipe(r, c, next));
        steps++;
        return true;
    }

    /** Skip the next pipe. Always succeeds. */
    public void skipPipe() {
        pipeQueue.consume();
        steps++;
    }

    /** Undo the last placement (not skip). */
    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        PlacedPipe pp = history.pop();
        Pipe.remove(board, pp.pipe(), pp.row(), pp.col());
        pipeQueue.undo(pp.pipe());
        steps++;
    }

    /** Current step count. */
    public int getSteps() {
        return steps;
    }

    /** Read‐only view of board for tests. */
    public char[][] getBoard() {
        char[][] copy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, width);
        }
        return copy;
    }

    /** Returns the current pipe‐queue as chars (no commas). */
    public String queueAsString() {
        StringBuilder sb = new StringBuilder();
        for (Pipe p : pipeQueue.getQueue()) {
            sb.append(p.toSingleChar());
        }
        return sb.toString();
    }

    /** Private record of placed pipe to support undo. */
    private static final class PlacedPipe {
        private final int row, col;
        private final Pipe pipe;
        PlacedPipe(int r, int c, Pipe p) {
            this.row = r; this.col = c; this.pipe = p;
        }
        int row() { return row; }
        int col() { return col; }
        Pipe pipe() { return pipe; }
    }
}