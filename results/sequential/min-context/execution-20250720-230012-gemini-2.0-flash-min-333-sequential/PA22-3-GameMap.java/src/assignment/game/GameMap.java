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
		String[] lines = mapText.split("\\r?\\n");
		if (lines.length < 1) {
			throw new IllegalArgumentException("Map text must have at least one line for undo limit.");
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
		Map<Character, Position> playerPositions = new HashMap<>();
		List<Character> boxChars = new ArrayList<>();
		List<Character> playerChars = new ArrayList<>();
		for (int y = 1; y < lines.length; y++) {
			String line = lines[y];
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				Position position = new Position(x, y - 1);
				switch (c) {
				case '#':
					map.put(position, new Wall());
					break;
				case '@':
					destinations.add(position);
					map.put(position, new Empty());
					break;
				case '.':
					map.put(position, new Empty());
					break;
				default:
					if (Character.isUpperCase(c)) {
						if (playerPositions.containsKey(c)) {
							throw new IllegalArgumentException("Multiple players with the same ID: " + c);
						}
						playerPositions.put(c, position);
						map.put(position, new Player(c - 'A'));
						playerChars.add(c);
					} else if (Character.isLowerCase(c)) {
						map.put(position, new Box(c - 'a'));
						boxChars.add(c);
					} else {
						throw new IllegalArgumentException("Invalid character in map: " + c);
					}
				}
			}
		}
		if (playerPositions.isEmpty()) {
			throw new IllegalArgumentException("No players in the map.");
		}
		if (boxChars.size() != destinations.size()) {
			throw new IllegalArgumentException("The number of boxes is not equal to the number of box destinations.");
		}
		Set<Integer> playerIds = playerPositions.keySet().stream().map(c -> (int) (c - 'A'))
				.collect(Collectors.toSet());
		Set<Integer> boxIds = boxChars.stream().map(c -> (int) (c - 'a')).collect(Collectors.toSet());
		if (!playerIds.equals(boxIds)) {
			throw new IllegalArgumentException("Players and boxes IDs do not match.");
		}
		int maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
		int maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
		return new GameMap(map, destinations, undoLimit);
	}

	public Entity getEntity(Position position) {
		return map.get(position);
	}

	/**
	 * Put one entity at the given position in the game map.
	 * @param position  the position in the game map to put the entity.
	 * @param entity    the entity to put into game map.
	 */
	public void putEntity(Position position, Entity entity) {
		map.put(position, entity);
	}

	/**
	 * Get all box destination positions as a set in the game map.
	 * @return  a set of positions.
	 */
	public Set<Position> getDestinations() {
		return this.destinations;
	}

	/**
	 * Get the undo limit of the game map.
	 * @return  undo limit.
	 */
	public Optional<Integer> getUndoLimit() {
		if (undoLimit == 0) {
			return Optional.empty();
		} else {
			return Optional.of(undoLimit);
		}
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
