
@Override
public boolean validate(Game game, Move move) {
    if (!(game.getPiece(move.getSource()) instanceof Knight)) {
        return true;
    }
    
    int dx = move.getDestination().x() - move.getSource().x();
    int dy = move.getDestination().y() - move.getSource().y();
    
    // Knight moves in L-shape (2 squares in one direction and 1 square perpendicular)
    if (Math.abs(dx * dy) != 2) {
        return false;
    }
    
    // Check for blocking piece (one square in the direction of the longer move)
    int blockX = move.getSource().x() + (Math.abs(dx) == 2 ? dx/2 : 0);
    int blockY = move.getSource().y() + (Math.abs(dy) == 2 ? dy/2 : 0);
    
    return game.getPiece(new Place(blockX, blockY)) == null;
}