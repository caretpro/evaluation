
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
     * <p>This method should perform the same action as {@link EntityCell#setEntity(Entity)},
     * except that the parameter and return value are both changed to {@link Player}.</p>
     *
     * @param newPlayer The new player of this cell.
     * @return The previous player occupying this cell, or {@code null} if no player was previously occupying this cell.
     */
    public Player setPlayer(final Player newPlayer) {
        @SuppressWarnings("unchecked")
        final Player prevPlayer = (Player) super.setEntity(newPlayer);
        return prevPlayer;
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
    public StopCell(final Position position) {
        super(position, null);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     * @throws IllegalArgumentException if {@code initialEntity} is non-null and not a {@link Player}.
     */
    public StopCell(final @NotNull Position position, final @Nullable Entity initialEntity) {
        super(position, initialEntity);
        if (initialEntity != null && !(initialEntity instanceof Player)) {
            throw new IllegalArgumentException(
                "StopCell can only host a Player or be empty; got: "
                + initialEntity.getClass().getSimpleName());
        }
    }

    @Override
    public Entity setEntity(final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException(
                "StopCell can only host a Player or be empty; got: "
                + newEntity.getClass().getSimpleName());
        }
        return super.setEntity(newEntity);
    }
}