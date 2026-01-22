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
		final String line = terminalScanner.nextLine().trim();
		final String[] parts = line.split("\\s+");
		if (parts.length != 2) {
			System.out.println(INVALID_INPUT_MESSAGE);
			return new InvalidInput(-1, INVALID_INPUT_MESSAGE);
		}
		final int turn;
		try {
			turn = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			System.out.println(INVALID_INPUT_MESSAGE);
			return new InvalidInput(-1, INVALID_INPUT_MESSAGE);
		}
		final String cmd = parts[1];
		if (cmd.equalsIgnoreCase(EXIT_COMMAND_TEXT)) {
			return new Exit(turn);
		}
		if (cmd.equalsIgnoreCase("U")) {
			return new Undo(turn);
		}
		Matcher m = MOVE_REGEX.matcher(cmd);
		if (m.matches()) {
			char key = m.group("action").toUpperCase().charAt(0);
			return new Move(turn, key);
		}
		System.out.println(INVALID_INPUT_MESSAGE);
		return new InvalidInput(turn, INVALID_INPUT_MESSAGE);
	}
}
