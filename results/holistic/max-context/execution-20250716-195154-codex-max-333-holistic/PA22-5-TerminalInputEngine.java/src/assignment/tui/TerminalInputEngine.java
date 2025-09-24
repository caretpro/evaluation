
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Undo;
import assignment.actions.impl.MoveEast;
import assignment.actions.impl.MoveNorth;
import assignment.actions.impl.MoveSouth;
import assignment.actions.impl.MoveWest;
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

    private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasdHJKLhjkl])$");

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    @NotNull
    public Action fetchAction() {
        String inputLine = terminalScanner.nextLine().trim();

        // Exit command (e.g. "exit")
        if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
            // pass 0 for normal termination
            return new Exit(0);
        }

        // Undo command ("U" or "u")
        if ("U".equalsIgnoreCase(inputLine)) {
            // undo one step
            return new Undo(1);
        }

        // Move commands (W/A/S/D or vi keys H/J/K/L)
        Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.matches()) {
            char actionChar = Character.toUpperCase(moveMatcher.group("action").charAt(0));
            switch (actionChar) {
                case 'W', 'K': // north/up
                    return new MoveNorth();
                case 'S', 'J': // south/down
                    return new MoveSouth();
                case 'A', 'H': // west/left
                    return new MoveWest();
                case 'D', 'L': // east/right
                    return new MoveEast();
                default:
                    throw new ShouldNotReachException();
            }
        }

        // Invalid input
        String msg = String.format(INVALID_INPUT_MESSAGE, inputLine);
        // we pass 0 as a dummy line index here
        return new InvalidInput(0, msg);
    }
}