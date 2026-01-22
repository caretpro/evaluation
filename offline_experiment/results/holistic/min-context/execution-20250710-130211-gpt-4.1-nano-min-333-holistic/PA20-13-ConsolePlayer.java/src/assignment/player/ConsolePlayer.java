
package assignment.player;

import assignment.protocol.Game;
import assignment.piece.*;
import assignment.protocol.Color;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.player.Rule;
import assignment.player.OutOfBoundaryRule;
import assignment.player.OccupiedRule;
import assignment.player.VacantRule;
import assignment.player.NilMoveRule;
import assignment.player.FirstNMovesProtectionRule;
import assignment.player.ArcherMoveRule;
import assignment.player.KnightMoveRule;
import assignment.player.KnightBlockRule;
import java.util.Scanner;

/**
 * The player that makes move according to user input from console.
 */
public class ConsolePlayer extends Player {
    private final Scanner scanner;

    public ConsolePlayer(String name, Color color) {
        super(name, color);
        this.scanner = new Scanner(System.in);
    }

    public ConsolePlayer(String name) {
        this(name, Color.GREEN);
    }

    /**
     * Choose a move from available moves.
     * This method will be called by {@link Game} object to get the move that the player wants to make when it is the
     * player's turn.
     * <p>
     * {@link ConsolePlayer} returns a move according to user's input in the console.
     * The console input format should conform the format described in the assignment description.
     * (e.g. {@literal a1->b3} means move the {@link Piece} at {@link Place}(x=0,y=0) to {@link Place}(x=1,y=2))
     * Note that in the {@link Game}.board, the index starts from 0 in both x and y dimension, while in the console
     * display, x dimension index starts from 'a' and y dimension index starts from 1.
     * <p>
     * Hint: be sure to handle invalid input to avoid invalid {@link Move}s.
     * <p>
     * <strong>Attention: Student should make sure the {@link Move} returned is valid.</strong>
     * <p>
     * <strong>Attention: {@link Place} object uses integer as index of x and y-axis, both starting from 0 to
     * facilitate programming.
     * This is VERY different from the coordinate used in console display.</strong>
     *
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return the chosen move
     */
    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            System.out.println("Enter your move (e.g., a1->b3):");
            String input = scanner.nextLine().trim();

            Move move = parseMove(input);
            if (move == null) {
                System.out.println("Invalid format. Please try again.");
                continue;
            }

            // Check if move is in available moves
            boolean isValid = false;
            for (Move m : availableMoves) {
                if (m.equals(move)) {
                    isValid = true;
                    move = m; // Use the actual move object from availableMoves
                    break;
                }
            }
            if (!isValid) {
                System.out.println("Move not available. Please select a valid move.");
                continue;
            }

            // Validate move with game rules
            String validationError = validateMove(game, move);
            if (validationError != null) {
                System.out.println("Invalid move: " + validationError);
                continue;
            }

            return move;
        }
    }

    private String validateMove(Game game, Move move) {
        var rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
            new KnightMoveRule(),
            new KnightBlockRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return rule.getDescription();
            }
        }
        var piece = game.getPiece(move.getSource());
        if (piece == null) {
            return "No piece at " + move.getSource().toString();
        }
        if (!this.equals(piece.getPlayer())) {
            return "Cannot move a piece not belonging to you";
        }
        return null;
    }

    private static Place parsePlace(String str) {
        if (str.length() < 2) {
            return null;
        }
        try {
            char colChar = str.charAt(0);
            int x = colChar - 'a';
            int y = Integer.parseInt(str.substring(1)) - 1;
            if (x < 0 || y < 0) {
                return null;
            }
            return new Place(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Move parseMove(String str) {
        String[] segments = str.split("->");
        if (segments.length != 2) {
            return null;
        }
        Place source = parsePlace(segments[0].trim());
        Place destination = parsePlace(segments[1].trim());
        if (source == null || destination == null) {
            return null;
        }
        return new Move(source, destination);
    }
}