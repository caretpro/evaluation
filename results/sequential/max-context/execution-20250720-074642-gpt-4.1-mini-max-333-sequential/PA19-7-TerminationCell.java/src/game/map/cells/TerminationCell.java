
public TerminationCell(@NotNull Coordinate coord, @NotNull Direction direction, @NotNull Type type) {
    super(coord);
    this.pointingTo = direction;
    this.type = type;
}