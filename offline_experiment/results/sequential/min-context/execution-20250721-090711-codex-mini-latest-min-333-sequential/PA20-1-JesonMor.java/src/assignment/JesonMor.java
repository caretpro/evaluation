
package assignment;

import assignment.protocol.*;
import assignment.piece.Knight;

import java.util.ArrayList;
import java.util.Arrays;

public class JesonMor extends Game {

    private final Player[] players;

    public JesonMor(Configuration configuration) {
        super(configuration);
        this.players = configuration.getPlayers();
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
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = board[x][y];
                if (p != null && p.player() == player) {
                    Place from = new Place(x, y);
                    moves.addAll(Arrays.asList(p.getAvailableMoves(this, from)));
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Player start() {
        numMoves = 0;
        board = configuration.getInitialBoard();
        refreshOutput();

        int turn = 0;
        while (true) {
            Player current = players[turn % players.length];
            Move[] available = getAvailableMoves(current);
            Move chosen = current.nextMove(this, available);
            movePiece(chosen);
            numMoves++;
            Piece movedPiece = board[chosen.dest().x()][chosen.dest().y()];
            updateScore(current, movedPiece, chosen);
            refreshOutput();

            Player winner = getWinner(current, movedPiece, chosen);
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n",
                                  winner.color(), winner.name(), Color.DEFAULT);
                return winner;
            }
            turn++;
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // Determine the opponent (two‐player game)
        Player opponent = (lastPlayer == players[0]) ? players[1] : players[0];

        // Win by capturing opponent (landing on their place)
        if (lastMove.dest().equals(opponent.place())) {
            return lastPlayer;
        }

        // Tie‐breaker: leaving the central place with a Knight within protection turns
        if (lastPiece instanceof Knight
            && lastMove.src().equals(configuration.getCentralPlace())
            && numMoves < configuration.getProtectionTurnLimit()) {
            return opponent;
        }

        return null;
    }

    /**
     * Update the score of a player according to the Manhattan distance between src and dest.
     *
     * @param player the player who just makes a move
     * @param piece  the piece that is just moved
     * @param move   the move that is just made
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place s = move.src();
        Place d = move.dest();
        int distance = Math.abs(s.x() - d.x()) + Math.abs(s.y() - d.y());
        player.setScore(player.getScore() + distance);
    }

    /**
     * Move a piece from source to destination on the board.
     *
     * @param move the move to make
     */
    @Override
    public void movePiece(Move move) {
        Place s = move.src();
        Place d = move.dest();
        Piece p = board[s.x()][s.y()];
        board[d.x()][d.y()] = p;
        board[s.x()][s.y()] = null;
    }
}