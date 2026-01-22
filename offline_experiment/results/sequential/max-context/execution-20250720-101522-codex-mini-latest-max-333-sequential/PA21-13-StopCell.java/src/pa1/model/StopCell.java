
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

	/**
	 * Creates an instance of {@link StopCell} at the given game board position.
	 *
	 * @param position The position where this cell belongs at.
	 */
	public StopCell(final @NotNull Position position) {
		super(position);
	}

	/**
	 * Creates an instance of {@link StopCell} at the given game board position.
	 *
	 * @param position      The position where this cell belongs at.
	 * @param initialEntity The initial {@link Entity} present in this cell.
	 * @throws IllegalArgumentException if {@code initialEntity} is non‐null and not a {@link Player}.
	 */
	public StopCell(final @NotNull Position position,
	                final @Nullable Entity initialEntity) {
		super(position, validate(initialEntity));
	}

	/** Javadoc see {@link #StopCell(Position, Entity)}. */
	private static @Nullable Entity validate(final @Nullable Entity e) {
		if (e != null && !(e instanceof Player)) {
			throw new IllegalArgumentException("StopCell can only hold a Player (or null)");
		}
		return e;
	}

	@Override
	public char toUnicodeChar() {
		return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
	}

	@Override
	public char toASCIIChar() {
		return getEntity() != null ? getEntity().toASCIIChar() : '#';
	}

	@Override
	public Entity setEntity(final Entity newEntity) {
		if (newEntity != null && !(newEntity instanceof Player)) {
			throw new IllegalArgumentException("StopCell can only hold a Player (or null)");
		}
		return super.setEntity(newEntity);
	}
}