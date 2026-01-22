
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

    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> movesList = new ArrayList<>();
        
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place currentPlace = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, currentPlace);
                    movesList.addAll(Arrays.asList(pieceMoves));
                }
            }
        }
        return movesList.toArray(new Move[0]);
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
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                continue;
            }
            Move chosenMove = currentPlayer.nextMove(this, availableMoves);
            movePiece(chosenMove);
            numMoves++;
            Piece movedPiece = board[chosenMove.getTo().x()][chosenMove.getTo().y()];
            updateScore(currentPlayer, movedPiece, chosenMove);
            refreshOutput();
            winner = getWinner(currentPlayer, movedPiece, chosenMove);
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            if (winner != null) {
                System.out.println();
                System.out.println("Congratulations! ");
                System.out.printf("Winner: %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
                return winner;
            }
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (lastPiece instanceof Knight) {
            int opponentBaseRow = lastPlayer.equals(configuration.getPlayers()[0]) ? board[0].length - 1 : 0;
            if (lastMove.getTo().y() == opponentBaseRow) {
                return lastPlayer;
            }
        }
        for (Player player : configuration.getPlayers()) {
            boolean hasPieces = false;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    if (board[x][y] != null && board[x][y].getPlayer().equals(player)) {
                        hasPieces = true;
                        break;
                    }
                }
                if (hasPieces) break;
            }
            if (!hasPieces) {
                return configuration.getPlayers()[0].equals(player) ? configuration.getPlayers()[1] : configuration.getPlayers()[0];
            }
        }
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place from = move.getFrom();
        Place to = move.getTo();
        int distance = Math.abs(from.x() - to.x()) + Math.abs(from.y() - to.y());
        player.setScore(player.getScore() + distance);
    }

    public void movePiece(Move move) {
        Place from = move.getFrom();
        Place to = move.getTo();
        Piece piece = board[from.x()][from.y()];
        board[to.x()][to.y()] = piece;
        board[from.x()][from.y()] = null;
    }
}