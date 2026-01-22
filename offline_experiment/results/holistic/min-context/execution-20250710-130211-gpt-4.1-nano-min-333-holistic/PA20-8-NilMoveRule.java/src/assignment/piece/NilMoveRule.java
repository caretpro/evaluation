
package assignment.protocol;

import java.util.Objects;

/**
 * Represents a move in the game, with source and destination positions.
 */
public class Move {
    private final Position source;
    private final Position destination;

    public Move(Position source, Position destination) {
        this.source = source;
        this.destination = destination;
    }

    public Position getSource() {
        return source;
    }

    public Position getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return source.equals(move.source) && destination.equals(move.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}

/**
 * Represents a position on the game board.
 */
class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}