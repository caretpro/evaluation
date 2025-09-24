
package pa1.util;

import org.jetbrains.annotations.NotNull;
import pa1.model.GameBoard;
import pa1.model.GameState;
import pa1.model.Cell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utilities to serialize and deserialize {@link GameState}s (and their underlying {@link GameBoard}s).
 */
public final class GameStateSerializer {

    private static final String SEPARATOR = " ";

    private GameStateSerializer() {
        // no instances
    }

    /**
     * Writes the given game state (including its board) to the given writer.
     *
     * The format is:
     *
     * <pre>
     *   numRows numCols initialLives numMoves numDeaths numUndos
     *   [for each cell row-major:]
     *     r c cellString
     * </pre>
     *
     * where cellString is the {@code toString()} of each {@link Cell}.
     *
     * @param state  the game state to serialize
     * @param writer the writer to write into
     * @throws IOException if an I/O error occurs
     */
    public static void saveTo(@NotNull GameState state, @NotNull BufferedWriter writer) throws IOException {
        Objects.requireNonNull(state);
        Objects.requireNonNull(writer);
        GameBoard board = state.getGameBoard();

        // write header
        writer.write(board.getNumRows() + SEPARATOR
                   + board.getNumCols() + SEPARATOR
                   + state.getNumLives() + SEPARATOR
                   + state.getNumMoves() + SEPARATOR
                   + state.getNumDeaths() + SEPARATOR
                   + state.getMoveStack().getPopCount());
        writer.newLine();

        // write each cell
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                writer.write(r + SEPARATOR + c + SEPARATOR + board.getCell(r, c).toString());
                writer.newLine();
            }
        }
        writer.flush();
    }

    /**
     * Reads a {@link GameState} (and its underlying board) from the given reader.
     *
     * @param reader the reader to read from
     * @return the deserialized game state
     * @throws IOException if an I/O error occurs
     */
    public static @NotNull GameState loadFrom(@NotNull BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);
        String header = reader.readLine();
        if (header == null) {
            throw new IOException("Empty input");
        }
        String[] parts = header.split(SEPARATOR);
        int numRows    = Integer.parseInt(parts[0]);
        int numCols    = Integer.parseInt(parts[1]);
        int numLives   = Integer.parseInt(parts[2]);
        int numMoves   = Integer.parseInt(parts[3]);
        int numDeaths  = Integer.parseInt(parts[4]);
        int numUndos   = Integer.parseInt(parts[5]);

        // Read cells in row-major order
        Cell[][] cells = new Cell[numRows][numCols];
        for (int i = 0; i < numRows * numCols; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Unexpected EOF");
            }
            String[] tok = line.split(SEPARATOR, 3);
            int r = Integer.parseInt(tok[0]);
            int c = Integer.parseInt(tok[1]);
            cells[r][c] = Cell.fromString(tok[2]);
        }

        // build state
        GameState state = new GameState(new GameBoard(numRows, numCols, cells), numLives);
        // replay stats
        for (int i = 0; i < numMoves;  i++) state.incrementNumMoves();
        for (int i = 0; i < numDeaths; i++) state.incrementNumDeaths();
        for (int i = 0; i < numUndos;  i++) state.getMoveStack().pop();

        return state;
    }

    /**
     * Convenience to save to a file.
     */
    public static void saveTo(@NotNull GameState state, @NotNull Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            saveTo(state, writer);
        }
    }

    /**
     * Convenience to load from a file.
     */
    public static @NotNull GameState loadFrom(@NotNull Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return loadFrom(reader);
        }
    }
}