
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;
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
        this.boardWidth    = map.getMaxWidth();
        this.boardHeight   = map.getMaxHeight();
        this.destinations  = Set.copyOf(map.getDestinations());
        this.undoQuota     = map.getUndoLimit().orElse(-1);
        this.entities      = new HashMap<>();

        /* initialize from GameMap.positions() and entityAt() */
        for (Position pos : map.positions()) {
            this.entities.put(pos, map.entityAt(pos));
        }
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public Position getPlayerPositionById(int id) {
        return this.entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No player with id " + id));
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    @Unmodifiable
    public Set<Position> getAllPlayerPositions() {
        return Collections.unmodifiableSet(
                this.entities.entrySet().stream()
                        .filter(e -> e.getValue() instanceof Player)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    @NotNull
    public Entity getEntity(Position position) {
        return Objects.requireNonNull(this.entities.get(position));
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    @Unmodifiable
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(this.destinations);
    }

    /**
     * Get the undo quota currently left.
     *
     * @return the undo quota left if limited; {@link Optional#empty()} if unlimited.
     */
    public Optional<Integer> getUndoQuota() {
        return this.undoQuota < 0 ? Optional.empty() : Optional.of(this.undoQuota);
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true if the game is won.
     */
    public boolean isWin() {
        return this.destinations.stream()
                .allMatch(dest -> this.entities.get(dest) instanceof Box);
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move has been ensured.
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = this.entities.get(from);
        this.entities.put(from, new Empty());
        this.entities.put(to, e);
    }

    /**
     * Record a checkpoint of the game state (for undo).
     */
    public void checkpoint() {
        history.push(this.currentTransition);
        this.currentTransition = new Transition();
    }

    /**
     * Apply transition on current entity map without touching history.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        for (Map.Entry<Position, Position> mv : transition.moves.entrySet()) {
            Position src = mv.getKey();
            Position dst = mv.getValue();
            Entity ent = this.entities.get(src);
            this.entities.put(src, new Empty());
            this.entities.put(dst, ent);
        }
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * Decreases undo quota by one (unless unlimited).
     */
    public void undo() {
        if (this.undoQuota == 0) {
            throw new IllegalStateException("No undo quota left");
        }
        this.undoQuota = Math.max(-1, this.undoQuota - 1);
        Transition last = history.isEmpty() ? new Transition() : history.pop();
        applyTransition(last.reverse());
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return this.boardWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return this.boardHeight;
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
            Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(from);
            this.moves.put(key, to);
        }

        private Transition reverse() {
            Map<Position, Position> rev = this.moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(rev);
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
}