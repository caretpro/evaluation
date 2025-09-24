
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        this.boardWidth   = map.getWidth();
        this.boardHeight  = map.getHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota    = map.getUndoLimit().orElse(-1);
        this.entities     = new HashMap<>();

        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position pos = new Position(x, y);
                Entity e = map.getEntity(pos);
                this.entities.put(pos, e != null ? e : new Empty());
            }
        }
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
                .orElseThrow(() -> new NoSuchElementException("Player id not found: " + id));
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    @Unmodifiable
    @NotNull
    public Set<Position> getAllPlayerPositions() {
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
    @NotNull
    public Entity getEntity(@NotNull Position position) {
        return entities.getOrDefault(position, new Empty());
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
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    @NotNull
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
    public void move(@NotNull Position from, @NotNull Position to) {
        currentTransition.add(from, to);
        applyTransition(currentTransition);
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
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(@NotNull Transition transition) {
        // For each move: clear source, then place entity at target.
        transition.moves.entrySet().stream()
                .map(e -> {
                    Entity moved = entities.remove(e.getKey());
                    entities.put(e.getKey(), new Empty());
                    return Map.entry(e.getValue(), moved);
                })
                .forEach(e -> entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases the undo quota by one.
     * If there is no checkpoint recorded, revert to the initial transition only.
     */
    public void undo() {
        if (undoQuota == 0) {
            throw new IllegalStateException("No undo left");
        }
        Transition toUndo = history.isEmpty()
                ? currentTransition.reverse()
                : history.pop().reverse();
        applyTransition(toUndo);
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

    private static class Transition {
        private final Map<Position, Position> moves;

        private Transition() {
            this.moves = new HashMap<>();
        }

        private Transition(@NotNull Map<Position, Position> moves) {
            this.moves = new HashMap<>(moves);
        }

        /** Record a move (possibly merging chained pushes). */
        private void add(@NotNull Position from, @NotNull Position to) {
            Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(from);
            this.moves.put(key, to);
        }

        /** Reverse all recorded moves. */
        private Transition reverse() {
            Map<Position, Position> rev = moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(rev);
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
}