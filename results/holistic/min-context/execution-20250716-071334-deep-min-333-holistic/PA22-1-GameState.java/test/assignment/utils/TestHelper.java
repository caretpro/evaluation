package assignment.utils;

import assignment.game.GameMap;

/**
 * Helper class for testing.
 */
public class TestHelper {
    /**
     * @param mapText The map text for input.
     * @return The parsed map.
     */
    public static GameMap parseGameMap(String mapText) {
        return GameMap.parse(mapText);
    }
}
