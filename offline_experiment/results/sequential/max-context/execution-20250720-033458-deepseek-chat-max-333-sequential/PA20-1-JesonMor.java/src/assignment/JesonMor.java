
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
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
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
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();
        while (true) {
            Player[] players = configuration.getPlayers();
            for (Player player : players) {
                this.currentPlayer = player;
                Move[] availableMoves = getAvailableMoves(player);
                if (availableMoves.length == 0) {
                    continue;
                }
                Move move = player.nextMove(this, availableMoves);
                Piece movedPiece = getPiece(move.getSource());
                movePiece(move);
                updateScore(player, movedPiece, move);
                this.numMoves++;
                this.refreshOutput();
                winner = getWinner(player, movedPiece, move);
                if (winner != null) {
                    break;
                }
            }
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Check if last move reached central place
        if (lastMove.getDestination().equals(this.getCentralPlace())) {
            return lastPlayer;
        }
        
        // Check if opponent has no pieces left
        Player opponent = Arrays.stream(this.getPlayers())
                              .filter(p -> !p.equals(lastPlayer))
                              .findFirst()
                              .orElse(null);
        
        if (opponent != null) {
            boolean opponentHasPieces = false;
            for (Piece[] row : this.board) {
                for (Piece piece : row) {
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
        
        // Check if last move left central place (special win condition)
        if (lastMove.getSource().equals(this.getCentralPlace())) {
            return lastPlayer;
        }
        
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = board[source.x()][source.y()];
        board[destination.x()][destination.y()] = piece;
        board[source.x()][source.y()] = null;
    }
}