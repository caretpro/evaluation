
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
        this.currentPlayer = null;
        this.refreshOutput();

        ArrayList<Player> players = new ArrayList<>(configuration.getPlayers());
        int currentIndex = 0;

        while (true) {
            currentPlayer = players.get(currentIndex);
            // Get available moves for current player
            Move[] moves = getAvailableMoves(currentPlayer);
            if (moves.length == 0) {
                // No moves available, current player loses, opponent wins
                winner = players.get((currentIndex + 1) % players.size());
                break;
            }
            // Get player's next move
            Move[] playerMoves = new Move[1];
            playerMoves[0] = currentPlayer.nextMove(this, moves);
            Move move = playerMoves[0];

            // Make the move
            movePiece(move);
            // Update score
            Piece movedPiece = board[move.getSource().x()][move.getSource().y()];
            updateScore(currentPlayer, movedPiece, move);
            // Refresh output
            this.refreshOutput();

            // Check for winner
            winner = getWinner(currentPlayer, movedPiece, move);
            if (winner != null) {
                break;
            }

            // Next player's turn
            currentIndex = (currentIndex + 1) % players.size();
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
        // Example logic: if a player's score reaches a certain threshold, they win
        // Or if a certain piece is captured, etc.
        // For demonstration, assume game ends when a player reaches 100 points
        if (lastPlayer.getScore() >= 100) {
            return lastPlayer;
        }
        // Alternatively, implement specific game-winning conditions here
        return null;
    }

    /**
     * Update the score of a player according to the {@link Piece} and corresponding move made by him just now.
     * This method will be called every time after a player makes a move, in order to update the corresponding score
     * of this player.
     * <p>
     * The score of a player is the cumulative score of each move he makes.
     * The score of each move is calculated with the Manhattan distance between the source and destination {@link Place}.
     * <p>
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
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                       Math.abs(move.getSource().y() - move.getDestination().y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    /**
     * Make a move.
     * This method performs moving a {@link Piece} from source to destination {@link Place} according {@link Move} object.
     * Note that after the move, there will be no {@link Piece} in source {@link Place}.
     * <p>
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
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = board[source.x()][source.y()];
        // Remove piece from source
        board[source.x()][source.y()] = null;
        // Place piece at destination
        board[destination.x()][destination.y()] = piece;
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
        ArrayList<Move> movesList = new ArrayList<Move>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getOwner().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    // Assuming getAvailableMoves already returns valid moves
                    movesList.addAll(Arrays.asList(pieceMoves));
                }
            }
        }
        return movesList.toArray(new Move[0]);
    }
}