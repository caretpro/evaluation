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
	public @NotNull Action fetchAction() {
		final String inputLine = terminalScanner.nextLine().trim();
		if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
			return new Exit(0);
		}
		Matcher m = MOVE_REGEX.matcher(inputLine);
		if (m.matches()) {
			char c = Character.toUpperCase(m.group("action").charAt(0));
			switch (c) {
			case 'W':
				return Move.of(0, -1);
			case 'A':
				return Move.of(-1, 0);
			case 'S':
				return Move.of(0, 1);
			case 'D':
				return Move.of(1, 0);
			case 'H':
				return Move.of(-1, 0);
			case 'J':
				return Move.of(0, 1);
			case 'K':
				return Move.of(0, -1);
			case 'L':
				return Move.of(1, 0);
			case 'R':
			case 'U':
				return new Undo(0);
			default:
				throw new ShouldNotReachException();
			}
		}
		System.out.println(INVALID_INPUT_MESSAGE);
		return new InvalidInput(0, inputLine);
	}
}
