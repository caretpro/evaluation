
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Direction;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
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

    private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasd])$");

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        String inputLine = terminalScanner.nextLine().trim();

        // 1) Exit request?
        if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
            return new Exit(0);
        }

        // 2) Undo request?
        if ("u".equalsIgnoreCase(inputLine)) {
            return new Undo(1);
        }

        // 3) Move request?
        Matcher m = MOVE_REGEX.matcher(inputLine);
        if (m.matches()) {
            char c = Character.toUpperCase(m.group("action").charAt(0));
            switch (c) {
                case 'W':
                    return new Move(1, Direction.UP);
                case 'A':
                    return new Move(1, Direction.LEFT);
                case 'S':
                    return new Move(1, Direction.DOWN);
                case 'D':
                    return new Move(1, Direction.RIGHT);
                default:
                    throw new IllegalStateException("Unexpected move: " + c);
            }
        }

        // 4) Otherwise, invalid input
        System.out.println(INVALID_INPUT_MESSAGE);
        return new InvalidInput(1, inputLine);
    }
}