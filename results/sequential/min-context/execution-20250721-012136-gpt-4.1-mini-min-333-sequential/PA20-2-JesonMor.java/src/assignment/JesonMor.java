
@Override
public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
    Player[] players = configuration.getPlayers();
    Player opponent = null;
    for (Player p : players) {
        if (!p.equals(lastPlayer)) {
            opponent = p;
            break;
        }
    }
    if (opponent == null) {
        return null;
    }
    for (int x = 0; x < board.length; x++) {
        for (int y = 0; y < board[x].length; y++) {
            Piece piece = board[x][y];
            if (piece != null) {
                // Use getPlayer() to get owner and check class name string for King
                if (piece.getClass().getSimpleName().equals("King") && piece.getPlayer().equals(opponent)) {
                    return null;
                }
            }
        }
    }
    return lastPlayer;
}