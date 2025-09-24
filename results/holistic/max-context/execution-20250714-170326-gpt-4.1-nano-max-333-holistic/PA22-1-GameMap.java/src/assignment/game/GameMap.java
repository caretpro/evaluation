
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
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.map = new HashMap<>();
	}

	private GameMap(@NotNull Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
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
		if (mapText == null || mapText.isBlank()) {
			throw new IllegalArgumentException("Map text cannot be null or blank");
		}
		String[] lines = mapText.lines().toArray(String[]::new);
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least two lines (undo limit + map)");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit format");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}

		List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
		int height = mapLines.size();
		int width = mapLines.stream().mapToInt(String::length).max().orElse(0);

		Set<Position> destinations = new HashSet<>();
		Map<Position, Entity> entities = new HashMap<>();
		Map<Character, Player> playersByChar = new HashMap<>();
		Map<Character, Box> boxesByChar = new HashMap<>();
		Set<Character> playerChars = new HashSet<>();
		Set<Character> boxChars = new HashSet<>();

		// First pass: identify walls, destinations, players, boxes, empty
		for (int y = 0; y < height; y++) {
			String line = mapLines.get(y);
			for (int x = 0; x < width; x++) {
				char ch = x < line.length() ? line.charAt(x) : ' ';
				Position pos = Position.of(x, y);
				switch (ch) {
					case '#':
						entities.put(pos, new Wall());
						break;
					case '@':
						destinations.add(pos);
						entities.put(pos, new Empty()); // mark destination
						break;
					case '.':
						entities.put(pos, new Empty());
						break;
					default:
						if (Character.isUpperCase(ch)) {
							if (playerChars.contains(ch)) {
								throw new IllegalArgumentException("Multiple players with same character: " + ch);
							}
							Player player = new Player(ch - 'A' + 1);
							playersByChar.put(ch, player);
							playerChars.add(ch);
							entities.put(pos, player);
						} else if (Character.isLowerCase(ch)) {
							char upper = Character.toUpperCase(ch);
							if (boxesByChar.containsKey(ch)) {
								throw new IllegalArgumentException("Multiple boxes with same character: " + ch);
							}
							Box box = new Box(upper - 'A' + 1);
							boxesByChar.put(ch, box);
							entities.put(pos, box);
						} else {
							entities.put(pos, new Empty());
						}
						break;
				}
			}
		}

		long boxCount = entities.values().stream().filter(e -> e instanceof Box).count();
		if (boxCount != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes does not match number of destinations");
		}
		if (playersByChar.isEmpty()) {
			throw new IllegalArgumentException("No players found in the map");
		}

		return new GameMap(width, height, destinations, undoLimit, entities);
	}

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
	 * Return a new GameMap with an entity placed at the specified position.
	 *
	 * @param position the position in the game map.
	 * @param entity   the entity to place.
	 * @return a new GameMap instance with the updated entity.
	 */
	public GameMap withEntity(Position position, Entity entity) {
		Map<Position, Entity> newMap = new HashMap<>(this.map);
		newMap.put(position, entity);
		return new GameMap(this.maxWidth, this.maxHeight, this.destinations, this.undoLimit, newMap);
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
		return undoLimit >= 0 ? Optional.of(undoLimit) : (undoLimit == -1 ? Optional.of(-1) : Optional.empty());
	}

	/**
	 * Get all players' ids as a set.
	 *
	 * @return a set of player ids.
	 */
	public Set<Integer> getPlayerIds() {
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