
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The player entity on a game board.
 *
 * <p>
 * There should be at most one player entity on a game board.
 * </p>
 */
public final class Player extends Entity {

    private EntityCell owner;

    /**
     * Creates an instance of {@link Player}, initially not present on any {@link EntityCell}.
     */
    public Player() {
        super();
        this.owner = null;
    }

    /**
     * Creates an instance of {@link Player}.
     *
     * @param owner The initial {@link EntityCell} the player belongs to.
     */
    public Player(final EntityCell owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF';
    }

    @Override
    public char toASCIIChar() {
        return '@';
    }

    @Override
    public @Nullable EntityCell getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@Nullable EntityCell newOwner) {
        this.owner = newOwner;
    }
}