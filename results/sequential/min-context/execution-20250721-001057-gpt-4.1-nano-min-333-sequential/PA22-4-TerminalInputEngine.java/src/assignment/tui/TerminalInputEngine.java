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
		if (inputLine.equalsIgnoreCase("U") || inputLine.equalsIgnoreCase("Undo")) {
			return new Undo(0);
		}
		Matcher matcher = MOVE_REGEX.matcher(inputLine);
		if (matcher.matches()) {
			String actionChar = matcher.group("action").toUpperCase();
			switch (actionChar) {
			case "W":
				return new MoveUp(0);
			case "A":
				return new MoveLeft(0);
			case "S":
				return new MoveDown(0);
			case "D":
				return new MoveRight(0);
			case "R":
				return new Restart(0);
			case "H":
				return new Help(0);
			case "J":
				return new Jump(0);
			case "K":
				return new Kick(0);
			case "L":
				return new Look(0);
			default:
				return new InvalidInput(0, INVALID_INPUT_MESSAGE);
			}
		}
		return new InvalidInput(0, INVALID_INPUT_MESSAGE);
	}
}
