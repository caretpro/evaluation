package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * A mine entity on a game board.
 *
 * <p>
 * A mine, when touched by the {@link Player} entity, will blow up and deduct a life from the player.
 * </p>
 */
public final class Mine extends Entity {

	@Override
	public char toUnicodeChar() {
		return '\u26A0';
	}

	@Override
	public char toASCIIChar() {
		return 'X';
	}

	/**
	 * Creates an instance of  {@link Mine} , initially not present on any  {@link EntityCell} .
	 */
	public Mine() {
		super();
	}

	protected void Entity(@Nullable final EntityCell owner) {
		this.owner = owner;
	}
}
