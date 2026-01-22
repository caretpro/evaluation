 use {@link util.PipePatterns}
     * </p>
     *
     * @return the character representation of a termination cell in game
     */
    @Override
    public char toSingleChar() {
        if (type == Type.SOURCE) {
            return switch (pointingTo) {
                case UP -> '^';
                case DOWN -> 'v';
                case LEFT -> '<';
                case RIGHT -> '>';
            };
        } else {
            return switch (pointingTo) {
                case UP -> 'U';
                case DOWN -> 'V';
                case LEFT -> 'L';
                case RIGHT -> 'R';
            };
        }
    }

    public enum Type {
        SOURCE, SINK
    }

    /**
     * Data class encapsulating the coordinate and direction of the {@link TerminationCell}.
     */
    public static class CreateInfo {

        public final Coordinate coord;
        public final Direction dir;

        public CreateInfo(@NotNull Coordinate coord, @NotNull Direction dir) {
            this.coord = coord;
            this.dir = dir;
        }
    }
}