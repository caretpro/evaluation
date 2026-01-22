
@Override
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<>();
    int size = this.configuration.getSize();

    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = this.board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place source = new Place(x, y);
                Move[] candidateMoves = piece.getAvailableMoves(this, source);
                if (candidateMoves != null) {
                    for (Move move : candidateMoves) {
                        if (move == null) continue;
                        try {
                            // Clone the game to simulate the move
                            JesonMor clonedGame = (JesonMor) this.clone();
                            // Make the move on the cloned game
                            clonedGame.movePiece(move);
                            // Update score on cloned game
                            Piece movedPiece = clonedGame.getPiece(move.getDestination());
                            clonedGame.updateScore(player, movedPiece, move);
                            // Check if move is valid: no winner yet if protection active
                            Player winner = clonedGame.getWinner(player, movedPiece, move);
                            if (this.numMoves < this.configuration.getNumMovesProtection()) {
                                // During protection moves, no winner allowed
                                if (winner == null) {
                                    validMoves.add(move);
                                }
                            } else {
                                // After protection moves, any move is valid
                                validMoves.add(move);
                            }
                        } catch (CloneNotSupportedException e) {
                            // Should not happen, but skip move if cloning fails
                        }
                    }
                }
            }
        }
    }
    return validMoves.toArray(Move[]::new);
}