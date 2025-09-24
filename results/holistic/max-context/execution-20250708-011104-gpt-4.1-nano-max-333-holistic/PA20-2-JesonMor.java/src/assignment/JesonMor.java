
package assignment;

import assignment.protocol.*;

public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        super(configuration);
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
            currentPlayer = players[currentPlayerIndex];
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;

            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                winner = players[(currentPlayerIndex) % players.length];
                break;
            }

            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            movePiece(chosenMove);
            Piece movedPiece = getPiece(chosenMove.getSource());
            updateScore(currentPlayer, movedPiece, chosenMove);
            this.numMoves++;
            this.refreshOutput();

            winner = getWinner(currentPlayer, movedPiece, chosenMove);
            if (winner != null) {
                break;
            }
        }

        System.out.println();
        System.out.printf("Congratulations! %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        Player[] players = getPlayers();
        Player opponent = null;
        for (Player p : players) {
            if (!p.equals(lastPlayer)) {
                opponent = p;
                break;
            }
        }

        Place opponentCenter = getConfiguration().getCentralPlace();

        // Check if last move moved a piece to opponent's central square
        if (lastMove.getDestination().equals(opponentCenter)) {
            return lastPlayer;
        }

        // Check if opponent has any pieces left
        boolean opponentHasPieces = false;
        for (int x = 0; x < getConfiguration().getSize(); x++) {
            for (int y = 0; y < getConfiguration().getSize(); y++) {
                Piece p = getPiece(new Place(x, y));
                if (p != null && p.getPlayer().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) break;
        }

        // Check if opponent's central place is occupied by opponent
        Piece opponentCentralPiece = getPiece(opponentCenter);
        if (opponentCentralPiece == null || !opponentCentralPiece.getPlayer().equals(opponent)) {
            // Opponent has left central place or has no pieces, lastPlayer wins
            return lastPlayer;
        }

        // No winner yet
        return null;
    }
}