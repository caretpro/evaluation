
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} that stops the {@link Player} from sliding further.
 */
public final class StopCell extends EntityCell {

    /**
     * Constructor accepting only position.
     * @param position The position of this cell.
     */
    public StopCell(@NotNull final Position position) {
        super(position);
    }

    /**
     * Constructor accepting position and initial entity.
     * @param position The position of this cell.
     * @param initialEntity The initial entity in this cell (e.g., Player).
     */
    public StopCell(@NotNull final Position position, @Nullable final Entity initialEntity) {
        super(position, initialEntity);
    }

    // Existing methods...
}