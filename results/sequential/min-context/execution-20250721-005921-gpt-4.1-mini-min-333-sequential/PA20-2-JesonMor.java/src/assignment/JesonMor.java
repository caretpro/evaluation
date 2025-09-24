
@Override
public void movePiece(Move move) {
    Place from = move.from();
    Place to = move.to();
    Piece piece = board[from.x()][from.y()];
    board[to.x()][to.y()] = piece;
    board[from.x()][from.y()] = null;
}