
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
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
		final String inputLine = terminalScanner.nextLine();
		Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);

		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		} else if (inputLine.equalsIgnoreCase("U") || inputLine.equalsIgnoreCase("u")) {
			return new Undo(0);
		} else if (moveMatcher.matches()) {
			String action = moveMatcher.group("action").toUpperCase();
			return switch (action) {
				case "W" -> new Move.Up(0);
				case "A" -> new Move.Left(0);
				case "S" -> new Move.Down(0);
				case "D" -> new Move.Right(0);
				case "H" -> new Move.Left(0);
				case "J" -> new Move.Down(0);
				case "K" -> new Move.Up(0);
				case "L" -> new Move.Right(0);
				case "U" -> new Move.Up(0);
				case "R" -> new Move.Right(0);
				default -> throw new IllegalStateException("Unexpected value: " + action);
			};
		} else {
			System.out.println(INVALID_INPUT_MESSAGE);
			return new InvalidInput(0, inputLine);
		}
	}
}