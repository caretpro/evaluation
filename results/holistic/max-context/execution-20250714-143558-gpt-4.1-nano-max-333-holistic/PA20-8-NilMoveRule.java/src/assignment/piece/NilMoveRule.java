
@Override
public @NotNull Move[] getAvailableMoves(@NotNull Player player) {
    List<Move> moves = new ArrayList<>();
    int size = configuration.getSize();

    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = getPiece(x, y);
            if (piece != null && piece.getPlayer().equals(player)) {
                // Generate moves for this piece
                // For example, for a knight:
                int[][] directions = {
                    {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                    {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
                };
                for (int[] dir : directions) {
                    int newX = x + dir[0];
                    int newY = y + dir[1];
                    if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                        Piece targetPiece = getPiece(newX, newY);
                        if (targetPiece == null || !targetPiece.getPlayer().equals(player)) {
                            moves.add(new Move(x, y, newX, newY));
                        }
                    }
                }
                // Add other move generation logic based on piece type
            }
        }
    }
    return moves.toArray(new Move[0]);
}