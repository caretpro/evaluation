
package assignment;

import assignment.piece.Knight;
import assignment.protocol.Color;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
        // reset all things
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int turn = 0;

        while (true) {
            currentPlayer = players[turn % players.length];
            Move[] moves = getAvailableMoves(currentPlayer);
            Move choice = currentPlayer.nextMove(this, moves);

            movePiece(choice);
            numMoves++;
            updateScore(currentPlayer,
                        board[choice.to().x()][choice.to().y()],
                        choice);
            refreshOutput();

            winner = getWinner(currentPlayer,
                               board[choice.to().x()][choice.to().y()],
                               choice);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n",
                                  winner.getColor(),
                                  winner.getName(),
                                  Color.DEFAULT);
                return winner;
            }
            turn++;
        }
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null;
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (!(lastPiece instanceof Knight)) {
            return null;
        }
        int x = lastMove.to().x();
        // win when knight reaches opponent baseline
        if (lastPlayer.getColor() == Color.WHITE && x == board.length - 1) {
            return lastPlayer;
        }
        if (lastPlayer.getColor() == Color.BLACK && x == 0) {
            return lastPlayer;
        }
        return null;
    }

    /**
     * Update score by Manhattan distance of the move.
     */
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.from().x() - move.to().x());
        int dy = Math.abs(move.from().y() - move.to().y());
        player.setScore(player.getScore() + dx + dy);
    }

    /**
     * Physically move a piece on the board.
     */
    public void movePiece(Move move) {
        Place src = move.from();
        Place dst = move.to();
        Piece p = board[src.x()][src.y()];
        board[dst.x()][dst.y()] = p;
        board[src.x()][src.y()] = null;
    }

    /**
     * Gather all valid moves for player's pieces.
     */
    public Move[] getAvailableMoves(Player player) {
        List<Move> all = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece p = board[i][j];
                if (p != null && p.getPlayer() == player) {
                    Move[] arr = p.getAvailableMoves(this, Place.of(i, j));
                    all.addAll(Arrays.asList(arr));
                }
            }
        }
        return all.toArray(Move[]::new);
    }
}