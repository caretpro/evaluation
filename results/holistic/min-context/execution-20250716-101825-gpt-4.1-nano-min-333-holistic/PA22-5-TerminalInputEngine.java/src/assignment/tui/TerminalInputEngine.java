
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
                    return new Move("UP");
                case "A":
                    return new Move("LEFT");
                case "S":
                    return new Move("DOWN");
                case "D":
                    return new Move("RIGHT");
                case "R":
                    return new Move("RESTART");
                case "H":
                    return new Move("HELP");
                case "J":
                    return new Move("JUMP");
                case "K":
                    return new Move("KICK");
                case "L":
                    return new Move("LOOK");
                case "U":
                    return new Undo(0);
                default:
                    return new InvalidInput(1, INVALID_INPUT_MESSAGE);
            }
        }

        // Check for special commands (e.g., exit, undo)
        String lowerInput = inputLine.toLowerCase();

        if (lowerInput.equals("exit")) {
            return new Exit(EXIT_COMMAND_TEXT);
        } else if (lowerInput.equals("undo")) {
            return new Undo(0);
        } else {
            // If input doesn't match any command, return invalid input
            return new InvalidInput(1, INVALID_INPUT_MESSAGE);
        }
    }
}