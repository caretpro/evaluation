
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private final Stack<Transition> history = new Stack<>();
    private final Map<Position, Entity> entities;
    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private int undoQuota;
    private Transition currentTransition = new Transition();

    /**
     * Create a running game state from a game map.
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.entities = new HashMap<>(map.getEntities());
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoQuota().orElse(-1);
    }

    // ... [rest of the class implementation] ...
}