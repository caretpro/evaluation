
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
		return maxHeight;
	}

	public static GameMap parse(String mapText) {
		if (mapText == null || mapText.isEmpty()) {
			throw new IllegalArgumentException("Map text cannot be null or empty");
		}
		String[] lines = mapText.split("\\R");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map must contain at least undo limit and one map line");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Undo limit must be an integer");
		}
		if (undoLimit < -1 || (undoLimit < 0 && undoLimit != -1)) {
			throw new IllegalArgumentException("Undo limit must be -1 or non-negative");
		}
		int height = lines.length - 1;

		// Determine max width among all lines (not just first line)
		int maxWidth = 0;
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].length() > maxWidth) {
				maxWidth = lines[i].length();
			}
		}

		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Character, Player> playersByChar = new HashMap<>();
		Map<Character, Position> playerPositions = new HashMap<>();
		Map<Character, List<Box>> boxesByPlayerChar = new HashMap<>();
		int boxCount = 0;
		int destinationCount = 0;

		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			// Pad line with spaces if shorter than maxWidth
			if (line.length() < maxWidth) {
				line = String.format("%-" + maxWidth + "s", line);
			}
			for (int x = 0; x < maxWidth; x++) {
				char c = line.charAt(x);
				Position pos = new Position(x, y);
				Entity entity;
				if (c == '#') {
					entity = new Wall();
				} else if (c == '@') {
					entity = new Empty();
					destinations.add(pos);
					destinationCount++;
				} else if (c == '.') {
					entity = new Empty();
				} else if (Character.isUpperCase(c)) {
					if (playersByChar.containsKey(c)) {
						throw new IllegalArgumentException("Duplicate player character: " + c);
					}
					Player player = new Player(c - 'A');
					playersByChar.put(c, player);
					playerPositions.put(c, pos);
					entity = player;
				} else if (Character.isLowerCase(c)) {
					char playerChar = Character.toUpperCase(c);
					Box box = new Box(playerChar - 'A');
					boxesByPlayerChar.computeIfAbsent(playerChar, k -> new ArrayList<>()).add(box);
					entity = box;
					boxCount++;
				} else if (c == ' ' || c == '\t') {
					// Treat spaces and tabs as empty
					entity = new Empty();
				} else {
					throw new IllegalArgumentException("Invalid character in map: " + c);
				}
				map.put(pos, entity);
			}
		}
		if (playersByChar.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one player on the map");
		}
		if (boxCount != destinationCount) {
			throw new IllegalArgumentException("Number of boxes must equal number of box destinations");
		}
		Set<Character> playerChars = playersByChar.keySet();
		Set<Character> boxPlayerChars = boxesByPlayerChar.keySet();
		for (char boxPlayerChar : boxPlayerChars) {
			if (!playerChars.contains(boxPlayerChar)) {
				throw new IllegalArgumentException(
						"Box player '" + boxPlayerChar + "' has no matching player on the board");
			}
		}
		for (char playerChar : playerChars) {
			if (!boxPlayerChars.contains(playerChar)) {
				throw new IllegalArgumentException("Player '" + playerChar + "' has no corresponding boxes");
			}
		}
		return new GameMap(map, destinations, undoLimit);
	}

	/**
	 * Get the entity object at the given position.
	 * @param position  the position of the entity in the game map.
	 * @return  Entity object.
	 */
	public Entity getEntity(Position position) {
		return map.getOrDefault(position, new Empty());
	}

	/**
	 * Put one entity at the given position in the game map.
	 * @param position  the position in the game map to put the entity.
	 * @param entity    the entity to put into game map.
	 */
	public void putEntity(Position position, Entity entity) {
		Objects.requireNonNull(position, "Position cannot be null");
		Objects.requireNonNull(entity, "Entity cannot be null");
		map.put(position, entity);
	}

	/**
	 * Get all box destination positions as a set in the game map.
	 * @return  a set of positions.
	 */
	public Set<Position> getDestinations() {
		return destinations;
	}

	/**
	 * Get the undo limit of the game map.
	 * @return  undo limit.
	 */
	public Optional<Integer> getUndoLimit() {
		if (undoLimit == 0) {
			return Optional.empty();
		}
		return Optional.of(undoLimit);
	}

	/**
	 * Get all players' id as a set.
	 * @return  a set of player id.
	 */
	public Set<Integer> getPlayerIds() {
		return map.values().stream().filter(entity -> entity instanceof Player).map(entity -> ((Player) entity).getId())
				.collect(Collectors.toSet());
	}

	/**
	 * Get the maximum width of the game map.
	 * @return  maximum width.
	 */
	public int getMaxWidth() {
		return maxWidth;
	}
}