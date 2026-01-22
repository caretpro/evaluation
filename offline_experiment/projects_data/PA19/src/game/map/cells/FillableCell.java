package game.map.cells;

import game.MapElement;
import game.map.cells.Cell;
import game.pipes.Pipe;
import util.Coordinate;

import java.io.*;
import java.util.*;

/**
 * Represents a {@link Cell} which can contain a pipe.
 */
public class FillableCell extends Cell implements MapElement {

    private Pipe pipe;

    /**
     * Constructs a {@link FillableCell}.
     *
     * @param coord Coordinate where the cell resides in the map.
     */
    public FillableCell(Coordinate coord) {
        super(coord);
        pipe = null;
    }

    /**
     * Constructs a {@link FillableCell} with a pipe already contained in it.
     *
     * @param coord Coordinate where the cell resides in the map.
     * @param pipe The pipe inside this cell.
     */
    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        pipe = null;
    }

    /**
     * @return An {@link java.util.Optional} representing the pipe in this tile.
     */
    public Optional<Pipe> getPipe() {
        // TODO
        return null;
    }

    /**
     * @return The character representation of the pipe, or {@code '.'} if the cell is empty.
     */
    @Override
    public char toSingleChar() {
        // TODO
        return '\0';
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }
}
