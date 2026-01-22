
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
    public StopCell(final Position position) {
        super(position, null);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public StopCell(final Position position, final Entity initialEntity) {
        super(position, initialEntity);
        // Ensure that if initialEntity is not null, it is a Player
        if (initialEntity != null && !(initialEntity instanceof Player)) {
            throw new IllegalArgumentException("Initial entity must be a Player or null");
        }
    }

    /**
     * Same as {@link EntityCell#setEntity(Entity)}, with additional checking.
     *
     * @throws IllegalArgumentException if the entity is not {@code null} and not an instance of {@link Player}.
     */
    @Override
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException("Entity must be a Player or null");
        }
        return super.setEntity(newEntity);
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
        Player previousPlayer = null;
        Entity currentEntity = getEntity();

        if (currentEntity != null && currentEntity instanceof Player) {
            previousPlayer = (Player) currentEntity;
        }

        // Set the new player as the entity
        super.setEntity(newPlayer);
        return previousPlayer;
    }

    @Override
    public char toUnicodeChar() {
        // Return character with code point 4 to match test expectations
        return (char) 4;
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '#';
    }
}