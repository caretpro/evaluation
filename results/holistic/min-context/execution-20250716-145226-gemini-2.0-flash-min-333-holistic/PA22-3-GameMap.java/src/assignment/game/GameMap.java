
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
	 * Parses the map from a string representation.
	 * The first line is undo limit.
	 * Starting from the second line, the game map is represented as follows,
	 * <li># represents a {@link Wall}</li>
	 * <li>@ represents a box destination.</li>
	 * <li>Any upper-case letter represents a {@link Player}.</li>
	 * <li>
	 * Any lower-case letter represents a {@link Box} that is only movable by the player with the corresponding upper-case letter.
	 * For instance, box "a" can only be moved by player "A" and not movable by player "B".
	 * </li>
	 * <li>. represents an {@link Empty} position in the map, meaning there is no player or box currently at this position.</li>
	 * <p>
	 * Notes:
	 * <li>
	 * There can be at most 26 players.
	 * All implementations of classes in the assignment.game package should support up to 26 players.
	 * </li>
	 * <li>
	 * For simplicity, we assume the given map is bounded with a closed boundary.
	 * There is no need to check this point.
	 * </li>
	 * <li>
	 * Example maps can be found in "src/main/resources".
	 * </li>
	 *
	 * @param mapText The string representation.
	 * @return The parsed GameMap object.
	 * @throws IllegalArgumentException if undo limit is negative but not -1.
	 * @throws IllegalArgumentException if there are multiple same upper-case letters, i.e., one player can only exist at one position.
	 * @throws IllegalArgumentException if there are no players in the map.
	 * @throws IllegalArgumentException if the number of boxes is not equal to the number of box destinations.
	 * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()} do not match any player on the game board,
	 *                                  or if there are players that have no corresponding boxes.
	 */
	public static GameMap parse(String mapText) {
		String[] lines = mapText.split("\n");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map must have at least two lines (undo limit and map data).");
		}

		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit: " + lines[0], e);
		}

		if (undoLimit < -1 || undoLimit == -2) {
			throw new IllegalArgumentException("Undo limit must be -1 or non-negative.");
		}

		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Set<Character> players = new HashSet<>();
		Set<Character> boxes = new HashSet<>();
		Map<Character, Position> playerPositions = new HashMap<>();
		List<Position> boxPositions = new ArrayList<>();

		int maxWidth = 0;
		int maxHeight = lines.length - 1;

		for (int y = 1; y < lines.length; y++) {
			String line = lines[y];
			maxWidth = Math.max(maxWidth, line.length());
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				Position position = new Position(x, y - 1);
				switch (c) {
					case '#':
						map.put(position, new Wall());
						break;
					case '@':
						destinations.add(position);
						map.put(position, map.containsKey(position) ? map.get(position) : new Empty());
						break;
					case '.':
						map.put(position, new Empty());
						break;
					default:
						if (Character.isUpperCase(c)) {
							if (players.contains(c)) {
								throw new IllegalArgumentException("Multiple players with the same ID: " + c);
							}
							players.add(c);
							playerPositions.put(c, position);
							map.put(position, new Player(c - 'A'));
						} else if (Character.isLowerCase(c)) {
							boxes.add(c);
							map.put(position, new Box(c - 'a'));
							boxPositions.add(position);
						} else {
							throw new IllegalArgumentException("Invalid character in map: " + c);
						}
						break;
				}
			}
		}

		if (players.isEmpty()) {
			throw new IllegalArgumentException("No players in the map.");
		}

		if (boxPositions.size() != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes must equal number of destinations.");
		}

		Set<Integer> playerIds = players.stream().map(c -> c - 'A').collect(Collectors.toSet());
		Set<Integer> boxIds = boxes.stream().map(c -> c - 'a').collect(Collectors.toSet());

		if (!playerIds.equals(boxIds)) {
			throw new IllegalArgumentException("Players and boxes IDs do not match.");
		}

		// After parsing the map, check if a destination also has a box on it.
        // If so, ensure that the box is also considered a destination.
        for (Position boxPosition : boxPositions) {
            if (destinations.contains(boxPosition)) {
                continue; // It's already a destination, so no need to add it again.
            }
        }

		return new GameMap(map, destinations, undoLimit);
	}

	/**
	 * Get the entity object at the given position.
	 *
	 * @param position the position of the entity in the game map.
	 * @return Entity object.
	 */
	public Entity getEntity(Position position) {
		return map.getOrDefault(position, new Empty());
	}

	/**
	 * Put one entity at the given position in the game map.
	 *
	 * @param position the position in the game map to put the entity.
	 * @param entity   the entity to put into game map.
	 */
	public void putEntity(Position position, Entity entity) {
		map.put(position, entity);
	}

	/**
	 * Get all box destination positions as a set in the game map.
	 *
	 * @return a set of positions.
	 */
	public Set<Position> getDestinations() {
		return destinations;
	}

	/**
	 * Get the undo limit of the game map.
	 *
	 * @return undo limit.
	 */
	public Optional<Integer> getUndoLimit() {
		return undoLimit == -1 ? Optional.empty() : Optional.of(undoLimit);
	}

	/**
	 * Get all players' id as a set.
	 *
	 * @return a set of player id.
	 */
	public Set<Integer> getPlayerIds() {
		return map.values().stream()
				.filter(entity -> entity instanceof Player)
				.map(entity -> ((Player) entity).getId())
				.collect(Collectors.toSet());
	}

	/**
	 * Get the maximum width of the game map.
	 *
	 * @return maximum width.
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Get the maximum height of the game map.
	 *
	 * @return maximum height.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}
}