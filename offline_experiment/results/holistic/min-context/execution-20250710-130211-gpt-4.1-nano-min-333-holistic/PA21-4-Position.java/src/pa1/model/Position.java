
package pa1.model;

/**
 * Represents an offset in position with delta row and delta column.
 */
public class PositionOffset {
    private final int dRow;
    private final int dCol;

    /**
     * Constructs a PositionOffset with specified deltas.
     *
     * @param dRow The delta in rows.
     * @param dCol The delta in columns.
     */
    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    /**
     * Gets the delta in rows.
     *
     * @return The delta row.
     */
    public int getDRow() {
        return dRow;
    }

    /**
     * Gets the delta in columns.
     *
     * @return The delta column.
     */
    public int getDCol() {
        return dCol;
    }
}