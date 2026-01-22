
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        // reset all things
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int currentPlayerIndex = 0;

        while (true) {
            // Determine current player
            currentPlayer = players[currentPlayerIndex];
            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, opponent wins
                winner = players[(currentPlayerIndex + 1) % 2];
                break;
            }
            // Get next move from player
            Move move = currentPlayer.nextMove(this, availableMoves);
            // Make the move
            movePiece(move);
            // Update score
            Piece movedPiece = getPiece(move.getDestination());
            updateScore(currentPlayer, movedPiece, move);
            // Increment move count
            this.numMoves++;
            // Check for winner
            winner = getWinner(currentPlayer, movedPiece, move);
            // Refresh output
            this.refreshOutput();
            // Check if game ended
            if (winner != null) {
                break;
            }
            // Switch to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % 2;
        }

        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Player[] players = getPlayers();
        Player opponent = (players[0].equals(lastPlayer)) ? players[1] : players[0];

        // Check if opponent's Knight is captured
        boolean opponentKnightExists = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(opponent) && piece instanceof Knight) {
                    opponentKnightExists = true;
                    break;
                }
            }
            if (opponentKnightExists) break;
        }
        if (!opponentKnightExists) {
            return lastPlayer; // lastPlayer wins
        }

        // Check if opponent has any available moves
        Move[] opponentMoves = getAvailableMoves(opponent);
        if (opponentMoves.length == 0) {
            return lastPlayer; // lastPlayer wins
        }

        // No winner yet
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
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
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place sourcePlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, sourcePlace);
                    for (Move m : pieceMoves) {
                        Place dest = m.getDestination();
                        // Validate move is within bounds
                        if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                            Piece targetPiece = getPiece(dest);
                            // Valid if target is empty or occupied by opponent
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