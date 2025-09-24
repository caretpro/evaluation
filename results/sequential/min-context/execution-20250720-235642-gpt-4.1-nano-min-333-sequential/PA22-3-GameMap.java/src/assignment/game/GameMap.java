
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameBoard is capable to create many GameState instances, each representing an ongoing game.
 */
public class GameMap {

	private final Map<Position, Entity> map;

	private final int maxWidth;

	private final int maxHeight;

	private final Set<Position> destinations;

	private final int undoLimit;

	/**
	 * Create a new GameMap with width, height, set of box destinations and undo limit.
	 *
	 * @param maxWidth     Width of the game map.
	 * @param maxHeight    Height of the game map.
	 * @param destinations Set of box destination positions.
	 * @param undoLimit    Undo limit.
	 *                     Positive numbers specify the maximum number of undo actions.
	 *                     0 means undo is not allowed.
	 *                     -1 means unlimited. Other negative numbers are not allowed.
	 */
	public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.map = new HashMap<>();
	}

	private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
		this.map = Collections.unmodifiableMap(map);
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
		this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
	}

	/**
	 * Get the maximum height of the game map.
	 *
	 * @return maximum height.
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	// Other methods omitted for brevity
}