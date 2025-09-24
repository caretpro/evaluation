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

	/**
	 * Peeks the topmost of the element of the stack.
	 *
	 * <p>
	 * This method is intended for internal use only, although we will not stop you if you want to use this method in
	 * your implementation.
	 * </p>
	 *
	 * @return The instance of {@link MoveResult} at the top of the stack, corresponding to the last move performed by
	 * the player.
	 */
	public MoveResult peek() {
		// TODO
		return null;
	}

	@Override
	public void push(final MoveResult move) {
		Objects.requireNonNull(move, "move must not be null");
		moves.add(move);
		popCount = 0;
	}

	/**
	 * @return  Whether the stack is currently empty.
	 */
	public boolean isEmpty() {
		return moves.size() - popCount == 0;
	}

	/**
	 * Pops a move from this stack.
	 * @return  The instance of  {@link MoveResult}  last performed by the player.
	 * @throws IllegalStateException  if the stack is empty
	 */
	public MoveResult pop() {
		if (isEmpty()) {
			throw new IllegalStateException("Cannot pop from empty MoveStack");
		}
		popCount++;
		return moves.remove(moves.size() - 1);
	}

	/**
	 * @return  The number of  {@link MoveStack#pop}  calls invoked.
	 */
	public int getPopCount() {
		return popCount;
	}
}
