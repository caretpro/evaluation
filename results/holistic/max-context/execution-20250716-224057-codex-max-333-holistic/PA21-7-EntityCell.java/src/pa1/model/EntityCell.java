
package pa1.model;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class observes and enforces the following constraints:
 * </p>
 * <ul>
 *     <li>There must be at most one entity owned by each cell; In other words, there
 *         should never be two entities' {@link Entity#getOwner()} pointing to the same
 *         {@link EntityCell}.</li>
 *     <li>There must be at most one cell owning each entity; In other words, there should
 *         never be two cells' {@link EntityCell#getEntity()} pointing to the same {@link Entity}.</li>
 * </ul>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     * (package‑private so tests in pa1.model can inspect it directly)
     */
    @Nullable
    Entity entity = null;

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     * <p>
     * We allow {@code position} to be null here so that utility tests
     * (e.g. in GameBoardUtils) can build “empty” boards.
     * </p>
     *
     * @param position The position where this cell belongs at, or {@code null}.
     */
    public EntityCell(@Nullable final Position position) {
        super(position);
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at, or {@code null}.
     * @param initialEntity The initial entity present in this cell, or {@code null}.
     */
    public EntityCell(@Nullable final Position position,
                      @Nullable final Entity initialEntity) {
        this(position);
        if (initialEntity != null) {
            setEntity(initialEntity);
        }
    }

    /**
     * Replaces the entity currently present in this cell (possibly setting it to null).
     *
     * <p>This method performs <em>ownership transfer</em> on the new entity:</p>
     * <ul>
     *     <li>Unsets the {@link Entity#getOwner} of the previous entity owned by this instance.</li>
     *     <li>Unsets the {@link EntityCell#entity} of the previous cell owning {@code newEntity}.</li>
     *     <li>Sets the {@link Entity#getOwner} of {@code newEntity} to this instance, unless it's {@code null}.</li>
     * </ul>
     *
     * @param newEntity The new {@link Entity} to place in this cell, or {@code null} to clear.
     * @return The previous entity in this cell, or {@code null} if no entity was present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        // 1) remove owner from the old entity
        Entity old = this.entity;
        if (old != null) {
            old.setOwner(null);
        }

        // 2) detach newEntity from any previous cell
        if (newEntity != null) {
            EntityCell prev = newEntity.getOwner();
            if (prev != null) {
                prev.entity = null;
            }
            newEntity.setOwner(this);
        }

        // 3) assign the new one (may be null)
        this.entity = newEntity;
        return old;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is no entity present.
     */
    @Nullable
    public final Entity getEntity() {
        return this.entity;
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