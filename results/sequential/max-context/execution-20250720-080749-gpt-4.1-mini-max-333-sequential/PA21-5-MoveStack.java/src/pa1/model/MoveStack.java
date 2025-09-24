
public void push(final MoveResult move) {
    Objects.requireNonNull(move, "move cannot be null");
    moves.add(move);
    if (popCount > 0) {
        popCount--;
    }
}