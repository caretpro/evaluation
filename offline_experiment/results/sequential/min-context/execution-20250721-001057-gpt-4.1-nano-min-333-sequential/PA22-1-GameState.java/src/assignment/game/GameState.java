
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
	 * Constructor that initializes GameState from a given GameMap.
	 *
	 * @param map the GameMap to initialize the game state from.
	 */
	public GameState(@NotNull GameMap map) {
	    this.entities = new HashMap<>();
	    for (int y = 0; y < map.getHeight(); y++) {
	        for (int x = 0; x < map.getWidth(); x++) {
	            Position pos = new Position(x, y);
	            Entity entity = map.getEntityAt(pos);
	            this.entities.put(pos, entity);
	        }
	    }
	    this.boardWidth = map.getWidth();
	    this.boardHeight = map.getHeight();
	    this.destinations = new HashSet<>(map.getDestinations());
	    this.undoQuota = Integer.MAX_VALUE;
	    this.currentTransition = new Transition();
	    this.history.clear();
	}

	private void applyTransition(Transition transition) {
		transition.moves.entrySet().stream().map(e -> {
			final Entity entity = this.entities.remove(e.getKey());
			this.entities.put(e.getKey(), new Empty());
			return Map.entry(e.getValue(), entity);
		}).toList().forEach(e -> this.entities.put(e.getKey(), e.getValue()));
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

	public Position getPlayerPositionById(int id) {
		for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
			Entity entity = entry.getValue();
			if (entity instanceof Player player && player.getId() == id) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Set<Position> getAllPlayerPositions() {
		return entities.entrySet().stream().filter(entry -> entry.getValue() instanceof Player).map(Map.Entry::getKey)
				.collect(Collectors.toUnmodifiableSet());
	}

	public Entity getEntity(@NotNull Position position) {
		return entities.getOrDefault(position, null);
	}

	public Set<Position> getDestinations() {
		return Collections.unmodifiableSet(destinations);
	}

	public Optional<Integer> getUndoQuota() {
		return (this.undoQuota == Integer.MAX_VALUE) ? Optional.empty() : Optional.of(this.undoQuota);
	}

	public boolean isWin() {
		return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
	}

	public void move(Position from, Position to) {
		Entity entity = entities.get(from);
		if (entity == null) {
			throw new IllegalArgumentException("No entity at the 'from' position");
		}
		currentTransition.add(from, to);
		entities.put(to, entity);
		entities.put(from, new Empty());
	}

	public void checkpoint() {
		Map<Position, Entity> entitiesCopy = new HashMap<>();
		for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
			entitiesCopy.put(entry.getKey(), entry.getValue());
		}
		int undoQuotaCopy = this.undoQuota;
		Transition transitionCopy = new Transition(new HashMap<>(this.currentTransition.moves));
		GameStateSnapshot snapshot = new GameStateSnapshot(entitiesCopy, undoQuotaCopy, transitionCopy);
		this.history.push(snapshot);
	}

	public void undo() {
		if (this.undoQuota == 0) {
			throw new IllegalStateException("No undo quota left");
		}
		if (this.history.isEmpty()) {
			throw new IllegalStateException("No checkpoint to undo to");
		}
		this.undoQuota--;
		GameStateSnapshot snapshot = this.history.pop();
		this.entities.clear();
		this.entities.putAll(snapshot.entities);
		this.undoQuota = snapshot.undoQuota;
		this.currentTransition = snapshot.transition;
	}

	public int getMapMaxWidth() {
		return this.boardWidth;
	}

	public int getMapMaxHeight() {
		return this.boardHeight;
	}
}