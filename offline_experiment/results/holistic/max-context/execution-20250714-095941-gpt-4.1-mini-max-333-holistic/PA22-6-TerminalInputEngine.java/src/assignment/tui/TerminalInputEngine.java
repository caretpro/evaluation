
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
		final String inputLine = terminalScanner.nextLine().trim();

		// Using player id 0 as default for all actions here
		final int playerId = 0;

		// Check for exit command (case insensitive)
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(playerId);
		}

		// Match move or other single-character commands
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").charAt(0);
			return switch (Character.toUpperCase(actionChar)) {
				case 'W' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row() - 1, current.col());
					}
				};
				case 'A' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row(), current.col() - 1);
					}
				};
				case 'S' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row() + 1, current.col());
					}
				};
				case 'D' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row(), current.col() + 1);
					}
				};
				case 'U' -> new Undo(playerId);
				case 'R' -> throw new ShouldNotReachException();
				case 'H' -> throw new ShouldNotReachException();
				case 'J' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row() + 1, current.col());
					}
				};
				case 'K' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row() - 1, current.col());
					}
				};
				case 'L' -> new Move(playerId) {
					@Override
					public Position nextPosition(Position current) {
						return new Position(current.row(), current.col() + 1);
					}
				};
				default -> new InvalidInput(playerId, INVALID_INPUT_MESSAGE);
			};
		}

		// If input does not match any known command, return invalid input
		return new InvalidInput(playerId, INVALID_INPUT_MESSAGE);
	}
}