//en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    /**
     * Returns an array of moves that are valid given the current place of the piece.
     * Given the {@link Game} object and the {@link Place} that current knight piece locates, this method should
     * return ALL VALID {@link Move}s according to the current {@link Place} of this knight piece.
     * All the returned {@link Move} should have source equal to the source parameter.
     * <p>
     * Hint: you should consider corner cases when the {@link Move} is not valid on the gameboard.
     * Several tests are provided and your implementation should pass them.
     * <p>
     * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int boardSize = game.getConfiguration().getBoardSize();

        // Check horizontal moves
        addHorizontalMoves(game, source, moves, boardSize);

        // Check vertical moves
        addVerticalMoves(game, source, moves, boardSize);

        return moves.toArray(new Move[0]);
    }

    private void addHorizontalMoves(Game game, Place source, List<Move> moves, int boardSize) {
        int row = source.getRow();
        int col = source.getCol();

        // Check right direction
        addMovesInDirection(game, source, moves, row, col, 0, 1, boardSize);

        // Check left direction
        addMovesInDirection(game, source, moves, row, col, 0, -1, boardSize);
    }

    private void addVerticalMoves(Game game, Place source, List<Move> moves, int boardSize) {
        int row = source.getRow();
        int col = source.getCol();

        // Check up direction
        addMovesInDirection(game, source, moves, row, col, -1, 0, boardSize);

        // Check down direction
        addMovesInDirection(game, source, moves, row, col, 1, 0, boardSize);
    }

    private void addMovesInDirection(Game game, Place source, List<Move> moves, int row, int col, int rowIncrement,
                                     int colIncrement, int boardSize) {
        int count = 0;
        for (int i = 1; i < boardSize; i++) {
            int newRow = row + rowIncrement * i;
            int newCol = col + colIncrement * i;

            if (newRow < 0 || newRow >= boardSize || newCol < 0 || newCol >= boardSize) {
                break; // Out of board
            }

            Place destination = new Place(newRow, newCol);
            Move move = new Move(source, destination);

            if (game.getPiece(destination) != null) {
                count++;
                if (count == 2) {
                    if (game.getPiece(destination).getPlayer() != this.getPlayer()) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    }
                    break;
                }
            } else {
                if (count == 0) {
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                } else {
                    // If there is one piece in between, then it cannot move to an empty place
                    break;
                }
            }
        }
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[]{new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(),};
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}