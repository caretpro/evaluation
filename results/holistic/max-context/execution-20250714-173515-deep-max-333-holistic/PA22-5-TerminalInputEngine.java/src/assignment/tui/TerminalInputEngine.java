
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

public class TerminalInputEngine implements InputEngine {
    private final Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasdRrHJKLhjklUu])$");

    @Override
    public Action fetchAction() {
        final String inputLine = terminalScanner.nextLine().trim();

        if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
            return new Exit(0);
        }

        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            String action = matcher.group("action").toUpperCase();
            switch (action) {
                case "U":
                    return new Undo(0);
                case "W":
                case "A":
                case "S":
                case "D":
                case "H":
                case "J":
                case "K":
                case "L":
                    return new Move() { // Anonymous class implementing abstract Move
                        @Override
                        public char getDirection() {
                            return action.charAt(0);
                        }
                    };
                default:
                    throw new ShouldNotReachException();
            }
        }

        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}