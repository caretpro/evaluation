
@Override
public boolean validate(Game game, Move move) {
    // Check if current move is within protected range and if it's a capture move
    if (game.getHistory().size() < numProtectedMoves && 
        move.getDestination().hasPiece() && 
        !move.getDestination().getPiece().getPlayer().equals(move.getPiece().getPlayer())) {
        return false; // Invalid move - capture within protected moves
    }
    return true; // Valid move
}