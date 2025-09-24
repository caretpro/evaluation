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
		if (inputLine.equalsIgnoreCase("exit")) {
			return new Exit(0);
		}
		if (inputLine.equalsIgnoreCase("undo")) {
			return new Undo(0);
		}
		Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
		if (moveMatcher.matches()) {
			String actionChar = moveMatcher.group("action").toLowerCase();
			switch (actionChar) {
			case "w":
				return new Move(assignment.actions.Move.Direction.UP);
			case "a":
				return new Move(assignment.actions.Move.Direction.LEFT);
			case "s":
				return new Move(assignment.actions.Move.Direction.DOWN);
			case "d":
				return new Move(assignment.actions.Move.Direction.RIGHT);
			case "r":
				return new Restart();
			case "h":
				return new Help();
			case "j":
				return new Jump();
			case "k":
				return new Kick();
			case "l":
				return new Look();
			case "u":
				return new Move(assignment.actions.Move.Direction.UP);
			default:
				break;
			}
		}
		return new InvalidInput(0, INVALID_INPUT_MESSAGE);
	}
}
