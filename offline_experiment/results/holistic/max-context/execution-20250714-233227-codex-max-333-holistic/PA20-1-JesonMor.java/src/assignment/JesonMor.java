
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the Jeson Mor game.
 */
public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Start the game.
     */
    @Override
    public Player start() {
        // reset game state
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int turnIndex = 0;

        // main game loop
        while (true) {
            this.currentPlayer = players[turnIndex];
            // fetch available moves
            Move[] moves = this.getAvailableMoves(this.currentPlayer);
            if (moves.length == 0) {
                // no legal moves -> other player wins
                winner = players[(turnIndex + 1) % players.length];
                break;
            }
            // ask player for their next move
            Move chosen = this.currentPlayer.nextMove(this, moves);
            // execute the move
            this.movePiece(chosen);
            // update score
            Piece moved = board[chosen.getDestination().x()][chosen.getDestination().y()];
            this.updateScore(this.currentPlayer, moved, chosen);
            this.numMoves++;
            // check for winner
            winner = this.getWinner(this.currentPlayer, moved, chosen);
            this.refreshOutput();
            if (winner != null) {
                break;
            }
            // next player's turn
            turnIndex = (turnIndex + 1) % players.length;
        }

        // announce the winner
        System.out.println();
        System.out.println("Congratulations!");
        System.out.printf("Winner: %s%s%s%n",
                winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    /**
     * Determine if the last move produced a winner.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // During protection phase no one can win or capture
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        Place dest = lastMove.getDestination();
        // Win by reaching the central square
        if (dest.equals(this.configuration.getCentralPlace())) {
            return lastPlayer;
        }
        // Win by capturing the opponent's Knight
        if (lastPiece instanceof Knight) {
            for (int x = 0; x < this.configuration.getSize(); x++) {
                for (int y = 0; y < this.configuration.getSize(); y++) {
                    Piece p = this.board[x][y];
                    if (p instanceof Knight && !p.getPlayer().equals(lastPlayer)) {
                        // an opponent knight still remains → no winner yet
                        return null;
                    }
                }
            }
            // no opponent knights left → lastPlayer wins
            return lastPlayer;
        }
        return null;
    }

    /**
     * Update the score of a player for the performed move (Manhattan distance).
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place s = move.getSource();
        Place d = move.getDestination();
        int dist = Math.abs(s.x() - d.x()) + Math.abs(s.y() - d.y());
        player.setScore(player.getScore() + dist);
    }

    /**
     * Execute the move on the board (including capturing).
     */
    @Override
    public void movePiece(@NotNull Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        Piece p = this.board[src.x()][src.y()];
        if (p == null) {
            throw new IllegalArgumentException("No piece at source " + src);
        }
        // remove from source
        this.board[src.x()][src.y()] = null;
        // place at destination (capturing if present)
        this.board[dst.x()][dst.y()] = p;
    }

    /**
     * Gather all valid moves for the given player.
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        List<Move> moves = new ArrayList<>();
        // scan board for player's pieces
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    // delegate to piece
                    for (Move m : p.getAvailableMoves(this, src)) {
                        // cannot land on own piece
                        Piece target = this.board[m.getDestination().x()][m.getDestination().y()];
                        if (target == null || !target.getPlayer().equals(player)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}