
@Override
public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
    if (this.numMoves < this.configuration.getNumMovesProtection()) {
        return null;
    }
    
    // Check central place win condition
    if (lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
        return lastPlayer;
    }
    
    // Check capture all win condition
    boolean opponentHasPieces = false;
    Player opponent = configuration.getPlayers()[0].equals(lastPlayer) 
        ? configuration.getPlayers()[1] 
        : configuration.getPlayers()[0];
    
    for (int x = 0; x < configuration.getSize(); x++) {
        for (int y = 0; y < configuration.getSize(); y++) {
            Piece piece = board[x][y];
            if (piece != null && piece.getPlayer().equals(opponent)) {
                opponentHasPieces = true;
                break;
            }
        }
        if (opponentHasPieces) break;
    }
    
    if (!opponentHasPieces) {
        return lastPlayer;
    }
    
    return null;
}