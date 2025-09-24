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

	/**
	 * Creates an instance of {@link Gem}, initially not present on any {@link EntityCell}.
	 */
	public Gem() {
		// TODO
	}

	@Override
	public char toUnicodeChar() {
		return '\u25C7';
	}

	@Override
	public char toASCIIChar() {
		return '*';
	}

	public Gem(final EntityCell owner) {
		super(owner);
		owner.setEntity(this);
	}
}
