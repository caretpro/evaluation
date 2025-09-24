
package assignment.tui;

import assignment.actions.Action;
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
import static assignment.utils.StringResources.UNDO_INPUT_TEXT;

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
    public @NotNull Action fetchAction() {
        String inputLine = terminalScanner.nextLine().trim();

        // 1) Exit (exact, case-sensitive)
        if (inputLine.equals(EXIT_COMMAND_TEXT)) {
            return new Exit(EXIT_COMMAND_TEXT.length());
        }

        // 2) Undo (single-char, case-insensitive)
        if (inputLine.equalsIgnoreCase(UNDO_INPUT_TEXT)) {
            return new Undo(UNDO_INPUT_TEXT.length());
        }

        // 3) Move (W/A/S/D or H/J/K/L)
        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            char dir = matcher.group("action").charAt(0);
            return Move.of(dir, 1);
        }

        // 4) Invalid input
        System.out.println(INVALID_INPUT_MESSAGE);
        return new InvalidInput(inputLine.length(), inputLine);
    }
}