
/**
	 * @param coord  coordination of this cell
	 * @param direction  direction of this termination
	 * @param type  type of this termination
	 */
	public TerminationCell(Coordinate coord, Direction direction, Type type) {
		super(coord);
		this.pointingTo = direction;
		this.type = type;
	}