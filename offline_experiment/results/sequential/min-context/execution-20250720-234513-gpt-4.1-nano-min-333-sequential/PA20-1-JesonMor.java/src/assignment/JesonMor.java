
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> allMoves = new ArrayList<Move>();
    for (int x = 0; x < board.length; x++) {
        for (int y = 0; y < board[x].length; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                // Retrieve available moves for this piece
                Move[] moves = piece.getAvailableMoves(this, new Place(x, y));
                for (Move move : moves) {
                    // Assuming getAvailableMoves already provides valid moves
                    allMoves.add(move);
                }
            }
        }
    }
    return allMoves.toArray(new Move[0]);
}