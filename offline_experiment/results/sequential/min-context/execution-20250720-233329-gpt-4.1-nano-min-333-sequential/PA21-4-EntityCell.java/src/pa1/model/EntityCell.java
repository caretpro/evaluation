
public EntityCell(final @NotNull Position position, final @Nullable Entity entity) {
    super(position);
    if (entity != null) {
        entity.setOwner(this);
        this.entity = entity;
    }
}