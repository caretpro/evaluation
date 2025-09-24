
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
		if (mapText == null || mapText.isBlank()) {
			throw new IllegalArgumentException("Map text cannot be null or empty");
		}
		String[] lines = mapText.split("\\R");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least undo limit line and one map line");
		}

		// Parse undo limit
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Undo limit line is not a valid integer");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}

		// Parse map lines
		int height = lines.length - 1;
		int width = lines[1].length();
		for (int i = 2; i < lines.length; i++) {
			if (lines[i].length() != width) {
				throw new IllegalArgumentException("All map lines must have the same width");
			}
		}

		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();

		// Track players and boxes
		Map<Character, Position> players = new HashMap<>();
		Map<Character, List<Position>> boxesByPlayer = new HashMap<>();

		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			for (int x = 0; x < width; x++) {
				char ch = line.charAt(x);
				Position pos = Position.of(x, y);
				switch (ch) {
					case '#':
						map.put(pos, new Wall());
						break;
					case '@':
						destinations.add(pos);
						map.put(pos, new Empty());
						break;
					case '.':
						map.put(pos, new Empty());
						break;
					default:
						if (Character.isUpperCase(ch)) {
							// Player
							if (players.containsKey(ch)) {
								throw new IllegalArgumentException("Duplicate player character: " + ch);
							}
							players.put(ch, pos);
							map.put(pos, new Player(ch));
						} else if (Character.isLowerCase(ch)) {
							// Box
							char playerChar = Character.toUpperCase(ch);
							boxesByPlayer.computeIfAbsent(playerChar, k -> new ArrayList<>()).add(pos);
							map.put(pos, new Box(playerChar));
						} else {
							throw new IllegalArgumentException("Invalid character in map: " + ch);
						}
				}
			}
		}

		if (players.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one player in the map");
		}

		// Check box count equals destination count
		int totalBoxes = boxesByPlayer.values().stream().mapToInt(List::size).sum();
		if (totalBoxes != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes (" + totalBoxes + ") does not equal number of destinations (" + destinations.size() + ")");
		}

		// Check boxes and players match
		Set<Character> playerIds = players.keySet();
		Set<Character> boxPlayerIds = boxesByPlayer.keySet();

		// Boxes must have matching players
		for (Character boxPlayerId : boxPlayerIds) {
			if (!playerIds.contains(boxPlayerId)) {
				throw new IllegalArgumentException("Box player id '" + boxPlayerId + "' has no matching player");
			}
		}

		// Players must have at least one box
		for (Character playerId : playerIds) {
			if (!boxPlayerIds.contains(playerId)) {
				throw new IllegalArgumentException("Player '" + playerId + "' has no corresponding boxes");
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
		if (position == null || entity == null) {
			throw new IllegalArgumentException("Position and entity cannot be null");
		}
		if (position.x() < 0 || position.x() >= maxWidth || position.y() < 0 || position.y() >= maxHeight) {
			throw new IllegalArgumentException("Position out of bounds");
		}
		if (map instanceof HashMap) {
			((HashMap<Position, Entity>) map).put(position, entity);
		} else {
			throw new UnsupportedOperationException("Cannot modify an unmodifiable map");
		}
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
		if (undoLimit == -1) {
			return Optional.empty();
		}
		return Optional.of(undoLimit);
	}

	/**
	 * Get all players' id as a set.
	 *
	 * @return a set of player id characters.
	 */
	public Set<Character> getPlayerIds() {
		return map.values().stream()
				.filter(e -> e instanceof Player)
				.map(e -> ((Player) e).getId())
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