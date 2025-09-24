
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

    /**
     * Constructor accepting only position.
     * @param position The position of this cell.
     */
    public StopCell(@NotNull Position position) {
        super(position, null);
    }

    /**
     * Constructor accepting position and initial entity.
     * @param position The position of this cell.
     * @param initialEntity The initial entity occupying this cell.
     */
    public StopCell(@NotNull Position position, @Nullable Entity initialEntity) {
        super(position, initialEntity);
    }

    /**
     * Constructor accepting position and initial entity of type Player.
     * @param position The position of this cell.
     * @param initialPlayer The initial Player occupying this cell.
     */
    public StopCell(@NotNull Position position, @Nullable Player initialPlayer) {
        super(position, initialPlayer);
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
            throw new IllegalArgumentException("Entity must be null or an instance of Player");
        }
        Entity previousEntity = getEntity();
        super.setEntity(newEntity);
        return previousEntity;
    }
}