
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

        if (inputLine.equalsIgnoreCase("exit")) {
            return new Exit(0);
        } else if (inputLine.equalsIgnoreCase("undo")) {
            return new Undo(0);
        } else {
            Matcher matcher = MOVE_REGEX.matcher(inputLine);
            if (matcher.matches()) {
                String actionChar = matcher.group("action").toLowerCase();
                switch (actionChar) {
                    case "w":
                        return new Move(Direction.UP);
                    case "a":
                        return new Move(Direction.LEFT);
                    case "s":
                        return new Move(Direction.DOWN);
                    case "d":
                        return new Move(Direction.RIGHT);
                    case "r":
                        return new Move(Direction.RESTART);
                    case "h":
                        return new Move(Direction.HELP);
                    case "j":
                        return new Move(Direction.JUMP);
                    case "k":
                        return new Move(Direction.KICK);
                    case "l":
                        return new Move(Direction.LOOK);
                    case "u":
                        return new Move(Direction.UNDO);
                    default:
                        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
                }
            } else {
                return new InvalidInput(0, INVALID_INPUT_MESSAGE);
            }
        }
    }

    /**
     * Enum representing directions for move actions.
     */
    private enum Direction {
        UP, DOWN, LEFT, RIGHT, RESTART, HELP, JUMP, KICK, LOOK, UNDO
    }
}