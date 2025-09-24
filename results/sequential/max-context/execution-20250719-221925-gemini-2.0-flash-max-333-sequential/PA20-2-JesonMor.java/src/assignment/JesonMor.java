
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
        int size = configuration.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place place = new Place(i, j);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    if (pieceMoves != null) { // Check for null
                        moves.addAll(Arrays.asList(pieceMoves));
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }

    /**
     * Start the game Players will take turns according to the order in  {@link Configuration#getPlayers()}  to make a move until a player wins. <p> In the implementation, student should implement the loop letting two players take turns to move pieces. The order of the players should be consistent to the order in  {@link Configuration#getPlayers()} . {@link Player#nextMove(Game,Move[])}  should be used to retrieve the player's choice of his next move. After each move,  {@link Game#refreshOutput()}  should be called to refresh the gameboard printed in the console. <p> When a winner appears, set the local variable  {@code  winner}  so that this method can return the winner.
     * @return  the winner
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
            this.currentPlayer = players[currentPlayerIndex];
            Move[] availableMoves = this.getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                winner = players[1 - currentPlayerIndex];
                break;
            }
            Move nextMove = this.currentPlayer.nextMove(this, availableMoves);
            if (nextMove == null) {
                winner = players[1 - currentPlayerIndex];
                break;
            }
            this.movePiece(nextMove);
            Piece movedPiece = this.getPiece(nextMove.getDestination());
            this.updateScore(this.currentPlayer, movedPiece, nextMove);
            this.numMoves++;
            this.refreshOutput();
            winner = this.getWinner(this.currentPlayer, movedPiece, nextMove);
            if (winner != null) {
                break;
            }
            currentPlayerIndex = 1 - currentPlayerIndex;
        }
        System.out.println();
        System.out.println("Congratulations! ");
        System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    /**
     * Get the winner of the game. If there is no winner yet, return null; This method will be called every time after a player makes a move and after {@link JesonMor#updateScore(Player,Piece,Move)}  is called, in order to check whether any  {@link Player}  wins. If this method returns a player (the winner), then the game will exit with the winner. If this method returns null, next player will be asked to make a move.
     * @param lastPlayer  the last player who makes a move
     * @param lastMove    the last move made by lastPlayer
     * @param lastPiece   the last piece that is moved by the player
     * @return  the winner if it exists, otherwise return null
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

        // Check if opponent has any pieces left
        boolean opponentHasPieces = false;
        for (int i = 0; i < configuration.getSize(); i++) {
            for (int j = 0; j < configuration.getSize(); j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getPlayer().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) break;
        }

        if (!opponentHasPieces) {
            return lastPlayer; // Last player wins by capturing all opponent's pieces
        }

        // Check if opponent has vacated the central place
        Place centralPlace = configuration.getCentralPlace();
        Piece centralPiece = getPiece(centralPlace);
        if (centralPiece != null && !centralPiece.getPlayer().equals(opponent)) {
            return lastPlayer; // Last player wins because opponent left central place
        }

        if (centralPiece == null) {
            boolean opponentCanMoveToCentral = false;
            for (int i = 0; i < configuration.getSize(); i++) {
                for (int j = 0; j < configuration.getSize(); j++) {
                    Piece piece = board[i][j];
                    if (piece != null && piece.getPlayer().equals(opponent)) {
                        Move[] availableMoves = piece.getAvailableMoves(this, new Place(i, j));
                        if (availableMoves != null) {
                            for (Move move : availableMoves) {
                                if (move.getDestination().equals(centralPlace)) {
                                    opponentCanMoveToCentral = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (opponentCanMoveToCentral) break;
                }
                if (opponentCanMoveToCentral) break;
            }
            if (!opponentCanMoveToCentral) {
                return lastPlayer;
            }
        }

        return null; // No winner yet
    }

    /**
     * Update the score of a player according to the  {@link Piece}  and corresponding move made by him just now. This method will be called every time after a player makes a move, in order to update the corresponding score of this player. <p> The score of a player is the cumulative score of each move he makes. The score of each move is calculated with the Manhattan distance between the source and destination  {@link Place} . <p> Student can use  {@link Player#getScore()}  to get the current score of a player before updating. {@link Player#setScore(int)}  can be used to update the score of a player. <p> <strong>Attention: do not need to validate move in this method.</strong>
     * @param player  the player who just makes a move
     * @param piece   the piece that is just moved
     * @param move    the move that is just made
     */
    public void updateScore(Player player, Piece piece, Move move) {
        int manhattanDistance = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        int currentScore = player.getScore();
        player.setScore(currentScore + manhattanDistance);
    }

    /**
     * Make a move. This method performs moving a  {@link Piece}  from source to destination  {@link Place}  according  {@link Move}  object. Note that after the move, there will be no  {@link Piece}  in source  {@link Place} . <p> Positions of all  {@link Piece} s on the gameboard are stored in  {@link JesonMor#board}  field as a 2-dimension array of {@link Piece}  objects. The x and y coordinate of a  {@link Place}  on the gameboard are used as index in  {@link JesonMor#board} . E.g.  {@code  board[place.x()][place.y()]} . If one  {@link Place}  does not have a piece on it, it will be null in  {@code  board[place.x()][place.y()]} . Student may modify elements in  {@link JesonMor#board}  to implement moving a  {@link Piece} . The  {@link Move}  object can be considered valid on present gameboard.
     * @param move  the move to make
     */
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece pieceToMove = board[source.x()][source.y()];
        board[source.x()][source.y()] = null;
        board[destination.x()][destination.y()] = pieceToMove;
    }
}