
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>This class maintains the one-to-one relationship between entities and
 * the cells that own them.</p>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    Entity entity;

    /**
     * Creates an instance of {@link EntityCell}.
     *
     * @param position the position of this cell, or {@code null} to use
     *                 position {@code (0, 0)}
     */
    public EntityCell(@Nullable final Position position) {
        super(position == null ? new Position(0, 0) : position);
    }

    /**
     * Creates an instance of {@link EntityCell} with an optional entity.
     *
     * @param position      the position of this cell, or {@code null} to use
     *                      position {@code (0, 0)}
     * @param initialEntity the entity initially placed in this cell
     */
    public EntityCell(
            @Nullable final Position position,
            @Nullable final Entity initialEntity
    ) {
        this(position);
        setEntity(initialEntity);
    }

    /**
     * Replaces the entity currently present in this cell and transfers
     * ownership of the new entity to this cell.
     *
     * @param newEntity the entity to place in this cell, or {@code null}
     * @return the entity previously present in this cell
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        final Entity previousEntity = entity;

        if (previousEntity == newEntity) {
            return previousEntity;
        }

        if (previousEntity != null && previousEntity.getOwner() == this) {
            previousEntity.setOwner(null);
        }

        entity = null;

        if (newEntity != null) {
            final EntityCell previousOwner = newEntity.getOwner();

            if (previousOwner != null && previousOwner != this) {
                previousOwner.setEntity(null);
            }

            entity = newEntity;
            newEntity.setOwner(this);
        }

        return previousEntity;
    }

    /**
     * @return the entity currently present in this cell, or {@code null}
     */
    @Nullable
    public final Entity getEntity() {
        return entity;
    }

    @Override
    public char toUnicodeChar() {
        return entity == null ? '.' : entity.toUnicodeChar();
    }

    @Override
    public char toASCIIChar() {
        return entity == null ? '.' : entity.toASCIIChar();
    }
}