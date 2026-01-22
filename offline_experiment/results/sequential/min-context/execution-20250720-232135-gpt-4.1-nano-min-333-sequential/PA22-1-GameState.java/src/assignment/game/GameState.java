
package assignment.game;

import assignment.actions.Move;
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
	 * Constructor to initialize GameState from a GameMap.
	 * @param map the GameMap to initialize from.
	 */
	public GameState(@NotNull GameMap map) {
		this.boardWidth = map.getWidth();
		this.boardHeight = map.getHeight();
		this.entities = new HashMap<>();
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				Position pos = new Position(x, y);
				Entity entity = map.getEntityAt(pos);
				this.entities.put(pos, entity);
			}
		}
		this.destinations = new HashSet<>(map.getDestinations());
		this.undoQuota = map.getInitialUndoQuota();
		this.history.clear();
		this.currentTransition = new Transition();
	}
	
	// ... rest of your class code ...
}