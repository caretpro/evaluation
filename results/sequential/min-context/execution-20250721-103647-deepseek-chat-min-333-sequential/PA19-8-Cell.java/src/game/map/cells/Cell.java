
public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
    switch (Character.toUpperCase(c)) {
        case 'W':
            return new Cell(coord) { // Anonymous wall cell implementation
                @Override
                public char toSingleChar() {
                    return 'W';
                }
            };
        case '.':
            return new FillableCell(coord);
        case '^':
            return terminationType != null ? new TerminationCell(coord, Direction.UP, terminationType) : null;
        case 'V':
            return terminationType != null ? new TerminationCell(coord, Direction.DOWN, terminationType) : null;
        case '<':
            return terminationType != null ? new TerminationCell(coord, Direction.LEFT, terminationType) : null;
        case '>':
            return terminationType != null ? new TerminationCell(coord, Direction.RIGHT, terminationType) : null;
        default:
            return null;
    }
}