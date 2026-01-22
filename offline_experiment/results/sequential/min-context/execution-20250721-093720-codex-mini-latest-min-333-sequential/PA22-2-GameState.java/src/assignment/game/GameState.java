
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;

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

    private final Stack<TransitionRecord> history = new Stack<>();
    private final Map<Position, Entity> entities;
    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private int undoQuota;
    private Transition currentTransition = new Transition();

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    /**
     * Apply a recorded transition to the current entity map.
     * History is not touched in this method.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
            .map(e -> {
                Entity eOld = this.entities.remove(e.getKey());
                this.entities.put(e.getKey(), new Empty());
                return Map.entry(e.getValue(), eOld);
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

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.undoQuota = map.getUndoLimit();
        this.entities = new HashMap<>();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                Position p = new Position(x, y);
                this.entities.put(p, map.getEntity(p));
            }
        }
        this.destinations = new HashSet<>(map.getDestinations());
    }

    /**
     * Undo the last checkpointed move (if any) and restore the undo quota.
     *
     * @return true if an undo was performed; false if no further undo is possible.
     */
    public boolean undo() {
        if (history.isEmpty() || undoQuota <= 0) {
            return false;
        }
        TransitionRecord tr = history.pop();
        applyTransition(tr.transitionToUndo);
        this.undoQuota = tr.undoQuotaBefore;
        return true;
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
            List<String> moves = this.moves.entrySet().stream()
                .map(e -> String.format("(%d,%d)->(%d,%d)",
                        e.getKey().x(), e.getKey().y(),
                        e.getValue().x(), e.getValue().y()))
                .toList();
            return String.join(",", moves);
        }
    }

    private static class TransitionRecord {
        private final int undoQuotaBefore;
        private final Transition transitionToUndo;

        TransitionRecord(int undoQuotaBefore, Transition transitionToUndo) {
            this.undoQuotaBefore = undoQuotaBefore;
            this.transitionToUndo = transitionToUndo;
        }
    }

    /**
     * Record a checkpoint of the game state (positions of entities + undo quota).
     * This is used by {@link #undo()} to revert to the last checkpoint.
     */
    public void checkpoint() {
        history.push(new TransitionRecord(undoQuota, currentTransition.reverse()));
        currentTransition = new Transition();
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     * @throws NoSuchElementException if no player with the given id exists.
     */
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player p && p.getId() == id)
            .map(Map.Entry::getKey)
            .findAny()
            .orElseThrow(() -> new NoSuchElementException("No player with id " + id));
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
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    @NotNull
    public Entity getEntity(Position position) {
        Entity e = entities.get(position);
        if (e == null) {
            throw new NoSuchElementException("Position out of bounds: " + position);
        }
        return e;
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions remaining.
     * If undo is unlimited, returns {@link Optional#empty()}.
     *
     * @return Optional undo quota.
     */
    public Optional<Integer> getUndoQuota() {
        return undoQuota > 0 ? Optional.of(undoQuota) : Optional.empty();
    }

    /**
     * Check whether the game is won: all box destinations are occupied by boxes.
     *
     * @return true if the game is won.
     */
    public boolean isWin() {
        return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another. This method assumes the move is valid.
     *
     * @param from the current position of the entity to move.
     * @param to   the target position.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        Entity e = entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, e);
    }

    /**
     * Get the maximum width of the game map. This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }
}