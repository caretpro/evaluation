
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
    @Override
    @Nullable
    public Player setPlayer(final Player newPlayer) {
        // Delegate to EntityCell#setEntity(Entity) and cast the previous Entity back to Player.
        return (Player) super.setEntity(newPlayer);
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
     * @param position The position where this cell belongs at.
     */
    public StopCell(final Position position) {
        super(position);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position.
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public StopCell(final Position position, final Entity initialEntity) {
        super(position, initialEntity);
    }

    /**
     * Same as {@link EntityCell#setEntity(Entity)}, with additional checking.
     * @throws IllegalArgumentException if the entity is not {@code null} and not an instance of {@link Player}.
     */
    @Override
    @Nullable
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException("StopCell may only hold a Player or be empty");
        }
        return super.setEntity(newEntity);
    }
}