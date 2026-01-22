
public class EntityCell extends Cell {
    @Nullable
    Entity entity = null;

    public EntityCell(@NotNull Position position) {
        super(position);
        this.entity = null;
    }

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        if (entity != null) {
            setEntity(entity);
        } else {
            this.entity = null;
        }
    }
}