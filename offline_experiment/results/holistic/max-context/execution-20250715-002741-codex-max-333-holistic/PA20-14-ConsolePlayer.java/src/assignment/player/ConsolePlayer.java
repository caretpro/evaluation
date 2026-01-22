
package assignment.player;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.protocol.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Scanner;

/**
 * The player that makes move according to user input from console.
 */
public class ConsolePlayer extends Player {
    private final Scanner scanner = new Scanner(System.in);

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
     *
     * {@link ConsolePlayer} returns a move according to user's input in the console.
     * The console input format should conform the format described in the assignment description.
     * (e.g. {@literal a1->b3} means move the {@link assignment.piece.Piece} at {@link Place}(x=0,y=0) to
     * {@link Place}(x=1,y=2))
     * Note that in the {@link Game}.board, the index starts from 0 in both x and y dimension, while in the console
     * display, x dimension index starts from 'a' and y dimension index starts from 1.
     *
     * Hint: be sure to handle invalid input to avoid invalid {@link Move}s.
     *
     * <strong>Attention: Student should make sure the {@link Move} returned is valid.</strong>
     *
     * <strong>Attention: {@link Place} object uses integer as index of x and y-axis, both starting from 0 to
     * facilitate programming.
     * This is VERY different from the coordinate used in console display.</strong>
     *
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return the chosen move
     */
    @Override
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        while (true) {
            // 1) redraw board and info
            game.refreshOutput();

            // 2) prompt
            System.out.print(getName()
                    + " move ("
                    + getColor() + availableMoves.length + Color.DEFAULT
                    + " options): ");

            // 3) read input
            String line = scanner.nextLine().strip();

            // 4) parse format
            Move candidate = parseMove(line);
            if (candidate == null) {
                System.out.println("Invalid format. Please use e.g. a1->b2.");
                continue;
            }

            // 5) semantic validation
            String err = validateMove(game, candidate);
            if (err != null) {
                System.out.println(err + ". Try again.");
                continue;
            }

            // 6) must be one of the available moves
            if (!Arrays.stream(availableMoves).anyMatch(m -> m.equals(candidate))) {
                System.out.println("That move is not in the list of legal moves. Try again.");
                continue;
            }
            return candidate;
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
        for (Rule rule : rules) {
            if (!rule.validate(game, move)) {
                return rule.getDescription();
            }
        }
        var piece = game.getPiece(move.getSource());
        if (piece == null) {
            return "No piece at " + move.getSource();
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
            int x = str.charAt(0) - 'a';
            int y = Integer.parseInt(str.substring(1)) - 1;
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
        Place src = parsePlace(segments[0].strip());
        Place dst = parsePlace(segments[1].strip());
        return (src != null && dst != null)
                ? new Move(src, dst)
                : null;
    }
}