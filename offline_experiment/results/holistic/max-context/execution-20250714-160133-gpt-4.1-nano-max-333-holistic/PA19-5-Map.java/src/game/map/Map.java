
package game.map.cells;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;

import java.util.Optional;

/**
 * Represents a fillable cell in the map.
 */
public class FillableCell extends Cell {

    private Pipe pipe;
    private boolean filled = false;

    public FillableCell(@NotNull Coordinate coord) {
        super(coord);
        this.pipe = null;
    }

    /**
     * Checks if the cell currently contains a pipe.
     *
     * @return true if a pipe is present, false otherwise.
     */
    public boolean hasPipe() {
        return pipe != null;
    }

    /**
     * Sets a pipe in this cell.
     *
     * @param p Pipe to set.
     */
    public void setPipe(@NotNull Pipe p) {
        this.pipe = p;
    }

    /**
     * Removes the pipe from this cell.
     */
    public void removePipe() {
        this.pipe = null;
    }

    /**
     * Checks if the cell is occupied (has a pipe).
     *
     * @return true if occupied, false otherwise.
     */
    public boolean isOccupied() {
        return pipe != null;
    }

    /**
     * Gets the pipe in this cell.
     *
     * @return Optional containing the pipe if present.
     */
    public Optional<Pipe> getPipe() {
        return Optional.ofNullable(pipe);
    }

    /**
     * Sets the cell as filled.
     */
    public void setFilled() {
        this.filled = true;
    }

    @Override
    public char toSingleChar() {
        if (pipe != null) {
            return pipe.toSingleChar();
        }
        return '.'; // Representation for empty fillable cell
    }
}