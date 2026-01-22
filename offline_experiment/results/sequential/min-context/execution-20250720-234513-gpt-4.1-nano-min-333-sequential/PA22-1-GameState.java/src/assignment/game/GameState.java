
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
		}).toList().forEach(e -> this.entities.put(e.getKey(), e.getValue()));
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

	public GameState(GameMap map) {
		this.boardWidth = map.getWidth();
		this.boardHeight = map.getHeight();
		this.destinations = new HashSet<>(map.getDestinations());
		this.entities = new HashMap<>();
		this.undoQuota = map.getUndoQuota().orElse(Integer.MAX_VALUE);
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Position pos = new Position(x, y);
				Entity entity = map.getEntityAt(pos);
				if (entity != null) {
					entities.put(pos, entity);
				} else {
					entities.put(pos, new Empty());
				}
			}
		}
		this.currentTransition = new Transition();
		this.history.clear();
	}
}