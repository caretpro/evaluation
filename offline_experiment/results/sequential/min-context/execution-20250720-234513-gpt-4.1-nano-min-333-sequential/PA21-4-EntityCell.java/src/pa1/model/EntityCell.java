
public class EntityCell extends Cell {
    @Nullable
    private Entity entity;
    private final Position position;

    public EntityCell(Position position) {
        super(position);
        this.position = position;
        this.entity = null;
    }

    public EntityCell(Position position, Entity entity) {
        super(position);
        this.position = position;
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    // Getters and setters if needed
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    public Position getPosition() {
        return position;
    }
}