
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
 * 
 * <p>GameState consists of things changing as the game goes, such as:
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
     * Apply transition on current entity map.
     * History is not touched in this method.
     * Callers should maintain history themselves.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
            .map(e -> {
                Entity old = this.entities.remove(e.getKey());
                this.entities.put(e.getKey(), new Empty());
                return Map.entry(e.getValue(), old);
            })
            .toList()
            .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
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
        private final Map<Position, Position> moves = new HashMap<>();

        private Transition() {}

        private Transition(Map<Position, Position> moves) {
            this.moves.putAll(moves);
        }

        private void add(Position from, Position to) {
            Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(from);
            this.moves.put(key, to);
        }

        private Transition reverse() {
            Map<Position, Position> reversed = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(reversed);
        }

        @Override
        public String toString() {
            return this.moves.entrySet().stream()
                .map(e -> String.format("(%d,%d)->(%d,%d)",
                    e.getKey().x(), e.getKey().y(),
                    e.getValue().x(), e.getValue().y()))
                .collect(Collectors.joining(","));
        }
    }

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = map.getDestinations();
        this.undoQuota = map.getUndoLimit().orElse(-1);
        this.entities = new HashMap<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = Position.of(x, y);
                Entity e = map.getEntity(p);
                this.entities.put(p, e != null ? e : new Empty());
            }
        }
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     * @throws IllegalArgumentException if no player with the given id is present.
     */
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player)
            .filter(e -> ((Player) e.getValue()).getId() == id)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No player with id " + id));
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    public @Unmodifiable Set<Position> getAllPlayerPositions() {
        return entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player)
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public Entity getEntity(Position position) {
        Objects.requireNonNull(position, "position must not be null");
        Entity entity = entities.get(position);
        if (entity == null) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return entity;
    }

    /**
     * Get all box destination positions as a set in the game map. This should
     * be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo
     * actions that can be performed from now on. If undo is unlimited,
     *
     * @return the undo quota left if the game has a limit; empty if unlimited.
     */
    public Optional<Integer> getUndoQuota() {
        return undoQuota >= 0 ? Optional.of(undoQuota) : Optional.empty();
    }

    /**
     * Check whether the game wins or not. The game wins only when all box
     * destinations have been occupied by boxes.
     *
     * @return true if the game wins.
     */
    public boolean isWin() {
        return destinations.stream()
            .allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another. This method assumes the
     * validity of this move is ensured.
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        applyTransition(currentTransition);
        history.push(currentTransition);
        currentTransition = new Transition();
    }

    /**
     * Record a checkpoint of the game state.
     */
    public void checkpoint() {
        history.push(new Transition(currentTransition.moves));
        currentTransition = new Transition();
    }

    /**
     * Undo the last move or checkpoint.
     */
    public void undo() {
        if (undoQuota >= 0) {
            undoQuota--;
        }
        if (!history.isEmpty()) {
            Transition last = history.pop();
            applyTransition(last.reverse());
        }
    }

    /**
     * Get the maximum width of the game map. This should be the same as that
     * in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }
}