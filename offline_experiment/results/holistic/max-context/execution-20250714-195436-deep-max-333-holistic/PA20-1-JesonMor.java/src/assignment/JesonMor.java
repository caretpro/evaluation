
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
        this.currentPlayer = configuration.getPlayers()[0];
        this.refreshOutput();
        
        while (winner == null) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            
            if (availableMoves.length == 0) {
                winner = getOpponent(currentPlayer);
                break;
            }
            
            Move move = currentPlayer.nextMove(this, availableMoves);
            movePiece(move);
            numMoves++;
            updateScore(currentPlayer, getPiece(move.getDestination()), move);
            refreshOutput();
            
            winner = getWinner(currentPlayer, getPiece(move.getDestination()), move);
            currentPlayer = getOpponent(currentPlayer);
        }
        
        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    private Player getOpponent(Player player) {
        return player.equals(configuration.getPlayers()[0]) ? 
               configuration.getPlayers()[1] : configuration.getPlayers()[0];
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Check if reached central place
        if (lastMove.getDestination().equals(configuration.getCentralPlace())) {
            return lastPlayer;
        }
        
        // Check if captured all opponent pieces
        Player opponent = getOpponent(lastPlayer);
        boolean opponentHasPieces = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
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
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    moves.addAll(Arrays.asList(piece.getAvailableMoves(this, new Place(x, y))));
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}