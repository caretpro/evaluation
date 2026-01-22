
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
     * Start the game
     * Players will take turns according to the order in {@link Configuration#getPlayers()} to make a move until
     * a player wins.
     * <p>
     * Implementation: loop letting two players take turns to move.
     * The order of players is based on {@link Configuration#getPlayers()}.
     * Use {@link Player#nextMove(Game, Move[])} to get the player's move.
     * After each move, call {@link Game#refreshOutput()}.
     * When a winner appears, set {@code winner} and return.
     *
     * @return the winner
     */
    @Override
    public Player start() {
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        ArrayList<Player> players = new ArrayList<>(configuration.getPlayers());
        int currentPlayerIndex = 0;
        this.refreshOutput();

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            this.currentPlayer = currentPlayer;

            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, current player loses or game ends
                winner = null;
                break;
            }

            // Get player's move
            Move[] moves = availableMoves;
            Move move = currentPlayer.nextMove(this, moves);

            // Make the move
            movePiece(move);
            numMoves++;
            refreshOutput();

            // Check for winner
            Piece lastPiece = findPieceAt(move.getDestination());
            winner = getWinner(currentPlayer, lastPiece, move);
            if (winner != null) {
                break;
            }

            // Switch to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    /**
     * Get the winner of the game.
     * For demonstration, suppose the game ends when one player's key piece is captured.
     * Implement logic accordingly.
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        for (Player player : configuration.getPlayers()) {
            boolean hasPieces = false;
            for (Piece[] row : board) {
                for (Piece p : row) {
                    if (p != null && p.getOwner().equals(player)) {
                        hasPieces = true;
                        break;
                    }
                }
                if (hasPieces) break;
            }
            if (!hasPieces) {
                for (Player p : configuration.getPlayers()) {
                    if (!p.equals(player)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Update the score of a player based on move distance.
     */
    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    /**
     * Make a move by updating the board.
     */
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece movingPiece = board[source.x()][source.y()];

        // Remove piece from source
        board[source.x()][source.y()] = null;
        // Capture if any piece at destination
        Piece targetPiece = board[destination.x()][destination.y()];
        if (targetPiece != null) {
            // Optionally handle capture logic
        }
        // Place piece at destination
        board[destination.x()][destination.y()] = movingPiece;
    }

    /**
     * Get all available moves for a player.
     */
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getOwner().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    for (Move m : pieceMoves) {
                        // Validate move (assuming getAvailableMoves already returns valid moves)
                        moves.add(m);
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Helper method to find a piece at a given place.
     */
    private Piece findPieceAt(Place place) {
        if (place == null) return null;
        if (place.x() < 0 || place.x() >= board.length || place.y() < 0 || place.y() >= board[0].length) {
            return null;
        }
        return board[place.x()][place.y()];
    }
}