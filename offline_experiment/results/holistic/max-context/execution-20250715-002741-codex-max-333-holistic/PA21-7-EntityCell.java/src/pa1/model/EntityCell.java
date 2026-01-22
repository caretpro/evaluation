
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class observes and enforces the following constraints:
 * </p>
 * <ul>
 *     <li>There is at most one entity owned by each cell; i.e., there shall never be two entities'
 *         {@link Entity#getOwner()} pointing to the same {@link EntityCell}.</li>
 *     <li>There is at most one cell owning each entity; i.e., there shall never be two cells'
 *         {@link EntityCell#getEntity()} pointing to the same {@link Entity}.</li>
 * </ul>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    private Entity entity = null;

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public EntityCell(final @NotNull Position position) {
        super(Objects.requireNonNull(position));
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell (may be {@code null} to start empty).
     */
    public EntityCell(final @NotNull Position position, final @Nullable Entity initialEntity) {
        this(position);
        if (initialEntity != null) {
            setEntity(initialEntity);
        }
    }

    /**
     * Replaces (or clears) the entity currently present in this cell.
     *
     * <p>
     * This method performs <em>ownership transfer</em> on the new entity if non-{@code null}.
     * </p>
     * <ul>
     *   <li>Unset the owner of the previous entity in this cell (if any).</li>
     *   <li>Unset the entity in the previous cell owning {@code newEntity} (if any).</li>
     *   <li>Set the owner of {@code newEntity} to this cell (if non-{@code null}).</li>
     * </ul>
     *
     * @param newEntity The new {@link Entity} to place in this cell, or {@code null} to clear it.
     * @return The previous entity in this cell, or {@code null} if none.
     */
    public Entity setEntity(final @Nullable Entity newEntity) {
        // 1. Detach the old occupant (if any)
        Entity old = this.entity;
        if (old != null) {
            old.setOwner(null);
        }

        // 2. Detach newEntity from its prior cell (if any)
        if (newEntity != null) {
            EntityCell prevOwner = newEntity.getOwner();
            if (prevOwner != null) {
                prevOwner.entity = null;
            }
        }

        // 3. Install newEntity (may be null) and fix ownership
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }

        return old;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is none.
     */
    public final @Nullable Entity getEntity() {
        return entity;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}