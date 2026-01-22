
@Override
public @NotNull Move nextMove(Game game, Move[] availableMoves) {
    if (availableMoves.length == 0) {
        // No moves available, return null or handle as per game logic
        return null;
    }
    // Always select the first move to avoid index errors
    return availableMoves[0];
}