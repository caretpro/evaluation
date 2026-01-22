
public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
    if (cells == null) {
        throw new IllegalArgumentException("Cells array cannot be null");
    }
    if (cells.length != numRows) {
        throw new IllegalArgumentException("numRows does not match cells.length");
    }
    if (numRows > 0 && (cells[0] == null || cells[0].length != numCols)) {
        throw new IllegalArgumentException("numCols does not match cells[0].length");
    }
    this.board = new Cell[numRows][numCols];
    for (int r = 0; r < numRows; r++) {
        if (cells[r] == null || cells[r].length != numCols) {
            throw new IllegalArgumentException("All rows must have length numCols");
        }
        System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
    }
    this.numRows = numRows;
    this.numCols = numCols;
    this.player = getSinglePlayer();
    int gemCount = 0;
    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            Cell cell = this.board[r][c];
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                gemCount++;
            }
        }
    }
    if (gemCount == 0) {
        throw new IllegalArgumentException("No gems present on the board");
    }
    if (!isAllGemsReachable()) {
        throw new IllegalArgumentException("Some gems are not reachable by the player");
    }
}