
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.EmptyStackException;

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
     * Peeks the topmost element of the stack.
     *
     * <p>
     * This method is intended for internal use only, although we will not stop you if you want to use this method in
     * your implementation.
     * </p>
     *
     * @return The instance of {@link MoveResult} at the top of the stack, corresponding to the last move performed by
     * the player.
     * @throws EmptyStackException if the stack is empty.
     */
    public MoveResult peek() {
        if (moves.isEmpty()) {
            throw new EmptyStackException();
        }
        return moves.get(moves.size() - 1);
    }

    /**
     * Pushes a move onto the stack.
     *
     * @param move the {@link MoveResult} to push; must not be null.
     * @throws NullPointerException if move is null.
     */
    public void push(final MoveResult move) {
        Objects.requireNonNull(move, "move must not be null");
        popCount = 0;
        moves.add(move);
    }

    /**
     * @return whether the stack is currently empty.
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    /**
     * Pops a move from this stack.
     *
     * @return The instance of {@link MoveResult} last performed by the player.
     * @throws EmptyStackException if there are no moves to pop.
     */
    public MoveResult pop() {
        if (moves.isEmpty()) {
            throw new EmptyStackException();
        }
        popCount++;
        return moves.remove(moves.size() - 1);
    }

    /**
     * @return The number of {@link #pop()} calls invoked.
     */
    public int getPopCount() {
        return popCount;
    }
}