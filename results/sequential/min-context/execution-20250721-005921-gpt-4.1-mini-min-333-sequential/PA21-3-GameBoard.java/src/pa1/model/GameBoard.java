
public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
    if (cells == null || cells.length != numRows) {
        throw new IllegalArgumentException("numRows does not match cells.length");
    }
    for (final Cell[] row : cells) {
        if (row == null || row.length != numCols) {
            throw new IllegalArgumentException("numCols does not match cells[0].length");
        }
    }
    this.board = new Cell[numRows][numCols];
    for (int r = 0; r < numRows; r++) {
        System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
    }
    this.numRows = numRows;
    this.numCols = numCols;
    this.player = getSinglePlayer();
    boolean hasGem = false;
    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            final Cell cell = this.board[r][c];
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                hasGem = true;
                break;
            }
        }
        if (hasGem) {
            break;
        }
    }
    if (!hasGem) {
        throw new IllegalArgumentException("No gems found on the board");
    }
    if (!isAllGemsReachable()) {
        throw new IllegalArgumentException("Some gems are not reachable by the player");
    }
}