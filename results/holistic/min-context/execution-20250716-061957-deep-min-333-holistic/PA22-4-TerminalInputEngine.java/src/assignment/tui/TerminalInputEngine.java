
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

        if (inputLine.equalsIgnoreCase("U")) {
            return new Undo(0);
        }

        Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.matches()) {
            String direction = moveMatcher.group("action").toUpperCase();
            return new SimpleMove(direction); // Using concrete implementation
        }

        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}