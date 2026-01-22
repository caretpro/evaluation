package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link java.util.Stack}-like data structure to track all valid moves made by a player.
 *
 * <p>A stack is a data structure which enforces Last-In First-Out (LIFO) ordering of its elements.</p>
 *
 * <p>You can read more about stacks <a href="https://en.wikipedia.org/wiki/Stack_(abstract_data_type)">here</a>.</p>
 */
public class MoveStack {

	@NotNull
	private final List<MoveResult> moves = new ArrayList<>();

	private int popCount = 0;

	public void push(final MoveResult move) {
		moves.add(move);
	}

	public boolean isEmpty() {
		return moves.isEmpty();
	}

	public MoveResult pop() {
		if (moves.isEmpty()) {
			return null;
		}
		MoveResult lastMove = moves.remove(moves.size() - 1);
		popCount++;
		return lastMove;
	}

	public int getPopCount() {
		return popCount;
	}

	public MoveResult peek() {
		if (moves.isEmpty()) {
			return null;
		}
		return moves.get(moves.size() - 1);
	}
}
