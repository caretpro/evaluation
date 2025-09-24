
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    /**
     * Start the game
     * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until
     * a player wins.
     * Implements the game loop, requesting moves, executing them, updating scores, and checking for a winner.
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
        int currentPlayerIndex = 0;

        while (true) {
            currentPlayer = players[currentPlayerIndex];
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                winner = players[(currentPlayerIndex + 1) % 2];
                break;
            }
            Move move = currentPlayer.nextMove(this, availableMoves);
            movePiece(move);
            Piece movedPiece = getPiece(move.getDestination());
            updateScore(currentPlayer, movedPiece, move);
            this.numMoves++;
            this.refreshOutput();
            winner = getWinner(currentPlayer, movedPiece, move);
            if (winner != null) {
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % 2;
        }

        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    /**
     * Determine if there is a winner.
     * The winner is the player who captures the opponent's central piece.
     * If the opponent's central piece is missing, last player to move wins.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Place central = getCentralPlace();
        Piece centralPiece = getPiece(central);
        Player opponent = null;
        for (Player p : getPlayers()) {
            if (!p.equals(lastPlayer)) {
                opponent = p;
                break;
            }
        }
        if (centralPiece == null || !centralPiece.getPlayer().equals(opponent)) {
            return lastPlayer;
        }
        return null;
    }

    /**
     * Update score based on Manhattan distance.
     */
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                       Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    /**
     * Execute move: move piece from source to destination.
     */
    @Override
    public void movePiece(@NotNull Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        Piece movingPiece = getPiece(source);
        // Remove from source
        board[source.x()][source.y()] = null;
        // Place at destination
        board[dest.x()][dest.y()] = movingPiece;
    }

    /**
     * Get all available moves for a player.
     */
    @Override
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    for (Move m : piece.getAvailableMoves(this, source)) {
                        if (m.getSource().equals(source) && isValidMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Validate move within bounds.
     */
    private boolean isValidMove(Move move) {
        int size = configuration.getSize();
        Place src = move.getSource();
        Place dest = move.getDestination();
        if (src.x() < 0 || src.x() >= size || src.y() < 0 || src.y() >= size) return false;
        if (dest.x() < 0 || dest.x() >= size || dest.y() < 0 || dest.y() >= size) return false;
        return true;
    }
}