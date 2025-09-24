
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
	 * @throws NullPointerException if {@code position} is {@code null}.
	 */
	public StopCell(final @NotNull Position position) {
		super(position, null);
	}

	/**
	 * Creates an instance of {@link StopCell} at the given game board position.
	 *
	 * @param position      The position where this cell belongs at.
	 * @param initialEntity The initial entity present in this cell.
	 * @throws NullPointerException     if {@code position} is {@code null}.
	 * @throws IllegalArgumentException if the entity is not {@code null} and not an instance of {@link Player}.
	 */
	public StopCell(final @NotNull Position position, final @Nullable Entity initialEntity) {
		super(position, null);
		if (initialEntity != null && !(initialEntity instanceof Player)) {
			throw new IllegalArgumentException("StopCell can only contain null or Player entities.");
		}
		super.setEntity(initialEntity);
	}

	/**
	 * Same as {@link EntityCell#setEntity(Entity)}, with additional checking.
	 *
	 * @throws IllegalArgumentException if the entity is not {@code null} and not an instance of {@link Player}.
	 */
	@Override
	public Entity setEntity(final @Nullable Entity newEntity) {
		if (newEntity != null && !(newEntity instanceof Player)) {
			throw new IllegalArgumentException("StopCell can only contain null or Player entities.");
		}
		return super.setEntity(newEntity);
	}

	/**
	 * Replaces the player on this {@link StopCell} with {@code newPlayer}.
	 *
	 * <p>
	 * This method should perform the same action as {@link EntityCell#setEntity(Entity)}, except that the parameter and
	 * return value are both changed to {@link Player}.
	 * </p>
	 *
	 * @param newPlayer The new player of this cell.
	 * @return The previous player occupying this cell, or {@code null} if no player was previously occupying this cell.
	 */
	public Player setPlayer(final @Nullable Player newPlayer) {
		Entity oldEntity = setEntity(newPlayer);
		return (Player) oldEntity;
	}

	@Override
	public char toUnicodeChar() {
		return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
	}

	@Override
	public char toASCIIChar() {
		return getEntity() != null ? getEntity().toASCIIChar() : '#';
	}
}