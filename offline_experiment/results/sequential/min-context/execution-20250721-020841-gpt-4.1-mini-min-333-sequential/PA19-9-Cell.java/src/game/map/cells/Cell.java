
public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
    return switch (c) {
        case 'W' -> new Wall(coord);
        case '.' -> new EmptyCell(coord);
        case '^' -> terminationType == null ? null : new TerminationCell(coord, Direction.UP, terminationType);
        case 'v' -> terminationType == null ? null : new TerminationCell(coord, Direction.DOWN, terminationType);
        case '<' -> terminationType == null ? null : new TerminationCell(coord, Direction.LEFT, terminationType);
        case '>' -> terminationType == null ? null : new TerminationCell(coord, Direction.RIGHT, terminationType);
        default -> null;
    };
}