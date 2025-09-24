
package pa1.model;

import org.jetbrains.annotations.NotNull;
import pa1.utils.Position;
import pa1.utils.Direction;

import java.util.Objects;

/**
 * Represents the state of the game at any point in time.
 * <ul>
 *     <li>Tracks the current score of the player.</li>
 *     <li>Manages undo/redo of moves via an internal MoveStack.</li>
 *     <li>Handles the effect of valid moves (Alive or Dead).</li>
 * </ul>
 */
public final class GameState {

    private static final int DEATH_PENALTY = 2;

    private int score = 0;
    private Position currentPosition = Position.START;
    @NotNull
    private final MoveStack moves = new MoveStack();

    /**
     * Executes a move in the given {@code direction}.
     *
     * <p>
     *   If the move is valid and keeps you alive, you gain 1 point.
     *   If the move causes death, you lose 2 points.
     * </p>
     *
     * @param direction the direction to move.
     * @return the {@link MoveResult} describing the outcome.
     */
    @NotNull
    public MoveResult move(@NotNull Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        MoveResult result = direction.apply(currentPosition);
        if (result instanceof MoveResult.Valid.Alive) {
            score += 1;
            currentPosition = ((MoveResult.Valid.Alive) result).newPosition();
        } else if (result instanceof MoveResult.Valid.Dead) {
            score -= DEATH_PENALTY;
            // position remains unchanged on death
        }
        moves.push(result);
        return result;
    }

    /**
     * Undoes the most recent move.
     *
     * <p>
     *   Reverts both position and score changes caused by that move.
     * </p>
     *
     * @throws IllegalStateException if there are no moves to undo.
     */
    public void undo() {
        MoveResult last = moves.pop();
        if (last instanceof MoveResult.Valid.Alive alive) {
            // revert the 1‑point gain and restore previous position
            score -= 1;
            currentPosition = alive.oldPosition();
        } else if (last instanceof MoveResult.Valid.Dead) {
            // revert the 2‑point death penalty
            score += DEATH_PENALTY;
            // position was not changed on death, so nothing to restore there
        }
    }

    /** @return the current score. */
    public int getScore() {
        return score;
    }

    /** @return the position you’re currently standing on. */
    @NotNull
    public Position getCurrentPosition() {
        return currentPosition;
    }
}