
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
        
        Player[] players = configuration.getPlayers();
        int currentPlayerIndex = 0;
        
        while (winner == null) {
            currentPlayer = players[currentPlayerIndex];
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            
            if (availableMoves.length == 0) {
                winner = players[currentPlayerIndex];
                break;
            }
            
            Move move = currentPlayer.nextMove(this, availableMoves);
            Piece movedPiece = getPiece(move.getSource());
            
            movePiece(move);
            updateScore(currentPlayer, movedPiece, move);
            numMoves++;
            refreshOutput();
            
            winner = getWinner(currentPlayer, movedPiece, move);
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }
        
        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Check if opponent has no pieces left
        Player opponent = Arrays.stream(configuration.getPlayers())
            .filter(p -> !p.equals(lastPlayer))
            .findFirst()
            .orElse(null);
        
        boolean opponentHasPieces = false;
        for (Piece[] row : board) {
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
        
        // Check central place capture
        if (lastMove.getDestination().equals(getCentralPlace())) {
            return lastPlayer;
        }
        
        // Check if last move left central place
        if (lastMove.getSource().equals(getCentralPlace())) {
            return opponent;
        }
        
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        int distance = dx + dy;
        if (distance == 0) distance = 1; // Ensure at least 1 point for any move
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Piece piece = board[move.getSource().x()][move.getSource().y()];
        board[move.getSource().x()][move.getSource().y()] = null;
        board[move.getDestination().x()][move.getDestination().y()] = piece;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    
                    for (Move move : pieceMoves) {
                        Place dest = move.getDestination();
                        if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                            Piece destPiece = board[dest.x()][dest.y()];
                            if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                                availableMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        
        return availableMoves.toArray(new Move[0]);
    }
}