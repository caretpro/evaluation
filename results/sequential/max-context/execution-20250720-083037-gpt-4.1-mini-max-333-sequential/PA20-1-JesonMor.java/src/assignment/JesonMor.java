
@Override
public Move[] getAvailableMoves(Player player) {
    ArrayList<Move> validMoves = new ArrayList<>();
    int size = configuration.getSize();

    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(player)) {
                Place source = new Place(x, y);
                Move[] pieceMoves = piece.getAvailableMoves(this, source);
                if (pieceMoves != null) {
                    for (Move move : pieceMoves) {
                        Place dest = move.getDestination();
                        // Check destination is inside board
                        if (dest.x() < 0 || dest.x() >= size || dest.y() < 0 || dest.y() >= size) {
                            continue;
                        }
                        // Check move validity without cloning the entire game
                        Piece destPiece = board[dest.x()][dest.y()];
                        boolean isCapture = destPiece != null && !destPiece.getPlayer().equals(player);

                        // During protection moves, no capture or win allowed
                        if (this.numMoves < this.configuration.getNumMovesProtection()) {
                            if (isCapture) {
                                continue; // capture not allowed during protection
                            }
                            // Check if move leads to a win (moving to central place)
                            if (dest.equals(this.getCentralPlace())) {
                                continue; // winning move not allowed during protection
                            }
                        }

                        // Simulate move on a temporary board to check for validity
                        Piece[][] tempBoard = new Piece[size][size];
                        for (int i = 0; i < size; i++) {
                            tempBoard[i] = board[i].clone();
                        }
                        // Move piece on temp board
                        tempBoard[dest.x()][dest.y()] = tempBoard[x][y];
                        tempBoard[x][y] = null;

                        // Check if after move, player still has pieces (optional, depending on rules)
                        boolean playerHasPieces = false;
                        for (int i = 0; i < size && !playerHasPieces; i++) {
                            for (int j = 0; j < size; j++) {
                                Piece p = tempBoard[i][j];
                                if (p != null && p.getPlayer().equals(player)) {
                                    playerHasPieces = true;
                                    break;
                                }
                            }
                        }
                        if (!playerHasPieces) {
                            continue; // invalid move if player loses all pieces (optional)
                        }

                        // If all checks passed, add the move
                        validMoves.add(move);
                    }
                }
            }
        }
    }
    return validMoves.toArray(new Move[0]);
}