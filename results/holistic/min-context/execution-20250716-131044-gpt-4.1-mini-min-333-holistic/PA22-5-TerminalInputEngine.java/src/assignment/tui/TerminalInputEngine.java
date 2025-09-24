
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

		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (!matcher.matches()) {
			return new InvalidInput(1);
		}

		char actionChar = matcher.group("action").charAt(0);

		return switch (Character.toUpperCase(actionChar)) {
			case 'W' -> Move.of(0); // UP
			case 'A' -> Move.of(1); // LEFT
			case 'S' -> Move.of(2); // DOWN
			case 'D' -> Move.of(3); // RIGHT
			case 'H' -> Move.of(4); // HOME
			case 'J' -> Move.of(2); // DOWN (vim key)
			case 'K' -> Move.of(0); // UP (vim key)
			case 'L' -> Move.of(3); // RIGHT (vim key)
			case 'R' -> new Exit(0);
			case 'U' -> new Undo(2);
			default -> throw new ShouldNotReachException();
		};
	}
}