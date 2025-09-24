
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

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
	public Player setPlayer(final Player newPlayer) {
		final Player oldPlayer = (Player) super.getEntity();
		super.setEntity(newPlayer);
		return oldPlayer;
	}

	@Override
	public char toUnicodeChar() {
		return getEntity() != null ? getEntity().toUnicodeChar() : '\u25A1';
	}

	@Override
	public char toASCIIChar() {
		return getEntity() != null ? getEntity().toASCIIChar() : '#';
	}

	/**
	 * Creates an instance of  {@link StopCell}  at the given game board position.
	 * @param position  The position where this cell belongs at.
	 */
	public StopCell(final Position position) {
		super(position);
	}

	/**
	 * Creates an instance of  {@link StopCell}  at the given game board position.
	 * @param position       The position where this cell belongs at.
	 * @param initialEntity  The initial entity present in this cell.
	 */
	public StopCell(final Position position, final Entity initialEntity) {
		super(position);
		setEntity(initialEntity);
	}

	/**
	 * The method `setEntity` in the `StopCell` class overrides the `setEntity` method from the parent class `EntityCell`. It sets the entity of the cell, but only if the new entity is either null or an instance of the `Player` class. If the new entity is not null and not an instance of `Player`, it throws an IllegalArgumentException.
	 * @param newEntity  The new entity to set for this cell.
	 * @return  The previous entity occupying this cell, or null if no entity was previously present.
	 * @throws IllegalArgumentException  if the entity is not  {@code  null}  and not an instance of  {@link Player} .
	 */
	@Override
	public Entity setEntity(final Entity newEntity) {
		if (newEntity != null && !(newEntity instanceof Player)) {
			throw new IllegalArgumentException("Entity must be a Player or null");
		}
		return super.setEntity(newEntity);
	}
}