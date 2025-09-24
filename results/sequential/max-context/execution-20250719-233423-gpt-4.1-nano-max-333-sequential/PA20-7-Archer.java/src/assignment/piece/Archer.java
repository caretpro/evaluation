
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

import java.util.ArrayList;

public class Archer extends Piece {
    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int x = source.x();
        int y = source.y();

        int[][] directions = { {0, 1}, {0, -1}, {1, 0}, {-1, 0} };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            boolean screenFound = false;
            int step = 1;

            while (true) {
                int nx = x + dx * step;
                int ny = y + dy * step;

                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }

                Place targetPlace = new Place(nx, ny);
                Piece targetPiece = game.getPiece(targetPlace);

                if (!screenFound) {
                    // Moving over empty squares before the screen
                    if (targetPiece == null) {
                        Move move = new Move(source, targetPlace);
                        if (validateMove(game, move) && move.getSource().equals(source)) {
                            moves.add(move);
                        }
                        step++;
                    } else {
                        // Found the first obstacle - potential screen
                        screenFound = true;
                        step++;
                    }
                } else {
                    // After the screen, look for opponent's piece to capture
                    if (targetPiece != null) {
                        if (!targetPiece.getPlayer().equals(game.getCurrentPlayer())) {
                            Move move = new Move(source, targetPlace);
                            if (validateMove(game, move) && move.getSource().equals(source)) {
                                moves.add(move);
                            }
                        }
                        break; // Stop after attempting capture
                    } else {
                        // Empty square after screen - cannot move further
                        step++;
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}