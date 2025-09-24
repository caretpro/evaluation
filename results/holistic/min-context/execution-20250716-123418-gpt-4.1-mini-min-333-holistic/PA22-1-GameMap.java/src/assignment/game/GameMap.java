
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

public class GameMap {

	private final Map<Position, Entity> map;

	private final int maxWidth;

	private final int maxHeight;

	private final Set<Position> destinations;

	private final int undoLimit;

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
		if (mapText == null || mapText.isEmpty()) {
			throw new IllegalArgumentException("Map text cannot be null or empty");
		}
		String[] lines = mapText.split("\\R");
		if (lines.length < 2) {
			throw new IllegalArgumentException("Map text must contain at least undo limit and one map line");
		}

		// Parse undo limit
		int undoLimit;
		try {
			undoLimit = Integer.parseInt(lines[0].trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
		}
		if (undoLimit < -1) {
			throw new IllegalArgumentException("Undo limit cannot be less than -1");
		}

		// Map lines start from index 1
		int height = lines.length - 1;
		int width = 0;
		for (int i = 1; i < lines.length; i++) {
			width = Math.max(width, lines[i].length());
		}

		Map<Position, Entity> map = new HashMap<>();
		Set<Position> destinations = new HashSet<>();
		Map<Integer, Position> players = new HashMap<>();
		Map<Position, Box> boxes = new HashMap<>();

		for (int y = 0; y < height; y++) {
			String line = lines[y + 1];
			for (int x = 0; x < width; x++) {
				char c = x < line.length() ? line.charAt(x) : ' ';
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
					case ' ':
						// Treat space as empty (outside playable area)
						map.put(pos, new Empty());
						break;
					default:
						if (Character.isUpperCase(c)) {
							int playerId = c - 'A';
							if (players.containsKey(playerId)) {
								throw new IllegalArgumentException("Duplicate player id: " + c);
							}
							players.put(playerId, pos);
							map.put(pos, new Player(playerId));
						} else if (Character.isLowerCase(c)) {
							int playerId = c - 'a';
							Box box = new Box(playerId);
							boxes.put(pos, box);
							map.put(pos, box);
						} else {
							throw new IllegalArgumentException("Invalid character in map: '" + c + "' at (" + x + "," + y + ")");
						}
						break;
				}
			}
		}

		if (players.isEmpty()) {
			throw new IllegalArgumentException("No players found in the map");
		}

		if (boxes.size() != destinations.size()) {
			throw new IllegalArgumentException("Number of boxes (" + boxes.size() + ") does not equal number of destinations (" + destinations.size() + ")");
		}

		// Check that all boxes have matching players and vice versa
		Set<Integer> playerIds = players.keySet();
		Set<Integer> boxPlayerIds = boxes.values().stream().map(Box::getPlayerId).collect(Collectors.toSet());

		if (!playerIds.equals(boxPlayerIds)) {
			Set<Integer> missingBoxes = new HashSet<>(playerIds);
			missingBoxes.removeAll(boxPlayerIds);
			Set<Integer> missingPlayers = new HashSet<>(boxPlayerIds);
			missingPlayers.removeAll(playerIds);

			StringBuilder sb = new StringBuilder();
			if (!missingBoxes.isEmpty()) {
				sb.append("Players without boxes: ");
				sb.append(missingBoxes.stream().map(i -> String.valueOf((char) (i + 'A'))).sorted().collect(Collectors.toList()));
				sb.append(". ");
			}
			if (!missingPlayers.isEmpty()) {
				sb.append("Boxes without players: ");
				sb.append(missingPlayers.stream().map(i -> String.valueOf((char) (i + 'a'))).sorted().collect(Collectors.toList()));
				sb.append(". ");
			}
			throw new IllegalArgumentException(sb.toString());
		}

		return new GameMap(map, destinations, undoLimit);
	}

	public Entity getEntity(Position position) {
		return map.getOrDefault(position, new Empty());
	}

	public void putEntity(Position position, Entity entity) {
		throw new UnsupportedOperationException("GameMap is immutable; cannot put entity");
	}

	public Set<Position> getDestinations() {
		return destinations;
	}

	public Optional<Integer> getUndoLimit() {
		if (undoLimit == -1) {
			return Optional.empty();
		}
		return Optional.of(undoLimit);
	}

	public Set<Integer> getPlayerIds() {
		return map.values().stream()
				.filter(e -> e instanceof Player)
				.map(e -> ((Player) e).getId())  // Use getId() method
				.collect(Collectors.toSet());
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}
}