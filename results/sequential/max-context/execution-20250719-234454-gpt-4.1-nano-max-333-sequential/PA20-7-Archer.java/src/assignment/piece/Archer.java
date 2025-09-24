
@Override
public Move[] getAvailableMoves(Game game, Place source) {
    ArrayList<Move> moves = new ArrayList<>();
    int size = game.getConfiguration().getSize();

    // Directions: up, down, left, right
    int[][] directions = { {0, 1}, {0, -1}, {1, 0}, {-1, 0} };

    for (int[] dir : directions) {
        int dx = dir[0];
        int dy = dir[1];

        boolean screenFound = false;
        int x = source.x();
        int y = source.y();

        while (true) {
            x += dx;
            y += dy;

            // Boundary check
            if (x < 0 || y < 0 || x >= size || y >= size) {
                break;
            }

            Place targetPlace = new Place(x, y);
            Piece targetPiece = game.getPiece(targetPlace);

            if (!screenFound) {
                // No screen yet
                if (targetPiece == null) {
                    // Empty square: move is valid
                    Move move = new Move(source, targetPlace);
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                } else {
                    // Found a piece: this is the screen
                    screenFound = true;
                }
            } else {
                // Screen already found, look for capturable piece
                if (targetPiece != null) {
                    // Can capture only opponent's piece
                    if (!targetPiece.getPlayer().equals(game.getCurrentPlayer())) {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    }
                    // Stop after encountering a piece
                    break;
                } else {
                    // Empty square beyond the screen, move is valid
                    Move move = new Move(source, targetPlace);
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                }
            }
        }
    }

    return moves.toArray(new Move[0]);
}