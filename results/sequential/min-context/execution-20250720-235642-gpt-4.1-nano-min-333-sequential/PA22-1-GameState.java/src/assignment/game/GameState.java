
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
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
	 * Constructor that initializes the GameState from a given GameMap.
	 *
	 * @param map the GameMap to initialize the game state from.
	 */
	public GameState(@NotNull GameMap map) {
		this.entities = new HashMap<>();
		for (Position pos : map.getAllPositions()) {
			Entity entity = map.getEntityAt(pos);
			if (entity instanceof Player) {
				this.entities.put(pos, new Player(((Player) entity).getId()));
			} else if (entity instanceof Box) {
				this.entities.put(pos, new Box());
			} else if (entity instanceof Empty) {
				this.entities.put(pos, new Empty());
			} else {
				this.entities.put(pos, entity);
			}
		}
		this.boardWidth = map.getWidth();
		this.boardHeight = map.getHeight();
		this.destinations = new HashSet<>(map.getDestinations());
		this.undoQuota = map.getInitialUndoQuota();
		this.currentTransition = new Transition();
		this.history.clear();
	}

	// existing methods...
    // (No changes needed here)
}