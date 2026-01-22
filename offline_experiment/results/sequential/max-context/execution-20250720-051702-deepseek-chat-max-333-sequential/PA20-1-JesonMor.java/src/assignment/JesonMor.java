
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    for (Move move : pieceMoves) {
                        if (move != null) {
                            Place dest = move.getDestination();
                            if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                                moves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        
        // Check central place win condition
        if (lastMove.getDestination().equals(this.getCentralPlace())) {
            return lastPlayer;
        }
        
        // Check if opponent has no pieces left
        Player opponent = Arrays.stream(this.configuration.getPlayers())
                              .filter(p -> !p.equals(lastPlayer))
                              .findFirst()
                              .orElse(null);
        
        if (opponent != null) {
            boolean opponentHasPieces = false;
            for (int x = 0; x < this.configuration.getSize(); x++) {
                for (int y = 0; y < this.configuration.getSize(); y++) {
                    Piece piece = this.board[x][y];
                    if (piece != null && piece.getPlayer().equals(opponent)) {
                        opponentHasPieces = true;
                        break;
                    }
                }
                if (opponentHasPieces) break;
            }
            if (!opponentHasPieces) {
                return lastPlayer;
            }
        }
        return null;
    }

    // Rest of the class remains unchanged...
}