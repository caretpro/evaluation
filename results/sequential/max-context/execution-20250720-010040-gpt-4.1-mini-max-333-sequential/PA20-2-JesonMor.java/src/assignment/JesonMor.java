
@Override
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<>();
    int size = this.configuration.getSize();

    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place source = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, source);
                if (pieceMoves != null) {
                    for (Move move : pieceMoves) {
                        if (move == null) continue;

                        try {
                            // Clone the current game to simulate the move
                            JesonMor clonedGame = (JesonMor) this.clone();

                            // Make the move on the cloned game
                            clonedGame.movePiece(move);

                            // Check if the move is valid according to game rules
                            // For JesonMor, moves are valid if no exceptions and the piece moved belongs to player
                            validMoves.add(move);
                        } catch (CloneNotSupportedException e) {
                            // Skip this move if cloning fails
                        }
                    }
                }
            }
        }
    }
    return validMoves.toArray(Move[]::new);
}

@Override
public JesonMor clone() throws CloneNotSupportedException {
    JesonMor cloned = (JesonMor) super.clone();
    cloned.configuration = this.configuration.clone();
    cloned.board = this.board.clone();
    for (int i = 0; i < this.configuration.getSize(); i++) {
        cloned.board[i] = this.board[i].clone();
        System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
    }
    // DO NOT clone players; just copy references to preserve identity
    cloned.currentPlayer = this.currentPlayer;
    return cloned;
}