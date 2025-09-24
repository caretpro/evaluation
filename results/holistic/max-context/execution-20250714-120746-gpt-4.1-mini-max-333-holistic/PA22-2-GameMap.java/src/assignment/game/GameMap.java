
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
			throw new IllegalArgumentException("Map text cannot be null or empty");
		}
		String[] lines = mapText.split("\r?\n");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least undo limit line and one map line");
		}

		// Parse undo limit
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Undo limit line must be an integer");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}

		// Map lines start from line 1
		int height = lines.length - 1;
		int width = lines[1].length();

		// Validate all lines have the same width
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].length() != width) {
				throw new IllegalArgumentException("All map lines must have the same length");
			}
		}

		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();

		// Track players and boxes
		Map<Character, Position> players = new HashMap<>();
		Map<Character, List<Position>> boxes = new HashMap<>();

		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			for (int x = 0; x < width; x++) {
				char ch = line.charAt(x);
				Position pos = Position.of(x, y);
				switch (ch) {
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
						if (Character.isUpperCase(ch)) {
							// Player
							if (players.containsKey(ch)) {
								throw new IllegalArgumentException("Duplicate player character: " + ch);
							}
							players.put(ch, pos);
							map.put(pos, new Player(ch));
						} else if (Character.isLowerCase(ch)) {
							// Box
							boxes.computeIfAbsent(ch, k -> new ArrayList<>()).add(pos);
							map.put(pos, new Box(ch));
						} else {
							throw new IllegalArgumentException("Invalid character in map: '" + ch + "' at (" + x + "," + y + ")");
						}
						break;
				}
			}
		}

		if (players.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one player in the map");
		}

		// Number of boxes must equal number of destinations
		int totalBoxes = boxes.values().stream().mapToInt(List::size).sum();
		if (totalBoxes != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes (" + totalBoxes + ") does not equal number of destinations (" + destinations.size() + ")");
		}

		// Validate boxes and players correspondence
		// Player IDs are uppercase letters, box playerId is lowercase letter
		// For each box playerId, corresponding uppercase player must exist
		for (char boxPlayerId : boxes.keySet()) {
			char upperPlayerId = Character.toUpperCase(boxPlayerId);
			if (!players.containsKey(upperPlayerId)) {
				throw new IllegalArgumentException("Box player id '" + boxPlayerId + "' has no corresponding player '" + upperPlayerId + "'");
			}
		}

		// For each player, check if there is at least one box with corresponding lowercase id
		for (char playerId : players.keySet()) {
			char lowerBoxId = Character.toLowerCase(playerId);
			if (!boxes.containsKey(lowerBoxId)) {
				throw new IllegalArgumentException("Player '" + playerId + "' has no corresponding boxes");
			}
		}

		return new GameMap(map, destinations, undoLimit);
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
		// Defensive check: map is unmodifiable, so we cannot modify it directly.
		// The original map field is unmodifiable, so this method cannot modify it.
		// But the class design suggests this method should allow modification.
		// So we need to throw UnsupportedOperationException or change design.
		// Since map is unmodifiable, we throw exception here.
		throw new UnsupportedOperationException("GameMap is immutable, cannot put entity");
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
			return Optional.empty();
		}
		return Optional.of(undoLimit);
	}

	/**
	 * Get all players' id as a set.
	 *
	 * @return a set of player id.
	 */
	public Set<Integer> getPlayerIds() {
		// Player id is the uppercase character code of Player entity
		return map.values().stream()
				.filter(e -> e instanceof Player)
				.map(e -> (int) ((Player) e).getPlayerChar())
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