
package assignment.entities;

import assignment.game.Position;

/**
 * Represents a box entity in the game.
 */
public class Box extends Entity {
    private final int playerId;
    private Position position;
    private final boolean onTarget;

    public Box(int playerId, Position position, boolean onTarget) {
        this.playerId = playerId;
        this.position = position;
        this.onTarget = onTarget;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Checks if the box is currently on a target position.
     * @return true if on target, false otherwise.
     */
    public boolean isOnTarget() {
        return onTarget;
    }
}