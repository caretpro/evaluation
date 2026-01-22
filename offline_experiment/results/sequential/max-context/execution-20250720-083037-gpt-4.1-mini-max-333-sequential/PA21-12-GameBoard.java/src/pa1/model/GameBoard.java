
public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
    if (cells == null) {
        throw new IllegalArgumentException("Cells array cannot be null.");
    }
    if (numRows != cells.length) {
        throw new IllegalArgumentException("numRows does not match cells.length");
    }
    if (numRows == 0) {
        throw new IllegalArgumentException("numRows must be greater than 0");
    }
    if (cells[0] == null || numCols != cells[0].length) {
        throw new IllegalArgumentException("numCols does not match cells[0].length");
    }
    if (numCols == 0) {
        throw new IllegalArgumentException("numCols must be greater than 0");
    }
    this.board = new Cell[numRows][numCols];
    for (int r = 0; r < numRows; r++) {
        if (cells[r] == null || cells[r].length != numCols) {
            throw new IllegalArgumentException("All rows must have length equal to numCols");
        }
        System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
    }
    this.numRows = numRows;
    this.numCols = numCols;
    this.player = getSinglePlayer();
    int gemCount = 0;
    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            Cell cell = board[r][c];
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                gemCount++;
            }
        }
    }
    if (gemCount == 0) {
        throw new IllegalArgumentException("There must be at least one gem on the board.");
    }
    if (!isAllGemsReachable()) {
        throw new IllegalArgumentException("Not all gems are reachable from the player's position.");
    }
}