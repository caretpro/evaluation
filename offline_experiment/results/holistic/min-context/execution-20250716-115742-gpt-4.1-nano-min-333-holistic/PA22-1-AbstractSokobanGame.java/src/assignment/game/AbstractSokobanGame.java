
package assignment.game;

import assignment.entities.Entity;
import assignment.entities.Box;
import assignment.entities.Player;
import java.util.Stack;

/**
 * Represents the state of the game, including the grid and history for undo functionality.
 */
public class GameState {
    private final Entity[][] grid;
    private final Stack<Entity[][]> history;
    private final int width;
    private final int height;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Entity[height][width];
        this.history = new Stack<>();
        initializeGrid();
    }

    private void initializeGrid() {
        // Initialize grid with Empty entities or as per game setup
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new assignment.entities.Empty();
            }
        }
        // Additional setup can be added here
    }

    public boolean allBoxesOnTarget() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity entity = grid[y][x];
                if (entity instanceof Box) {
                    // Check if the box is on a target position
                    // Assuming that a box on a target is represented differently,
                    // or that the game logic handles this check elsewhere.
                    // For now, just check if the box is on a target position if such info exists.
                    // If no such info, this method may need to be adjusted accordingly.
                    // Placeholder: assume boxes are always on target if no specific check is needed.
                }
            }
        }
        // Implement actual check based on your game logic
        return true; // Placeholder
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void undo() {
        if (canUndo()) {
            Entity[][] previousState = history.pop();
            for (int y = 0; y < height; y++) {
                System.arraycopy(previousState[y], 0, grid[y], 0, width);
            }
        }
    }

    public Position findPlayerPosition(int playerId) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity entity = grid[y][x];
                if (entity instanceof assignment.entities.Player player && player.getId() == playerId) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    public Entity getEntity(Position position) {
        if (position.x() < 0 || position.x() >= width || position.y() < 0 || position.y() >= height) {
            return null;
        }
        return grid[position.y()][position.x()];
    }

    public void move(Position from, Position to) {
        Entity entity = getEntity(from);
        if (entity != null) {
            saveStateForUndo();
            grid[to.y()][to.x()] = entity;
            grid[from.y()][from.x()] = new assignment.entities.Empty();
        }
    }

    private void saveStateForUndo() {
        Entity[][] currentState = new Entity[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(grid[y], 0, currentState[y], 0, width);
        }
        history.push(currentState);
    }
}