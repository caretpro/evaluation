
package assignment.game;

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
		// Initialize entities as a deep copy of the map entities
		this.entities = new HashMap<>();
		for (Map.Entry<Position, Entity> entry : map.getMap().entrySet()) {
			Entity entity = entry.getValue();
			if (entity instanceof assignment.entities.Player) {
				this.entities.put(entry.getKey(), new assignment.entities.Player(((assignment.entities.Player) entity).getId()));
			} else if (entity instanceof Box) {
				this.entities.put(entry.getKey(), new Box(((Box) entity).getPlayerId()));
			} else if (entity instanceof assignment.entities.Wall) {
				this.entities.put(entry.getKey(), new assignment.entities.Wall());
			} else if (entity instanceof Empty) {
				this.entities.put(entry.getKey(), new Empty());
			} else {
				// fallback for unknown entities
				this.entities.put(entry.getKey(), entity);
			}
		}
		this.boardWidth = map.getMaxWidth();
		this.boardHeight = map.getMaxHeight();
		this.destinations = new HashSet<>(map.getDestinations());
		// Set undo quota based on map's undo limit
		this.undoQuota = map.getUndoLimit().orElse(-1);
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
		return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
	}

	/**
	 * Check whether the game wins or not.
	 * The game wins only when all box destinations have been occupied by boxes.
	 *
	 * @return true if the game wins.
	 */
	public boolean isWin() {
		// Check if all destinations have boxes
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
		if (entity == null) {
			throw new IllegalArgumentException("No entity at source position");
		}
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
		// Save current transition moves
		history.push(currentTransition);
		// Reset current transition
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
					final Entity entity = entities.remove(e.getKey());
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
	 * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
	 * revert to the initial game state.
	 */
	public void undo() {
		if (history.isEmpty()) {
			// No checkpoint, cannot undo, do nothing or throw exception
			return;
		}
		if (undoQuota == 0) {
			// No undo quota left
			return;
		}
		Transition lastTransition = history.pop();
		// Revert the transition
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

	// Static inner class Transition
	private static class Transition {
		private final Map<Position, Position> moves;

		private Transition() {
			this.moves = new HashMap<>();
		}

		private Transition(Map<Position, Position> moves) {
			this.moves = moves;
		}

		private void add(Position from, Position to) {
			final Position key = this.moves.entrySet().stream()
					.filter(e -> e.getValue().equals(from))
					.map(Map.Entry::getKey)
					.findFirst()
					.orElse(from);
			this.moves.put(key, to);
		}

		private Transition reverse() {
			final Map<Position, Position> reversedMoves = this.moves.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
			return new Transition(reversedMoves);
		}

		@Override
		public String toString() {
			final List<String> movesStr = this.moves.entrySet().stream()
					.map(e -> String.format("(%d,%d)->(%d,%d)",
							e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
					.toList();
			return String.join(",", movesStr);
		}
	}
}