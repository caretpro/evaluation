
package assignment;

import assignment.protocol.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> validMoves = new ArrayList<>();
        int rows = board.length;
        int cols = board[0].length;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] candidateMoves = piece.getAvailableMoves(this, source);
                    for (Move move : candidateMoves) {
                        Place dest = move.getDestination();
                        int dx = dest.x();
                        int dy = dest.y();
                        if (dx >= 0 && dx < rows && dy >= 0 && dy < cols) {
                            // Check if move is valid: destination is empty or occupied by opponent
                            Piece destPiece = board[dx][dy];
                            if (destPiece == null || !destPiece.getPlayer().equals(player)) {
                                validMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return validMoves.toArray(new Move[0]);
    }

    @Override
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();
        Player[] players = configuration.getPlayers();
        int playerCount = players.length;
        int currentIndex = 0;

        while (true) {
            this.currentPlayer = players[currentIndex];
            Move[] moves = getAvailableMoves(this.currentPlayer);
            if (moves.length == 0) {
                // No moves for current player, check if opponent has moves
                int nextIndex = (currentIndex + 1) % playerCount;
                Player opponent = players[nextIndex];
                Move[] opponentMoves = getAvailableMoves(opponent);
                if (opponentMoves.length == 0) {
                    // Deadlock: no moves for both players, game ends with no winner
                    return null;
                }
                currentIndex = nextIndex;
                continue;
            }
            Move move = this.currentPlayer.nextMove(this, moves);
            movePiece(move);
            Piece movedPiece = this.board[move.getDestination().x()][move.getDestination().y()];
            updateScore(this.currentPlayer, movedPiece, move);
            winner = getWinner(this.currentPlayer, movedPiece, move);
            this.refreshOutput();
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
            currentIndex = (currentIndex + 1) % playerCount;
        }
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
        if (opponent == null) {
            return null;
        }
        Move[] opponentMoves = getAvailableMoves(opponent);
        if (opponentMoves.length == 0) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        int manhattanDistance = Math.abs(srcX - destX) + Math.abs(srcY - destY);
        int newScore = player.getScore() + manhattanDistance;
        player.setScore(newScore);
    }

    @Override
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = board[source.x()][source.y()];
        board[destination.x()][destination.y()] = piece;
        board[source.x()][source.y()] = null;
    }
}