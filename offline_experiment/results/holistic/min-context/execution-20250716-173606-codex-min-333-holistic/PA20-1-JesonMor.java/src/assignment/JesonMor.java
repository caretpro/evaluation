
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Start the game
     * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until
     * a player wins.
     */
    @Override
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        while (true) {
            // 1. pick next player in round-robin
            currentPlayer = players[numMoves % players.length];

            // 2. get all valid moves for currentPlayer
            Move[] validMoves = getAvailableMoves(currentPlayer);

            // 3. ask player to choose next move
            Move selected = currentPlayer.nextMove(this, validMoves);

            // 4. apply the move
            movePiece(selected);

            // 5. update score
            Piece moved = board[selected.getTo().x()][selected.getTo().y()];
            updateScore(currentPlayer, moved, selected);

            // 6. increment move count and refresh output
            numMoves++;
            refreshOutput();

            // 7. check winner
            winner = getWinner(currentPlayer, moved, selected);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
        }
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null;
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (lastPiece instanceof Knight) {
            int toY = lastMove.getTo().y();
            int maxRow = board[0].length - 1;
            if (toY == 0 || toY == maxRow) {
                return lastPlayer;
            }
        }
        return null;
    }

    /**
     * Update the score of a player according to the Manhattan distance of the move.
     */
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getFrom().x() - move.getTo().x());
        int dy = Math.abs(move.getFrom().y() - move.getTo().y());
        player.setScore(player.getScore() + dx + dy);
    }

    /**
     * Make a move.
     */
    public void movePiece(Move move) {
        Place from = move.getFrom();
        Place to   = move.getTo();
        Piece p    = board[from.x()][from.y()];
        board[from.x()][from.y()] = null;
        board[to.x()][to.y()] = p;
        p.setPlace(to);
    }

    /**
     * Get all available moves of one player.
     */
    public Move[] getAvailableMoves(Player player) {
        List<Move> list = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = board[x][y];
                if (p != null && p.getOwner() == player) {
                    Place from = p.getPlace();
                    Move[] cand = p.getAvailableMoves(this, from);
                    for (Move m : cand) {
                        if (this.isValidMove(player, m)) {
                            list.add(m);
                        }
                    }
                }
            }
        }
        return list.toArray(new Move[0]);
    }
}