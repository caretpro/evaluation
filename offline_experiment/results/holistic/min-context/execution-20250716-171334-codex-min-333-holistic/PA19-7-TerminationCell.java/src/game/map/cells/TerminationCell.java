
// File: io/Deserializer.java
package io;

import game.map.cells.TerminationCell;            // ← add this import
import game.map.cells.TerminationCell.Type;       // ← …and this
import game.map.cells.Cell;
import util.Coordinate;
import util.Direction;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple level parser.
 */
public final class Deserializer {
    …
    private TerminationCell.CreateInfo parseTermination(String[] parts) {
        Coordinate coord = new Coordinate(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        Direction dir = Direction.valueOf(parts[3]);
        Type type = Type.valueOf(parts[4]);        // now compiles
        return new TerminationCell.CreateInfo(coord, dir);
    }

    public List<TerminationCell.CreateInfo> parseString(String s) {
        …
        // e.g. lines starting with "TERMINATION"
        if ("TERMINATION".equals(keyword)) {
            infos.add(parseTermination(tokens));
        }
        …
    }
    …
}