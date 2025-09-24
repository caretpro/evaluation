
public class JesonMor extends Game {
    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> validMoves = new ArrayList<>();
        int size = this.configuration.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Place sourcePlace = new Place(x, y);
                Piece piece = getPiece(sourcePlace);
                if (piece != null && piece.getPlayer().equals(player)) {
                    // Get all available moves for this piece
                    Move[] potentialMoves = piece.getAvailableMoves(this, sourcePlace);
                    for (Move move : potentialMoves) {
                        Place dest = move.getDestination();
                        // Check if destination is within bounds
                        if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                            Piece destPiece = getPiece(dest);
                            // Move is valid if destination is empty or occupied by opponent
                            if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                                // Additional validation: ensure move is legal (e.g., not moving onto own piece)
                                validMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return validMoves.toArray(new Move[0]);
    }
}