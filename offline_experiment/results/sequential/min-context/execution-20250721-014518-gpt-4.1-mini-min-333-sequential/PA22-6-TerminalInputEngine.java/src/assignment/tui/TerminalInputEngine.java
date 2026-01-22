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

	@Override
	public Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		}
		if (inputLine.equalsIgnoreCase("U")) {
			return new Undo(0);
		}
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").toUpperCase().charAt(0);
			return new Move(0) {
				@Override
				public assignment.game.Position nextPosition(assignment.game.Position currentPosition) {
					int x = currentPosition.x();
					int y = currentPosition.y();
					return switch (actionChar) {
					case 'W', 'K', 'U':
						yield new assignment.game.Position(x, y - 1);
					case 'A', 'H', 'L':
						yield new assignment.game.Position(x - 1, y);
					case 'S', 'J', 'R':
						yield new assignment.game.Position(x, y + 1);
					case 'D':
						yield new assignment.game.Position(x + 1, y);
					default:
						yield currentPosition;
					};
				}
			};
		}
		return new InvalidInput(0, INVALID_INPUT_MESSAGE);
	}
}
