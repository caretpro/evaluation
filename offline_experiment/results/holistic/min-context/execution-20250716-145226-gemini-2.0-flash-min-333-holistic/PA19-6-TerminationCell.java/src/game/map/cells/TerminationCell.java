 use {@link util.PipePatterns}
	 * </p>
	 *
	 * @return the character representation of a termination cell in game
	 */
	@Override
	public char toSingleChar() {
		if (type == Type.SOURCE) {
			return PipePatterns.get(PipePatterns.SOURCE, pointingTo);
		} else {
			return PipePatterns.get(PipePatterns.SINK, pointingTo);
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