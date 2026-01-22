
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
 * As the game goes, the game state changes while players are moving while the original game map stays unmodified.
 * <b>The game state should not modify the original game map.</b>
 * <p>
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
public class GameState {

    private final Deque<Checkpoint> history = new ArrayDeque<>();
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
        // copy bounds & destinations & quota
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit().orElse(-1);

        // deep‐copy map entities so we never share mutable Boxes/Players
        this.entities = new HashMap<>();
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = Position.of(x, y);
                Entity e = map.getEntity(p);
                if (e instanceof Box) {
                    entities.put(p, new Box(((Box) e).getPlayerId()));
                } else if (e instanceof Player) {
                    entities.put(p, new Player(((Player) e).getId()));
                } else {
                    // share Empty or Wall safely
                    entities.put(p, e);
                }
            }
        }
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return Optional of position of the player.
     */
    public Optional<Position> getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst();
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
     * Returns Empty if out of bounds or nothing present.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public Entity getEntity(Position position) {
        return entities.getOrDefault(position, new Empty());
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    @NotNull
    @Unmodifiable
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     *
     * @return the undo quota left if limited; empty if unlimited.
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
        return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another.
     * Caller ensures validity.
     *
     * @param from current location.
     * @param to   target location.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity ent = entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, ent);
    }

    /**
     * Record a checkpoint of the game state, including current transition and quota.
     * Every undo will revert to the last checkpoint.
     */
    public void checkpoint() {
        history.push(new Checkpoint(currentTransition, undoQuota));
        currentTransition = new Transition();
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * Assumes undoQuota != 0; decreases quota if limited.
     * If no checkpoint recorded and no moves, does nothing.
     */
    public void undo() {
        if (undoQuota == 0) {
            throw new IllegalStateException("No undos remaining");
        }
        // nothing moved since start?
        if (history.isEmpty() && currentTransition.isEmpty()) {
            return;
        }
        Checkpoint cp = history.isEmpty()
                ? new Checkpoint(currentTransition, undoQuota)
                : history.pop();
        applyTransition(cp.transition.reverse());
        this.undoQuota = cp.prevQuota;
        if (undoQuota > 0) {
            undoQuota--;
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

    // apply a batch of moves
    private void applyTransition(Transition t) {
        t.moves.entrySet().forEach(e -> {
            Position from = e.getKey(), to = e.getValue();
            Entity ent = entities.remove(from);
            entities.put(from, new Empty());
            entities.put(to, ent);
        });
    }

    /** batch of moves */
    private static class Transition {
        private final Map<Position, Position> moves = new LinkedHashMap<>();

        private void add(Position from, Position to) {
            // chain multi‐step moves
            Position start = moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            moves.put(start, to);
        }

        private Transition reverse() {
            Transition r = new Transition();
            // preserve insertion order of reversed mapping
            moves.forEach((k, v) -> r.moves.put(v, k));
            return r;
        }

        private boolean isEmpty() {
            return moves.isEmpty();
        }

        @Override
        public String toString() {
            return moves.entrySet().stream()
                    .map(e -> String.format("(%d,%d)->(%d,%d)",
                            e.getKey().x(), e.getKey().y(),
                            e.getValue().x(), e.getValue().y()))
                    .collect(Collectors.joining(","));
        }
    }

    /** snapshot of transition plus quota */
    private static class Checkpoint {
        final Transition transition;
        final int prevQuota;

        Checkpoint(Transition t, int quota) {
            // copy moves to freeze
            this.transition = new Transition();
            t.moves.forEach(this.transition.moves::put);
            this.prevQuota = quota;
        }
    }
}