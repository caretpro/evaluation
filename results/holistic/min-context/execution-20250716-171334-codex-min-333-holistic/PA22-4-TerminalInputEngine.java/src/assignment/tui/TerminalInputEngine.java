
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.utils.ShouldNotReachException;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static assignment.utils.StringResources.EXIT_COMMAND_TEXT;
import static assignment.utils.StringResources.INVALID_INPUT_MESSAGE;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

    /**
     * The {@link Scanner} for reading input from the terminal.
     */
    private final Scanner terminalScanner;

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        final String inputLine = terminalScanner.nextLine().trim();

        // 1) Check for move commands (single W/A/S/D/R/H/J/K/L/U, case‐insensitive)
        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            char direction = matcher.group("action").toUpperCase().charAt(0);
            // Use the Move factory to get the correct concrete Move subclass.
            return Move.of(direction);
        }

        // 2) Undo (playerId 0 by convention)
        if ("U".equalsIgnoreCase(inputLine)) {
            return new Undo(0);
        }

        // 3) Exit (playerId 0 by convention)
        if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
            return new Exit(0);
        }

        // 4) Anything else is invalid
        if (!inputLine.isEmpty()) {
            System.out.println(INVALID_INPUT_MESSAGE);
            return new InvalidInput(0, INVALID_INPUT_MESSAGE);
        }

        // Should not reach here
        throw new ShouldNotReachException();
    }
}