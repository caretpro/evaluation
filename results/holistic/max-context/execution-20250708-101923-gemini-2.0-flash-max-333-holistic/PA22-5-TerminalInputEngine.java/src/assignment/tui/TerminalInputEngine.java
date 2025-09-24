
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.game.Position;
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

	private static class Up extends Move {
		public Up(String direction) {
			super(direction);
		}

		@Override
		public Position nextPosition(@NotNull Position current) {
			return new Position(current.getX(), current.getY() - 1);
		}
	}

	private static class Down extends Move {
		public Down(String direction) {
			super(direction);
		}

		@Override
		public Position nextPosition(@NotNull Position current) {
			return new Position(current.getX(), current.getY() + 1);
		}
	}

	private static class Left extends Move {
		public Left(String direction) {
			super(direction);
		}

		@Override
		public Position nextPosition(@NotNull Position current) {
			return new Position(current.getX() - 1, current.getY());
		}
	}

	private static class Right extends Move {
		public Right(String direction) {
			super(direction);
		}

		@Override
		public Position nextPosition(@NotNull Position current) {
			return new Position(current.getX() + 1, current.getY());
		}
	}

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

		final Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);

		if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
			return new Exit(0);
		} else if (moveMatcher.matches()) {
			final String action = moveMatcher.group("action");
			String upperCaseAction = action.toUpperCase();
			switch (upperCaseAction) {
				case "W":
				case "K":
				case "U":
					return new Up(upperCaseAction);
				case "S":
				case "J":
					return new Down(upperCaseAction);
				case "A":
				case "H":
					return new Left(upperCaseAction);
				case "D":
				case "R":
					return new Right(upperCaseAction);
				default:
					return new InvalidInput(0, INVALID_INPUT_MESSAGE); // Should not happen, but handle it anyway
			}
		} else if ("U".equalsIgnoreCase(inputLine)) {
			return new Undo(0);
		} else {
			return new InvalidInput(0, INVALID_INPUT_MESSAGE);
		}
	}
}