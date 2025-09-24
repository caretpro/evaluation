
@Override
public List<Move> getAvailableMoves(Game game) {
    List<Move> moves = new ArrayList<>();
    Place current = this.getPosition();
    Player owner = this.getPlayer();

    int[] directions = {-1, 0, 1};

    for (int dx : directions) {
        for (int dy : directions) {
            if (dx == 0 && dy == 0) continue; // skip no movement

            int step = 1;
            while (true) {
                int newX = current.getX() + dx * step;
                int newY = current.getY() + dy * step;
                Place newPlace = new Place(newX, newY);

                if (!game.isValidPlace(newPlace)) break;

                var pieceAtDest = game.getPiece(newPlace);
                if (pieceAtDest == null) {
                    moves.add(new Move(current, newPlace));
                } else {
                    if (!pieceAtDest.getPlayer().equals(owner)) {
                        moves.add(new Move(current, newPlace)); // capture
                    }
                    break; // cannot jump over pieces
                }
                step++;
            }
        }
    }
    return moves;
}