
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

/**
 * Base class for all cells in the game map.
 */
public abstract class Cell {
    @NotNull public final Coordinate coord;
    @NotNull public final Direction direction;
    private boolean filled = false;

    /**
     * Termination‑type for sources/sinks.
     * Used by both the TerminationCell and the factory below.
     */
    public enum Type {
        SOURCE, SINK
    }

    protected Cell(@NotNull Coordinate coord, @NotNull Direction direction) {
        this.coord = coord;
        this.direction = direction;
    }

    /**
     * Mark this cell as filled (e.g. by flowing water).
     */
    public void setFilled() {
        filled = true;
    }

    /**
     * @return true once setFilled() has been called.
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * @return a single-character representation of this cell in the game.
     */
    public abstract char toSingleChar();

    /**
     * Factory: build the right Cell subclass from a map‐character.
     * If the char denotes a termination cell, you must pass in the Type,
     * otherwise terminationType may be ignored (and is permitted to be null).
     */
    public static Cell fromChar(char ch, @NotNull Coordinate coord, Type terminationType) {
        // Wall chars
        if (ch == PipePatterns.WALL_CHAR) {
            return new Wall(coord, Direction.NONE);
        }
        // Fillable‑pipe chars
        for (Direction d : Direction.values()) {
            if (PipePatterns.pipe(d).emptyChar() == ch || PipePatterns.pipe(d).filledChar() == ch) {
                FillableCell c = new FillableCell(coord, d);
                if (PipePatterns.pipe(d).filledChar() == ch) {
                    c.setFilled();
                }
                return c;
            }
        }
        // Termination cell (source or sink)
        if (terminationType != null) {
            if (PipePatterns.source(Direction.UP).emptyChar() == ch
             || PipePatterns.source(Direction.UP).filledChar() == ch
             || PipePatterns.source(Direction.RIGHT).emptyChar() == ch
             || PipePatterns.source(Direction.RIGHT).filledChar() == ch
             || PipePatterns.source(Direction.DOWN).emptyChar() == ch
             || PipePatterns.source(Direction.DOWN).filledChar() == ch
             || PipePatterns.source(Direction.LEFT).emptyChar() == ch
             || PipePatterns.source(Direction.LEFT).filledChar() == ch) {
                TerminationCell t = new TerminationCell(coord, Direction.fromChar(ch), Type.SOURCE);
                if (PipePatterns.source(t.direction).filledChar() == ch) {
                    t.setFilled();
                }
                return t;
            }
            if (PipePatterns.sink(Direction.UP).emptyChar() == ch
             || PipePatterns.sink(Direction.UP).filledChar() == ch
             || PipePatterns.sink(Direction.RIGHT).emptyChar() == ch
             || PipePatterns.sink(Direction.RIGHT).filledChar() == ch
             || PipePatterns.sink(Direction.DOWN).emptyChar() == ch
             || PipePatterns.sink(Direction.DOWN).filledChar() == ch
             || PipePatterns.sink(Direction.LEFT).emptyChar() == ch
             || PipePatterns.sink(Direction.LEFT).filledChar() == ch) {
                TerminationCell t = new TerminationCell(coord, Direction.fromChar(ch), Type.SINK);
                if (PipePatterns.sink(t.direction).filledChar() == ch) {
                    t.setFilled();
                }
                return t;
            }
        }
        // Unknown character
        return null;
    }
}