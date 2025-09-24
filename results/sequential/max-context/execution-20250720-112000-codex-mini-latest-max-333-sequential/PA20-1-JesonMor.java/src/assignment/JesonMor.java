
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JesonMor game logic.
 */
public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Get all available moves of one player.
     */
    @Override
    public Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        boolean protectPhase = numMoves < configuration.getNumMovesProtection();

        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = board[x][y];
                if (p != null && p.getPlayer().equals(player)) {
                    Place src = new Place(x, y);
                    for (Move m : p.getAvailableMoves(this, src)) {
                        Place dst = m.getDestination();
                        // must stay on board
                        if (dst.x() < 0 || dst.x() >= size || dst.y() < 0 || dst.y() >= size) {
                            continue;
                        }
                        // during protection phase cannot capture or move onto central place
                        if (protectPhase) {
                            if (board[dst.x()][dst.y()] != null) {
                                continue;
                            }
                            if (dst.equals(getCentralPlace())) {
                                continue;
                            }
                        }
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Main game loop—unchanged.
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
            int idx = this.numMoves % players.length;
            Player player = players[idx];
            this.currentPlayer = player;
            Move[] availableMoves = getAvailableMoves(player);
            Move move = player.nextMove(this, availableMoves);
            Piece lastPiece = getPiece(move.getSource());
            movePiece(move);
            this.numMoves++;
            updateScore(player, lastPiece, move);
            refreshOutput();
            winner = getWinner(player, lastPiece, move);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
        }
    }

    /**
     * Determine winner after each move.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Place src = lastMove.getSource();
        Place dst = lastMove.getDestination();
        boolean protectPhase = numMoves <= configuration.getNumMovesProtection();

        // 1) Leaving central square after protection => immediate win
        if (!protectPhase && src.equals(getCentralPlace())) {
            return lastPlayer;
        }

        // 2) Entering central square after protection => immediate win
        if (!protectPhase && dst.equals(getCentralPlace())) {
            return lastPlayer;
        }

        // 3) Surrounding opponent’s knight => win
        Player opponent = configuration.getPlayers()[0].equals(lastPlayer)
                ? configuration.getPlayers()[1]
                : configuration.getPlayers()[0];
        Place knightPos = null;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = board[x][y];
                if (p instanceof Knight && p.getPlayer().equals(opponent)) {
                    knightPos = new Place(x, y);
                    break;
                }
            }
            if (knightPos != null) break;
        }
        if (knightPos != null) {
            int trapped = 0;
            for (Place adj : List.of(
                    new Place(knightPos.x() + 1, knightPos.y()),
                    new Place(knightPos.x() - 1, knightPos.y()),
                    new Place(knightPos.x(), knightPos.y() + 1),
                    new Place(knightPos.x(), knightPos.y() - 1))) {
                if (adj.x() < 0 || adj.x() >= configuration.getSize()
                        || adj.y() < 0 || adj.y() >= configuration.getSize()
                        || board[adj.x()][adj.y()] != null
                        || adj.equals(getCentralPlace())) {
                    trapped++;
                }
            }
            if (trapped == 4) {
                return lastPlayer;
            }
        }

        // 4) Deadlock: if opponent has no moves either, higher score wins
        Move[] oppMoves = getAvailableMoves(opponent);
        if (oppMoves.length == 0 && getAvailableMoves(lastPlayer).length == 0) {
            return lastPlayer.getScore() >= opponent.getScore() ? lastPlayer : opponent;
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        int dist = Math.abs(src.x() - dst.x()) + Math.abs(src.y() - dst.y());
        player.setScore(player.getScore() + dist);
    }

    @Override
    public void movePiece(Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        Piece moving = board[src.x()][src.y()];
        board[src.x()][src.y()] = null;
        board[dst.x()][dst.y()] = moving;
    }
}