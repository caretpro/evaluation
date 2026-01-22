
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

	public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit, Map<Position, Entity> entities) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
		this.map = Collections.unmodifiableMap(entities);
	}

	private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
		this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
		this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
		this.destinations = Collections.unmodifiableSet(destinations);
		this.undoLimit = undoLimit;
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
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least two lines (undo limit and map).");
		}
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit format.");
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1.");
		}

		Set<Position> destinations = new HashSet<>();
		Map<Position, Entity> entities = new HashMap<>();
		Set<Character> upperPlayers = new HashSet<>();
		Set<Character> lowerBoxes = new HashSet<>();
		Map<Character, Player> playersByChar = new HashMap<>();
		List<Box> boxes = new ArrayList<>();
		AtomicInteger boxIdCounter = new AtomicInteger(1);
		Map<Character, Integer> playerIdMap = new HashMap<>();
		int playerIdCounter = 1;

		List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
		int height = mapLines.size();
		int width = mapLines.stream().mapToInt(String::length).max().orElse(0);

		for (int y = 0; y < height; y++) {
			String line = mapLines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);
				Position pos = new Position(x, y);
				switch (ch) {
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
						if (Character.isUpperCase(ch)) {
							if (upperPlayers.contains(ch)) {
								throw new IllegalArgumentException("Multiple players with same label: " + ch);
							}
							upperPlayers.add(ch);
							Player player = new Player(playerIdCounter);
							playerIdMap.put(ch, player.getId());
							playersByChar.put(ch, player);
							entities.put(pos, player);
							playerIdCounter++;
						} else if (Character.isLowerCase(ch)) {
							if (lowerBoxes.contains(ch)) {
								throw new IllegalArgumentException("Multiple boxes with same label: " + ch);
							}
							lowerBoxes.add(ch);
							Box box = new Box(boxIdCounter.getAndIncrement());
							// No setPlayerId method; assume box is unassociated initially
							boxes.add(box);
							entities.put(pos, box);
						} else {
							entities.put(pos, new Empty());
						}
						break;
				}
			}
		}

		long boxCount = boxes.size();
		long destinationCount = destinations.size();
		if (boxCount != destinationCount) {
			throw new IllegalArgumentException("Number of boxes does not match number of destinations.");
		}

		return new GameMap(width, height, destinations, undoLimit, entities);
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
		throw new UnsupportedOperationException("GameMap is immutable; create a new instance to modify.");
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
		return undoLimit >= 0 ? Optional.of(undoLimit) : Optional.empty();
	}

	/**
	 * Get all players' id as a set.
	 *
	 * @return a set of player id.
	 */
	public Set<Integer> getPlayerIds() {
		return map.values().stream()
				.filter(entity -> entity instanceof Player)
				.map(entity -> ((Player) entity).getId())
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