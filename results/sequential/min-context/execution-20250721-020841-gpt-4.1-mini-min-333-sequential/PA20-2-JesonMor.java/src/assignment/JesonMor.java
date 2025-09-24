
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
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.player.equals(player)) {
                    Place place = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    if (pieceMoves != null) {
                        moves.addAll(Arrays.asList(pieceMoves));
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
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
        int turnIndex = 0;
        while (true) {
            this.currentPlayer = players[turnIndex];
            Move[] moves = getAvailableMoves(this.currentPlayer);
            if (moves.length == 0) {
                turnIndex = (turnIndex + 1) % playerCount;
                continue;
            }
            Move move = this.currentPlayer.nextMove(this, moves);
            movePiece(move);
            Piece movedPiece = board[move.to.x()][move.to.y()];
            updateScore(this.currentPlayer, movedPiece, move);
            winner = getWinner(this.currentPlayer, movedPiece, move);
            this.refreshOutput();
            this.numMoves++;
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
            turnIndex = (turnIndex + 1) % playerCount;
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
        boolean opponentKingAlive = false;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.player.equals(opponent)
                        && piece.getClass().getSimpleName().equals("King")) {
                    opponentKingAlive = true;
                    break;
                }
            }
            if (opponentKingAlive)
                break;
        }
        if (!opponentKingAlive) {
            return lastPlayer;
        }
        Move[] opponentMoves = getAvailableMoves(opponent);
        if (opponentMoves.length == 0) {
            return lastPlayer;
        }
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place from = move.from;
        Place to = move.to;
        int distance = Math.abs(from.x() - to.x()) + Math.abs(from.y() - to.y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    public void movePiece(Move move) {
        Place from = move.from;
        Place to = move.to;
        Piece piece = board[from.x()][from.y()];
        board[to.x()][to.y()] = piece;
        board[from.x()][from.y()] = null;
    }
}