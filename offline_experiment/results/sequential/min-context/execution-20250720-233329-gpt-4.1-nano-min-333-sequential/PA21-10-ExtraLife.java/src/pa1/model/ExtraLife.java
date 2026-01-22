package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * An extra life entity on a game board.
 *
 * <p>
 * An extra life entity gives the player an extra life when picked up.
 * </p>
 */
public final class ExtraLife extends Entity {

	@Override
	public char toUnicodeChar() {
		return '\u2661';
	}

	@Override
	public char toASCIIChar() {
		return 'L';
	}

	public void ExtraLife() {
		super(null);
	}

	public void ExtraLife(final EntityCell owner) {
		super(owner);
	}
}
