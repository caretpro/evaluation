
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
        
        int playerIndex = 0;
        Player[] players = configuration.getPlayers();
        
        while (true) {
            currentPlayer = players[playerIndex];
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            
            if (availableMoves.length == 0) {
                playerIndex = (playerIndex + 1) % players.length;
                continue;
            }
            
            Move move = currentPlayer.nextMove(this, availableMoves);
            movePiece(move);
            updateScore(currentPlayer, board[move.getDestination().x()][move.getDestination().y()], move);
            refreshOutput();
            
            winner = getWinner(currentPlayer, board[move.getDestination().x()][move.getDestination().y()], move);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
            
            playerIndex = (playerIndex + 1) % players.length;
            numMoves++;
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
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place place = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
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