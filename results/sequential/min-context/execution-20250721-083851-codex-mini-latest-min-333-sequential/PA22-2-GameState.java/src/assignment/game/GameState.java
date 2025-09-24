
package assignment.game;

import assignment.actions.Move;
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
     * Apply transition on current entity map.
     * History is not touched in this method.
     * Callers should maintain history themselves.
     *
     * @param transition the transition to apply.
     */
    private void applyTransition(Transition transition) {
        transition.moves.entrySet().stream()
            .map(e -> {
                final Entity entity = this.entities.remove(e.getKey());
                this.entities.put(e.getKey(), new Empty());
                return Map.entry(e.getValue(), entity);
            })
            .toList()
            .forEach(e -> this.entities.put(e.getKey(), e.getValue()));
    }

    /**
     * Create a running game state from a game map.
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit();
        this.entities = new HashMap<>();
        for (Position pos : map.getAllPositions()) {
            this.entities.put(pos, map.getEntity(pos));
        }
        this.history.clear();
        this.currentTransition = new Transition();
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
     * Get the maximum width of the game map. This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return boardWidth;
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
            .filter(e -> e.getValue() instanceof Player player && player.getId() == id)
            .map(Map.Entry::getKey)
            .findAny()
            .orElseThrow(() -> new NoSuchElementException("No player with id " + id + " in current game state"));
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    @NotNull
    @Unmodifiable
    public Set<Position> getAllPlayerPositions() {
        return Collections.unmodifiableSet(
            entities.entrySet().stream()
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
    public Entity getEntity(Position position) {
        return entities.getOrDefault(position, new Empty());
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
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
     * Check whether the game wins or not. The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true if the game is won.
     */
    public boolean isWin() {
        return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
    }

    /**
     * Move the entity from one position to another. This method assumes the validity of this move is ensured.
     * <b>The validity of the move of the entity in one position to another need not be checked.</b>
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        currentTransition.add(from, to);
        applyTransition(currentTransition);
        history.push(new Transition(currentTransition.moves));
        currentTransition = new Transition();
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     * <p>Checkpoint is used in {@link GameState#undo()}. Every undo action reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        Map<Position, Position> snapshotMoves = entities.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey));
        Transition snapshot = new Transition(snapshotMoves);
        snapshot.add(new Position(-1, -1), new Position(undoQuota, 0));
        history.push(snapshot);
    }

    /**
     * Revert the game state to the last checkpoint in history. This method assumes there is still undo quota left,
     * and decreases the undo quota by one.
     * <p>If there is no checkpoint recorded, i.e., before moving any box when the game starts, revert to the initial game state.
     */
    public void undo() {
        Transition checkpoint = history.isEmpty() ? new Transition() : history.pop();
        Position quotaPos = new Position(-1, -1);
        Position quotaVal = checkpoint.moves.remove(quotaPos);
        this.undoQuota = (quotaVal != null ? quotaVal.x() : undoQuota);
        Transition reverseTransition = checkpoint.reverse();
        applyTransition(reverseTransition);
        if (this.undoQuota >= 0) {
            this.undoQuota--;
        }
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
                    e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
                .toList();
            return String.join(",", moves);
        }
    }
}