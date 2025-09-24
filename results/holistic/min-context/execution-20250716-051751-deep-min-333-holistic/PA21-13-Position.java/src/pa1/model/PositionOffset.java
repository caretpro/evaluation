package pa1.model;

import java.util.*;

/**
 * A positional offset.
 *
 * @param dRow The number of rows to offset by.
 * @param dCol The number of columns to offset by.
 */
public class PositionOffset {
    private int dRow;

    private int dCol;

    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int dRow() {
        return dRow;
    }

    public void dRow(int dRow) {
        this.dRow = dRow;
    }

    public int dCol() {
        return dCol;
    }

    public void dCol(int dCol) {
        this.dCol = dCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionOffset that = (PositionOffset) o;
        return dRow == that.dRow && dCol == that.dCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dRow, dCol);
    }
}
