
public class EntityCell extends Cell {
    @Nullable
    private Entity entity = null;

    public EntityCell(@NotNull Position position) {
        super(position);
    }

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(@Nullable Entity newEntity) {
        this.entity = newEntity;
        if (newEntity != null) {
            newEntity.setOwner(this);
        }
    }
}