
package assignment.tui;

import assignment.actions.Action;
import assignment.actions.Exit;
import assignment.actions.InvalidInput;
import assignment.actions.Move;
import assignment.actions.Undo;
import assignment.game.InputEngine;
import assignment.game.Position;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static assignment.utils.StringResources.EXIT_COMMAND_TEXT;
import static assignment.utils.StringResources.INVALID_INPUT_MESSAGE;

public class TerminalInputEngine implements InputEngine {
    private final Scanner terminalScanner;

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

        Matcher moveMatcher = MOVE_REGEX.matcher(inputLine);
        if (moveMatcher.matches()) {
            String action = moveMatcher.group("action").toUpperCase();
            return new Move(0) {
                @Override
                public char getDirection() {
                    return action.charAt(0);
                }

                @Override
                public Position nextPosition(Position current) {
                    char dir = Character.toUpperCase(getDirection());
                    int x = current.getX();
                    int y = current.getY();
                    
                    switch (dir) {
                        case 'W': case 'K': return new Position(x, y - 1);
                        case 'A': case 'H': return new Position(x - 1, y);
                        case 'S': case 'J': return new Position(x, y + 1);
                        case 'D': case 'L': return new Position(x + 1, y);
                        default: return current;
                    }
                }
            };
        }

        if (inputLine.equalsIgnoreCase("U")) {
            return new Undo(0);
        }

        return new InvalidInput(0, INVALID_INPUT_MESSAGE);
    }
}