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
			return new InvalidInput(1, INVALID_INPUT_MESSAGE);
		}
		String command = inputLine.toUpperCase();
		Matcher moveMatcher = MOVE_REGEX.matcher(command);
		if (moveMatcher.matches()) {
			String actionChar = moveMatcher.group("action").toUpperCase();
			switch (actionChar) {
			case "W":
				return new Move(0, Move.Direction.UP);
			case "A":
				return new Move(0, Move.Direction.LEFT);
			case "S":
				return new Move(0, Move.Direction.DOWN);
			case "D":
				return new Move(0, Move.Direction.RIGHT);
			case "R":
				return new Move(0, Move.Direction.RESTART);
			case "U":
				return new Undo(0);
			default:
				break;
			}
		}
		if (command.equals("U")) {
			return new Undo(0);
		} else if (command.equals("Q") || command.equals("E")) {
			return new Exit(0);
		}
		return new InvalidInput(1, INVALID_INPUT_MESSAGE);
	}
}
