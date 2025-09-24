package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
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
	 * @return  the user action.
	 */
	@Override
	public Action fetchAction() {
		final String inputLine = terminalScanner.nextLine();
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		}
		if (inputLine.equalsIgnoreCase("undo")) {
			return new Undo(0);
		}
		final Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			final String action = matcher.group("action").toUpperCase();
			char moveChar = action.charAt(0);
			return switch (moveChar) {
			case 'W':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'W';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'A':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'A';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'S':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'S';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'D':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'D';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'R':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'R';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'H':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'H';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'J':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'J';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'K':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'K';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'L':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'L';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			case 'U':
				yield new Move(0) {
					@Override
					public char getDirection() {
						return 'U';
					}

					@Override
					public Position nextPosition(Position position) {
						return position;
					}
				};
			default:
				throw new ShouldNotReachException();
			};
		}
		System.out.println(INVALID_INPUT_MESSAGE);
		return new InvalidInput(0, inputLine);
	}
}
