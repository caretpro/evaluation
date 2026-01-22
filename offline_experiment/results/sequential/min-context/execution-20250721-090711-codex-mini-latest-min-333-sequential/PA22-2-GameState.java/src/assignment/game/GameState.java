
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The state of the Sokoban Game.
 * Each game state represents an ongoing game.
 * As the game goes, the game state changes while players are moving while the original game map stays unmodified.
 * <b>The game state should not modify the original game map.</b>
 *
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
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
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.undoQuota = map.getUndoQuota().orElse(-1);
        this.destinations = new HashSet<>(map.getDestinations());
        this.entities = new HashMap<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = new Position(x, y);
                Entity e = map.getEntity(p);
                if (!(e instanceof Empty)) {
                    entities.put(p, e);
                }
            }
        }
    }

    /**
     * Get the maximum width of the game map.
     * This should be the same as that in {@link GameMap}.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap}.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return boardHeight;
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap}.
     *
     * @return an unmodifiable set of positions.
     */
    @Unmodifiable
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    private static class Transition {
        private final Map<Position, Position> moves = new HashMap<>();

        private void add(Position from, Position to) {
            Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(from);
            this.moves.put(key, to);
        }

        private Transition reverse() {
            Map<Position, Position> rev = this.moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue,
                                              Map.Entry::getKey));
            Transition t = new Transition();
            t.moves.putAll(rev);
            return t;
        }

        private Transition mergeInto(Transition other) {
            Transition merged = new Transition();
            merged.moves.putAll(other.moves);
            merged.moves.putAll(this.moves);
            return merged;
        }
    }

    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
            .map(e -> {
                Entity entity = this.entities.remove(e.getKey());
                this.entities.put(e.getKey(), new Empty());
                return Map.entry(e.getValue(), entity);
            })
            .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    public Optional<Integer> getUndoQuota() {
        return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
    }

    public Entity getEntity(Position position) {
        return entities.getOrDefault(position, new Empty());
    }

    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No player with id " + id));
    }

    @Unmodifiable
    public Set<Position> getAllPlayerPositions() {
        return Collections.unmodifiableSet(
                entities.entrySet().stream()
                        .filter(e -> e.getValue() instanceof Player)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet()));
    }

    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, e);
        if (e instanceof Box) {
            history.push(currentTransition);
            currentTransition = new Transition();
        }
    }

    public void checkpoint() {
        Map<Position, Entity> snapshot = new HashMap<>(entities);
        history.push(new Checkpoint(snapshot, undoQuota));
    }

    public void undo() {
        undoQuota--;
        Transition merged = new Transition();
        while (!history.isEmpty()) {
            Transition top = history.pop();
            if (top instanceof Checkpoint cp) {
                this.entities.clear();
                this.entities.putAll(cp.snapshot);
                this.undoQuota = cp.quota;
                return;
            } else {
                merged = top.reverse().mergeInto(merged);
            }
        }
        applyTransition(merged);
    }

    public boolean isWin() {
        return destinations.stream().allMatch(pos -> getEntity(pos) instanceof Box);
    }

    private static class Checkpoint extends Transition {
        private final Map<Position, Entity> snapshot;
        private final int quota;

        Checkpoint(Map<Position, Entity> snapshot, int quota) {
            super();
            this.snapshot = snapshot;
            this.quota = quota;
        }
    }
}