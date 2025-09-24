
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
     * <p>
     * In the implementation, student should implement the loop letting two players take turns to move pieces.
     * The order of the players should be consistent to the order in {@link Configuration#getPlayers()}.
     * {@link Player#nextMove(Game, Move[])} should be used to retrieve the player's choice of his next move.
     * After each move, {@link Game#refreshOutput()} should be called to refresh the gameboard printed in the console.
     * <p>
     * When a winner appears, set the local variable {@code winner} so that this method can return the winner.
     *
     * @return the winner
     */
    @Override
    public Player start() {
        // reset all things
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        List<Player> players = new ArrayList<>(configuration.getPlayers());
        int currentPlayerIndex = 0;
        this.refreshOutput();

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            this.currentPlayer = currentPlayer;

            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                // No moves available, current player loses or game ends
                // Check if opponent has no pieces or other win condition
                // For simplicity, assume no moves means current player loses
                winner = players.get((currentPlayerIndex + 1) % players.size());
                break;
            }

            // Get next move from player
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            // Make the move
            movePiece(chosenMove);
            // Update score based on move
            Piece movedPiece = getPieceAt(chosenMove.source());
            updateScore(currentPlayer, movedPiece, chosenMove);
            // Refresh output
            this.refreshOutput();

            // Check for winner
            winner = getWinner(currentPlayer, getPieceAt(chosenMove.destination()), chosenMove);
            if (winner != null) {
                break;
            }

            // Next player's turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            numMoves++;
        }

        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null;
     * This method will be called every time after a player makes a move and after
     * {@link JesonMor#updateScore(Player, Piece, Move)} is called, in order to
     * check whether any {@link Player} wins.
     * If this method returns a player (the winner), then the game will exit with the winner.
     * If this method returns null, next player will be asked to make a move.
     *
     * @param lastPlayer the last player who makes a move
     * @param lastPiece  the last piece that is moved by the player
     * @param lastMove   the last move made by lastPlayer
     * @return the winner if it exists, otherwise return null
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Player opponent = null;
        for (Player p : configuration.getPlayers()) {
            if (!p.equals(lastPlayer)) {
                opponent = p;
                break;
            }
        }
        if (opponent != null) {
            boolean opponentHasPieces = false;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    Piece p = board[i][j];
                    if (p != null && p.owner().equals(opponent)) {
                        opponentHasPieces = true;
                        break;
                    }
                }
                if (opponentHasPieces) break;
            }
            if (!opponentHasPieces) {
                return lastPlayer;
            }
        }
        return null;
    }

    /**
     * Update the score of a player according to the {@link Piece} and corresponding move made by him just now.
     * This method will be called every time after a player makes a move, in order to update the corresponding score
     * of this player.
     * The score of a player is the cumulative score of each move he makes.
     * The score of each move is calculated with the Manhattan distance between the source and destination {@link Place}.
     * Student can use {@link Player#getScore()} to get the current score of a player before updating.
     * {@link Player#setScore(int)} can be used to update the score of a player.
     * <p>
     * <strong>Attention: do not need to validate move in this method.</strong>
     *
     * @param player the player who just makes a move
     * @param piece  the piece that is just moved
     * @param move   the move that is just made
     */
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.source().x() - move.destination().x()) +
                       Math.abs(move.source().y() - move.destination().y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    /**
     * Make a move.
     * This method performs moving a {@link Piece} from source to destination {@link Place} according {@link Move} object.
     * Note that after the move, there will be no {@link Piece} in source {@link Place}.
     * Positions of all {@link Piece}s on the gameboard are stored in {@link JesonMor#board} field as a 2-dimension array of
     * {@link Piece} objects.
     * The x and y coordinate of a {@link Place} on the gameboard are used as index in {@link JesonMor#board}.
     * E.g. {@code board[place.x()][place.y()]}.
     * If one {@link Place} does not have a piece on it, it will be null in {@code board[place.x()][place.y()]}.
     * Student may modify elements in {@link JesonMor#board} to implement moving a {@link Piece}.
     * The {@link Move} object can be considered valid on present gameboard.
     *
     * @param move the move to make
     */
    public void movePiece(Move move) {
        Place source = move.source();
        Place destination = move.destination();
        Piece movingPiece = getPieceAt(source);
        // Remove from source
        board[source.x()][source.y()] = null;
        // Place at destination
        board[destination.x()][destination.y()] = movingPiece;
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
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.owner().equals(player)) {
                    Place currentPlace = new Place(i, j);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    for (Move m : pieceMoves) {
                        if (isValidMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Helper method to get the piece at a specific place.
     */
    private Piece getPieceAt(Place place) {
        if (place.x() >= 0 && place.x() < board.length && place.y() >= 0 && place.y() < board[0].length) {
            return board[place.x()][place.y()];
        }
        return null;
    }

    /**
     * Helper method to validate a move.
     * For simplicity, assume move is valid if source has a piece owned by current player and destination is within bounds.
     */
    private boolean isValidMove(Move move) {
        Place src = move.source();
        Place dest = move.destination();
        if (src.x() < 0 || src.x() >= board.length || src.y() < 0 || src.y() >= board[0].length) return false;
        if (dest.x() < 0 || dest.x() >= board.length || dest.y() < 0 || dest.y() >= board[0].length) return false;
        Piece srcPiece = getPieceAt(src);
        if (srcPiece == null) return false;
        if (!srcPiece.owner().equals(currentPlayer)) return false;
        // Additional validation can be added based on game rules
        return true;
    }
}