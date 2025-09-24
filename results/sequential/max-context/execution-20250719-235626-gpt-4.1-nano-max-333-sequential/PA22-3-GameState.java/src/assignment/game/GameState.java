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

	public void GameState(GameMap map) {
		this.boardWidth = map.getMaxWidth();
		this.boardHeight = map.getMaxHeight();
		this.destinations = new HashSet<>(map.getDestinations());
		this.undoQuota = map.getUndoLimit().orElse(-1);
		this.entities = new HashMap<>();
		for (Map.Entry<Position, Entity> entry : map.map.entrySet()) {
			Position pos = entry.getKey();
			Entity entity = entry.getValue();
			this.entities.put(pos, entity);
		}
		this.currentTransition = new Transition();
		for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
			if (entry.getValue() instanceof Player) {
			}
		}
	}

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
		throw new NoSuchElementException("Player with id " + id + " not found");
	}

	public Set<Position> getAllPlayerPositions() {
		return entities.entrySet().stream().filter(entry -> entry.getValue() instanceof Player).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	public Entity getEntity(Position position) {
		return entities.get(position);
	}

	public Set<Position> getDestinations() {
		return destinations;
	}

	public Optional<Integer> getUndoQuota() {
		return (undoQuota >= 0) ? Optional.of(undoQuota) : Optional.empty();
	}

	public boolean isWin() {
		return destinations.stream().allMatch(pos -> entities.get(pos) instanceof Box);
	}

	public void move(Position from, Position to) {
		Entity entity = entities.get(from);
		if (entity == null) {
			throw new IllegalArgumentException("No entity at the 'from' position");
		}
		entities.remove(from);
		entities.put(to, entity);
		currentTransition.add(from, to);
	}

	public void checkpoint() {
		Map<Position, Entity> entitiesCopy = new HashMap<>();
		for (Map.Entry<Position, Entity> entry : entities.entrySet()) {
			entitiesCopy.put(entry.getKey(), entry.getValue());
		}
		int undoQuotaCopy = this.undoQuota;
		Checkpoint checkpoint = new Checkpoint(entitiesCopy, undoQuotaCopy);
		this.history.push(checkpoint);
	}

	public void undo() {
		if (undoQuota <= 0) {
			return;
		}
		if (history.isEmpty()) {
			return;
		}
		Object checkpointObj = history.pop();
		if (!(checkpointObj instanceof Checkpoint)) {
			return;
		}
		Checkpoint checkpoint = (Checkpoint) checkpointObj;
		this.entities.clear();
		this.entities.putAll(checkpoint.entities);
		this.undoQuota = checkpoint.undoQuota;
		this.undoQuota--;
		this.currentTransition = new Transition();
	}

	public int getMapMaxWidth() {
		return gameMap.getMaxWidth();
	}

	public int getMapMaxHeight() {
		return this.gameMap.getMaxHeight();
	}
}
