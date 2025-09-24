
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

    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    moves.addAll(Arrays.asList(pieceMoves));
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
        
        // Check opponent elimination
        Player opponent = Arrays.stream(this.getPlayers())
                              .filter(p -> !p.getName().equals(lastPlayer.getName()))
                              .findFirst()
                              .orElse(null);
        
        if (opponent != null) {
            boolean opponentHasPieces = false;
            for (int x = 0; x < this.configuration.getSize(); x++) {
                for (int y = 0; y < this.configuration.getSize(); y++) {
                    Piece piece = this.getPiece(x, y);
                    if (piece != null && piece.getPlayer().getName().equals(opponent.getName())) {
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

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int distance = Math.abs(destination.x() - source.x()) + Math.abs(destination.y() - source.y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source.x() >= 0 && source.x() < board.length &&
            source.y() >= 0 && source.y() < board[0].length &&
            destination.x() >= 0 && destination.x() < board.length &&
            destination.y() >= 0 && destination.y() < board[0].length) {
            Piece piece = board[source.x()][source.y()];
            board[destination.x()][destination.y()] = piece;
            board[source.x()][source.y()] = null;
        }
    }

    // Rest of the class remains unchanged...
}