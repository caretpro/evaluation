
public abstract Player start();
public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
public abstract void updateScore(Player player, Piece piece, Move move);
public abstract void movePiece(@NotNull Move move);
public abstract @NotNull Move[] getAvailableMoves(Player player);