
public class EntityCell extends Cell {
    @Nullable
    private Entity entity;

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    public Entity setEntity(@Nullable Entity newEntity) {
        Entity previous = this.entity;
        if (previous != null) {
            previous.setOwner(null);
        }
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
        this.entity = newEntity;
        return previous;
    }
}