
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in Chinese chess.
 * Rules of move of Archer can be found in Wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
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
     * All the returned {@link Move}s should have source equal to the source parameter.
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
        ArrayList<Move> moves = new ArrayList<>();
        int[] directions = {-1, 1}; // for row and column traversal

        int currentRow = source.row; // access public fields directly
        int currentCol = source.col;

        // Check all four directions: up, down
        for (int delta : directions) {
            int row = currentRow + delta;
            int col = currentCol;
            boolean jumped = false; // to track if we've jumped over a piece
            while (game.getBoard().isWithinBounds(row, col)) {
                Place targetPlace = new Place(row, col);
                Piece targetPiece = game.getBoard().getPieceAt(targetPlace);
                if (targetPiece == null) {
                    if (!jumped) {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    }
                    row += delta;
                } else {
                    if (!jumped) {
                        jumped = true;
                        row += delta;
                    } else {
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, targetPlace);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Left and Right
        for (int delta : directions) {
            int row = currentRow;
            int col = currentCol + delta;
            boolean jumped = false;
            while (game.getBoard().isWithinBounds(row, col)) {
                Place targetPlace = new Place(row, col);
                Piece targetPiece = game.getBoard().getPieceAt(targetPlace);
                if (targetPiece == null) {
                    if (!jumped) {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    } else {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move)) {
                            moves.add(move);
                        }
                    }
                    col += delta;
                } else {
                    if (!jumped) {
                        jumped = true;
                        col += delta;
                    } else {
                        if (targetPiece.getPlayer() != this.getPlayer()) {
                            Move move = new Move(source, targetPlace);
                            if (validateMove(game, move)) {
                                moves.add(move);
                            }
                        }
                        break;
                    }
                }
            }
        }

        return moves.toArray(new Move[0]);
    }

    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(), };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}