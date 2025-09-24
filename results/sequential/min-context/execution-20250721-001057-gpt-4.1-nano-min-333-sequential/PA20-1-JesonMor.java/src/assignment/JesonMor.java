
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<Move>();
    for (int x = 0; x < board.length; x++) {
        for (int y = 0; y < board[x].length; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place currentPlace = new Place(x, y);
                for (Move move : piece.getAvailableMoves(this, currentPlace)) {
                    Place dest = move.getDestination();
                    int dx = dest.x();
                    int dy = dest.y();
                    if (dx >= 0 && dx < board.length && dy >= 0 && dy < board[0].length) {
                        Piece targetPiece = board[dx][dy];
                        if (targetPiece == null || !targetPiece.getPlayer().equals(player)) {
                            validMoves.add(move);
                        }
                    }
                }
            }
        }
    }
    return validMoves.toArray(new Move[0]);
}