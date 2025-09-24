
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

/**
 * The state of the Sokoban Game.
 * Each game state represents an ongoing game.
 * As the game goes, the game state changes while players move
 * while the original game map stays unmodified.
 * <b>The game state does not modify the original game map.</b>
 *
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
public class GameState {

    private final Stack<Checkpoint> history = new Stack<>();
    private final Map<Position, Entity> entities;
    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private int undoQuota;
    private Transition currentTransition = new Transition();

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        // copy board dimensions and destinations
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        // initialize undo quota
        this.undoQuota = map.getUndoLimit().orElse(-1);
        // deep-copy the map's entities
        this.entities = new HashMap<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = Position.of(x, y);
                Entity e = map.getEntity(p);
                this.entities.put(p, e != null ? e : new Empty());
            }
        }
        // push initial checkpoint for perfect undo to the very start
        history.push(new Checkpoint(new Transition(), this.undoQuota, copyEntities()));
    }

    private Map<Position, Entity> copyEntities() {
        // shallow clones the Entities map (Empty/Box/Player are immutable for our purposes)
        return new HashMap<>(entities);
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such player: " + id));
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    public Set<Position> getAllPlayerPositions() {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public Entity getEntity(Position position) {
        return entities.get(position);
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    @Unmodifiable
    @NotNull
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that
     * can be performed from now on.
     * If undo is unlimited, returns Optional.empty().
     *
     * @return the undo quota left if limited, otherwise empty.
     */
    public Optional<Integer> getUndoQuota() {
        return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true if the game wins.
     */
    public boolean isWin() {
        return destinations.stream()
                .allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = entities.get(from);
        entities.put(from, new Empty());
        entities.put(to, e);
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     *
     * Checkpoint is used in {@link #undo()}.
     * Every undo action reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        history.push(new Checkpoint(currentTransition, undoQuota, copyEntities()));
        currentTransition = new Transition();
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases it by one.
     * If there is no checkpoint recorded besides the initial one,
     * revert to the initial game state.
     */
    public void undo() {
        if (undoQuota == 0) {
            throw new IllegalStateException("No undo remaining");
        }
        // pop the last checkpoint (always at least the initial one)
        Checkpoint cp = history.pop();
        // restore entities and undoQuota exactly as saved
        this.entities.clear();
        this.entities.putAll(cp.entitiesSnapshot);
        this.undoQuota = cp.savedQuota;
        // consume one undo if limited
        if (undoQuota > 0) {
            undoQuota--;
        } else if (undoQuota == 0) {
            // exactly hits zero -- further undo disallowed
        }
        // reset currentTransition for next sequence
        currentTransition = new Transition();
        // ensure that initial checkpoint stays (so history never truly empties)
        if (history.isEmpty()) {
            history.push(new Checkpoint(new Transition(), this.undoQuota, copyEntities()));
        }
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return boardHeight;
    }

    private static class Transition {
        private final Map<Position, Position> moves = new HashMap<>();

        private void add(Position from, Position to) {
            // if a chain already moved TO 'from', retarget its original source
            Position sourceKey = moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(from);
            moves.put(sourceKey, to);
        }

        private Transition reverse() {
            Transition rev = new Transition();
            moves.forEach((k, v) -> rev.moves.put(v, k));
            return rev;
        }
    }

    private static class Checkpoint {
        final Transition transition;
        final int savedQuota;
        final Map<Position, Entity> entitiesSnapshot;

        Checkpoint(Transition t, int quota, Map<Position, Entity> snap) {
            this.transition = t;
            this.savedQuota = quota;
            this.entitiesSnapshot = snap;
        }
    }
}