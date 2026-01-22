
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<>();
    for (int x = 0; x < board.length; x++) {
        for (int y = 0; y < board[x].length; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place currentPlace = new Place(x, y);
                for (Move move : piece.getAvailableMoves(this, currentPlace)) {
                    Place dest = move.getDestination();
                    Piece destPiece = board[dest.x()][dest.y()];
                    if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                        validMoves.add(move);
                    }
                }
            }
        }
    }
    return validMoves.toArray(new Move[0]);
}