
public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
    switch (c) {
        case 'W':
            return new Wall(coord);
        case '.':
            return new FillableCell(coord);
        case '^':
        case 'v':
        case '<':
        case '>':
            if (terminationType == null) return null;
            Direction dir = switch (c) {
                case '^' -> Direction.UP;
                case 'v' -> Direction.DOWN;
                case '<' -> Direction.LEFT;
                case '>' -> Direction.RIGHT;
                default -> throw new IllegalArgumentException("Invalid direction character");
            };
            return new TerminationCell(coord, dir, terminationType);
        default:
            return null;
    }
}