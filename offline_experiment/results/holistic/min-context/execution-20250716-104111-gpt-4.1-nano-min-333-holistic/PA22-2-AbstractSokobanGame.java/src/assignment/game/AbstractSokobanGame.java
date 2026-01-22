
package assignment.game;

import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Box;
import assignment.entities.Empty;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents the state of the game, including the grid and undo history.
 */
public class GameState {
    private final Entity[][] grid;
    private final List<Entity[][]> history;
    private int undoCount;

    public GameState(int rows, int cols) {
        this.grid = new Entity[rows][cols];
        this.history = new ArrayList<>();
        this.undoCount = 3; // example initial undo quota
        // Initialize grid with Empty or other entities as needed
    }

    /**
     * Checks if all boxes are on target positions.
     * @return true if all boxes are on targets, false otherwise.
     */
    public boolean allBoxesOnTargets() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Entity entity = grid[i][j];
                if (entity instanceof Box) {
                    // Check if the box is on a target
                    // Assuming Box has a method isOnTarget()
                    if (!((Box) entity).isOnTarget()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the number of remaining undo actions.
     * @return undo count.
     */
    public int getUndoCount() {
        return undoCount;
    }

    /**
     * Finds the position of the player with the given ID.
     * @param playerId The ID of the player.
     * @return Position of the player or null if not found.
     */
    public Position findPlayerPosition(int playerId) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Entity entity = grid[i][j];
                if (entity instanceof Player player && player.getId() == playerId) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    // Additional methods like move(), undo(), checkpoint(), getEntity(), etc., should be implemented here.
}