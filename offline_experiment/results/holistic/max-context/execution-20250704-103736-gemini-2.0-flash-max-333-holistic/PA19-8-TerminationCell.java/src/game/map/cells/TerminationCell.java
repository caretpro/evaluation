 use {@link util.PipePatterns}
	 * </p>
	 *
	 * @return the character representation of a termination cell in game
	 */
	@Override
	public char toSingleChar() {
		if (type == Type.SOURCE) {
			return switch (pointingTo) {
				case UP -> PipePatterns.SOURCE_UP;
				case DOWN -> PipePatterns.SOURCE_DOWN;
				case LEFT -> PipePatterns.SOURCE_LEFT;
				case RIGHT -> PipePatterns.SOURCE_RIGHT;
			};
		} else {
			return switch (pointingTo) {
				case UP -> PipePatterns.SINK_UP;
				case DOWN -> PipePatterns.SINK_DOWN;
				case LEFT -> PipePatterns.SINK_LEFT;
				case RIGHT -> PipePatterns.SINK_RIGHT;
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