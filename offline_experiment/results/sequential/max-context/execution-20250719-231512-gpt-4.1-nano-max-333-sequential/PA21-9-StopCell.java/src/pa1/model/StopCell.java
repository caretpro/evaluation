
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link EntityCell} which stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

    /**
     * Constructor accepting only position.
     *
     * @param position The position of this cell.
     */
    public StopCell(@NotNull final Position position) {
        super(position);
    }

    /**
     * Constructor accepting position and initial entity.
     *
     * @param position The position of this cell.
     * @param initialEntity The initial entity in this cell.
     */
    public StopCell(@NotNull final Position position, @Nullable final Entity initialEntity) {
        super(position, initialEntity);
    }

    /**
     * Constructor accepting position and initial Player.
     *
     * @param position The position of this cell.
     * @param initialPlayer The initial Player entity in this cell.
     */
    public StopCell(@NotNull final Position position, @Nullable final Player initialPlayer) {
        super(position, initialPlayer);
    }
}