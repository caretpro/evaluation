
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
 * GameMap consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameMap is capable to create many GameState instances, each representing an ongoing game.
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
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.map = new HashMap<>();
	}

	private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
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
		String[] lines = mapText.lines().toArray(String[]::new);
		if (lines.length == 0) {
			throw new IllegalArgumentException("Map text cannot be empty");
		}
		// Parse undo limit from the first line
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}
		Set<Position> destinations = new HashSet<>();
		Map<Position, Entity> entities = new HashMap<>();
		Map<Character, Player> playersMap = new HashMap<>();
		Map<Character, Box> boxesMap = new HashMap<>();
		int maxWidth = 0;
		int totalBoxes = 0;

		// Process each line
		for (int y = 0; y < lines.length - 1; y++) {
			String line = lines[y + 1];
			maxWidth = Math.max(maxWidth, line.length());
			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);
				Position pos = new Position(x, y);
				switch (ch) {
					case '#':
						entities.put(pos, new Wall());
						break;
					case '@':
						destinations.add(pos);
						entities.put(pos, new Empty()); // destination can be empty or have a box/player
						break;
					case '.':
						entities.put(pos, new Empty());
						break;
					default:
						if (Character.isUpperCase(ch)) {
							// Player
							if (playersMap.containsKey(ch)) {
								throw new IllegalArgumentException("Multiple players with same ID: " + ch);
							}
							Player player = new Player(ch - 'A' + 1); // assign IDs starting from 1
							playersMap.put(ch, player);
							entities.put(pos, player);
						} else if (Character.isLowerCase(ch)) {
							// Box
							char upper = Character.toUpperCase(ch);
							Box box = new Box(upper - 'A' + 1);
							boxesMap.put(ch, box);
							entities.put(pos, box);
							totalBoxes++;
						} else {
							// Empty or unrecognized character
							entities.put(pos, new Empty());
						}
						break;
				}
			}
		}
		int totalPlayers = playersMap.size();

		// Validate players
		if (totalPlayers == 0) {
			throw new IllegalArgumentException("No players found in the map");
		}

		long boxCount = boxesMap.size();
		long destinationCount = destinations.size();

		if (boxCount != destinationCount) {
			throw new IllegalArgumentException("Number of boxes does not match number of destinations");
		}

		// Validate boxes' player IDs
		for (Box box : boxesMap.values()) {
			int playerId = box.getPlayerId();
			if (playerId <= 0 || playerId > totalPlayers) {
				throw new IllegalArgumentException("Box with invalid player ID: " + playerId);
			}
		}

		// Initialize the map with all entities
		Map<Position, Entity> finalMap = new HashMap<>(entities);
		return new GameMap(maxWidth, lines.length - 1, destinations, undoLimit, finalMap);
	}

	// Constructor with map parameter
	private GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit, Map<Position, Entity> entities) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.map = Collections.unmodifiableMap(new HashMap<>(entities));
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
		// Since map is unmodifiable, create a new map with the updated entity
		throw new UnsupportedOperationException("Immutable map, create a new GameMap instance with updated map");
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
			return Optional.of(-1);
		} else if (undoLimit == 0) {
			return Optional.of(0);
		} else {
			return Optional.of(undoLimit);
		}
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