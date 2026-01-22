
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Jeson Mor main game implementation.
 */
public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Get all available moves of one player.
     * This method is called when it is the {@link Player}'s turn to make a move.
     * It will iterate all {@link Piece}s belonging to the {@link Player} on board and obtain available moves of
     * each of the {@link Piece}s through method {@link Piece#getAvailableMoves(Game, Place)} of each {@link Piece}.
     * <p>
     * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
     *
     * @param player the player whose available moves to get
     * @return an array of available moves
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        boolean protection = (numMoves < configuration.getNumMovesProtection());

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    for (Move m : p.getAvailableMoves(this, src)) {
                        Place dst = m.getDestination();
                        // Must stay on board
                        if (dst.x() < 0 || dst.x() >= size || dst.y() < 0 || dst.y() >= size) {
                            continue;
                        }
                        Piece target = board[dst.x()][dst.y()];
                        // Can't land on own piece
                        if (target != null && target.getPlayer().equals(player)) {
                            continue;
                        }
                        // In protection, can't capture
                        if (protection && target != null && !target.getPlayer().equals(player)) {
                            continue;
                        }
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Start the game. Players alternate until someone wins by reaching central square,
     * by capturing the last opponent, or by deadlock (no legal replies).
     *
     * @return the winner
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
            currentPlayer = players[numMoves % players.length];
            Move[] available = getAvailableMoves(currentPlayer);
            // deadlock (current player cannot move) → the other player wins
            if (available.length == 0) {
                winner = players[(numMoves + 1) % players.length];
                break;
            }

            Move move = currentPlayer.nextMove(this, available);
            Piece moved = board[move.getSource().x()][move.getSource().y()];
            movePiece(move);
            updateScore(currentPlayer, moved, move);
            numMoves++;
            refreshOutput();

            // check central‐square win or capture win
            winner = getWinner(currentPlayer, moved, move);
            if (winner != null) {
                break;
            }
        }

        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        // capture‐all? (no opponent pieces left on board)
        boolean opponentHasAny = false;
        for (int x = 0; x < configuration.getSize() && !opponentHasAny; x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = board[x][y];
                if (p != null && !p.getPlayer().equals(lastPlayer)) {
                    opponentHasAny = true;
                    break;
                }
            }
        }
        if (!opponentHasAny) {
            return lastPlayer;
        }
        // central‐place win by Knight
        if (lastPiece instanceof Knight && lastMove.getDestination().equals(getCentralPlace())) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getSource().x() - move.getDestination().x());
        int dy = Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + dx + dy);
    }

    @Override
    public void movePiece(Move move) {
        Place s = move.getSource(), d = move.getDestination();
        Piece p = board[s.x()][s.y()];
        board[d.x()][d.y()] = p;
        board[s.x()][s.y()] = null;
    }
}