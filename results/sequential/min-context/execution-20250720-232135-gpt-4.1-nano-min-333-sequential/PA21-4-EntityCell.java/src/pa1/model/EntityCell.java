
public class EntityCell extends Cell {
    @Nullable
    private Entity entity = null;

    public EntityCell(@NotNull Position position) {
        super(position);
    }

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Override
    public char toUnicodeChar() {
        return entity != null ? entity.toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return entity != null ? entity.toASCIIChar() : '.';
    }
}