
/**
 * Constructs a new GameBoard with the specified number of rows, columns, and cells.
 *
 * @param numRows Number of rows in the game board.
 * @param numCols Number of columns in the game board.
 * @param cells   2D array of cells representing the game board.
 * @throws IllegalArgumentException if cells is null, dimensions mismatch, no player found,
 *                                  multiple players found, no gems found, or some gems are unreachable.
 */
public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
    if (cells == null) {
        throw new IllegalArgumentException("Cells array cannot be null");
    }
    if (numRows != cells.length) {
        throw new IllegalArgumentException("numRows does not match cells.length");
    }
    if (numCols == 0 || cells[0] == null || numCols != cells[0].length) {
        throw new IllegalArgumentException("numCols does not match cells[0].length");
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
    int gemCount = 0;
    for (int r = 0; r < numRows; r++) {
        for (int c = 0; c < numCols; c++) {
            Cell cell = this.board[r][c];
            if (cell instanceof EntityCell ec) {
                var entity = ec.getEntity();
                if (entity instanceof Player p) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException("More than one player found");
                    }
                    foundPlayer = p;
                } else if (entity instanceof Gem) {
                    gemCount++;
                }
            }
        }
    }
    if (foundPlayer == null) {
        throw new IllegalArgumentException("No player found on the board");
    }
    if (gemCount == 0) {
        throw new IllegalArgumentException("No gems found on the board");
    }
    this.player = foundPlayer;
    if (!isAllGemsReachable()) {
        throw new IllegalArgumentException("Some gems are not reachable by the player");
    }
}