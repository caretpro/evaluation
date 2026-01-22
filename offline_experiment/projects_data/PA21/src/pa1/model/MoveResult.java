package pa1.model;

import pa1.model.ExtraLife;
import pa1.model.Gem;
import pa1.model.Mine;
import pa1.model.Position;

import java.util.*;

/**
 * The result after moving a player.
 */
public abstract class MoveResult {

    /**
     * The {@link Position} of the player after moving.
     */
    public final Position newPosition;

    /**
     * Represents a valid move.
     */
    public static class Valid extends MoveResult {

        /**
         * The original position of the player before the move.
         */
        public final Position origPosition;

        /**
         * Creates an instance of {@link MoveResult.Valid}, indicating that the move is valid.
         *
         * @param newPosition  The new {@link Position} of the player after moving.
         * @param origPosition The original {@link Position} of the player before moving.
         */
        private Valid(final Position newPosition, final Position origPosition) {
            // TODO
            super(null);

            this.origPosition = null;
        }

        /**
         * Represents a valid move, and the player is alive after making the move.
         */
        public static final class Alive extends Valid {

            /**
             * List of positions representing the location of {@link Gem} collected in this move.
             */
            public final List<Position> collectedGems;
            /**
             * List of positions representing the location of {@link ExtraLife} collected in this move.
             */
            public final List<Position> collectedExtraLives;

            /**
             * Creates an instance of {@link MoveResult.Valid.Alive}, indicating that the move is valid and the player
             * is still alive after the move.
             *
             * <p>
             * {@link Alive#collectedGems} and {@link Alive#collectedExtraLives} will be initialized to an empty list
             * using this constructor.
             * </p>
             *
             * @param newPosition  The new {@link Position} of the player after the move.
             * @param origPosition The original {@link Position} of the player before the move.
             */
            public Alive(final Position newPosition, final Position origPosition) {
                this(newPosition, origPosition, Collections.emptyList(), Collections.emptyList());
            }

            /**
             * Creates an instance of {@link MoveResult.Valid.Alive}, indicating that the move is valid and the player
             * is still alive after the move.
             *
             * @param newPosition         The new {@link Position} of the player after the move.
             * @param origPosition        The original {@link Position} of the player before the move.
             * @param collectedGems       The list of positions of all gems collected in this move.
             * @param collectedExtraLives The list of positions of all extra lives collected in this move.
             */
            public Alive(final Position newPosition,
                         final Position origPosition,
                         final List<Position> collectedGems,
                         final List<Position> collectedExtraLives
            ) {
                // TODO
                super(null, null);

                this.collectedGems = null;
                this.collectedExtraLives = null;
            }
        }

        /**
         * Represents a valid move, but the player has died after making the move.
         *
         * <p>
         * A valid-but-dead move occurs when a player is able to move one or more tiles, but hits a mine while
         * moving in the direction.
         * </p>
         */
        public static final class Dead extends Valid {

            /**
             * The {@link Position} of the {@link Mine} which the player encounters and dies from.
             */
            public final Position minePosition;

            /**
             * Creates an instance of {@link MoveResult.Valid.Dead}, indicating that the move was valid, but results in
             * the player dying.
             *
             * @param newPosition  The {@link Position} of the player after this move. This should be the same as the
             *                     original position before the move.
             * @param minePosition The {@link Position} of the mine which the player encounters during the move (and
             *                     dies from).
             */
            public Dead(final Position newPosition, final Position minePosition) {
                // TODO
                super(null, null);

                this.minePosition = null;
            }
        }
    }

    /**
     * Represents an invalid move.
     *
     * <p>
     * An invalid move occurs when a player has made a move which immediately hits a wall or goes out of the game board
     * bounds.
     * </p>
     */
    public static final class Invalid extends MoveResult {

        /**
         * Creates an instance of {@link MoveResult.Invalid}, indicating the move was invalid.
         *
         * @param newPosition The {@link Position} of the player after this move. This should be the same as the
         *                    original position before the move.
         */
        public Invalid(final Position newPosition) {
            // TODO
            super(null);
        }
    }

    /**
     * Creates an instance of {@link MoveResult}.
     *
     * @param newPosition The new {@link Position} of the player after making the move.
     */
    private MoveResult(final Position newPosition) {
        // TODO
        this.newPosition = null;
    }
}

