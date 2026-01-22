
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<>();
    int size = this.configuration.getSize();

    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Place sourcePlace = new Place(x, y);
            Piece piece = getPiece(sourcePlace);
            if (piece != null && piece.getPlayer().equals(player)) {
                // Get all available moves for this piece
                Move[] moves = piece.getAvailableMoves(this, sourcePlace);
                for (Move move : moves) {
                    Place dest = move.getDestination();
                    // Check if destination is within bounds
                    if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                        Piece destPiece = getPiece(dest);
                        // Valid if destination is empty or occupied by opponent
                        if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                            validMoves.add(move);
                        }
                    }
                }
            }
        }
    }
    return validMoves.toArray(new Move[0]);
}