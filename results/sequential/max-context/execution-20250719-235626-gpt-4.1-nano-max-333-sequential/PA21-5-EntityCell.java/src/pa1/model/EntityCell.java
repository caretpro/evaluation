
public class EntityCell extends Cell {
    @Nullable
    private Entity entity = null;

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }
}