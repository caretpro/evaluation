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
		if (inputLine.isEmpty()) {
			return new InvalidInput(0, "Empty input");
		}
		if (inputLine.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(0);
		}
		if (inputLine.equalsIgnoreCase("U")) {
			return new Undo(0);
		}
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			String actionChar = matcher.group("action").toLowerCase();
			switch (actionChar) {
			case "w":
				return new Move(Direction.UP);
			case "a":
				return new Move(Direction.LEFT);
			case "s":
				return new Move(Direction.DOWN);
			case "d":
				return new Move(Direction.RIGHT);
			case "h":
				return new Move(Direction.LEFT);
			case "j":
				return new Move(Direction.DOWN);
			case "k":
				return new Move(Direction.UP);
			case "l":
				return new Move(Direction.RIGHT);
			default:
				return new InvalidInput(1, "Invalid move command");
			}
		}
		return new InvalidInput(2, "Unrecognized command");
	}
}
