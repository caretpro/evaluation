
package pa1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the game board with its configuration.
 */
public class GameBoard {

    private final int rows;
    private final int cols;
    private final List<Gem> gems;
    private final Cell[][] grid;
    private final Player player;

    /**
     * Constructor initializing an empty game board with specified dimensions.
     * Ensures all internal structures are non-null to prevent NullPointerException.
     */
    public GameBoard() {
        this.rows = 0;
        this.cols = 0;
        this.gems = new ArrayList<>();
        this.grid = new Cell[0][0];
        this.player = new Player(); // or null if appropriate
        // Additional initialization if needed
    }

    /**
     * Constructor with parameters, ensuring non-null fields.
     */
    public GameBoard(int rows, int cols, List<Gem> gems, Cell[][] grid, Player player) {
        this.rows = rows;
        this.cols = cols;
        this.gems = Objects.requireNonNullElse(gems, new ArrayList<>());
        this.grid = Objects.requireNonNullElse(grid, new Cell[rows][cols]);
        this.player = Objects.requireNonNullElse(player, new Player());
    }

    // Existing methods...

    /**
     * Checks if all gems are reachable.
     * Ensure this method does not throw NullPointerException by verifying internal state.
     */
    public boolean isAllGemsReachable() {
        // Implementation that safely accesses internal fields
        // For example:
        if (gems == null || grid == null) {
            return false; // or throw an exception
        }
        // Proceed with reachability logic
        return true; // placeholder
    }
}