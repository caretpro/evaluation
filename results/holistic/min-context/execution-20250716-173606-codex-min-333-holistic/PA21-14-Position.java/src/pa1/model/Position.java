
package pa1.model;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * A row/column offset to apply to a {@link Position}.
 */
public class PositionOffset {

    private final int dRow;
    private final int dCol;

    /**
     * @param dRow how many rows to shift (may be negative).
     * @param dCol how many columns to shift (may be negative).
     */
    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int dRow() {
        return dRow;
    }

    public int dCol() {
        return dCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionOffset)) return false;
        PositionOffset that = (PositionOffset) o;
        return dRow == that.dRow && dCol == that.dCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dRow, dCol);
    }

    @Override
    public String toString() {
        return "Offset(" + dRow + "," + dCol + ")";
    }
}