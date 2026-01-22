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

	public static GameMap parse(String mapText) {
		if (mapText == null || mapText.isBlank()) {
			throw new IllegalArgumentException("Map text cannot be null or empty");
		}
		String[] lines = mapText.split("\n");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map must contain at least undo limit and one map line");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Undo limit must be an integer");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}
		int height = lines.length - 1;
		int width = lines[1].length();
		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Character, Position> players = new HashMap<>();
		Map<Character, List<Position>> boxes = new HashMap<>();
		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			if (line.length() != width) {
				throw new IllegalArgumentException("All map lines must have the same width");
			}
			for (int x = 0; x < width; x++) {
				char c = line.charAt(x);
				Position pos = new Position(x, y);
				switch (c) {
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
					if (Character.isUpperCase(c)) {
						if (players.containsKey(c)) {
							throw new IllegalArgumentException("Duplicate player '" + c + "' found");
						}
						players.put(c, pos);
						map.put(pos, new Player(c));
					} else if (Character.isLowerCase(c)) {
						boxes.computeIfAbsent(c, k -> new ArrayList<>()).add(pos);
						map.put(pos, new Box(Character.toUpperCase(c)));
					} else {
						throw new IllegalArgumentException("Invalid character '" + c + "' in map");
					}
					break;
				}
			}
		}
		if (players.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one player in the map");
		}
		int totalBoxes = boxes.values().stream().mapToInt(List::size).sum();
		if (totalBoxes != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes (" + totalBoxes
					+ ") does not equal number of destinations (" + destinations.size() + ")");
		}
		Set<Character> playerIds = players.keySet();
		Set<Character> boxPlayerIds = boxes.keySet().stream().map(Character::toUpperCase).collect(Collectors.toSet());
		for (char boxPlayerId : boxPlayerIds) {
			if (!playerIds.contains(boxPlayerId)) {
				throw new IllegalArgumentException(
						"Box with player id '" + boxPlayerId + "' has no corresponding player");
			}
		}
		for (char playerId : playerIds) {
			if (!boxPlayerIds.contains(playerId)) {
				throw new IllegalArgumentException("Player '" + playerId + "' has no corresponding boxes");
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

	public void putEntity(Position position, Entity entity) {
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
	 * @return  undo limit as Optional. Optional.empty() means unlimited undo (-1).
	 */
	public Optional<Integer> getUndoLimit() {
		return undoLimit == -1 ? Optional.empty() : Optional.of(undoLimit);
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

	/**
	 * Get the maximum height of the game map.
	 * @return  maximum height.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}
}
