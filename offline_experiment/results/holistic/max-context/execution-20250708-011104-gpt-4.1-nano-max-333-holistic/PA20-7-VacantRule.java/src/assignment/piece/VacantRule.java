
@Override
public @NotNull Move[] getAvailableMoves(Player player) {
    List<Move> moves = new ArrayList<>();
    int size = configuration.getSize();
    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = getPiece(x, y);
            if (piece != null && piece.getPlayer().equals(player)) {
                // Generate moves based on piece type and game rules
                // For example, for a Knight piece:
                for (int[] delta : KNIGHT_MOVES) {
                    int newX = x + delta[0];
                    int newY = y + delta[1];
                    if (isValidPosition(newX, newY)) {
                        Piece targetPiece = getPiece(newX, newY);
                        if (targetPiece == null || !targetPiece.getPlayer().equals(player)) {
                            moves.add(new Move(new Place(x, y), new Place(newX, newY)));
                        }
                    }
                }
                // Add other move generation logic for different piece types
            }
        }
    }
    return moves.toArray(new Move[0]);
}

private boolean isValidPosition(int x, int y) {
    int size = configuration.getSize();
    return x >= 0 && x < size && y >= 0 && y < size;
}

// Define move offsets for Knight, for example
private static final int[][] KNIGHT_MOVES = {
    {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
    {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
};