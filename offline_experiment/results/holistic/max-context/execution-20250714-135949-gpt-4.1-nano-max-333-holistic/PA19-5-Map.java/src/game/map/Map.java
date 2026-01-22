
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import game.pipes.Pipe;

/**
 * Represents a fillable cell that can contain a pipe.
 */
public class FillableCell extends Cell {

    private Pipe pipe;

    public FillableCell(@NotNull Coordinate coord) {
        super(coord);
        this.pipe = null;
    }

    /**
     * Checks if the cell has a pipe.
     */
    public boolean hasPipe() {
        return pipe != null;
    }

    /**
     * Sets a pipe in this cell.
     */
    public void setPipe(@NotNull Pipe p) {
        this.pipe = p;
    }

    /**
     * Clears the pipe from this cell.
     */
    public void clearPipe() {
        this.pipe = null;
    }

    /**
     * Gets the pipe in this cell.
     */
    public @NotNull java.util.Optional<Pipe> getPipe() {
        return java.util.Optional.ofNullable(pipe);
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.';
    }
}