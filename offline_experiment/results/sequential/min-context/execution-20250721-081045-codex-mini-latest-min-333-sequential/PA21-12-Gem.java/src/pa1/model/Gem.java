package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * An gem entity on a game board.
 *
 * <p>
 * A gem is the item which the player needs to collect in the game.
 * </p>
 */
public final class Gem extends Entity {

	@Override
	public char toUnicodeChar() {
		return '\u25C7';
	}

	@Override
	public char toASCIIChar() {
		return '*';
	}

	/**
	 * Creates an instance of  {@link Gem} , initially not present on any  {@link EntityCell} .
	 */
	public Gem() {
		this(null);
	}

	/**
	 * Constructs an entity and places it into the given cell.
	 * @param owner  the cell to place this entity into (may be null)
	 */
	protected Entity(final EntityCell owner) {
		this.owner = owner;
		if (owner != null) {
			owner.setEntity(this);
		}
	}
}
