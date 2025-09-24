
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
                Piece piece = board[move.getSource().x()][move.getSource().y()];
                
                movePiece(move);
                updateScore(player, piece, move);
                refreshOutput();
                
                winner = getWinner(player, piece, move);
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
        if (board[lastMove.getDestination().x()][lastMove.getDestination().y()] instanceof Knight) {
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
                if (board[x][y] != null && board[x][y].getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = board[x][y].getAvailableMoves(this, currentPlace);
                    
                    for (Move move : pieceMoves) {
                        if (isValidMove(move)) {
                            moves.add(move);
                        }
                    }
                }
            }
        }
        
        return moves.toArray(new Move[0]);
    }
    
    private boolean isValidMove(Move move) {
        if (move.getDestination().x() < 0 || move.getDestination().x() >= board.length ||
            move.getDestination().y() < 0 || move.getDestination().y() >= board[0].length) {
            return false;
        }
        
        Piece destPiece = board[move.getDestination().x()][move.getDestination().y()];
        if (destPiece != null && destPiece.getPlayer().equals(board[move.getSource().x()][move.getSource().y()].getPlayer())) {
            return false;
        }
        
        return true;
    }
}