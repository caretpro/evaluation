 bound must be positive` in `RandomPlayer.nextMove()`. This occurs when `availableMoves.length` is 0, leading to `Random.nextInt(0)`, which is invalid. Also, there are issues with capture protection logic and determining the winner by capturing all pieces. The `movePiece` method was not correctly reverting the move when capture protection was active, and the `getWinner` method was not correctly identifying a winner when all opponent's pieces were captured.

```java
package assignment;

import assignment.piece.Knight;
import assignment.protocol.*;
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
        this.currentPlayer = configuration.getPlayers()[0];
        this.refreshOutput();

        while (true) {
            Move[] availableMoves = getAvailableMoves(currentPlayer);

            if (availableMoves.length == 0) {
                // Check for stalemate: if the other player also has no moves, it's a tie
                Player nextPlayer = (currentPlayer == configuration.getPlayers()[0]) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
                if (getAvailableMoves(nextPlayer).length == 0) {
                    // It's a tie, return null
                    return null;
                } else {
                    // No available moves, the other player wins
                    winner = nextPlayer;
                    break;
                }
            }

            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            try {
                movePiece(chosenMove);
            } catch (IllegalArgumentException e) {
                // If capture is illegal due to protection, skip the move and continue
                System.out.println(e.getMessage());
                continue;
            }
            numMoves++;
            refreshOutput();

            Piece movedPiece = getPiece(chosenMove.getDestination());
            winner = getWinner(currentPlayer, movedPiece, chosenMove);

            if (winner != null) {
                break;
            }

            // Switch to the next player
            currentPlayer = (currentPlayer == configuration.getPlayers()[0]) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
        }

        if (winner != null) {
            System.out.println();
            System.out.println("Congratulations! ");
            System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
            return winner;
        }
        return null;
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
        if (lastMove.getDestination().equals(configuration.getCentralPlace())) {
            updateScore(lastPlayer, lastPiece, lastMove);
            return lastPlayer;
        }

        // Check if the player has captured all the opponent's pieces
        Player opponent = (lastPlayer == configuration.getPlayers()[0]) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
        boolean opponentHasPieces = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) {
                break;
            }
        }

        if (!opponentHasPieces) {
            updateScore(lastPlayer, lastPiece, lastMove);
            return lastPlayer;
        }

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
        int manhattanDistance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + manhattanDistance);
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

        Piece pieceToMove = board[source.x()][source.y()];
        Piece capturedPiece = board[destination.x()][destination.y()];

        if (capturedPiece != null && numMoves < configuration.getNumMovesProtection()) {
            // Revert the move
            //board[source.x()][source.y()] = pieceToMove;
            //board[destination.x()][destination.y()] = capturedPiece;
            throw new IllegalArgumentException("Cannot capture pieces in the first " + configuration.getNumMovesProtection() + " moves.");
        }

        board[source.x()][source.y()] = null;
        board[destination.x()][destination.y()] = pieceToMove;
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
        List<Move> availableMoves = new ArrayList<>();
        int size = configuration.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    availableMoves.addAll(Arrays.asList(pieceMoves));
                }
            }
        }

        return availableMoves.toArray(new Move[0]);
    }
}