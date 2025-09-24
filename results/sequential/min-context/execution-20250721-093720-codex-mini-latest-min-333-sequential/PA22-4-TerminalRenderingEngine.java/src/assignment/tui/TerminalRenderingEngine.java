
package assignment.tui;

import assignment.game.GameState;
import assignment.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Objects;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private final PrintStream outputStream;

    /**
     * @param outputStream The {@link PrintStream} to write the output to.
     */
    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    @Override
    public void render(@NotNull GameState state) {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                var pos = new assignment.game.Position(x, y);
                var entity = state.getEntity(pos);
                char toPrint = entity instanceof assignment.entities.Wall             ? '#'
                             : entity instanceof assignment.entities.Box box       ? (char)('a' + box.getPlayerId())
                             : entity instanceof assignment.entities.Player player ? (char)('A' + player.getId())
                             : state.getDestinations().contains(pos)               ? '@'
                             : '.';
                builder.append(toPrint);
            }
            builder.append('\n');
        }
        outputStream.print(builder);
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}