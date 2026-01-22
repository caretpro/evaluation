
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
	 * Get the maximum height of the game map.
	 *
	 * @return maximum height.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}

	public static GameMap parse(String mapText) {
		String[] lines = mapText.split("\\r?\\n");
		if (lines.length == 0) {
			throw new IllegalArgumentException("Map text is empty.");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit: " + lines[0], e);
		}
		if (undoLimit < -1 || undoLimit == 0) {
			throw new IllegalArgumentException("Invalid undo limit: " + undoLimit + ". It should be -1 or positive.");
		}
		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Character, Integer> playerIds = new HashMap<>();
		Set<Character> boxChars = new HashSet<>();
		List<Player> players = new ArrayList<>();
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
						map.put(position, new Empty());
						destinations.add(position);
						break;
					case '.':
						map.put(position, new Empty());
						break;
					case '+':
						map.put(position, new Player(0)); // Assuming player ID 0 for '+'
						destinations.add(position);
						break;
					case '*':
						map.put(position, new Box(0)); // Assuming box player ID 0 for '*'
						destinations.add(position);
						break;
					default:
						if (Character.isUpperCase(c)) {
							if (playerIds.containsKey(c)) {
								throw new IllegalArgumentException("Multiple players with the same ID: " + c);
							}
							int playerId = c - 'A';
							playerIds.put(c, playerId);
							Player player = new Player(playerId);
							map.put(position, player);
						} else if (Character.isLowerCase(c)) {
							boxChars.add(c);
							int playerId = c - 'a';
							Box box = new Box(playerId);
							map.put(position, box);
						} else {
							throw new IllegalArgumentException("Invalid character in map: " + c);
						}
				}
			}
		}
		if (players.isEmpty() && playerIds.isEmpty()) {
			throw new IllegalArgumentException("No players in the map.");
		}
		if (boxChars.size() != destinations.size() && boxChars.size() != destinations.size() -1 && !boxChars.isEmpty()) {
			throw new IllegalArgumentException("The number of boxes is not equal to the number of box destinations.");
		}
		Set<Integer> existingPlayerIds = playerIds.values().stream().collect(Collectors.toSet());
		for (char boxChar : boxChars) {
			int playerId = boxChar - 'a';
			if (!existingPlayerIds.contains(playerId)) {
				throw new IllegalArgumentException("Box with ID " + boxChar + " does not have a corresponding player.");
			}
		}
		Set<Integer> boxPlayerIds = boxChars.stream().map(c -> (int) (c - 'a')).collect(Collectors.toSet());
		for (int playerId : existingPlayerIds) {
			if (!boxPlayerIds.contains(playerId)) {
				throw new IllegalArgumentException(
						"Player with ID " + (char) ('A' + playerId) + " does not have a corresponding box.");
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
		Set<Integer> playerIds = new HashSet<>();
		for (Entity entity : map.values()) {
			if (entity instanceof Player) {
				playerIds.add(((Player) entity).getId());
			}
		}
		return playerIds;
	}

	public int getMaxWidth() {
		return maxWidth;
	}
}