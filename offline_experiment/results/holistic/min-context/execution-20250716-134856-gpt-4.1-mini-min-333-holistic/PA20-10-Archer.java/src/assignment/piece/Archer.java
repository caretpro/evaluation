
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
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
        int srcRow = source.getRow();
        int srcCol = source.getColumn();

        // Directions: up, down, left, right
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        var board = game.board();

        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];

            // Step 1: Move along empty squares without jumping any piece
            int r = srcRow + dRow;
            int c = srcCol + dCol;
            while (board.isValid(r, c) && board.getPiece(r, c) == null) {
                Move move = new Move(source, new Place(r, c));
                if (validateMove(game, move)) {
                    moves.add(move);
                }
                r += dRow;
                c += dCol;
            }

            // Step 2: Find the screen piece to jump over
            if (!board.isValid(r, c)) {
                continue; // no screen piece in this direction
            }
            Piece screenPiece = board.getPiece(r, c);
            if (screenPiece == null) {
                continue; // no screen piece
            }

            // Step 3: After the screen, look for capture or vacant squares
            r += dRow;
            c += dCol;
            while (board.isValid(r, c)) {
                Piece targetPiece = board.getPiece(r, c);
                Move move = new Move(source, new Place(r, c));
                if (targetPiece == null) {
                    // Can move to empty square after screen
                    if (validateMove(game, move)) {
                        moves.add(move);
                    }
                } else {
                    // Can capture only if target piece belongs to opponent
                    if (!targetPiece.getPlayer().equals(this.getPlayer())) {
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    }
                    break; // cannot jump over second piece
                }
                r += dRow;
                c += dCol;
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}