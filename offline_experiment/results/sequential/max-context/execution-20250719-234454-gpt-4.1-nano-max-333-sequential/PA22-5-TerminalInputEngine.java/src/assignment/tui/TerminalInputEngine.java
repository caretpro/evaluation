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
		Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
		if (moveMatcher.matches()) {
			String actionChar = moveMatcher.group("action").toLowerCase();
			switch (actionChar) {
			case "w":
				return new Move(assignment.actions.Direction.UP);
			case "a":
				return new Move(assignment.actions.Direction.LEFT);
			case "s":
				return new Move(assignment.actions.Direction.DOWN);
			case "d":
				return new Move(assignment.actions.Direction.RIGHT);
			case "r":
				return new Move(assignment.actions.Direction.RESTART);
			case "h":
				return new Move(assignment.actions.Direction.HELP);
			case "j":
				return new Move(assignment.actions.Direction.JUMP);
			case "k":
				return new Move(assignment.actions.Direction.KICK);
			case "l":
				return new Move(assignment.actions.Direction.LOOK);
			case "u":
				return new Undo(0);
			default:
				return new InvalidInput(0, INVALID_INPUT_MESSAGE);
			}
		}
		if (inputLine.equalsIgnoreCase("exit")) {
			return new Exit(0);
		}
		return new InvalidInput(0, INVALID_INPUT_MESSAGE);
	}
}
