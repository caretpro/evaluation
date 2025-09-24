
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
    // ... [all other existing methods remain exactly the same] ...

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must contain at least two lines");
        }
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be a valid integer (undo limit)");
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
                            if (!players.containsKey(playerChar)) {
                                throw new IllegalArgumentException("Box " + c + " has no corresponding player");
                            }
                            entities.put(pos, new Box(c - 'a'));
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
        if (boxCounts.values().stream().mapToInt(Integer::intValue).sum() != destinations.size()) {
            throw new IllegalArgumentException("Total number of boxes must match number of destinations");
        }
        for (char player : players.keySet()) {
            char boxChar = Character.toLowerCase(player);
            if (!boxCounts.containsKey(boxChar)) {
                throw new IllegalArgumentException("Player " + player + " has no corresponding boxes");
            }
        }
        return new GameMap(entities, destinations, undoLimit);
    }

    // ... [all other existing methods remain exactly the same] ...
}