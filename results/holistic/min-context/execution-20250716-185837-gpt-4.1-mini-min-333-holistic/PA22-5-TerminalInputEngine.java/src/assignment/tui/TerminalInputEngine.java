
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

	/**
	 * Fetch an action from user in terminal to process.
	 *
	 * @return the user action.
	 */
	@Override
	public Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		}

		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").toUpperCase().charAt(0);
			switch (actionChar) {
				case 'W', 'A', 'S', 'D', 'H', 'J', 'K', 'L' -> {
					// Create an anonymous subclass of Move implementing nextPosition
					return new Move(0) {
						@Override
						public @NotNull Position nextPosition(@NotNull Position currentPosition) {
							int x = currentPosition.x();
							int y = currentPosition.y();
							return switch (actionChar) {
								case 'W', 'K' -> new Position(x, y - 1); // Up (y-1)
								case 'S', 'J' -> new Position(x, y + 1); // Down (y+1)
								case 'A', 'H' -> new Position(x - 1, y); // Left
								case 'D', 'L' -> new Position(x + 1, y); // Right
								default -> throw new IllegalStateException("Unexpected move action: " + actionChar);
							};
						}
					};
				}
				case 'U' -> {
					return new Undo(0);
				}
				case 'R' -> {
					// No Reset action defined, treat as invalid input
					return new InvalidInput(0, INVALID_INPUT_MESSAGE);
				}
				default -> {
					return new InvalidInput(0, INVALID_INPUT_MESSAGE);
				}
			}
		}

		return new InvalidInput(0, INVALID_INPUT_MESSAGE);
	}
}