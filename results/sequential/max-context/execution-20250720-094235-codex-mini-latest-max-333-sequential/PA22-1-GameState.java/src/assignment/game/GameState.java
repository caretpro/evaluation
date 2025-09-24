
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

    private final Stack<Transition> history = new Stack<>();

    private final Map<Position, Entity> entities;

    private final int boardWidth;

    private final int boardHeight;

    private final Set<Position> destinations;

    private int undoQuota;

    private Transition currentTransition = new Transition();

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    public Optional<Integer> getUndoQuota() {
        return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
    }

    /**
     * Apply transition on current entity map.
     * History is not touched in this method.
     * Callers should maintain history themselves.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream().map(e -> {
            final Entity entity = this.entities.remove(e.getKey());
            this.entities.put(e.getKey(), new Empty());
            return Map.entry(e.getValue(), entity);
        }).toList().forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return boardHeight;
    }

    private static class Transition {
        private final Map<Position, Position> moves;

        private void add(Position from, Position to) {
            final Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            this.moves.put(key, to);
        }

        private Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        private Transition() {
            this.moves = new HashMap<>();
        }

        private Transition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

        @Override
        public String toString() {
            final List<String> moves = this.moves.entrySet().stream()
                    .map(e -> String.format("(%d,%d)->(%d,%d)",
                        e.getKey().x(), e.getKey().y(),
                        e.getValue().x(), e.getValue().y()))
                    .toList();
            return String.join(",", moves);
        }
    }

    /**
     * Create a running game state from a game map.
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = map.getDestinations();
        this.undoQuota = map.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = Position.of(x, y);
                Entity e = map.getEntity(p);
                this.entities.put(p, e == null ? new Empty() : e);
            }
        }
    }

    /**
     * Get the current position of the player with the given id.
     * @param id player id.
     * @return the current position of the player.
     * @throws IllegalArgumentException if no player with the given id exists.
     */
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No player with id " + id));
    }

    /**
     * Get current positions of all players in the game map.
     * @return a set of positions of all players.
     */
    public Set<Position> getAllPlayerPositions() {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get the entity that is currently at the given position.
     * @param position the position of the entity.
     * @return the entity object.
     */
    public Entity getEntity(Position position) {
        Entity e = entities.get(position);
        if (e == null) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return e;
    }

    /**
     * Get all box destination positions as a set in the game map. This should be the same as that in {@link GameMap} class.
     * @return a set of positions.
     */
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    @Override
    public boolean isWin() {
        return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another.  This method assumes validity has been checked.
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, e);
        history.push(new Transition(currentTransition.moves));
        currentTransition = new Transition();
    }

    /**
     * Record a checkpoint of the game state, including entity positions and undo quota.
     * Used later by {@link #undo()}.
     */
    public void checkpoint() {
        Map<Position, Entity> snapshot = new HashMap<>();
        for (Map.Entry<Position, Entity> e : this.entities.entrySet()) {
            snapshot.put(e.getKey(), e.getValue());
        }
        history.push(new Checkpoint(snapshot, undoQuota));
    }

    /**
     * Revert the game state to the last checkpoint in history.  Decreases undo quota if limited.
     * If no checkpoint exists, replays or resets without a checkpoint.
     */
    public void undo() {
        if (undoQuota > 0) {
            undoQuota--;
        }
        Checkpoint target = null;
        List<Transition> toRevert = new ArrayList<>();
        while (!history.isEmpty()) {
            Object top = history.pop();
            if (top instanceof Checkpoint cp) {
                target = cp;
                break;
            } else {
                toRevert.add(((Transition) top).reverse());
            }
        }
        if (target == null) {
            toRevert.forEach(this::applyTransition);
            return;
        }
        toRevert.forEach(this::applyTransition);
        this.entities.clear();
        this.entities.putAll(target.entitiesSnapshot);
        this.undoQuota = target.savedUndoQuota;
    }

    /**
     * Get the maximum width of the game map.  Should match {@link GameMap#getMaxWidth()}.
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /** Internal record for checkpoints. */
    private static class Checkpoint {
        private final Map<Position, Entity> entitiesSnapshot;
        private final int savedUndoQuota;
        private Checkpoint(Map<Position, Entity> snapshot, int undoQuota) {
            this.entitiesSnapshot = snapshot;
            this.savedUndoQuota = undoQuota;
        }
    }
}