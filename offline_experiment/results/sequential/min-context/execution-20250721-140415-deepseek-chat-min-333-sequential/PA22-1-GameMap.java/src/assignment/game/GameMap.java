
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;

import java.util.*;

public class GameMap {
    // ... (keep all existing fields and methods)

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must contain at least 2 lines");
        }
        int undoLimit = Integer.parseInt(lines[0].trim());
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be -1 or non-negative");
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
                        case '.':
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                        case '\f':
                            entities.put(pos, new Empty());
                            break;
                    default:
                        if (Character.isUpperCase(c)) {
                            players.put(c, pos);
                            entities.put(pos, new Player(c - 'A'));
                        } else if (Character.isLowerCase(c)) {
                            entities.put(pos, new Box(c - 'a'));
                            boxCounts.put(c, boxCounts.getOrDefault(c, 0) + 1);
                        }
                }
            }
        }
        
        return new GameMap(entities, destinations, undoLimit);
    }
    
    // ... (rest of the class)
}