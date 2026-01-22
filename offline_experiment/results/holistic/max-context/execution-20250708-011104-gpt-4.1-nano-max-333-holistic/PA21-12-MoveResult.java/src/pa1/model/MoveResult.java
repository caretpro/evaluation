
package pa1.model;

import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The result after moving a player.
 */
public abstract class MoveResult {

    /**
     * The {@link Position} of the player after moving.
     */
    @NotNull
    public final Position newPosition;

    /**
     * Creates an instance of {@link MoveResult}.
     *
     * @param newPosition The new {@link Position} of the player after making the move.
     */
    protected MoveResult(@NotNull final Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }

    /**
     * Represents a valid move.
     */
    public static class Valid extends MoveResult {

        /**
         * The original position of the player before the move.
         */
        @NotNull
        public final Position origPosition;

        /**
         * Creates an instance of {@link Valid}.
         *
         * @param newPosition  The new position after move.
         * @param origPosition The original position before move.
         */
        public Valid(@NotNull final Position newPosition, @NotNull final Position origPosition) {
            super(newPosition);
            this.origPosition = Objects.requireNonNull(origPosition);
        }

        /**
         * Represents a move where the player is still alive after the move.
         */
        public static final class Alive extends Valid {

            /**
             * List of positions representing collected gems.
             */
            @NotNull
            public final List<Position> collectedGems;

            /**
             * List of positions representing collected extra lives.
             */
            @NotNull
            public final List<Position> collectedExtraLives;

            /**
             * Constructor initializing with empty collections.
             *
             * @param newPosition  The new position.
             * @param origPosition The original position.
             */
            public Alive(@NotNull final Position newPosition, @NotNull final Position origPosition) {
                this(newPosition, origPosition, Collections.emptyList(), Collections.emptyList());
            }

            /**
             * Constructor with specified collected entities.
             *
             * @param newPosition          The new position.
             * @param origPosition         The original position.
             * @param collectedGems        List of collected gems.
             * @param collectedExtraLives List of collected extra lives.
             */
            public Alive(@NotNull final Position newPosition, @NotNull final Position origPosition,
                         @NotNull final List<Position> collectedGems,
                         @NotNull final List<Position> collectedExtraLives) {
                super(newPosition, origPosition);
                this.collectedGems = Collections.unmodifiableList(Objects.requireNonNull(collectedGems));
                this.collectedExtraLives = Collections.unmodifiableList(Objects.requireNonNull(collectedExtraLives));
            }
        }

        /**
         * Represents a move where the player hits a mine and dies.
         */
        public static final class Dead extends Valid {

            /**
             * Position of the mine encountered.
             */
            @NotNull
            public final Position minePosition;

            /**
             * Constructor.
             *
             * @param newPosition  The position after move (same as before).
             * @param minePosition The position of the mine encountered.
             */
            public Dead(@NotNull final Position newPosition, @NotNull final Position minePosition) {
                super(newPosition, newPosition);
                this.minePosition = Objects.requireNonNull(minePosition);
            }
        }
    }

    /**
     * Represents an invalid move.
     */
    public static class Invalid extends MoveResult {

        /**
         * Constructor.
         *
         * @param newPosition The position after move (should be same as before).
         */
        public Invalid(@NotNull final Position newPosition) {
            super(newPosition);
        }
    }
}