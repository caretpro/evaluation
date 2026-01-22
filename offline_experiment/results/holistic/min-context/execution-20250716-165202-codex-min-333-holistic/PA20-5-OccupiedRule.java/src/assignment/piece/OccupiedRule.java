
// Game.java
package assignment.protocol;
public interface Game {
    /** Returns the piece at the given board coordinates, or null if empty. */
    Piece getPieceAt(int row, int column);
    // …
}