
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
		String[] lines = mapText.split("\\r?\\n");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least two lines (undo limit and map)");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit format");
		}
		if (undoLimit < -1 || undoLimit == 0) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1 or zero");
		}
		List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
		int height = mapLines.size();
		int width = mapLines.stream().mapToInt(String::length).max().orElse(0);
		Set<Position> destinations = new HashSet<>();
		Set<Integer> playerIds = new HashSet<>();
		Map<Position, Entity> entityMap = new HashMap<>();
		Map<Integer, Position> playersPositions = new HashMap<>();
		Map<Character, Box> boxesMap = new HashMap<>();
		List<Box> boxesList = new ArrayList<>();
		for (int y = 0; y < height; y++) {
			String line = mapLines.get(y);
			for (int x = 0; x < width; x++) {
				char ch = x < line.length() ? line.charAt(x) : ' ';
				Position pos = new Position(x, y);
				switch (ch) {
				case '#':
					entityMap.put(pos, new Wall());
					break;
				case '@':
					destinations.add(pos);
					entityMap.put(pos, new Empty());
					break;
				case '.':
					entityMap.put(pos, new Empty());
					break;
				default:
					if (Character.isUpperCase(ch)) {
						int pid = (int) ch;
						if (playersPositions.containsValue(pos)) {
							throw new IllegalArgumentException("Multiple players with same ID: " + ch);
						}
						playerIds.add(pid);
						playersPositions.put(pid, pos);
						entityMap.put(pos, new Player(pid));
					} else if (Character.isLowerCase(ch)) {
						char upperIdChar = Character.toUpperCase(ch);
						int pid = (int) upperIdChar;
						Box box = new Box(upperIdChar);
						boxesMap.put(ch, box);
						boxesList.add(box);
						entityMap.put(pos, box);
					} else {
						entityMap.put(pos, new Empty());
					}
					break;
				}
			}
		}
		if (playersPositions.isEmpty()) {
			throw new IllegalArgumentException("No players found in the map");
		}
		int boxCount = boxesMap.size();
		int destinationCount = destinations.size();
		if (boxCount != destinationCount) {
			throw new IllegalArgumentException("Number of boxes (" + boxCount
					+ ") does not match number of destinations (" + destinationCount + ")");
		}
		for (Map.Entry<Character, Box> entry : boxesMap.entrySet()) {
			char boxChar = entry.getKey();
			Box box = entry.getValue();
			int playerId = box.getPlayerId();
			if (!playerIds.contains(playerId)) {
				throw new IllegalArgumentException(
						"Box with player ID " + playerId + " does not match any player");
			}
		}
		Set<Integer> playersWithBoxes = boxesMap.values().stream().map(Box::getPlayerId).collect(Collectors.toSet());
		for (Integer pid : playersPositions.keySet()) {
			if (!playersWithBoxes.contains(pid)) {
				throw new IllegalArgumentException("Player " + pid + " has no corresponding box");
			}
		}
		GameMap gameMap = new GameMap(width, height, destinations, undoLimit);
		for (Map.Entry<Position, Entity> entry : entityMap.entrySet()) {
			gameMap.putEntity(entry.getKey(), entry.getValue());
		}
		return gameMap;
	}

	public Entity getEntity(@NotNull Position position) {
		return map.get(position);
	}

	public void putEntity(Position position, Entity entity) {
		map.put(position, entity);
	}

	public Set<Position> getDestinations() {
		return destinations;
	}

	public Optional<Integer> getUndoLimit() {
		if (undoLimit == -1) {
			return Optional.empty();
		} else {
			return Optional.of(undoLimit);
		}
	}

	public Set<Integer> getPlayerIds() {
		return map.values().stream()
				.filter(entity -> entity instanceof Player)
				.map(entity -> ((Player) entity).getId())
				.collect(Collectors.toSet());
	}

	public int getMaxWidth() {
		return maxWidth;
	}
}