
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
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();
        
        while (true) {
            for (Player player : configuration.getPlayers()) {
                this.currentPlayer = player;
                Move[] availableMoves = getAvailableMoves(player);
                
                if (availableMoves.length == 0) {
                    continue;
                }
                
                Move move = player.nextMove(this, availableMoves);
                movePiece(move);
                updateScore(player, board[move.getDestination().x()][move.getDestination().y()], move);
                refreshOutput();
                
                winner = getWinner(player, board[move.getDestination().x()][move.getDestination().y()], move);
                if (winner != null) {
                    System.out.println();
                    System.out.println("Congratulations! ");
                    System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                    return winner;
                }
            }
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (lastPiece instanceof Knight) {
            return lastPlayer;
        }
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) + 
                       Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Piece piece = board[move.getSource().x()][move.getSource().y()];
        board[move.getDestination().x()][move.getDestination().y()] = piece;
        board[move.getSource().x()][move.getSource().y()] = null;
        numMoves++;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place place = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    moves.addAll(Arrays.asList(pieceMoves));
                }
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}