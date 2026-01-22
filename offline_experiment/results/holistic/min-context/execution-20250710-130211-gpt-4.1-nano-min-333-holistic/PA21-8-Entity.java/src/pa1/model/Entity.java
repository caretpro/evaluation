
package pa1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the game board with cells, gems, and entities.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final List<List<Cell>> grid;
    private final List<Gems> gems;
    private final Player player;

    /**
     * Creates a default game board with specified dimensions.
     * Initializes grid, gems, and player.
     */
    public GameBoard() {
        this.numRows = 10; // default size, can be parameterized
        this.numCols = 10;
        this.grid = new ArrayList<>();
        for (int r = 0; r < numRows; r++) {
            List<Cell> row = new ArrayList<>();
            for (int c = 0; c < numCols; c++) {
                row.add(new Cell(r, c));
            }
            grid.add(row);
        }
        this.gems = new ArrayList<>();
        // Initialize gems at default positions or leave empty
        this.player = new Player();
        // Additional setup if needed
    }

    /**
     * Constructor with parameters to initialize all fields.
     */
    public GameBoard(int numRows, int numCols, List<List<Cell>> grid, List<Gems> gems, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.grid = Objects.requireNonNull(grid, "grid must not be null");
        this.gems = Objects.requireNonNull(gems, "gems must not be null");
        this.player = Objects.requireNonNull(player, "player must not be null");
    }

    /**
     * Checks if all gems are reachable from the player's position.
     * Assumes the internal grid and gem positions are initialized.
     */
    public boolean isAllGemsReachable() {
        if (grid == null || gems == null || player == null) {
            throw new NullPointerException("GameBoard not properly initialized");
        }
        // Implement BFS or DFS to verify reachability of all gems
        // For example:
        // 1. Find player's current position
        // 2. Traverse accessible cells
        // 3. Verify each gem's position is reachable
        // Placeholder implementation:
        return true; // Replace with actual reachability logic
    }

    // Additional methods, getters, and setters as needed

    /**
     * Retrieves the cell at specified position.
     */
    public Cell getCell(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Invalid cell position");
        }
        return grid.get(row).get(col);
    }

    /**
     * Retrieves the number of columns.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Retrieves the number of rows.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Retrieves the list of gems.
     */
    public List<Gems> getGems() {
        return gems;
    }

    /**
     * Retrieves the player.
     */
    public Player getPlayer() {
        return player;
    }

    // Inner classes for Cell, Gems, Player, etc., if needed
    public static class Cell {
        private final int row;
        private final int col;
        // Additional properties like walls, entities, etc.

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public static class Gems {
        private final int row;
        private final int col;

        public Gems(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public static class Player {
        private int score;
        private int lives;

        public Player() {
            this.score = 0;
            this.lives = 3; // default lives
        }

        public int getScore() {
            return score;
        }

        public void addScore(int points) {
            this.score += points;
        }

        public int getLives() {
            return lives;
        }

        public void decrementLives() {
            if (lives > 0) {
                lives--;
            }
        }

        public void incrementLives() {
            lives++;
        }
    }
}