
package assignment;

import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
                winner = players[(currentPlayerIndex + 1) % players.length];
                break;
            }
            
            Move move = currentPlayer.nextMove(this, availableMoves);
            Piece movedPiece = getPiece(move.getSource());
            movePiece(move);
            numMoves++;
            
            updateScore(currentPlayer, movedPiece, move);
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
        // Check if piece reached central place
        if (lastMove.getDestination().equals(getCentralPlace())) {
            return lastPlayer;
        }
        
        // Check if all opponent pieces are captured
        Player opponent = configuration.getPlayers()[0].equals(lastPlayer) ? 
                          configuration.getPlayers()[1] : configuration.getPlayers()[0];
        
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(opponent)) {
                    return null;
                }
            }
        }
        return lastPlayer;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().x() - move.getSource().x()) + 
                      Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Piece piece = board[move.getSource().x()][move.getSource().y()];
        board[move.getSource().x()][move.getSource().y()] = null;
        board[move.getDestination().x()][move.getDestination().y()] = piece;
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    
                    for (Move move : pieceMoves) {
                        Place dest = move.getDestination();
                        if (dest.x() >= 0 && dest.x() < configuration.getSize() && 
                            dest.y() >= 0 && dest.y() < configuration.getSize()) {
                            
                            Piece destPiece = board[dest.x()][dest.y()];
                            if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                                if (numMoves >= configuration.getNumMovesProtection() || 
                                    destPiece == null || 
                                    !destPiece.getPlayer().equals(player)) {
                                    availableMoves.add(move);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return availableMoves.toArray(new Move[0]);
    }
}