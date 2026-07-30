
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
	 * Represents a valid move.
	 */
	public static class Valid extends MoveResult {

		/**
		 * The original position of the player before the move.
		 */
		@NotNull
		public final Position origPosition;

		/**
		 * Creates an instance of {@link Valid}, indicating that the move is valid.
		 *
		 * @param newPosition  The new {@link Position} of the player after moving.
		 * @param origPosition The original {@link Position} of the player before moving.
		 */
		private Valid(@NotNull final Position newPosition, @NotNull final Position origPosition) {
			super(newPosition);
			this.origPosition = Objects.requireNonNull(origPosition);
		}

		/**
		 * Represents a valid move, and the player is alive after making the move.
		 */
		public static final class Alive extends Valid {

			/**
			 * List of positions representing the location of {@link Gem} collected in this move.
			 */
			@NotNull
			public final List<Position> collectedGems;

			/**
			 * List of positions representing the location of {@link ExtraLife} collected in this move.
			 */
			@NotNull
			public final List<Position> collectedExtraLives;

			/**
			 * Creates an instance of {@link Alive} with no collected entities.
			 *
			 * @param newPosition  The new {@link Position} of the player after moving.
			 * @param origPosition The original {@link Position} of the player before moving.
			 */
			public Alive(@NotNull final Position newPosition, @NotNull final Position origPosition) {
				this(newPosition, origPosition, Collections.emptyList(), Collections.emptyList());
			}

			/**
			 * Creates an instance of {@link Alive}.
			 *
			 * @param newPosition         The new {@link Position} of the player after moving.
			 * @param origPosition        The original {@link Position} of the player before moving.
			 * @param collectedGems       Positions of gems collected during the move.
			 * @param collectedExtraLives Positions of extra lives collected during the move.
			 */
			public Alive(
					@NotNull final Position newPosition,
					@NotNull final Position origPosition,
					@NotNull final List<Position> collectedGems,
					@NotNull final List<Position> collectedExtraLives) {
				super(newPosition, origPosition);
				this.collectedGems = Collections.unmodifiableList(
						Objects.requireNonNull(collectedGems));
				this.collectedExtraLives = Collections.unmodifiableList(
						Objects.requireNonNull(collectedExtraLives));
			}
		}

		/**
		 * Represents a valid move that results in the player's death.
		 */
		public static final class Dead extends Valid {

			/**
			 * The position of the mine that killed the player.
			 */
			@NotNull
			public final Position minePosition;

			/**
			 * Creates an instance of {@link Dead}.
			 *
			 * @param newPosition  The player's position after the move.
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
	public static final class Invalid extends MoveResult {

		/**
		 * Creates an instance of {@link Invalid}.
		 *
		 * @param newPosition The player's position after the attempted move.
		 */
		public Invalid(@NotNull final Position newPosition) {
			super(newPosition);
		}
	}

	/**
	 * Creates an instance of {@link MoveResult}.
	 *
	 * @param newPosition The player's position after making the move.
	 */
	private MoveResult(@NotNull final Position newPosition) {
		this.newPosition = Objects.requireNonNull(newPosition);
	}
}