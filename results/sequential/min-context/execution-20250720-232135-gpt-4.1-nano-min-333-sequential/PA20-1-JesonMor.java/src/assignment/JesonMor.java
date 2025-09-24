
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> moves = new ArrayList<Move>();
    Piece[][] board = this.board; // Access the current game board
    for (int x = 0; x < board.length; x++) {
        for (int y = 0; y < board[x].length; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                // Retrieve potential moves from the piece
                Move[] potentialMoves = piece.getAvailableMoves(this, new Place(x, y));
                for (Move move : potentialMoves) {
                    // Validate move: ensure destination is within board bounds and not occupied by same player's piece
                    Place dest = move.getDestination();
                    int destX = dest.x();
                    int destY = dest.y();
                    if (destX >= 0 && destX < board.length && destY >= 0 && destY < board[destX].length) {
                        Piece targetPiece = board[destX][destY];
                        // Move is valid if destination is empty or occupied by opponent
                        if (targetPiece == null || !targetPiece.getPlayer().equals(player)) {
                            // Additional validation can be added here if needed
                            moves.add(move);
                        }
                    }
                }
            }
        }
    }
    return moves.toArray(new Move[0]);
}