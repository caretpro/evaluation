
public class Player extends Entity {
    private final Position position;
    private final int id;

    public Player(Position position, int id) {
        this.position = position;
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }
}