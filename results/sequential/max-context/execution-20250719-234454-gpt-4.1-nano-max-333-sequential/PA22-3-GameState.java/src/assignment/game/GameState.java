
package assignment.game;

import assignment.entities.Entity;
import assignment.game.GameMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	 * Constructor to create a new GameState from a given GameMap.
	 *
	 * @param map the GameMap to initialize the game state.
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
		this.currentTransition = new Transition();
		Optional<Position> playerPos = this.entities.entrySet().stream()
				.filter(e -> e.getValue() instanceof Player)
				.map(Map.Entry::getKey)
				.findFirst();
		if (playerPos.isEmpty()) {
			throw new IllegalStateException("No player found in the map");
		}
	}

	// Existing methods...

	private void applyTransition(Transition transition) {
		transition.moves.entrySet().stream().map(e -> {
			final Entity entity = this.entities.remove(e.getKey());
			this.entities.put(e.getKey(), new Empty());
			return Map.entry(e.getValue(), entity);
		}).toList().forEach(e -> this.entities.put(e.getKey(), e.getValue()));
	}

	public int getMapMaxHeight() {
		return this.boardHeight;
	}

	// Rest of the class remains unchanged...
}