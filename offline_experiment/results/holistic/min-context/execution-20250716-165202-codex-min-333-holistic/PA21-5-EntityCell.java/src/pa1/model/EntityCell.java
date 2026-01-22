
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class enforces the following constraints:
 * </p>
 * <ul>
 *     <li>There is at most one entity owned by each cell: no two entities' {@link Entity#getOwner()}
 *         point to the same {@link EntityCell}.</li>
 *     <li>There is at most one cell owning each entity: no two cells' {@link EntityCell#getEntity()}
 *         point to the same {@link Entity}.</li>
 * </ul>
 */
public class EntityCell extends Cell {

    /** The entity which resides on this cell. */
    @Nullable
    private Entity entity = null;

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position The position where this cell belongs.
     */
    public EntityCell(final Position position) {
        super(position);
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs.
     * @param initialEntity The initial entity present in this cell (may be null).
     */
    public EntityCell(final Position position, @Nullable final Entity initialEntity) {
        this(position);
        if (initialEntity != null) {
            setEntity(initialEntity);
        }
    }

    /**
     * Replaces the entity currently present in this cell, returning the previous one (or null).
     *
     * <p>This method performs <em>ownership transfer</em> on the new entity:</p>
     * <ul>
     *   <li>Detach the previous entity from this cell (unset its owner).</li>
     *   <li>If newEntity is non-null, detach it from its old cell (if any), and attach it here.</li>
     *   <li>If newEntity is null, simply clear this cell.</li>
     * </ul>
     *
     * @param newEntity The new {@link Entity} to place in this cell, or null to clear it.
     * @return The previous entity in this cell, or null if none was present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        // 1) Detach and clear out the current occupant, if any.
        Entity previous = this.entity;
        if (previous != null) {
            previous.setOwner(null);
        }

        // 2) If newEntity is null, just clear the cell.
        if (newEntity == null) {
            this.entity = null;
            return previous;
        }

        // 3) Detach newEntity from any old cell.
        EntityCell oldCell = newEntity.getOwner();
        if (oldCell != null) {
            oldCell.entity = null;
        }

        // 4) Attach newEntity to this cell.
        this.entity = newEntity;
        newEntity.setOwner(this);

        return previous;
    }

    /**
     * @return The entity currently present in this cell, or null if empty.
     */
    @Nullable
    public final Entity getEntity() {
        return entity;
    }

    @Override
    public char toUnicodeChar() {
        return entity != null ? entity.toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return entity != null ? entity.toASCIIChar() : '.';
    }
}