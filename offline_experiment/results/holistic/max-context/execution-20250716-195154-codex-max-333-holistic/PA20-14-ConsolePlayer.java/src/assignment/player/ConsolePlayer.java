
package assignment.player;

import assignment.protocol.Color;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Player;
import assignment.player.rule.ArcherMoveRule;
import assignment.player.rule.FirstNMovesProtectionRule;
import assignment.player.rule.KnightBlockRule;
import assignment.player.rule.KnightMoveRule;
import assignment.player.rule.NilMoveRule;
import assignment.player.rule.OccupiedRule;
import assignment.player.rule.OutOfBoundaryRule;
import assignment.player.rule.Rule;
import assignment.player.rule.VacantRule;
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
     * (e.g. {@literal a1->b3} means move the {@link assignment.piece.Piece}
     * at {@link Place}(x=0,y=0) to {@link Place}(x=1,y=2))
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
    public @NotNull Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // redraw board, scores, guide, etc.
            game.refreshOutput();

            // prompt the user
            System.out.printf("%s%s, enter your move (e.g. a1->b2): %s",
                    getColor(), getName(), Color.DEFAULT);
            String line = scanner.nextLine().trim();

            // parse user input
            Move move = parseMove(line);
            if (move == null) {
                System.out.println("  [Error] Invalid format. Use e.g. a1->b2");
                continue;
            }

            // rule‑based validation (bounds, occupancy, move legality, first‑move protection, etc.)
            String error = validateMove(game, move);
            if (error != null) {
                System.out.println("  [Error] " + error);
                continue;
            }

            // ensure the move is actually in availableMoves
            boolean found = false;
            for (Move m : availableMoves) {
                if (m.equals(move)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("  [Error] That move is not available.");
                continue;
            }

            // all good
            return move;
        }
    }

    private String validateMove(Game game, Move move) {
        Rule[] rules = new Rule[]{
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
            new KnightMoveRule(),
            new KnightBlockRule(),
        };
        for (Rule rule : rules) {
            if (!rule.validate(game, move)) {
                return rule.getDescription();
            }

            var piece = game.getPiece(move.getSource());
            if (piece == null) {
                return "No piece at " + move.getSource();
            }
            if (!this.equals(piece.getPlayer())) {
                return "Cannot move a piece not belonging to you";
            }
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
        if (segments.length < 2) {
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