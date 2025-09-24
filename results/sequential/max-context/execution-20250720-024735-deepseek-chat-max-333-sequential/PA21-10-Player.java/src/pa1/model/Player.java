package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * The player entity on a game board.
 *
 * <p>
 * There should be at most one player entity on a game board.
 * </p>
 */
public final class Player extends Entity {

	@Override
	public char toUnicodeChar() {
		return '\u25EF';
	}

	@Override
	public char toASCIIChar() {
		return '@';
	}

	/**
	 * Creates an instance of  {@link Player} , initially not present on any  {@link EntityCell} .
	 */
	public void Player() {
		super();
	}

	/**
	 * Creates an instance of  {@link Player} .
	 * @param owner  The initial  {@link EntityCell}  the player belongs to.
	 */
	public void Player(final EntityCell owner) {
		super(owner);
		if (owner != null) {
			owner.setEntity(this);
		}
	}
}
