
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
    @Nullable
    public Player setPlayer(@Nullable final Player newPlayer) {
        @SuppressWarnings("unchecked")
        final Player prev = (Player) setEntity(newPlayer);
        return prev;
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
     * Creates an instance of {@link StopCell} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public StopCell(@NotNull final Position position) {
        super(position, null);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position,
     * initially containing the given {@link Player}.
     *
     * @param position The position where this cell belongs at.
     * @param player   The initial {@link Player} on this cell.
     */
    public StopCell(@NotNull final Position position, @Nullable final Player player) {
        super(position, player);
    }

    @Override
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException(
                "StopCell can only hold a Player or be empty, not "
                + newEntity.getClass().getSimpleName());
        }
        return super.setEntity(newEntity);
    }
}