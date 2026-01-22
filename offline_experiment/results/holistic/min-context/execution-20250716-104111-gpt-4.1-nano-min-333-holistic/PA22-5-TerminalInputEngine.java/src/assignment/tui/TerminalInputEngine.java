
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
            char actionChar = matcher.group("action").toUpperCase().charAt(0);
            switch (actionChar) {
                case 'W':
                    return new Move(Move.Direction.UP);
                case 'A':
                    return new Move(Move.Direction.LEFT);
                case 'S':
                    return new Move(Move.Direction.DOWN);
                case 'D':
                    return new Move(Move.Direction.RIGHT);
                case 'R':
                    return new Move(Move.Direction.RESTART);
                case 'H':
                    return new Move(Move.Direction.HELP);
                case 'J':
                    return new Move(Move.Direction.JUMP);
                case 'K':
                    return new Move(Move.Direction.K);
                case 'L':
                    return new Move(Move.Direction.L);
                case 'U':
                    return new Move(Move.Direction.UNDO);
                default:
                    break;
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

        // If input does not match any command, return InvalidInput
        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}