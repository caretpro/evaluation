
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Undo;
import assignment.actions.moves.MoveDown;
import assignment.actions.moves.MoveLeft;
import assignment.actions.moves.MoveRight;
import assignment.actions.moves.MoveUp;
import assignment.game.InputEngine;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static assignment.utils.StringResources.EXIT_COMMAND_TEXT;
import static assignment.utils.StringResources.INVALID_INPUT_MESSAGE;
import static assignment.utils.StringResources.UNDO_COMMAND_TEXT;
import static assignment.utils.StringResources.ERROR_CODE_EXIT;
import static assignment.utils.StringResources.ERROR_CODE_UNDO;
import static assignment.utils.StringResources.ERROR_CODE_MOVE;
import static assignment.utils.StringResources.ERROR_CODE_INVALID_INPUT;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

    /**
     * Scanner reading from the terminal.
     */
    private final Scanner terminalScanner;

    /**
     * Constructs an engine reading from the given stream.
     *
     * @param terminalStream stream to read commands from
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    /** Regex matching a single move character (W/A/S/D). */
    private static final Pattern MOVE_REGEX =
            Pattern.compile("^(?<action>[WASDwasd])$");

    /**
     * Fetches and parses the next action from the terminal.
     *
     * @return a non-null Action
     */
    @Override
    @NotNull
    public Action fetchAction() {
        String input = terminalScanner.nextLine().trim();

        // exit?
        if (input.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
            return new Exit(ERROR_CODE_EXIT);
        }

        // undo?
        if (input.equalsIgnoreCase(UNDO_COMMAND_TEXT)) {
            return new Undo(ERROR_CODE_UNDO);
        }

        // move? (W/A/S/D or lowercase variants)
        Matcher m = MOVE_REGEX.matcher(input);
        if (m.matches()) {
            char c = Character.toUpperCase(m.group("action").charAt(0));
            return switch (c) {
                case 'W' -> new MoveUp(ERROR_CODE_MOVE);
                case 'S' -> new MoveDown(ERROR_CODE_MOVE);
                case 'A' -> new MoveLeft(ERROR_CODE_MOVE);
                case 'D' -> new MoveRight(ERROR_CODE_MOVE);
                default -> throw new IllegalStateException("Unexpected move: " + c);
            };
        }

        // otherwise invalid
        return new InvalidInput(ERROR_CODE_INVALID_INPUT, INVALID_INPUT_MESSAGE);
    }
}