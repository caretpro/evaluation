
package assignment.entities;

public class Player extends Entity {
    private final int playerId;
    public Player(int playerId) { this.playerId = playerId; }
    public int getPlayerId() { return playerId; }
    // … other Player logic …
}