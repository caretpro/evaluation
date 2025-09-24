
package assignment.entities;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * A Sokoban player entity, identified by an integer id (0..25 mapping to 'A'..'Z').
 */
public class Player implements Entity {
    private final int playerId;

    /**
     * Create a Player for the given id.
     * @param playerId 0-based id of this player (0 == 'A', 1 == 'B', …, up to 25 == 'Z').
     */
    public Player(int playerId) {
        if (playerId < 0 || playerId >= 26) {
            throw new IllegalArgumentException("playerId must be between 0 and 25");
        }
        this.playerId = playerId;
    }

    /**
     * @return the 0-based player id.
     */
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public @NotNull String toString() {
        return Character.toString((char) ('A' + playerId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return playerId == player.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}