
// pa1/model/GameBoard.java
package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * A mutable board for playing.
 */
public class GameBoard {
    public GameBoard(@NotNull Cell[][] cells, int numRows, int numCols, int playerRow, int playerCol, int lives, int gemsRemaining) { … }
    public @NotNull Cell getCell(@NotNull Position pos) { … }
    public void setCell(@NotNull Position pos, @NotNull Cell cell) { … }
    public @NotNull Player getPlayer() { … }
    public int getPlayerRow() { … }
    public int getPlayerCol() { … }
    public void setPlayerRow(int row) { … }
    public void setPlayerCol(int col) { … }
    public int getLives() { … }
    public void setLives(int lives) { … }
    public int getRemainingGems() { … }
    public void setRemainingGems(int gems) { … }
    public int getNumRows() { … }
    public int getNumCols() { … }
}