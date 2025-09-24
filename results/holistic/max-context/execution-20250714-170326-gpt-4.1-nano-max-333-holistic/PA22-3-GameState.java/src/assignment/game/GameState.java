
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;

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
	 * Create a running game state from a game map.
	 *
	 * @param map the game map from which to create this game state.
	 */
	public GameState(GameMap map) {
		// Initialize entities by copying all entities from the map
		this.entities = new HashMap<>();
		for (int y = 0; y < map.getMaxHeight(); y++) {
			for (int x = 0; x < map.getMaxWidth(); x++) {
				Position pos = Position.of(x, y);
				Entity entity = map.getEntity(pos);
				if (entity != null) {
					this.entities.put(pos, entity);
				}
			}
		}
		this.destinations = new HashSet<>(map.getDestinations());
		this.boardWidth = map.getMaxWidth();
		this.boardHeight = map.getMaxHeight();

		// Set undoQuota based on map's undo limit
		Optional<Integer> limitOpt = map.getUndoLimit();
		if (limitOpt.isPresent()) {
			this.undoQuota = limitOpt.get();
		} else {
			this.undoQuota = -1; // unlimited
		}

		// Initialize currentTransition as empty
		this.currentTransition = new Transition();

		// Save initial state as the first checkpoint
		checkpoint();
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
	public Entity getEntity(Position position) {
		return entities.get(position);
	}

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
	 * @return true if the game wins.
	 */
	public boolean isWin() {
		return destinations.stream().allMatch(pos -> {
			Entity entity = entities.get(pos);
			return entity instanceof Box;
		});
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
		if (entity == null) {
			throw new IllegalArgumentException("No entity at the source position");
		}
		// Record the move in currentTransition
		currentTransition.add(from, to);
		// Update entities map
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
		// Save current transition to history
		history.push(currentTransition);
		// Reset currentTransition
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
		transition.moves.entrySet().forEach(e -> {
			final Entity entity = this.entities.remove(e.getKey());
			this.entities.put(e.getKey(), new Empty());
			this.entities.put(e.getValue(), entity);
		});
	}

	/**
	 * Revert the game state to the last checkpoint in history.
	 * This method assumes there is still undo quota left, and decreases the undo quota by one.
	 * <p>
	 * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
	 * revert to the initial game state.
	 */
	public void undo() {
		if (history.isEmpty()) {
			// No checkpoint, revert to initial state
			throw new IllegalStateException("No checkpoint to undo to");
		}
		if (undoQuota == 0) {
			throw new IllegalStateException("No undo quota left");
		}
		Transition lastTransition = history.pop();
		Transition reverseTransition = lastTransition.reverse();
		applyTransition(reverseTransition);
		if (undoQuota > 0) {
			undoQuota--;
		}
		currentTransition = new Transition();
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
			final Position key = this.moves.entrySet().stream()
					.filter(e -> e.getValue().equals(from))
					.map(Map.Entry::getKey)
					.findFirst()
					.orElse(from);
			this.moves.put(key, to);
		}

		private Transition(Map<Position, Position> moves) {
			this.moves = new HashMap<>(moves);
		}

		private Transition() {
			this.moves = new HashMap<>();
		}

		private Transition reverse() {
			final Map<Position, Position> moves = new HashMap<>();
			this.moves.forEach((k, v) -> moves.put(v, k));
			return new Transition(moves);
		}

		@Override
		public String toString() {
			final List<String> movesStr = this.moves.entrySet().stream()
					.map(e -> String.format("(%d,%d)->(%d,%d)",
							e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
					.collect(Collectors.toList());
			return String.join(",", movesStr);
		}
	}
}