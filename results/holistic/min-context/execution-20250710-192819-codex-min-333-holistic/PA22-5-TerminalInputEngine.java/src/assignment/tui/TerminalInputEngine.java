
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

    /** The {@link Scanner} for reading input from the terminal. */
    private final Scanner terminalScanner;

    private static final Pattern MOVE_REGEX =
        Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        String inputLine = terminalScanner.nextLine().trim();
        Matcher matcher = MOVE_REGEX.matcher(inputLine);

        if (!matcher.matches()) {
            // InvalidInput expects an error code (e.g. 1) and a message
            return new InvalidInput(1, INVALID_INPUT_MESSAGE);
        }

        char c = matcher.group("action").charAt(0);
        switch (Character.toUpperCase(c)) {
            case 'W', 'K':
                return new Move(Move.Direction.UP);
            case 'A', 'H':
                return new Move(Move.Direction.LEFT);
            case 'S', 'J':
                return new Move(Move.Direction.DOWN);
            case 'D', 'L':
                return new Move(Move.Direction.RIGHT);
            case 'U':
                // Undo takes an error code (e.g. 0) in its constructor
                return new Undo(0);
            case 'R':
                // Exit takes the exit-command text
                return new Exit(EXIT_COMMAND_TEXT);
            default:
                // This should never happen due to the regex coverage
                throw new ShouldNotReachException();
        }
    }
}