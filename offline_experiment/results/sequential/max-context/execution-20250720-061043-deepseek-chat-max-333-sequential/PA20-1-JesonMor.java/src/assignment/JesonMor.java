
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> moves = new ArrayList<>();
    int size = configuration.getSize();
    
    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place currentPlace = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                if (pieceMoves != null) {
                    for (Move move : pieceMoves) {
                        Place dest = move.getDestination();
                        if (dest.x() >= 0 && dest.x() < size &&
                            dest.y() >= 0 && dest.y() < size &&
                            !dest.equals(currentPlace)) {
                            moves.add(move);
                        }
                    }
                }
            }
        }
    }
    return moves.toArray(new Move[0]);
}