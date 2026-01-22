
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.utils.ShouldNotReachException;

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
			return new InvalidInput(1, INVALID_INPUT_MESSAGE);
		}

		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (!matcher.matches()) {
			return new InvalidInput(1, INVALID_INPUT_MESSAGE);
		}

		char actionChar = matcher.group("action").charAt(0);

		switch (Character.toUpperCase(actionChar)) {
			case 'W', 'A', 'S', 'D', 'H', 'J', 'K', 'L' -> {
				return createMove(actionChar);
			}
			case 'R' -> {
				return new Exit(EXIT_COMMAND_TEXT);
			}
			case 'U' -> {
				return new Undo(1);
			}
			default -> throw new ShouldNotReachException();
		}
	}

	/**
	 * Create a concrete Move action instance for the given action character.
	 *
	 * @param actionChar the character representing the move direction.
	 * @return a concrete Move instance.
	 */
	private static Move createMove(char actionChar) {
		// Since Move is abstract, create an anonymous subclass.
		return new Move(1, actionChar) {
			@Override
			public String toString() {
				return "Move{" + "id=1, actionChar=" + actionChar + '}';
			}
		};
	}
}