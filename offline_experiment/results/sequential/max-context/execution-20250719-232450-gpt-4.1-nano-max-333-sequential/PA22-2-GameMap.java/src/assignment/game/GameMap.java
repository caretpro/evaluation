
package assignment.game;

import assignment.entities.Entity;
import assignment.entities.Wall;
import assignment.entities.Player;
import assignment.entities.Box;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Parses a map text into a GameMap instance.
 */
public static GameMap parse(String mapText) {
    if (mapText == null || mapText.isEmpty()) {
        throw new IllegalArgumentException("Map text cannot be null or empty");
    }
    String[] lines = mapText.split("\\r?\\n");
    if (lines.length < 1) {
        throw new IllegalArgumentException("Map text must contain at least one line for undo limit");
    }
    int undoLimit;
    try {
        undoLimit = Integer.parseInt(lines[0].trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid undo limit format");
    }
    if (undoLimit < -1) {
        throw new IllegalArgumentException("Undo limit cannot be less than -1");
    }

    Set<Position> destinations = new HashSet<>();
    Map<Position, Entity> mapEntities = new HashMap<>();
    Map<Character, Position> playersPositions = new HashMap<>();
    Set<Integer> playerIds = new HashSet<>();
    int maxWidth = 0;
    int height = lines.length - 1;

    for (int y = 0; y < height; y++) {
        String line = lines[y + 1];
        maxWidth = Math.max(maxWidth, line.length());
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            Position pos = Position.of(x, y);
            switch (ch) {
                case '#':
                    mapEntities.put(pos, new Wall());
                    break;
                case '@':
                    destinations.add(pos);
                    break;
                case '.':
                    // Empty cell, do nothing
                    break;
                default:
                    if (Character.isUpperCase(ch)) {
                        if (playersPositions.containsKey(ch)) {
                            throw new IllegalArgumentException("Multiple players with same label: " + ch);
                        }
                        playersPositions.put(ch, pos);
                        int playerId = ch - 'A' + 1;
                        if (playerIds.contains(playerId)) {
                            throw new IllegalArgumentException("Duplicate player ID for label: " + ch);
                        }
                        playerIds.add(playerId);
                        mapEntities.put(pos, new Player(playerId));
                    } else if (Character.isLowerCase(ch)) {
                        mapEntities.put(pos, new Box(ch));
                    }
                    // Ignore other characters
                    break;
            }
        }
    }

    if (playersPositions.isEmpty()) {
        throw new IllegalArgumentException("No players found in the map");
    }

    long boxCount = mapEntities.values().stream()
            .filter(e -> e instanceof Box)
            .count();
    long destinationCount = destinations.size();

    if (boxCount != destinationCount) {
        throw new IllegalArgumentException("Number of boxes (" + boxCount + ") does not match number of destinations (" + destinationCount + ")");
    }

    // Validate boxes' player IDs
    for (Entity entity : mapEntities.values()) {
        if (entity instanceof Box) {
            int pid = ((Box) entity).getPlayerId();
            if (pid < 1 || pid > 26 || !playerIds.contains(pid)) {
                throw new IllegalArgumentException("Box at " + entity + " has invalid player ID: " + pid);
            }
        }
    }

    // Validate each player has at least one box
    Set<Integer> playersWithBoxes = new HashSet<>();
    for (Entity entity : mapEntities.values()) {
        if (entity instanceof Box) {
            int pid = ((Box) entity).getPlayerId();
            playersWithBoxes.add(pid);
        }
    }
    for (int pid : playerIds) {
        if (!playersWithBoxes.contains(pid)) {
            throw new IllegalArgumentException("Player with ID " + pid + " has no boxes");
        }
    }

    // Instantiate GameMap
    return new GameMap(maxWidth, height, destinations, undoLimit) {
        {
            for (Map.Entry<Position, Entity> entry : mapEntities.entrySet()) {
                putEntity(entry.getKey(), entry.getValue());
            }
        }
    };
}