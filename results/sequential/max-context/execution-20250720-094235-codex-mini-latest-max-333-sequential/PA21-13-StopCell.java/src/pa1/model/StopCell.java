
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
    public StopCell(@NotNull final Position position) {
        super(position, null);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position,
     * with an initial {@link Player} occupying it.
     *
     * @param position      The position where this cell belongs at.
     * @param initialPlayer The initial player present in this cell (may be null).
     */
    public StopCell(@NotNull final Position position,
                    @Nullable final Player initialPlayer) {
        super(position, initialPlayer);
    }

    /**
     * Replaces the player on this {@link StopCell} with {@code newPlayer}.
     *
     * <p>
     * This method performs the same ownership‑transfer logic as
     * {@link EntityCell#setEntity(Entity)}, but its parameter/return types
     * are specialized to {@link Player}.
     * </p>
     *
     * @param newPlayer The new player of this cell.
     * @return The previous player occupying this cell, or {@code null}
     *         if no player was previously occupying this cell.
     */
    public Player setPlayer(final Player newPlayer) {
        return (Player) setEntity(newPlayer);
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
            throw new IllegalArgumentException("Entity must be a Player or null");
        }
        return super.setEntity(newEntity);
    }
}