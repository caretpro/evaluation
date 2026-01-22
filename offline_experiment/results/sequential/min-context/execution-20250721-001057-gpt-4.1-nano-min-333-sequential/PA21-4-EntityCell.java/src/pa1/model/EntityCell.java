
public class EntityCell extends Cell {
    @Nullable
    Entity entity = null;

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setCell(this);
        }
    }

    public EntityCell(@NotNull Position position) {
        this(position, null);
    }
}