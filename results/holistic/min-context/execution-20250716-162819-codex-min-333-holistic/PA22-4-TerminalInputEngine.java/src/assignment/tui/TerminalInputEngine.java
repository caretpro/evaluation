
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.MoveEast;
import assignment.actions.MoveNorth;
import assignment.actions.MoveSouth;
import assignment.actions.MoveWest;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.utils.ShouldNotReachException;

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

    /** The {@link Scanner} for reading input from the terminal. */
    private final Scanner terminalScanner;

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    private static final Pattern MOVE_REGEX =
        Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public Action fetchAction() {
        String inputLine = terminalScanner.nextLine().trim();

        // Exit command
        if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
            // Exit ctor requires the length of the command text
            return new Exit(EXIT_COMMAND_TEXT.length());
        }

        // Undo command ('U' or 'u')
        if (inputLine.equalsIgnoreCase("U")) {
            // Undo ctor requires a non‑zero int (e.g. 1)
            return new Undo(1);
        }

        // Single-character move commands
        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            char c = Character.toUpperCase(matcher.group("action").charAt(0));
            return switch (c) {
                case 'W', 'K' -> new MoveNorth(1);
                case 'A', 'H' -> new MoveWest(1);
                case 'S', 'J' -> new MoveSouth(1);
                case 'D', 'L', 'R' -> new MoveEast(1);
                default -> throw new ShouldNotReachException();
            };
        }

        // Anything else is invalid
        return new InvalidInput(inputLine.length(), INVALID_INPUT_MESSAGE);
    }
}