
package assignment.entities;

/**
 * Represents a box entity in the Sokoban game.
 */
public class Box extends Entity {
    private Integer playerId; // Nullable, indicates associated player if any

    /**
     * Default constructor.
     */
    public Box() {
        super();
        this.playerId = null;
    }

    /**
     * Sets the player ID associated with this box.
     *
     * @param playerId the player ID to set
     */
    public void setPlayerId(@Nullable Integer playerId) {
        this.playerId = playerId;
    }

    /**
     * Gets the player ID associated with this box.
     *
     * @return the player ID, or null if none
     */
    @Nullable
    public Integer getPlayerId() {
        return playerId;
    }
}