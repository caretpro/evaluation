diff
--- a/pa1/model/GameBoard.java
+++ b/pa1/model/GameBoard.java
@@
-   /**
-    * Creates an instance using the provided creation parameters.
-    * @param numRows  The number of rows in the game board.
-    * @param numCols  The number of columns in the game board.
-    * @param cells    The initial values of cells.
-    * @throws IllegalArgumentException  if any of the following are true: <ul> <li> {@code  numRows}  is not equal to  {@code  cells.length} </li> <li> {@code  numCols}  is not equal to  {@code  cells[0].length} </li> <li>There is no player or more than one player in  {@code  cells} </li> <li>There are no gems in  {@code  cells} </li> <li>There are some gems which cannot be reached by the player</li> </ul>
-    */
-   public void GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
+   /**
+    * Creates an instance using the provided creation parameters.
+    * @param numRows  The number of rows in the game board.
+    * @param numCols  The number of columns in the game board.
+    * @param cells    The initial values of cells.
+    * @throws IllegalArgumentException  if any of the following are true: <ul> <li> {@code  numRows}  is not equal to  {@code  cells.length} </li> <li> {@code  numCols}  is not equal to  {@code  cells[0].length} </li> <li>There is no player or more than one player in  {@code  cells} </li> <li>There are no gems in  {@code  cells} </li> <li>There are some gems which cannot be reached by the player</li> </ul>
+    */
+   public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells == null || cells.length != numRows || cells[0] == null || cells[0].length != numCols) {
            throw new IllegalArgumentException("Board dimensions do not match cells array");
        }
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("Row " + r + " has wrong length");
            }
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.player = getSinglePlayer();
        int gemCount = 0;
        for (Cell[] row : board) {
            for (Cell c : row) {
                if (c instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gemCount++;
                }
            }
        }
        if (gemCount == 0) {
            throw new IllegalArgumentException("No gems on the board");
        }
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Some gems are unreachable");
        }
    }