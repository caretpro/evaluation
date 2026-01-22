
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

	private Map<Position, Entity> map;

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
		this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
		this.undoLimit = undoLimit;
		this.map = new HashMap<>();
	}

	private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
		this.map = new HashMap<>(map);
		this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
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
			throw new IllegalArgumentException("Map text is empty");
		}
		// First line: undo limit
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

		int height = lines.length - 1;
		int width = 0;
		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			if (line.length() > width) {
				width = line.length();
			}
		}

		int playerCount = 0;
		int boxCount = 0;

		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);
				Position pos = Position.of(x, y);
				switch (ch) {
					case '#':
						entities.put(pos, new Wall());
						break;
					case '@':
						destinations.add(pos);
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
							Player player = new Player(ch - 'A' + 1); // Assign ID 1..26
							playersMap.put(ch, player);
							entities.put(pos, player);
							playerCount++;
						} else if (Character.isLowerCase(ch)) {
							// Box
							char upper = Character.toUpperCase(ch);
							Box box = new Box(upper - 'A' + 1);
							boxesMap.put(ch, box);
							entities.put(pos, box);
							boxCount++;
						} else {
							// Empty or unrecognized character
							entities.put(pos, new Empty());
						}
						break;
				}
			}
		}

		if (playerCount == 0) {
			throw new IllegalArgumentException("No players found in the map");
		}

		// Only validate boxes if any are present
		if (!boxesMap.isEmpty()) {
			long totalBoxes = boxesMap.size();
			if (totalBoxes != destinations.size()) {
				throw new IllegalArgumentException("Number of boxes does not match number of destinations");
			}
			// Validate boxes' player IDs
			for (Box box : boxesMap.values()) {
				int playerId = box.getPlayerId();
				if (playerId < 1 || playerId > 26) {
					throw new IllegalArgumentException("Box has invalid player ID: " + playerId);
				}
				// Check if corresponding player exists
				boolean playerExists = playersMap.values().stream()
						.anyMatch(p -> p.getId() == playerId);
				if (!playerExists) {
					throw new IllegalArgumentException("Box's player ID " + playerId + " does not match any player");
				}
			}
		}

		return new GameMap(width, height, destinations, undoLimit).withEntities(entities);
	}

	// Helper method to create a new GameMap with entities
	private GameMap withEntities(Map<Position, Entity> entities) {
		this.map = new HashMap<>(entities);
		return this;
	}

	public Entity getEntity(@NotNull Position position) {
		return map.getOrDefault(position, null);
	}

	public void putEntity(@NotNull Position position, Entity entity) {
		if (entity == null || entity instanceof Empty) {
			map.remove(position);
		} else {
			map.put(position, entity);
		}
	}

	public Set<Position> getDestinations() {
		return destinations;
	}

	public Optional<Integer> getUndoLimit() {
		return undoLimit >= 0 ? Optional.of(undoLimit) : (undoLimit == -1 ? Optional.of(-1) : Optional.empty());
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

	public int getMaxHeight() {
		return maxHeight;
	}
}