
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
        super(position);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position with an initial entity.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public StopCell(@NotNull final Position position, @Nullable final Entity initialEntity) {
        super(position, initialEntity);
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
            throw new IllegalArgumentException("Entity must be null or an instance of Player.");
        }
        return super.setEntity(newEntity);
    }
}