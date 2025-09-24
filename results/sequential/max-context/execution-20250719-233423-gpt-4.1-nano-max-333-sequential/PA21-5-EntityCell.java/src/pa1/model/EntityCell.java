
public class EntityCell extends Cell {
    @Nullable
    Entity entity = null;

    public EntityCell(@NotNull Position position) {
        super(position);
        this.entity = null;
    }

    public EntityCell(@NotNull Position position, @Nullable Entity entity) {
        super(position);
        this.entity = entity;
        if (entity != null) {
            entity.setOwner(this);
        }
    }

    // Additional constructors for specific entity types if needed
    public EntityCell(@NotNull Position position, @Nullable Player player) {
        super(position);
        this.entity = player;
        if (player != null) {
            player.setOwner(this);
        }
    }

    public EntityCell(@NotNull Position position, @Nullable Mine mine) {
        super(position);
        this.entity = mine;
        if (mine != null) {
            mine.setOwner(this);
        }
    }

    public EntityCell(@NotNull Position position, @Nullable ExtraLife extraLife) {
        super(position);
        this.entity = extraLife;
        if (extraLife != null) {
            extraLife.setOwner(this);
        }
    }

    public EntityCell(@NotNull Position position, @Nullable Gem gem) {
        super(position);
        this.entity = gem;
        if (gem != null) {
            gem.setOwner(this);
        }
    }
}