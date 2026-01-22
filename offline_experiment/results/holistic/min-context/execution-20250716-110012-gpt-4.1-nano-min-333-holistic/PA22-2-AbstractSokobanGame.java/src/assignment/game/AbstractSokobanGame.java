
package assignment.game;

import assignment.entities.Entity;
import assignment.entities.Box;
import assignment.entities.Target;
import assignment.entities.Player;
import java.util.List;

/**
 * Represents the state of the game, including the grid, player positions, and history.
 */
public class GameState {
    private final List<List<Entity>> grid;
    private final List<Position> playerPositions;
    private int undoCount;

    public GameState(List<List<Entity>> grid, List<Position> playerPositions) {
        this.grid = grid;
        this.playerPositions = playerPositions;
        this.undoCount = 0;
    }

    /**
     * Checks if all boxes are on target positions.
     * @return true if all boxes are on targets, false otherwise.
     */
    public boolean allBoxesOnTargets() {
        for (List<Entity> row : grid) {
            for (Entity entity : row) {
                if (entity instanceof Box) {
                    // Check if the box is on a target
                    boolean onTarget = false;
                    for (Entity e : row) {
                        if (e instanceof Target && e.equals(entity)) {
                            onTarget = true;
                            break;
                        }
                    }
                    if (!onTarget) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the current undo count.
     * @return the number of remaining undo actions.
     */
    public int getUndoCount() {
        return undoCount;
    }

    /**
     * Finds the position of the player with the given ID.
     * @param playerId The ID of the player.
     * @return The Position of the player or null if not found.
     */
    public Position findPlayerPosition(int playerId) {
        for (Position pos : playerPositions) {
            Entity entity = getEntity(pos);
            if (entity instanceof Player && ((Player) entity).getId() == playerId) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Placeholder for getting an entity at a position.
     */
    public Entity getEntity(Position position) {
        // Implementation depends on your grid structure
        // For example:
        // return grid.get(position.getRow()).get(position.getCol());
        return null; // Placeholder
    }

    /**
     * Placeholder for moving an entity from one position to another.
     */
    public void move(Position from, Position to) {
        // Implementation to update grid and positions
    }

    /**
     * Placeholder for undo operation.
     */
    public void undo() {
        // Implementation for undo
        if (undoCount > 0) {
            undoCount--;
        }
    }

    /**
     * Placeholder for checkpoint after moving a box.
     */
    public void checkpoint() {
        // Implementation for saving state
    }
}