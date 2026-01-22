
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the complete state of the game at any point in time.
 */
public class GameState {

    private static final int POINTS_PER_GEM = 10;
    private static final int POINTS_PER_EXTRA_LIFE = 50;
    private static final int POINTS_DEDUCTED_ON_DEATH = 5;

    @NotNull
    private final GameMap map;

    @NotNull
    private final MoveStack moves;

    @NotNull
    private Position playerPos;

    private int score;

    private int lives;

    /**
     * Creates a new  {@link GameState}  with the given  {@link GameMap}.
     *
     * <p>The player starts at the map's spawn position, with one life and zero score.</p>
     *
     * @param map The map to use for this game state.
     */
    public GameState(@NotNull final GameMap map) {
        this.map = Objects.requireNonNull(map);
        this.moves = new MoveStack();
        this.playerPos = map.getSpawnPosition();
        this.score = 0;
        this.lives = 1;
    }

    /**
     * Moves the player in the given direction, updating all pieces of state.
     *
     * @param dir The direction to move.
     * @return The  {@link MoveResult}  of this move.
     */
    @NotNull
    public MoveResult move(@NotNull final MoveDirection dir) {
        Objects.requireNonNull(dir);

        final Position oldPos = playerPos;
        final MoveResult result = map.movePlayer(playerPos, dir);

        moves.push(result);

        if (result instanceof MoveResult.Valid.Alive alive) {
            playerPos = alive.newPosition;
            this.score += alive.collectedGems.size() * POINTS_PER_GEM
                        + alive.collectedExtraLives.size() * POINTS_PER_EXTRA_LIFE;
            this.lives += alive.collectedExtraLives.size();
        } else if (result instanceof MoveResult.Valid.Dead) {
            // death: deduct points and lose a life
            this.score -= POINTS_DEDUCTED_ON_DEATH;
            this.lives--;
        }

        return result;
    }

    /**
     * Undoes the last move, restoring player position, score, and lives.
     *
     * @return the  {@link MoveResult}  that was undone.
     * @throws IllegalStateException if there is no move to undo.
     */
    @NotNull
    public MoveResult undoLastMove() {
        MoveResult last = moves.pop();

        // restore position unconditionally
        playerPos = last.newPosition instanceof Position ? last.newPosition : playerPos;

        if (last instanceof MoveResult.Valid.Alive alive) {
            // subtract collected gem/extra-life points and lives
            this.score -= alive.collectedGems.size() * POINTS_PER_GEM
                        + alive.collectedExtraLives.size() * POINTS_PER_EXTRA_LIFE;
            this.lives -= alive.collectedExtraLives.size();
        } else if (last instanceof MoveResult.Valid.Dead) {
            // when undoing a death, re-add the death penalty and life
            this.score += POINTS_DEDUCTED_ON_DEATH;
            this.lives++;
        }

        return last;
    }

    // ... other methods remain unchanged ...
}