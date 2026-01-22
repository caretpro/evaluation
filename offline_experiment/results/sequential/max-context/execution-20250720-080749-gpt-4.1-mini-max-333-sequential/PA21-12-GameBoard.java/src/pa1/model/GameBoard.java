
public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
    if (cells == null) {
        throw new IllegalArgumentException("Cells array cannot be null.");
    }
    if (numRows != cells.length) {
        throw new IllegalArgumentException("numRows does not match cells.length");
    }
    if (numRows == 0) {
        throw new IllegalArgumentException("numRows must be positive");
    }
    if (cells[0] == null || numCols != cells[0].length) {
        throw new IllegalArgumentException("numCols does not match cells[0].length");
    }
    if (numCols == 0) {
        throw new IllegalArgumentException("numCols must be positive");
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
    Player foundPlayer = null;
    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            Cell cell = this.board[r][c];
            if (cell instanceof EntityCell ec) {
                if (ec.getEntity() instanceof Player p) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException("More than one player found");
                    }
                    foundPlayer = p;
                }
            }
        }
    }
    if (foundPlayer == null) {
        throw new IllegalArgumentException("No player found");
    }
    this.player = foundPlayer;
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
        throw new IllegalArgumentException("No gems found");
    }
    if (!isAllGemsReachable()) {
        throw new IllegalArgumentException("Some gems are not reachable by the player");
    }
}