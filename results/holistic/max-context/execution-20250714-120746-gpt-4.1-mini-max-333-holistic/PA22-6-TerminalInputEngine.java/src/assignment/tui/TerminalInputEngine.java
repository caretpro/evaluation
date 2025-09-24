
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
	 * The player ID to pass to actions that require it.
	 */
	private final int playerId;

	/**
	 * @param terminalStream The stream to read terminal inputs.
	 * @param playerId       The player ID to associate with actions.
	 */
	public TerminalInputEngine(InputStream terminalStream, int playerId) {
		this.terminalScanner = new Scanner(terminalStream);
		this.playerId = playerId;
	}

	private static final Pattern MOVE_REGEX = Pattern.compile("^(?<action>[WASDwasdUu])$");

	/**
	 * Fetch an action from user in terminal to process.
	 *
	 * @return the user action.
	 */
	@Override
	public @NotNull Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();

		// Check for exit command (case insensitive)
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(playerId);
		}

		// Check for single character commands
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").toUpperCase().charAt(0);
			return switch (actionChar) {
				case 'W' -> new MoveUp(playerId);
				case 'A' -> new MoveLeft(playerId);
				case 'S' -> new MoveDown(playerId);
				case 'D' -> new MoveRight(playerId);
				case 'U' -> new Undo(playerId);
				default -> throw new ShouldNotReachException();
			};
		}

		// If input does not match any known command, return invalid input action
		return new InvalidInput(playerId, INVALID_INPUT_MESSAGE);
	}
}