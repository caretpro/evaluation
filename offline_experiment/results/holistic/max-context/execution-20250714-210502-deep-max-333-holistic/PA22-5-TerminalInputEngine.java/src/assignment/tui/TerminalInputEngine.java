
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.game.Position;
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

        if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
            return new Exit(0);
        }

        if (inputLine.equalsIgnoreCase("U")) {
            return new Undo(0);
        }

        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            String action = matcher.group("action").toUpperCase();
            return new Move(0) {
                @Override
                public String getDirection() {
                    return action;
                }

                @Override
                public Position nextPosition(Position current) {
                    int x = current.getX();
                    int y = current.getY();
                    switch (action) {
                        case "W": case "K": y--; break;
                        case "A": case "H": x--; break;
                        case "S": case "J": y++; break;
                        case "D": case "L": x++; break;
                    }
                    return new Position(x, y);
                }
            };
        }

        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}