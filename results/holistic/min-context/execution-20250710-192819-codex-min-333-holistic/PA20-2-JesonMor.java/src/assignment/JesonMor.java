
package assignment;

import assignment.piece.Knight;
import assignment.protocol.Color;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;
import assignment.protocol.Player;
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
        Player winner = null;
        this.numMoves = 0;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = null;
        this.refreshOutput();

        Player[] players = configuration.getPlayers();
        int currentIndex = 0;
        while (winner == null) {
            Player player = players[currentIndex];
            this.currentPlayer = player;

            // get all valid moves for this player
            Move[] moves = getAvailableMoves(player);
            // ask player to choose a move
            Move chosen = player.nextMove(this, moves);
            // execute the move
            movePiece(chosen);
            // update the score for this move
            Piece movedPiece = board[chosen.to().x()][chosen.to().y()];
            updateScore(player, movedPiece, chosen);
            // increment move counter and refresh the display
            numMoves++;
            refreshOutput();
            // check for a winner
            winner = getWinner(player, movedPiece, chosen);

            currentIndex = (currentIndex + 1) % players.length;
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
     * @param lastPiece  the last piece that is moved by lastPlayer
     * @param lastMove   the last move made by lastPlayer
     * @return the winner if it exists, otherwise return null
     */
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // A knight reaching the opponent's baseline wins.
        int maxY = board[0].length - 1;
        if (lastPiece instanceof Knight) {
            int y = lastMove.to().y();
            if (lastPlayer.getColor() == Color.WHITE && y == maxY) {
                return lastPlayer;
            } else if (lastPlayer.getColor() == Color.BLACK && y == 0) {
                return lastPlayer;
            }
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
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.from().x() - move.to().x());
        int dy = Math.abs(move.from().y() - move.to().y());
        player.setScore(player.getScore() + dx + dy);
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
    @Override
    public void movePiece(Move move) {
        Piece p = board[move.from().x()][move.from().y()];
        board[move.to().x()][move.to().y()] = p;
        board[move.from().x()][move.from().y()] = null;
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
    public @NotNull Move[] getAvailableMoves(Player player) {
        List<Move> all = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = board[x][y];
                if (p != null && p.getOwner() == player) {
                    Place from = Place.of(x, y);
                    for (Move m : p.getAvailableMoves(this, from)) {
                        all.add(m);
                    }
                }
            }
        }
        return all.toArray(new Move[0]);
    }
}