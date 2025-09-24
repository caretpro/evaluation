
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
    public Action fetchAction() {
        final String inputLine = terminalScanner.nextLine().trim();

        // Check for move commands (WASD, etc.)
        Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.matches()) {
            String actionChar = moveMatcher.group("action").toUpperCase();
            // Use a concrete subclass or factory method for Move if Move is abstract
            // Assuming Move has a static factory method 'of' or a concrete constructor
            return new Move(actionChar.charAt(0));
        }

        // Check for special commands
        switch (inputLine.toUpperCase()) {
            case "R":
                return new Undo(0);
            case "Q":
                return new Exit(EXIT_COMMAND_TEXT);
            case "H":
                return new InvalidInput(1, "Help command not implemented.");
            default:
                return new InvalidInput(1, INVALID_INPUT_MESSAGE);
        }
    }
}