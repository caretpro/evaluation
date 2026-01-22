 if a player has no pieces left, they lose
        for (Player player : configuration.getPlayers()) {
            boolean hasPiece = false;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    Piece p = board[x][y];
                    if (p != null && p.owner().equals(player)) {
                        hasPiece = true;
                        break;
                    }
                }
                if (hasPiece) break;
            }
            if (!hasPiece) {
                // Player has no pieces, they lose, so the other player wins
                for (Player p : configuration.getPlayers()) {
                    if (!p.equals(player)) {
                        return p;
                    }
                }
            }
        }
        // Alternatively, implement specific win conditions here
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // Calculate Manhattan distance between source and destination
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                       Math.abs(move.getSource().y() - move.getDestination().y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece movingPiece = board[source.x()][source.y()];
        // Move the piece
        board[destination.x()][destination.y()] = movingPiece;
        board[source.x()][source.y()] = null;
        // Optionally, update piece's internal position if needed
        // (Assuming Piece has a method to set position, if applicable)
        // movingPiece.setPosition(destination);
    }

    public Move[] getAvailableMoves(Player player) {
        List<Move> movesList = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.owner().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    // Get available moves for this piece
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    for (Move move : pieceMoves) {
                        // Validate move (assuming move is valid as per description)
                        // Additional validation can be added if needed
                        movesList.add(move);
                    }
                }
            }
        }
        return movesList.toArray(new Move[0]);
    }
}