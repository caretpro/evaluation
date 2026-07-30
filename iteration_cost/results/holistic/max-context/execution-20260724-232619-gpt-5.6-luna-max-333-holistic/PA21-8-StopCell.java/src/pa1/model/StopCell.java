
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
    public StopCell(@Nullable final Position position) {
        super(position != null ? position : new Position(0, 0));
    }

    /**
     * Creates an instance of {@link StopCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public StopCell(
            @Nullable final Position position,
            @Nullable final Entity initialEntity
    ) {
        super(position != null ? position : new Position(0, 0), initialEntity);
    }

    /**
     * Same as {@link EntityCell#setEntity(Entity)}, with additional checking.
     *
     * @param newEntity The new entity to place in this cell.
     * @return The previous entity occupying this cell, or {@code null} if empty.
     * @throws IllegalArgumentException if the entity is not {@code null} and not
     *                                  an instance of {@link Player}.
     */
    @Override
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        if (newEntity != null && !(newEntity instanceof Player)) {
            throw new IllegalArgumentException(
                    "A StopCell can only contain a Player or null."
            );
        }

        return super.setEntity(newEntity);
    }

    /**
     * Replaces the player on this {@link StopCell} with {@code newPlayer}.
     *
     * @param newPlayer The new player of this cell.
     * @return The previous player occupying this cell, or {@code null} if empty.
     */
    @Nullable
    public Player setPlayer(@Nullable final Player newPlayer) {
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
}