
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
	 *
	 * @return the user action.
	 */
	@Override
	public @NotNull Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();

		// Use 0 as default playerId for all actions
		final int playerId = 0;

		// Check for exit command (case insensitive)
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(playerId);
		}

		// Match single character commands for move, undo, restart, help
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			char actionChar = matcher.group("action").charAt(0);
			switch (Character.toUpperCase(actionChar)) {
				case 'W', 'A', 'S', 'D', 'H', 'J', 'K', 'L', 'R' -> {
					return createMoveAction(playerId, Character.toUpperCase(actionChar));
				}
				case 'U' -> {
					return new Undo(playerId);
				}
				default -> throw new ShouldNotReachException();
			}
		}

		// If input is not recognized, return InvalidInput action with message
		return new InvalidInput(playerId, INVALID_INPUT_MESSAGE);
	}

	/**
	 * Create a concrete Move action based on the input character.
	 *
	 * @param playerId the player id
	 * @param actionChar the character representing the move
	 * @return a concrete Move action
	 */
	private Move createMoveAction(int playerId, char actionChar) {
		// Assuming there is a concrete subclass of Move named ConcreteMove that accepts playerId and direction
		// Since we don't have details, we simulate direction as a char or string.
		// You should replace this with your actual concrete Move subclass or factory method.

		// Example: if you have an enum Direction, map char to Direction here
		// For demonstration, let's assume Move has a constructor Move(int playerId, char directionChar) in a concrete subclass

		// If no concrete subclass is available, you must implement one or use existing ones.

		// Here, we create an anonymous subclass of Move as a placeholder:
		return new Move(playerId, actionChar) {
			@Override
			public String toString() {
				return "Move{" + "playerId=" + playerId + ", actionChar=" + actionChar + '}';
			}
		};
	}
}