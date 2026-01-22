
package game;

import game.map.cells.FillableCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * Class encapsulating an undo stack.
 */
class CellStack {

	private final Stack<@NotNull FillableCell> cellStack = new Stack<>();
	private int count = 0;

	/**
	 * @return Number of undos (i.e. {@link CellStack#pop()}) invoked.
	 */
	int getUndoCount() {
		return count;
	}

	/**
	 * Displays the current undo count to {@link System#out}.
	 */
	void display() {
		System.out.println("Undo Count: " + count);
	}

	void push(final FillableCell cell) {
		cellStack.push(cell);
	}

	FillableCell pop() {
		if (cellStack.isEmpty()) {
			return null;
		}
		count++;
		return cellStack.pop();
	}
}