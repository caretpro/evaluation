
package assignment.game;

import assignment.entities.Entity;
import assignment.game.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

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
	 * Constructor to initialize GameState from a GameMap.
	 *
	 * @param map the initial game map.
	 */
	public GameState(@NotNull GameMap map) {
		this.entities = new HashMap<>(map.map);
		this.boardWidth = map.getMaxWidth();
		this.boardHeight = map.getMaxHeight();
		this.destinations = new HashSet<>(map.getDestinations());
		Optional<Integer> undoLimitOpt = map.getUndoLimit();
		if (undoLimitOpt.isPresent()) {
			this.undoQuota = undoLimitOpt.get();
		} else {
			this.undoQuota = -1;
		}
		this.history.clear();
		Optional<Position> playerPos = this.entities.entrySet().stream()
				.filter(e -> e.getValue() instanceof Player)
				.map(Map.Entry::getKey)
				.findFirst();
		if (playerPos.isEmpty()) {
			throw new IllegalArgumentException("No player found in the game map");
		}
		this.currentTransition = new Transition();
	}

	// Existing methods...

	private static class Transition {
		private final Map<Position, Position> moves;

		private void add(Position from, Position to) {
			final Map<Position, Position> moves = this.moves;
			final Optional<Position> existingKeyOpt = moves.entrySet().stream()
					.filter(e -> e.getValue().equals(from))
					.map(Map.Entry::getKey)
					.findFirst();
			final Position key = existingKeyOpt.orElse(from);
			moves.put(key, to);
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
			final List<String> movesStr = this.moves.entrySet().stream()
					.map(e -> String.format("(%d,%d)->(%d,%d)",
							e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y()))
					.collect(Collectors.toList());
			return String.join(",", movesStr);
		}
	}

	// Other existing methods...
}