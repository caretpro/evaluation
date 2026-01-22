
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.MoveUp;
import assignment.actions.MoveDown;
import assignment.actions.MoveLeft;
import assignment.actions.MoveRight;
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
        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            String actionChar = matcher.group("action").toUpperCase();
            switch (actionChar) {
                case "W":
                    return new MoveUp();
                case "A":
                    return new MoveLeft();
                case "S":
                    return new MoveDown();
                case "D":
                    return new MoveRight();
                case "R":
                case "H":
                case "J":
                case "K":
                case "L":
                case "U":
                    // Handle other directions if applicable
                    // For simplicity, assuming only four directions here
                    return new Move(actionChar.charAt(0));
                default:
                    return new InvalidInput(0, INVALID_INPUT_MESSAGE);
            }
        }

        // Check for exit command
        if (inputLine.equalsIgnoreCase("exit")) {
            return new Exit(0);
        }

        // Check for undo command
        if (inputLine.equalsIgnoreCase("undo")) {
            return new Undo(0);
        }

        // If input doesn't match any command, return InvalidInput
        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}