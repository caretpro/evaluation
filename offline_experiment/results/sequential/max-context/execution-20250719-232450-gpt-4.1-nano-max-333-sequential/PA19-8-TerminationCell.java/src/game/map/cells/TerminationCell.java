
public TerminationCell(@NotNull Coordinate coord, @NotNull Direction pointingTo, @NotNull Type type) {
    super(coord);
    this.pointingTo = pointingTo;
    this.type = type;
    this.isFilled = false;
}