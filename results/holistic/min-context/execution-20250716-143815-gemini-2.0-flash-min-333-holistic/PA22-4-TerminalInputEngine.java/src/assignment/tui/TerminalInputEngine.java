
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
				case "W", "U" -> new Move.Up(1);
				case "A", "L" -> new Move.Left(1);
				case "S", "J" -> new Move.Down(1);
				case "D", "K" -> new Move.Right(1);
				case "R", "H" -> new Undo(1);
				default -> throw new ShouldNotReachException();
			};
		}

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(1);
		}

		System.out.println(INVALID_INPUT_MESSAGE);
		return new InvalidInput(1, inputLine);
	}
}