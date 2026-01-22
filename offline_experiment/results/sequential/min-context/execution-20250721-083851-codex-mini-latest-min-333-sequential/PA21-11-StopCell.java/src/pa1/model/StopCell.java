
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

    /**
     * Creates a StopCell at the given game board position with no player.
     * @param position The position where this cell belongs.
     */
    public StopCell(final Position position) {
        super(position, null);
    }

    /**
     * Creates a StopCell at the given game board position, initially holding the specified player.
     * @param position The position where this cell belongs.
     * @param initialPlayer The initial player on this cell (may be null).
     */
    public StopCell(final Position position, final Player initialPlayer) {
        super(position, initialPlayer);
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
    @Override
    public Player setPlayer(final Player newPlayer) {
        @SuppressWarnings("unchecked")
        final Player oldPlayer = (Player) super.setEntity(newPlayer);
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

    @Override
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException(
                    "StopCell may only hold a Player (or null), but got: " + newEntity.getClass().getSimpleName());
        }
        return super.setEntity(newEntity);
    }
}