package game;

import game.map.cells.FillableCell;

import java.io.*;
import java.util.*;

/**
 * Class encapsulating an undo stack.
 */
public class CellStack {

    private final Stack<FillableCell> cellStack = new Stack<>();
    private int count = 0;

    /**
     * Pushes a cell into the stack.
     *
     * @param cell Cell to push into the stack.
     */
    void push(final FillableCell cell) {
        // TODO
    }

    /**
     * Pops a cell from the stack.
     *
     * @return The last-pushed cell, or {@code null} if the stack is empty.
     */
    FillableCell pop() {
        // TODO
    }

    /**
     * @return Number of undos (i.e. {@link CellStack#pop()}) invoked.
     */
    int getUndoCount() {
        // TODO
        return 0;
    }

    /**
     * Displays the current undo count to {@link System#out}.
     */
    void display() {
        System.out.println("Undo Count: " + count);
    }
}
