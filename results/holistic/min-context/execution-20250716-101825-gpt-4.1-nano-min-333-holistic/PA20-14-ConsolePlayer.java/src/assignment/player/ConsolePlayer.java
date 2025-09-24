
package assignment.player;

import assignment.protocol.Game;
import assignment.piece.*;
import assignment.protocol.Color;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import java.util.Scanner;
import assignment.protocol.rules.Rule;
import assignment.protocol.rules.OutOfBoundaryRule;
import assignment.protocol.rules.OccupiedRule;
import assignment.protocol.rules.VacantRule;
import assignment.protocol.rules.NilMoveRule;
import assignment.protocol.rules.FirstNMovesProtectionRule;
import assignment.protocol.rules.ArcherMoveRule;
import assignment.protocol.rules.KnightMoveRule;
import assignment.protocol.rules.KnightBlockRule;

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
     * facilitate programming.</strong>
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
                System.out.println("Invalid format. Please enter move in format 'a1->b3'.");
                continue;
            }

            // Validate the move against available moves
            boolean isValid = false;
            for (Move m : availableMoves) {
                if (m.equals(move)) {
                    isValid = true;
                    move = m; // Use the actual move object from availableMoves
                    break;
                }
            }
            if (!isValid) {
                System.out.println("Move not available. Please choose a valid move from the list.");
                continue;
            }

            // Optional: validate move with game rules
            String validationError = validateMove(game, move);
            if (validationError != null) {
                System.out.println("Invalid move: " + validationError);
                continue;
            }

            return move;
        }
    }

    private String validateMove(Game game, Move move) {
        Rule[] rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
            new KnightMoveRule(),
            new KnightBlockRule()
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
            char xChar = str.charAt(0);
            if (xChar < 'a' || xChar > 'z') {
                return null;
            }
            int x = xChar - 'a';

            String yStr = str.substring(1);
            int y = Integer.parseInt(yStr) - 1;
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
        Place source = parsePlace(segments[0].strip());
        if (source == null) {
            return null;
        }
        Place destination = parsePlace(segments[1].strip());
        if (destination == null) {
            return null;
        }
        return new Move(source, destination);
    }
}