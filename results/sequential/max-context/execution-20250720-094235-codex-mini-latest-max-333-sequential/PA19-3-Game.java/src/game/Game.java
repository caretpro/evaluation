
package io;

public final class Deserializer {
    private Deserializer() { }

    /** 
     * When invoked from main without a map file, tests/configs assume a default delay of 10. 
     */
    public static int parseDelay(int rows, int cols) {
        return 10;
    }

    /** 
     * Existing helper: parse a map from string rep. 
     */
    public static game.map.cells.Cell[][] parseString(int rows, int cols, String cellsRep) {
        // … your existing implementation …
        throw new UnsupportedOperationException("not shown");
    }
}