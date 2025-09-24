package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

	@Nullable
	private EntityCell owner;

	protected void Entity() {
		this.owner = null;
	}

	protected void Entity(final EntityCell owner) {
		this.owner = owner;
	}

	public final EntityCell setOwner(final EntityCell owner) {
		EntityCell previousOwner = this.owner;
		if (previousOwner != null) {
			previousOwner.setEntity(null);
		}
		this.owner = owner;
		if (owner != null) {
			owner.setEntity(this);
		}
		return previousOwner;
	}

	public final EntityCell getOwner() {
		return owner;
	}
}
