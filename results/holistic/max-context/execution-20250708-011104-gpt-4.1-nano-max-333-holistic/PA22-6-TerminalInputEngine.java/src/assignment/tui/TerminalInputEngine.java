
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.MoveUp;
import assignment.actions.MoveDown;
import assignment.actions.MoveLeft;
import assignment.actions.MoveRight;
import assignment.actions.Restart;
import assignment.actions.Help;
import assignment.actions.Jump;
import assignment.actions.Kick;
import assignment.actions.Look;
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

        if (inputLine.isEmpty()) {
            return new InvalidInput(1, "Empty input");
        }

        // Check for exit command
        if (inputLine.equalsIgnoreCase("exit")) {
            return new Exit(0);
        }

        // Check for undo command
        if (inputLine.equalsIgnoreCase("undo")) {
            return new Undo(0);
        }

        // Check for move commands using regex
        Matcher matcher = MOVE_REGEX.matcher(inputLine);
        if (matcher.matches()) {
            String actionChar = matcher.group("action").toLowerCase();
            switch (actionChar) {
                case "w":
                    return new MoveUp();
                case "a":
                    return new MoveLeft();
                case "s":
                    return new MoveDown();
                case "d":
                    return new MoveRight();
                case "r":
                    return new Restart();
                case "h":
                    return new Help();
                case "j":
                    return new Jump();
                case "k":
                    return new Kick();
                case "l":
                    return new Look();
                case "u":
                    return new MoveUp(); // or a specific move up if different
                default:
                    return new InvalidInput(1, "Invalid move command");
            }
        }

        // If input does not match any known command
        return new InvalidInput(1, "Unknown command");
    }
}