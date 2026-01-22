
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

	// Backup of initial entities for undo to initial state
	private final Map<Position, Entity> initialEntities;

	/**
	 * Create a running game state from a game map.
	 *
	 * @param map the game map from which to create this game state.
	 */
	public GameState(GameMap map) {
		this.boardWidth = map.getMaxWidth();
		this.boardHeight = map.getMaxHeight();
		this.destinations = Collections.unmodifiableSet(new HashSet<>(map.getDestinations()));
		// Initialize undoQuota to -1 (unlimited undo)
		this.undoQuota = -1;

		// Initialize entities map by copying from the map's entities
		Map<Position, Entity> tempEntities = new HashMap<>();
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Position pos = new Position(x, y);
				Entity entity = map.getEntity(pos);
				tempEntities.put(pos, entity);
			}
		}
		this.entities = Collections.synchronizedMap(tempEntities);

		// Make a deep copy for initialEntities to restore on undo when no history
		this.initialEntities = Collections.unmodifiableMap(new HashMap<>(this.entities));
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
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("Player with id " + id + " not found"));
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
	public Entity getEntity(Position position) {
		return entities.getOrDefault(position, new Empty());
	}

	/**
	 * Get all box destination positions as a set in the game map.
	 * This should be the same as that in {@link GameMap} class.
	 *
	 * @return a set of positions.
	 */
	public Set<Position> getDestinations() {
		return destinations;
	}

	/**
	 * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
	 * If undo is unlimited,
	 *
	 * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
	 * {@link Optional#empty()} if the game has unlimited undo.
	 */
	public Optional<Integer> getUndoQuota() {
		if (undoQuota < 0) {
			return Optional.empty();
		} else {
			return Optional.of(undoQuota);
		}
	}

	/**
	 * Check whether the game wins or not.
	 * The game wins only when all box destinations have been occupied by boxes.
	 *
	 * @return true is the game wins.
	 */
	public boolean isWin() {
		// All destinations must be occupied by Box entities
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
		Entity entity = entities.get(from);
		if (entity == null || entity instanceof Empty) {
			throw new IllegalArgumentException("No entity to move at position " + from);
		}

		// Record the move in current transition
		currentTransition.add(from, to);

		// Apply the move immediately to entities map
		entities.put(to, entity);
		entities.put(from, new Empty());
	}

	/**
	 * Record a checkpoint of the game state, including:
	 * <li>All current positions of entities in the game map.</li>
	 * <li>Current undo quota</li>
	 * <p>
	 * Checkpoint is used in {@link GameState#undo()}.
	 * Every undo actions reverts the game state to the last checkpoint.
	 */
	public void checkpoint() {
		// Push current transition to history if it has any moves
		if (!currentTransition.moves.isEmpty()) {
			history.push(currentTransition);
			currentTransition = new Transition();
		}
	}

	/**
	 * Apply transition on current entity map.
	 * History is not touched in this method.
	 * Callers should maintain history themselves.
	 *
	 * @param transition the transition to apply.
	 */
	private void applyTransition(Transition transition) {
		for (Map.Entry<Position, Position> move : transition.moves.entrySet()) {
			Position from = move.getKey();
			Position to = move.getValue();
			Entity entity = entities.get(from);
			if (entity == null) {
				entity = new Empty();
			}
			entities.put(to, entity);
			entities.put(from, new Empty());
		}
	}

	/**
	 * Revert the game state to the last checkpoint in history.
	 * This method assumes there is still undo quota left, and decreases the undo quota by one.
	 * <p>
	 * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
	 * revert to the initial game state.
	 */
	public void undo() {
		if (undoQuota == 0) {
			throw new IllegalStateException("No undo quota left");
		}

		if (history.isEmpty()) {
			// No checkpoint, revert to initial state by restoring initialEntities
			entities.clear();
			entities.putAll(initialEntities);
			if (undoQuota > 0) {
				undoQuota--;
			}
			return;
		}

		Transition lastTransition = history.pop();
		Transition reverseTransition = lastTransition.reverse();

		applyTransition(reverseTransition);

		if (undoQuota > 0) {
			undoQuota--;
		}
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
}