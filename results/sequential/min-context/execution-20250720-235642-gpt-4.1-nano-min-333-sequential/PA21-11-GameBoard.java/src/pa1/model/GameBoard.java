
public final class GameBoard {
    private final int numRows;
    private final int numCols;
    private final Cell[][] board;
    private final Player player;

    // Existing constructors and methods...

    /**
     * Constructor to initialize GameBoard with specified dimensions and cell data.
     *
     * @param numRows Number of rows in the game board.
     * @param numCols Number of columns in the game board.
     * @param board   2D array of cells representing the game board.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] board) {
        if (board.length != numRows) {
            throw new IllegalArgumentException("Number of rows does not match");
        }
        if (board.length == 0 || board[0].length != numCols) {
            throw new IllegalArgumentException("Number of columns does not match");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (board[r].length != numCols) {
                throw new IllegalArgumentException("Row " + r + " length does not match numCols");
            }
            System.arraycopy(board[r], 0, this.board[r], 0, numCols);
        }
        // Initialize player, gems, and other state as needed
        Player foundPlayer = null;
        int gemCount = 0;
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
                    if (ec.getEntity() instanceof Gem) {
                        gemCount++;
                    }
                }
            }
        }
        if (foundPlayer == null) {
            throw new IllegalArgumentException("No player found");
        }
        this.player = foundPlayer;
        // Additional setup if necessary
    }
}