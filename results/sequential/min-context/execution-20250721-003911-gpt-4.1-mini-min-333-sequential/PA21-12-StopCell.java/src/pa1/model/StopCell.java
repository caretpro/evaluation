
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

    // Corrected constructors (no void return type)
    public StopCell(final Position position) {
        super(position, null);
    }

    public StopCell(final Position position, final Entity initialEntity) {
        super(position, initialEntity);
    }

    @Override
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException("Entity must be null or an instance of Player");
        }
        return super.setEntity(newEntity);
    }
}