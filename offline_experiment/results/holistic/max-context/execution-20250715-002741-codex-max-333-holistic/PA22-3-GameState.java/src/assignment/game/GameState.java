
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
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.entities = new HashMap<>();

        // Clone the immutable GameMap entities into our mutable state
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = Position.of(x, y);
                Entity e = map.getEntity(p);
                if (e != null) {
                    this.entities.put(p, e);
                }
            }
        }

        // Capture undo limit (empty = unlimited => -1)
        this.undoQuota = map.getUndoLimit().orElse(-1);
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player, or null if not found.
     */
    @Nullable
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
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
    @Nullable
    public Entity getEntity(Position position) {
        return entities.get(position);
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
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    public Optional<Integer> getUndoQuota() {
        return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true if the game is won.
     */
    public boolean isWin() {
        return destinations.stream()
                .allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     * <b>The validity of the move of the entity in one position to another need not to check.</b>
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, e);
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     * <p>
     * Checkpoint is used in {@link GameState#undo()}.
     * Every undo action reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        history.push(currentTransition);
        currentTransition = new Transition();
    }

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
                    Entity entity = entities.remove(e.getKey());
                    entities.put(e.getKey(), new Empty());
                    return Map.entry(e.getValue(), entity);
                })
                .toList()
                .forEach(e -> entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases the undo quota by one.
     * <p>
     * If there is no checkpoint recorded (i.e., before moving any box when the game starts),
     * or if there are moves in the current transition but no prior checkpoint,
     * revert only those uncheckpointed moves, without consuming quota when there's nothing to undo.
     */
    public void undo() {
        // If nothing has ever been moved (no checkpoint & no current moves), do nothing
        if (history.isEmpty() && currentTransition.moves.isEmpty()) {
            return;
        }
        // Now we know we have something to undo—consume quota if limited
        if (undoQuota > 0) {
            undoQuota--;
        } else if (undoQuota == 0) {
            throw new IllegalStateException("No undo quota left");
        }
        // Choose the transition to undo (either last checkpoint or current uncheckpointed moves)
        Transition toUndo = history.isEmpty()
                ? currentTransition.reverse()
                : history.pop().reverse();
        applyTransition(toUndo);
    }

    /**
     * Get the maximum width of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
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

        private Transition() {
            this.moves = new HashMap<>();
        }

        private Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        private void add(Position from, Position to) {
            Position key = moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(from);
            moves.put(key, to);
        }

        private Transition reverse() {
            Map<Position, Position> rev = moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(rev);
        }

        @Override
        public String toString() {
            List<String> out = moves.entrySet().stream()
                    .map(e -> String.format("(%d,%d)->(%d,%d)",
                            e.getKey().x(), e.getKey().y(),
                            e.getValue().x(), e.getValue().y()))
                    .toList();
            return String.join(",", out);
        }
    }
}