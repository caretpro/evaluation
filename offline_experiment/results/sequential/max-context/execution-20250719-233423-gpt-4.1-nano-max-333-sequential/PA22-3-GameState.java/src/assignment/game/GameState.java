
package assignment.game;

import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the state of the Sokoban game.
 */
public class GameState {
    private final Map<Position, Entity> entities;
    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private int undoQuota;
    private final Stack<Transition> history = new Stack<>();
    private Transition currentTransition = new Transition();

    /**
     * Constructor to create a GameState from a GameMap.
     *
     * @param map the initial game map.
     */
    public GameState(@NotNull GameMap map) {
        this.entities = new HashMap<>(map.map);
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit().orElse(-1);
        this.history.clear();
        this.currentTransition = new Transition();
    }

    // Existing methods...
    // (Include other methods as needed, such as move, undo, checkpoint, etc.)
}