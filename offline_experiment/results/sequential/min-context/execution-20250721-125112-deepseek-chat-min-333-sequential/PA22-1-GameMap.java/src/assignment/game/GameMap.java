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
		String[] lines = mapText.split("\n");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least 2 lines (undo limit and map)");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Undo limit must be an integer");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit must be -1 (unlimited) or non-negative");
		}
		Map<Position, Entity> entities = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Character, Position> players = new HashMap<>();
		Map<Character, Integer> boxCounts = new HashMap<>();
		int maxWidth = 0;
		int maxHeight = lines.length - 1;
		for (int y = 1; y < lines.length; y++) {
			String line = lines[y];
			maxWidth = Math.max(maxWidth, line.length());
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				Position pos = new Position(x, y - 1);
				switch (c) {
				case '#':
					entities.put(pos, new Wall());
					break;
				case '@':
					destinations.add(pos);
					entities.put(pos, new Empty());
					break;
				case '.':
					entities.put(pos, new Empty());
					break;
				default:
					if (Character.isUpperCase(c)) {
						if (players.containsKey(c)) {
							throw new IllegalArgumentException("Duplicate player: " + c);
						}
						players.put(c, pos);
						entities.put(pos, new Player(c - 'A'));
					} else if (Character.isLowerCase(c)) {
						char playerChar = Character.toUpperCase(c);
						entities.put(pos, new Box(playerChar - 'A'));
						boxCounts.put(c, boxCounts.getOrDefault(c, 0) + 1);
					} else {
						throw new IllegalArgumentException("Invalid character in map: " + c);
					}
				}
			}
		}
		if (players.isEmpty()) {
			throw new IllegalArgumentException("Map must contain at least one player");
		}
		for (Map.Entry<Character, Integer> entry : boxCounts.entrySet()) {
			char boxChar = entry.getKey();
			char playerChar = Character.toUpperCase(boxChar);
			if (!players.containsKey(playerChar)) {
				throw new IllegalArgumentException("Box " + boxChar + " has no corresponding player " + playerChar);
			}
		}
		for (Character playerChar : players.keySet()) {
			char boxChar = Character.toLowerCase(playerChar);
			if (!boxCounts.containsKey(boxChar)) {
				throw new IllegalArgumentException("Player " + playerChar + " has no corresponding boxes");
			}
		}
		int totalBoxes = boxCounts.values().stream().mapToInt(Integer::intValue).sum();
		if (totalBoxes != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes (" + totalBoxes
					+ ") does not match number of destinations (" + destinations.size() + ")");
		}
		return new GameMap(entities, destinations, undoLimit);
	}

	/**
	 * Get the entity object at the given position.
	 * @param position  the position of the entity in the game map.
	 * @return  Entity object.
	 */
	public Entity getEntity(Position position) {
		return map.get(position);
	}

	/**
	 * Put one entity at the given position in the game map.
	 * @param position  the position in the game map to put the entity.
	 * @param entity    the entity to put into game map.
	 */
	public void putEntity(Position position, Entity entity) {
		if (position.x() < 0 || position.x() >= maxWidth || position.y() < 0 || position.y() >= maxHeight) {
			throw new IllegalArgumentException("Position is out of bounds");
		}
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}
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

	/**
	 * Get the maximum height of the game map.
	 * @return  maximum height.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}
}
