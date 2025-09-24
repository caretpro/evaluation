
package assignment.protocol;

public class Move {
    private final Place from;
    private final Place to;

    public Move(Place from, Place to) {
        this.from = from;
        this.to = to;
    }

    public Place from() {
        return this.from;
    }

    public Place to() {
        return this.to;
    }
}