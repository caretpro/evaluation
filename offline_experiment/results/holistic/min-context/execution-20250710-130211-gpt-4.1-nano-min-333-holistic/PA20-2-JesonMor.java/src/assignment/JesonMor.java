
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
            // Determine the current player based on turn order
            int currentIndex = (numMoves % configuration.getPlayers().length);
            Player currentPlayer = configuration.getPlayers()[currentIndex];
            this.currentPlayer = currentPlayer;

            // Get available moves for current player
            Move[] moves = getAvailableMoves(currentPlayer);
            if (moves.length == 0) {
                // No moves available, current player loses
                winner = getOpponent(currentPlayer);
                break;
            }

            // Get the player's next move
            Move[] playerMoves = currentPlayer.nextMove(this, moves);
            if (playerMoves.length == 0) {
                // Player chooses no move, treat as pass or forfeit
                winner = getOpponent(currentPlayer);
                break;
            }

            // For simplicity, assume the first move is chosen
            Move chosenMove = playerMoves[0];

            // Make the move
            movePiece(chosenMove);
            numMoves++;
            refreshOutput();

            // Update score based on move
            updateScore(currentPlayer, getPieceAt(chosenMove.getSource()), chosenMove);

            // Check for winner
            Player winnerCandidate = getWinner(currentPlayer, getPieceAt(chosenMove.getDestination()), chosenMove);
            if (winnerCandidate != null) {
                winner = winnerCandidate;
                break;
            }
        }

        System.out.println();
        System.out.printf("Congratulations! %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Example winning condition: opponent has no pieces left
        Player opponent = getOpponent(lastPlayer);
        boolean opponentHasPieces = false;
        for (Piece[] row : board) {
            for (Piece p : row) {
                if (p != null && p.owner().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) break;
        }
        if (!opponentHasPieces) {
            return lastPlayer;
        }
        // Additional winning conditions can be added here
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece movingPiece = getPieceAt(source);
        if (movingPiece != null) {
            board[source.x()][source.y()] = null;
            board[destination.x()][destination.y()] = movingPiece;
        }
    }

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> movesList = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.owner().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    for (Move m : pieceMoves) {
                        // Validate move (assuming getAvailableMoves already returns valid moves)
                        movesList.add(m);
                    }
                }
            }
        }
        return movesList.toArray(new Move[0]);
    }

    private Piece getPieceAt(Place place) {
        return board[place.x()][place.y()];
    }

    private Player getOpponent(Player player) {
        for (Player p : configuration.getPlayers()) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return null; // Should not happen if two players exist
    }
}