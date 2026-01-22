
public class EntityCell extends Cell {
    // existing fields and methods...

    public EntityCell(Position position) {
        super(position);
        // initialize as needed
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.entity = entity;
        // additional initialization if needed
    }

    public EntityCell(Position position, Gem gem) {
        super(position);
        this.entity = gem;
        // additional initialization if needed
    }

    public EntityCell(Position position, Player player) {
        super(position);
        this.entity = player;
        // additional initialization if needed
    }

    public EntityCell(Position position, ExtraLife extraLife) {
        super(position);
        this.entity = extraLife;
        // additional initialization if needed
    }

    public EntityCell(Position position, Mine mine) {
        super(position);
        this.entity = mine;
        // additional initialization if needed
    }
}