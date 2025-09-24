
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
		// This is an example showing how to read a line from the Scanner class.
		// Feel free to change it if you do not like it.
		final String inputLine = terminalScanner.nextLine();

		final Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);

		if (moveMatcher.matches()) {
			final String action = moveMatcher.group("action").toUpperCase();
			return switch (action) {
				case "W", "K" -> new Move(Move.Direction.UP);
				case "A", "H" -> new Move(Move.Direction.LEFT);
				case "S", "J" -> new Move(Move.Direction.DOWN);
				case "D", "L" -> new Move(Move.Direction.RIGHT);
				case "R", "U" -> new Undo(1);
				default -> throw new ShouldNotReachException();
			};
		}

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(1);
		}

		System.out.println(INVALID_INPUT_MESSAGE);
		return new InvalidInput(1, INVALID_INPUT_MESSAGE);
	}
}