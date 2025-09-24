
public final class GameBoard {
    private final int numRows;
    private final int numCols;
    private final Cell[][] board;
    // other fields...

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
            System.arraycopy(board[r], 0, this.board[r], 0, numCols);
        }
        // initialize other fields as needed
    }
}