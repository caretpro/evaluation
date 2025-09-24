
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
	 * Directions for Move actions.
	 */
	private enum Direction {
		UP, DOWN, LEFT, RIGHT, HOME, RESET
	}

	/**
	 * Fetch an action from user in terminal to process.
	 *
	 * @return the user action.
	 */
	@Override
	public Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();

		final int playerId = 0;

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(playerId);
		}

		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").charAt(0);
			Direction direction;
			switch (Character.toUpperCase(actionChar)) {
				case 'W' -> direction = Direction.UP;
				case 'A' -> direction = Direction.LEFT;
				case 'S' -> direction = Direction.DOWN;
				case 'D' -> direction = Direction.RIGHT;
				case 'H' -> direction = Direction.HOME;
				case 'J' -> direction = Direction.DOWN;
				case 'K' -> direction = Direction.UP;
				case 'L' -> direction = Direction.RIGHT;
				case 'R' -> direction = Direction.RESET;
				case 'U' -> {
					return new Undo(playerId);
				}
				default -> throw new ShouldNotReachException();
			}
			return Move.of(playerId, direction);
		}

		return new InvalidInput(playerId, INVALID_INPUT_MESSAGE);
	}
}