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
		String command = inputLine.toLowerCase();
		switch (command) {
		case "exit":
			return new Exit(0);
		case "undo":
			return new Undo(0);
		case "w":
		case "a":
		case "s":
		case "d":
		case "r":
		case "h":
		case "j":
		case "k":
		case "l":
			return new Move(command);
		default:
			Matcher matcher = MOVE_REGEX.matcher(command);
			if (matcher.matches()) {
				String actionChar = matcher.group("action").toLowerCase();
				return new Move(actionChar);
			}
			return new InvalidInput(0, INVALID_INPUT_MESSAGE);
		}
	}
}
