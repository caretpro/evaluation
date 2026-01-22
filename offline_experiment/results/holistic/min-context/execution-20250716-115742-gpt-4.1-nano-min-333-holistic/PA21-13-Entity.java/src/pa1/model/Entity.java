
package pa1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the game board containing cells, gems, entities, and game state.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] grid;
    private final List<Gem> gems;
    private final Player player;
    private int score;
    private int numLives;
    private int numDeaths;

    /**
     * Creates a default game board with specified dimensions.
     * Initializes grid, gems, player, and other fields to default non-null values.
     */
    public GameBoard(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive");
        }
        this.numRows = rows;
        this.numCols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.grid[r][c] = new Cell(r, c);
            }
        }
        this.gems = new ArrayList<>();
        this.player = new Player();
        this.score = 0;
        this.numLives = 3; // default lives
        this.numDeaths = 0;
    }

    /**
     * Creates a game board with specified parameters, ensuring all internal structures are initialized.
     */
    public GameBoard(int rows, int cols, List<Gem> gems, Player player, int score, int lives, int deaths) {
        if (rows <= 0 || cols <= 0 || gems == null || player == null) {
            throw new IllegalArgumentException("Invalid arguments for GameBoard");
        }
        this.numRows = rows;
        this.numCols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.grid[r][c] = new Cell(r, c);
            }
        }
        this.gems = new ArrayList<>(gems);
        this.player = player;
        this.score = score;
        this.numLives = lives;
        this.numDeaths = deaths;
    }

    /**
     * Checks if all gems are reachable from the player's current position.
     * Uses BFS or DFS to verify reachability.
     */
    public boolean isAllGemsReachable() {
        // Ensure internal data structures are initialized
        Objects.requireNonNull(grid, "Grid must be initialized");
        Objects.requireNonNull(gems, "Gems list must be initialized");
        Objects.requireNonNull(player, "Player must be initialized");

        // For each gem, check if reachable from player's position
        for (Gem gem : gems) {
            if (!isReachable(player.getCell(), gem.getCell())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to determine if target cell is reachable from start cell.
     */
    private boolean isReachable(Cell start, Cell target) {
        if (start == null || target == null) {
            return false;
        }
        boolean[][] visited = new boolean[numRows][numCols];
        List<Cell> toVisit = new ArrayList<>();
        toVisit.add(start);
        visited[start.getRow()][start.getCol()] = true;

        while (!toVisit.isEmpty()) {
            Cell current = toVisit.remove(0);
            if (current.equals(target)) {
                return true;
            }
            for (Cell neighbor : getNeighbors(current)) {
                if (!visited[neighbor.getRow()][neighbor.getCol()] && !neighbor.isWall()) {
                    visited[neighbor.getRow()][neighbor.getCol()] = true;
                    toVisit.add(neighbor);
                }
            }
        }
        return false;
    }

    /**
     * Returns neighboring cells (up, down, left, right) within bounds and not walls.
     */
    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();

        if (r > 0) neighbors.add(grid[r - 1][c]);
        if (r < numRows - 1) neighbors.add(grid[r + 1][c]);
        if (c > 0) neighbors.add(grid[r][c - 1]);
        if (c < numCols - 1) neighbors.add(grid[r][c +]);

        return neighbors;
    }

    // Getters for rows, cols, grid, gems, player, score, lives, deaths
    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Invalid cell position");
        }
        return grid[row][col];
    }

    public List<Gem> getGems() {
        return new ArrayList<>(gems);
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public int getNumLives() {
        return numLives;
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    // Additional methods for modifying game state (increment/decrement lives, score, etc.) can be added here
}