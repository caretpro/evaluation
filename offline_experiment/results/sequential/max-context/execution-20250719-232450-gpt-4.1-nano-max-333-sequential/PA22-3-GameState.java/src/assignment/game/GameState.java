
package assignment.game;

import assignment.entities.Entity;
import assignment.game.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents an ongoing game state in Sokoban.
 */
public class GameState {

    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private final Map<Position, Entity> entities;
    private int undoQuota;
    private final Stack<Transition> history = new Stack<>();
    private Transition currentTransition = new Transition();

    // Existing methods...

    /**
     * Constructor that initializes GameState from a given GameMap.
     *
     * @param map the GameMap to initialize the game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : map.map.entrySet()) {
            this.entities.put(entry.getKey(), entry.getValue());
        }
        this.history.clear();
        this.currentTransition = new Transition();
    }

    // Other methods as needed...
}