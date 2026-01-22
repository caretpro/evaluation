package assignment.tui;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import assignment.game.GameState;
import assignment.game.Position;
import assignment.game.RenderingEngine;
import assignment.utils.NotImplementedException;

import java.io.PrintStream;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private final PrintStream outputStream;

    /**
     * @param outputStream The {@link PrintStream} to write the output to.
     */
    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        final StringBuilder builder = new StringBuilder();
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                final Entity entity = state.getEntity(Position.of(x, y));

                char charToPrint = ' ';
                if (entity instanceof Wall) {
                    throw new UnsupportedOperationException();
                } else if (entity instanceof Box) {
                    throw new UnsupportedOperationException();
                } else if (entity instanceof Player) {
                    throw new UnsupportedOperationException();
                } else if (entity instanceof Empty) {
                    throw new UnsupportedOperationException();
                } else if (entity == null) {
                    charToPrint = ' ';
                }
                builder.append(charToPrint);
            }
            builder.append('\n');
        }
        outputStream.print(builder);
    }

    @Override
    public void message(String content) {
        // TODO
        // Hint: System.out is also a PrintStream.
        throw new NotImplementedException();
    }
}
