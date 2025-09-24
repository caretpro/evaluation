
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
     * Pushes a move to this stack.
     *
     * <p>
     * In our testing, we will only push instances of {@link MoveResult.Valid.Alive}; How you handle other
     * sealed-subclasses of {@link MoveResult} is entirely up to you.
     * </p>
     *
     * @param move The move to push into this stack.
     */
    public void push(final MoveResult move) {
        moves.add(Objects.requireNonNull(move));
    }

    /**
     * @return Whether the stack is currently empty.
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    /**
     * Pops a move from this stack.
     *
     * @return The instance of {@link MoveResult} last performed by the player.
     */
    public MoveResult pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        popCount++;
        return moves.remove(moves.size() - 1);
    }

    /**
     * @return The number of {@link MoveStack#pop} calls invoked.
     */
    public int getPopCount() {
        return popCount;
    }

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
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return moves.get(moves.size() - 1);
    }
}