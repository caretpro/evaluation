
/**
 * Undoes the last pop operation by pushing back the last popped move.
 *
 * @param move The {@link MoveResult} to restore to the top of the stack.
 */
public void undoPop(final MoveResult move) {
    Objects.requireNonNull(move, "move cannot be null");
    moves.add(move);
    // Do not decrement popCount here, as popCount tracks total pops performed.
}