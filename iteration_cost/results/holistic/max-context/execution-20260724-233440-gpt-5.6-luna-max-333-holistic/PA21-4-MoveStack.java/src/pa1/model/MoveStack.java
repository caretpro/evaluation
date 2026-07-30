
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
 * <p>You can read more about stacks
 * <a href="https://en.wikipedia.org/wiki/Stack_(abstract_data_type)">here</a>.</p>
 */
public class MoveStack {

    @NotNull
    private final List<MoveResult> moves = new ArrayList<>();

    private int popCount;

    /**
     * Pushes an alive, valid move to this stack.
     *
     * @param move the move to push into this stack
     */
    public void push(final MoveResult move) {
        final MoveResult nonNullMove = Objects.requireNonNull(move);

        if (nonNullMove instanceof MoveResult.Valid.Alive) {
            moves.add(nonNullMove);
        }
    }

    /**
     * @return whether the stack is currently empty
     */
    public boolean isEmpty() {
        return moves.isEmpty();
    }

    /**
     * Pops the most recently recorded move.
     *
     * @return the most recently recorded move, or {@code null} if the stack is empty
     */
    public MoveResult pop() {
        popCount++;

        if (moves.isEmpty()) {
            return null;
        }

        return moves.remove(moves.size() - 1);
    }

    /**
     * @return the number of {@link #pop()} calls invoked
     */
    public int getPopCount() {
        return popCount;
    }

    /**
     * Peeks at the most recently recorded move.
     *
     * @return the most recently recorded move, or {@code null} if the stack is empty
     */
    public MoveResult peek() {
        if (moves.isEmpty()) {
            return null;
        }

        return moves.get(moves.size() - 1);
    }
}