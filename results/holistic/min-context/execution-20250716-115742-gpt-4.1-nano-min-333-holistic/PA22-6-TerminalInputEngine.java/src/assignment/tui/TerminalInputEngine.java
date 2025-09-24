
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.MoveAction;
import assignment.actions.Undo;
import assignment.game.InputEngine;
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

        // Check for move commands
        Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.matches()) {
            char moveChar = Character.toUpperCase(inputLine.charAt(0));
            return new MoveAction(moveChar);
        }

        String upperInput = inputLine.toUpperCase();

        // Check for quit command
        if (upperInput.equals("Q") || upperInput.equals("QUIT")) {
            return new Exit(EXIT_COMMAND_TEXT);
        }

        // Check for undo command
        if (upperInput.equals("U") || upperInput.equals("UNDO")) {
            return new Undo(1); // Explicitly specify undo steps
        }

        // Check for restart command
        if (upperInput.equals("R") || upperInput.equals("RESTART")) {
            return new Exit(EXIT_COMMAND_TEXT); // Assuming restart handled as exit + restart elsewhere
        }

        // Check for help command
        if (upperInput.equals("H") || upperInput.equals("HELP")) {
            return new InvalidInput(0, "Help command not implemented.");
        }

        // Default invalid input
        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}