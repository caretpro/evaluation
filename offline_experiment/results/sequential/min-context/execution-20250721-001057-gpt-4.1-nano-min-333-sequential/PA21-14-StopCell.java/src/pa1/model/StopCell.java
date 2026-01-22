
public final class StopCell extends EntityCell {

    /**
     * Constructor with only position.
     *
     * @param position The position of the cell.
     */
    public StopCell(final Position position) {
        super(position, null);
    }

    /**
     * Constructor with position and initial Player entity.
     *
     * @param position The position of the cell.
     * @param initialPlayer The initial Player occupying the cell.
     */
    public StopCell(final Position position, final Player initialPlayer) {
        super(position, initialPlayer);
    }
}