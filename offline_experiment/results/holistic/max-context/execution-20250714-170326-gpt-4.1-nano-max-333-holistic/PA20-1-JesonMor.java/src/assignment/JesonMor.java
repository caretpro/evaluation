
package assignment;

import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Jeson Mor game.
 */
public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        // Reset game state
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        // Set current player to the first Player object from configuration to maintain identity
        Player[] players = configuration.getPlayers();
        this.currentPlayer = players[0];
        this.refreshOutput();

        while (true) {
            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, current player loses
                winner = players[(0 + 1) % 2]; // Opponent
                break;
            }
            // Get next move from player
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            // Make the move
            movePiece(chosenMove);
            // Update game state
            Piece movedPiece = getPiece(chosenMove.getSource());
            updateScore(currentPlayer, movedPiece, chosenMove);
            // Check for winner
            winner = getWinner(currentPlayer, movedPiece, chosenMove);
            this.numMoves++;
            this.refreshOutput();

            if (winner != null) {
                break;
            }
            // Switch to next player using the same Player object references
            int nextIndex = (0 + 1) % 2; // Since only two players
            this.currentPlayer = players[nextIndex];
        }
        System.out.println();
        System.out.printf("Congratulations! %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Player[] players = configuration.getPlayers();
        Player opponent = null;
        for (Player p : players) {
            if (!p.equals(lastPlayer)) {
                opponent = p;
                break;
            }
        }
        // Check if opponent's central piece is captured
        Piece centralPiece = getPiece(configuration.getCentralPlace());
        if (centralPiece == null || !centralPiece.getPlayer().equals(opponent)) {
            // Opponent's central piece is gone, lastPlayer wins
            return lastPlayer;
        }
        // Check if opponent has any remaining pieces
        boolean opponentHasPieces = false;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = getPiece(x, y);
                if (p != null && p.getPlayer().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) break;
        }
        if (!opponentHasPieces) {
            return lastPlayer;
        }
        // No winner yet
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        // Calculate Manhattan distance between source and destination
        Place source = move.getSource();
        Place dest = move.getDestination();
        int distance = Math.abs(source.x() - dest.x()) + Math.abs(source.y() - dest.y());
        // Update player's score
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(@NotNull Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        Piece movingPiece = getPiece(source);
        // Move the piece
        board[dest.x()][dest.y()] = movingPiece;
        board[source.x()][source.y()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place sourcePlace = new Place(x, y);
                    for (Move m : piece.getAvailableMoves(this, sourcePlace)) {
                        // Validate move: ensure destination is within bounds and not occupied by same player's piece
                        Place dest = m.getDestination();
                        if (dest.x() >= 0 && dest.x() < board.length && dest.y() >= 0 && dest.y() < board.length) {
                            Piece targetPiece = getPiece(dest);
                            if (targetPiece == null || !targetPiece.getPlayer().equals(player)) {
                                moves.add(m);
                            }
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}