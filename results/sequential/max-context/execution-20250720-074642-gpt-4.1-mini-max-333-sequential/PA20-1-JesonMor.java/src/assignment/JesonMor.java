
package assignment;

import assignment.protocol.*;

import java.util.ArrayList;

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
    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = this.configuration.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    if (pieceMoves != null) {
                        for (Move move : pieceMoves) {
                            Place dest = move.getDestination();
                            if (dest.x() >= 0 && dest.x() < size && dest.y() >= 0 && dest.y() < size) {
                                try {
                                    // Clone move and its places to avoid aliasing issues
                                    Move clonedMove = move.clone();
                                    clonedMove.getSource().clone(); // ensure deep clone of source
                                    clonedMove.getDestination().clone(); // ensure deep clone of destination
                                    moves.add(clonedMove);
                                } catch (CloneNotSupportedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves.toArray(Move[]::new);
    }

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
            // Use original player instances to keep identity consistent
            this.currentPlayer = players[currentPlayerIndex];
            Move[] availableMoves = getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                continue;
            }
            Move chosenMove = this.currentPlayer.nextMove(this, availableMoves);
            movePiece(chosenMove);
            Piece movedPiece = this.getPiece(chosenMove.getDestination());
            updateScore(this.currentPlayer, movedPiece, chosenMove);
            this.numMoves++;
            winner = getWinner(this.currentPlayer, movedPiece, chosenMove);
            this.refreshOutput();
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int distance = Math.abs(source.x() - destination.x()) + Math.abs(source.y() - destination.y());
        int newScore = player.getScore() + distance;
        player.setScore(newScore);
    }

    @Override
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = board[source.x()][source.y()];
        board[destination.x()][destination.y()] = piece;
        board[source.x()][source.y()] = null;
    }
}