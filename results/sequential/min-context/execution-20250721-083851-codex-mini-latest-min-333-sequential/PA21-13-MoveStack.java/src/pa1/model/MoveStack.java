
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the current state of the game, including player's position,
 * score, and the stack of moves performed so far.
 */
public class GameState {

    private static final int DEATH_PENALTY = 4;

    @NotNull
    private final MoveStack moveStack = new MoveStack();

    private int score = 0;

    /**
     * Performs the given move, updates the score, and records it.
     *
     * @param move the move to perform
     */
    public void performMove(@NotNull Move move) {
        Objects.requireNonNull(move, "move must not be null");
        MoveResult result = move.execute();
        score += result.getScoreDelta();
        moveStack.push(result);
    }

    /**
     * Undoes the last move, updates the score accordingly.
     *
     * @throws IllegalStateException if there are no moves to undo
     */
    public void undoMove() {
        MoveResult last = moveStack.pop();

        // Reverse the score delta of the move we just popped
        score -= last.getScoreDelta();

        // If the move was a death, apply the death penalty when undoing
        if (last.isDeath()) {
            score -= DEATH_PENALTY;
        }
    }

    /**
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * @return whether there are moves to undo
     */
    public boolean canUndo() {
        return !moveStack.isEmpty();
    }
}