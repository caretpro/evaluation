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
		if (mapText == null) {
			throw new IllegalArgumentException("mapText must not be null");
		}
		String[] lines = mapText.split("\\R");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map must have at least undo limit and one row");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid undo limit: " + lines[0], ex);
		}
		if (undoLimit < -1 || undoLimit == -1 && undoLimit != -1) {
			throw new IllegalArgumentException("Undo limit must be >= 0 or -1 for unlimited");
		}
		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Character, Position> playerPositions = new HashMap<>();
		Map<Character, Integer> boxCounts = new HashMap<>();
		int height = lines.length - 1;
		for (int row = 0; row < height; row++) {
			String line = lines[row + 1];
			for (int col = 0; col < line.length(); col++) {
				char ch = line.charAt(col);
				Position pos = Position.of(col, row);
				switch (ch) {
				case '#':
					map.put(pos, new Wall());
					break;
				case '.':
					map.put(pos, new Empty());
					break;
				case '@':
					map.put(pos, new Empty());
					destinations.add(pos);
					break;
				default:
					if (Character.isUpperCase(ch)) {
						if (playerPositions.put(ch, pos) != null) {
							throw new IllegalArgumentException("Duplicate player letter: " + ch);
						}
						int pid = ch - 'A';
						map.put(pos, new Player(pid));
					} else if (Character.isLowerCase(ch)) {
						int pid = ch - 'a';
						map.put(pos, new Box(pid));
						boxCounts.merge(ch, 1, Integer::sum);
					} else {
						throw new IllegalArgumentException("Invalid character in map at " + pos + ": '" + ch + "'");
					}
				}
			}
		}
		if (playerPositions.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one player");
		}
		int totalBoxes = boxCounts.values().stream().mapToInt(Integer::intValue).sum();
		if (totalBoxes != destinations.size()) {
			throw new IllegalArgumentException(
					"Box count (" + totalBoxes + ") != destination count (" + destinations.size() + ")");
		}
		for (char boxCh : boxCounts.keySet()) {
			char playerCh = Character.toUpperCase(boxCh);
			if (!playerPositions.containsKey(playerCh)) {
				throw new IllegalArgumentException("Box '" + boxCh + "' has no matching player '" + playerCh + "'");
			}
		}
		for (char playerCh : playerPositions.keySet()) {
			char boxCh = Character.toLowerCase(playerCh);
			if (!boxCounts.containsKey(boxCh)) {
				throw new IllegalArgumentException(
						"Player '" + playerCh + "' has no corresponding box '" + boxCh + "'");
			}
		}
		return new GameMap(map, destinations, undoLimit);
	}

	/**
	 * Get the entity object at the given position.
	 * @param position  the position of the entity in the game map.
	 * @return  Entity object.
	 * @throws IllegalArgumentException  if position is out of bounds.
	 */
	public Entity getEntity(Position position) {
		Objects.requireNonNull(position, "position must not be null");
		int x = position.x();
		int y = position.y();
		if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
			throw new IllegalArgumentException(
					String.format("Position %s is out of bounds [0..%d)×[0..%d)", position, maxWidth, maxHeight));
		}
		return map.get(position);
	}

	/**
	 * Put one entity at the given position in the game map.
	 * @param position  the position in the game map to put the entity.
	 * @param entity    the entity to put into game map.
	 * @throws NullPointerException       if position or entity is null.
	 * @throws IllegalArgumentException   if position is out of bounds.
	 */
	public void putEntity(@NotNull Position position, @NotNull Entity entity) {
		Objects.requireNonNull(position, "position must not be null");
		Objects.requireNonNull(entity, "entity must not be null");
		int x = position.x();
		int y = position.y();
		if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
			throw new IllegalArgumentException(
					String.format("Position %s is out of bounds [0..%d)×[0..%d)", position, maxWidth, maxHeight));
		}
		map.put(position, entity);
	}

	/**
	 * Get all box destination positions as a set in the game map.
	 * @return  a set of positions.
	 */
	@Unmodifiable
	@NotNull
	public Set<Position> getDestinations() {
		return destinations;
	}

	/**
	 * Get the undo limit of the game map.
	 * @return  undo limit, or empty if unlimited.
	 */
	public Optional<Integer> getUndoLimit() {
		return undoLimit < 0 ? Optional.empty() : Optional.of(undoLimit);
	}

	/**
	 * Get all players' id as a set.
	 * @return  a set of player id.
	 */
	public Set<Integer> getPlayerIds() {
		return map.values().stream().filter(e -> e instanceof Player).map(e -> ((Player) e).getId())
				.collect(Collectors.toUnmodifiableSet());
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
