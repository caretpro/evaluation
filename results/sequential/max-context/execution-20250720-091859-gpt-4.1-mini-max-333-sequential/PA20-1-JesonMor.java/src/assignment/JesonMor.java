
@Override
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> moves = new ArrayList<>();
    int size = configuration.getSize();
    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place source = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, source);
                if (pieceMoves != null) {
                    for (Move move : pieceMoves) {
                        if (move == null) continue;
                        Place dest = move.getDestination();
                        // Validate destination is inside board bounds
                        if (dest.x() < 0 || dest.x() >= size || dest.y() < 0 || dest.y() >= size) {
                            continue;
                        }
                        // Validate source matches piece position
                        Place src = move.getSource();
                        if (src.x() != x || src.y() != y) {
                            continue;
                        }
                        // Destination cannot be occupied by player's own piece
                        Piece destPiece = board[dest.x()][dest.y()];
                        if (destPiece != null && destPiece.getPlayer().equals(player)) {
                            continue;
                        }
                        moves.add(move);
                    }
                }
            }
        }
    }
    return moves.toArray(Move[]::new);
}