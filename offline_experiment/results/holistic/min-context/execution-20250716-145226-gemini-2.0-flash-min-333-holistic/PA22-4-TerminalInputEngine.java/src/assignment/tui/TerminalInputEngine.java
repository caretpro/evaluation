
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.MoveDown;
import assignment.actions.MoveLeft;
import assignment.actions.MoveRight;
import assignment.actions.MoveUp;
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

		if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
			return new Exit(0);
		} else if (moveMatcher.matches()) {
			final String action = moveMatcher.group("action");
			return switch (action.toUpperCase()) {
				case "W", "U" -> new MoveUp(0);
				case "A", "H" -> new MoveLeft(0);
				case "S", "J" -> new MoveDown(0);
				case "D", "K" -> new MoveRight(0);
				case "R" -> new Undo(0);
				default -> throw new ShouldNotReachException();
			};
		} else {
			System.out.println(INVALID_INPUT_MESSAGE);
			return new InvalidInput(0, INVALID_INPUT_MESSAGE);
		}
	}
}