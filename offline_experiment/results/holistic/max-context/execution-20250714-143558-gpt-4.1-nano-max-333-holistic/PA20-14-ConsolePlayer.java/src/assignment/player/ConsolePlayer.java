
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.Color;

// Import the rule classes used for move validation from the correct package
import assignment.player.Rule;
import assignment.player.OutOfBoundaryRule;
import assignment.player.OccupiedRule;
import assignment.player.VacantRule;
import assignment.player.NilMoveRule;
import assignment.player.FirstNMovesProtectionRule;
import assignment.player.ArcherMoveRule;
import assignment.player.KnightMoveRule;
import assignment.player.KnightBlockRule;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * The player that makes move according to user input from console.
 */
public class ConsolePlayer extends Player {
    public ConsolePlayer(String name, Color color) {
        super(name, color);
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
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (e.g., a1->b3): ");
            String input = scanner.nextLine().trim();

            Move move = parseMove(input);
            if (move == null) {
                System.out.println("Invalid format. Please enter move in format 'a1->b3'.");
                continue;
            }

            // Check if move is in available moves
            boolean isValid = false;
            for (Move availableMove : availableMoves) {
                if (availableMove.equals(move)) {
                    isValid = true;
                    move = availableMove; // Use the actual move object from availableMoves
                    break;
                }
            }
            if (!isValid) {
                System.out.println("Invalid move. Please select a move from the available options.");
                continue;
            }

            // Validate move with game rules
            String validationMsg = validateMove(game, move);
            if (validationMsg != null) {
                System.out.println("Invalid move: " + validationMsg);
                continue;
            }

            return move;
        }
    }

    private String validateMove(Game game, Move move) {
        var rules = new assignment.player.Rule[] { 
            new assignment.player.OutOfBoundaryRule(), 
            new assignment.player.OccupiedRule(), 
            new assignment.player.VacantRule(), 
            new assignment.player.NilMoveRule(),
            new assignment.player.FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), 
            new assignment.player.ArcherMoveRule(),
            new assignment.player.KnightMoveRule(), 
            new assignment.player.KnightBlockRule() 
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
            var x = str.charAt(0) - 'a';
            var y = Integer.parseInt(str.substring(1)) - 1;
            return new Place(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Move parseMove(String str) {
        var segments = str.split("->");
        if (segments.length < 2) {
            return null;
        }
        var source = parsePlace(segments[0].strip());
        if (source == null) {
            return null;
        }
        var destination = parsePlace(segments[1].strip());
        if (destination == null) {
            return null;
        }
        return new Move(source, destination);
    }
}