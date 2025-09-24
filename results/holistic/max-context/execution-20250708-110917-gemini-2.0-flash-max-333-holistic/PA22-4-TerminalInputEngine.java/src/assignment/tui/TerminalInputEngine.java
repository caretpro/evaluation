
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.game.Position;
import assignment.utils.ShouldNotReachException;
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
	public @NotNull Action fetchAction() {
		// This is an example showing how to read a line from the Scanner class.
		// Feel free to change it if you do not like it.
		final String inputLine = terminalScanner.nextLine();

		Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		} else if (inputLine.equalsIgnoreCase("U") || inputLine.equalsIgnoreCase("u")) {
			return new Undo(0);
		} else if (moveMatcher.matches()) {
			String action = moveMatcher.group("action");
			// Assuming ConcreteMove is a concrete subclass of Move
			return new Move(0) { // Provide a default value for the constructor
                @Override
                public Position nextPosition(Position current) {
                    // Implement the logic to calculate the next position based on the action
                    // For example:
                    switch (action.toUpperCase()) {
                        case "W":
                            return new Position(current.getX(), current.getY() - 1); // Move up
                        case "A":
                            return new Position(current.getX() - 1, current.getY()); // Move left
                        case "S":
                            return new Position(current.getX(), current.getY() + 1); // Move down
                        case "D":
                            return new Position(current.getX() + 1, current.getY()); // Move right
                        default:
                            return current; // No change
                    }
                }
            };
		} else {
			System.out.println(INVALID_INPUT_MESSAGE);
			return new InvalidInput(0, INVALID_INPUT_MESSAGE);
		}
	}
}