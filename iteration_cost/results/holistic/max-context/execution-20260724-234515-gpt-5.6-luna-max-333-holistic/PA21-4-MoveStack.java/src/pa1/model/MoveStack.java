
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;

/**
 * A {@link java.util.Stack}-like data structure to track all valid moves made by a player.
 *
 * <p>A stack is a data structure which enforces Last-In First-Out (LIFO) ordering of its elements.</p>
 *
 * <p>You can read more about stacks
 * <a href="https://en.wikipedia.org/wiki/Stack_(abstract_data_type)">here</a>.</p>
 */
public class MoveStack {

    @NotNull
    private final List<MoveResult> moves = new ArrayList<>();

    private int popCount = 0;

    /**
     * Pushes a move to this stack.
     *
     * @param move The move to push into this stack.
     */
    public void push(final MoveResult move) {
        moves.add(Objects.requireNonNull(move, "move must not be null"));
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
     * @throws EmptyStackException if this stack is empty.
     */
    public MoveResult pop() {
        popCount++;

        if (moves.isEmpty()) {
            throw new EmptyStackException();
        }

        return moves.remove(moves.size() - 1);
    }

    /**
     * @return The number of {@link MoveStack#pop} calls invoked.
     */
    public int getPopCount() {
        return popCount;
    }

    /**
     * Peeks the topmost element of the stack.
     *
     * @return The instance of {@link MoveResult} at the top of the stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public MoveResult peek() {
        if (moves.isEmpty()) {
            throw new EmptyStackException();
        }

        return moves.get(moves.size() - 1);
    }
}