
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pa1.model.entity.Entity;
import pa1.model.entity.Player;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

    /**
     * Creates an instance of {@link StopCell} at the given game board position,
     * initially empty.
     *
     * @param position The position where this cell belongs at.
     */
    public StopCell(final Position position) {
        this(position, null);
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position,
     * with the given initial player (or {@code null} if empty).
     *
     * @param position  The position where this cell belongs at.
     * @param player The initial player occupying this cell, or {@code null}.
     */
    public StopCell(@NotNull final Position position,
                    @Nullable final Player player) {
        super(position, player);
    }

    /**
     * Replaces the player on this {@link StopCell} with {@code newPlayer}.
     *
     * <p>
     * This method should perform the same action as {@link EntityCell#setEntity(Entity)},
     * except that the parameter and return value are both changed to {@link Player}.
     * </p>
     *
     * @param newPlayer The new player of this cell.
     * @return The previous player occupying this cell, or {@code null} if no player was
     *     previously occupying this cell.
     */
    @Override
    public Player setPlayer(final Player newPlayer) {
        // Delegate to EntityCell#setEntity(Entity), which returns the previous Entity (or null).
        // We know StopCell only ever holds Players (see overridden setEntity), so the cast is safe.
        @SuppressWarnings("unchecked")
        Player oldPlayer = (Player) super.setEntity(newPlayer);
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
                    "StopCell may only hold a Player or be empty; got: "
                    + newEntity.getClass().getSimpleName());
        }
        return super.setEntity(newEntity);
    }
}