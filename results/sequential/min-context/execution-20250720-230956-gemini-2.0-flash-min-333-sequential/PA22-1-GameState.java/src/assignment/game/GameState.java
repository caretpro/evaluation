
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
 * As the game goes, the game state changes while players are moving while the original game map stays the unmodified.
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
        transition.moves.entrySet().stream().map(e -> {
            final Entity entity = this.entities.remove(e.getKey());
            this.entities.put(e.getKey(), new Empty());
            return Map.entry(e.getValue(), entity);
        }).toList().forEach(e -> this.entities.put(e.getValue(), e.getValue()));
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return this.boardHeight;
    }

    private static class Transition {
        private final Map<Position, Position> moves;

        private void add(Position from, Position to) {
            final Position key = this.moves.entrySet().stream().filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey).findFirst().orElse(from);
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
            final List<String> moves = this.moves.entrySet().stream().map(e -> String.format("(%d,%d)->(%d,%d)",
                    e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y())).toList();
            return String.join(",", moves);
        }
    }

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(GameMap map) {
        this.boardWidth = map.getWidth();
        this.boardHeight = map.getHeight();
        this.entities = new HashMap<>();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit().orElse(-1);
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position position = new Position(x, y);
                Entity entity = map.getEntity(position);
                this.entities.put(position, entity);
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
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getId() == id) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> playerPositions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            Entity entity = entry.getValue();
            if (entity instanceof Player) {
                playerPositions.add(entry.getKey());
            }
        }
        return playerPositions;
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
     * Get all box destination positions as a set in the game map. This should be the same as that in  {@link GameMap}  class.
     *
     * @return a set of positions.
     */
    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(this.destinations);
    }

    public Optional<Integer> getUndoQuota() {
        if (undoQuota < 0) {
            return Optional.empty();
        } else {
            return Optional.of(undoQuota);
        }
    }

    /**
     * Check whether the game wins or not. The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true is the game wins.
     */
    public boolean isWin() {
        for (Position destination : destinations) {
            Entity entity = entities.get(destination);
            if (!(entity instanceof Box)) {
                return false;
            }
        }
        return true;
    }

    public void move(Position from, Position to) {
        Entity entity = entities.get(from);
        entities.remove(from);
        entities.put(from, new Empty());
        entities.put(to, entity);
    }

    /**
     * Record a checkpoint of the game state, including: <li>All current positions of entities in the game map.</li> <li>Current undo quota</li>
     * <p>
     * Checkpoint is used in  {@link GameState#undo()} . Every undo actions reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        Map<Position, Entity> entityCopy = new HashMap<>();
        for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
            entityCopy.put(entry.getKey(), entry.getValue());
        }
        int undoQuotaCopy = this.undoQuota;
        Transition transitionCopy = new Transition(new HashMap<>(this.currentTransition.moves));
        CheckpointState checkpointState = new CheckpointState(entityCopy, undoQuotaCopy, transitionCopy);
        history.push(this.currentTransition);
        this.currentTransition = new Transition();
    }

    /**
     * Revert the game state to the last checkpoint in history. This method assumes there is still undo quota left, and decreases the undo quota by one.
     * <p>
     * If there is no checkpoint recorded, i.e., before moving any box when the game starts, revert to the initial game state.
     */
    public void undo() {
        if (!history.isEmpty()) {
            Transition lastTransition = history.pop();
            Transition reversedTransition = lastTransition.reverse();
            applyTransition(reversedTransition);
            undoQuota--;
        } else {
            System.out.println("No more checkpoints to undo to.");
        }
    }

    /**
     * Get the maximum width of the game map. This should be the same as that in  {@link GameMap}  class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return this.boardWidth;
    }
}