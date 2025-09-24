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
		while (true) {
			final String inputLine = terminalScanner.nextLine().trim();
			if (EXIT_COMMAND_TEXT.equalsIgnoreCase(inputLine)) {
				return new Exit(0);
			}
			if ("U".equalsIgnoreCase(inputLine)) {
				return new Undo(0);
			}
			final Matcher m = MOVE_REGEX.matcher(inputLine);
			if (m.matches()) {
				char c = Character.toUpperCase(m.group("action").charAt(0));
				return switch (c) {
				case 'W', 'K':
					yield new Move.North(0);
				case 'A', 'H':
					yield new Move.West(0);
				case 'S', 'J':
					yield new Move.South(0);
				case 'D', 'L':
					yield new Move.East(0);
				default:
					throw new ShouldNotReachException();
				};
			}
			System.out.println(INVALID_INPUT_MESSAGE);
		}
	}
}
