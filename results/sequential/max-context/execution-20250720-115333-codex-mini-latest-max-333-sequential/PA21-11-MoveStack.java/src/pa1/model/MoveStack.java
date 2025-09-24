
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Tracks the current game state, including score and history of moves.
 */
public class GameState {

    private static final int MOVE_SCORE       = 1;
    private static final int GEM_SCORE        = 10;
    private static final int EXTRALIFE_SCORE  = 25;
    private static final int DEATH_SCORE      = 2;

    @NotNull
    private final MoveStack moves;

    private int score;
    private int lives;

    /**
     * Initializes a GameState with the given number of lives.
     *
     * @param initialLives the starting lives of the player
     */
    public GameState(int initialLives) {
        if (initialLives < 0) {
            throw new IllegalArgumentException("Lives must be non-negative");
        }
        this.lives = initialLives;
        this.moves = new MoveStack();
        this.score = 0;
    }

    /**
     * Executes a move, updates score and lives accordingly, and records it.
     *
     * @param result the MoveResult of attempting the move
     */
    public void applyMove(@NotNull MoveResult result) {
        Objects.requireNonNull(result, "result must not be null");
        // Always record the move
        moves.push(result);

        if (result instanceof MoveResult.Valid valid) {
            // A valid move always gives MOVE_SCORE
            score += MOVE_SCORE;

            if (valid instanceof MoveResult.Valid.Alive alive) {
                // Collect gems and extra lives
                score += alive.collectedGems.size() * GEM_SCORE;
                score += alive.collectedExtraLives.size() * EXTRALIFE_SCORE;
                lives += alive.collectedExtraLives.size();
            } else if (valid instanceof MoveResult.Valid.Dead dead) {
                // Valid but dead: subtract death penalty and lose a life
                score -= DEATH_SCORE;
                lives--;
            }
        }
        // Invalid moves change nothing except being recorded
    }

    /**
     * Undoes the last move, reverting score and lives as if it never happened.
     *
     * @throws IllegalStateException if there is no move to undo
     */
    public void undoMove() {
        MoveResult last = moves.pop();

        if (last instanceof MoveResult.Valid valid) {
            // Revert the base move score
            score -= MOVE_SCORE;

            if (valid instanceof MoveResult.Valid.Alive alive) {
                // Revert gems and extra lives
                score -= alive.collectedGems.size() * GEM_SCORE;
                score -= alive.collectedExtraLives.size() * EXTRALIFE_SCORE;
                lives -= alive.collectedExtraLives.size();
            } else if (valid instanceof MoveResult.Valid.Dead dead) {
                // Revert death: subtract death penalty again and restore life
                score -= DEATH_SCORE;  // mirror the penalty on undo
                lives++;
            }
        }
        // Invalid moves had no effect beyond recording
    }

    /** @return the current score */
    public int getScore() {
        return score;
    }

    /** @return the current lives */
    public int getLives() {
        return lives;
    }

    /** @return how many moves have been undone so far */
    public int getUndoCount() {
        return moves.getPopCount();
    }
}