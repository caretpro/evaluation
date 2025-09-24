
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.PipePatterns;

import game.map.cells.Wall;      // ← import the Wall cell
import game.map.cells.PipeEnd;   // ← and any other subclasses you reference
import game.map.cells.PipeStraight;
import game.map.cells.PipeTurn;
import game.map.cells.PipeCross;
import game.map.cells.Empty;

/**
 * Common base for all cells on the {@link game.map.Map}.
 */
public abstract class Cell {

    protected final Coordinate coord;

    /**
     * @param coord coordinates of this cell
     */
    protected Cell(@NotNull Coordinate coord) {
        this.coord = coord;
    }

    /**
     * Returns the cell‐type corresponding to the given character.
     *
     * @param c the map character
     * @param coord the cell’s coordinate
     * @return a new cell instance or {@code null} if the character is unrecognized
     */
    public static Cell fromChar(char c, Coordinate coord) {
        // use the patterns in PipePatterns (N,E,S,W bit‑mask)
        switch (c) {
            case 'x': return new Wall(coord);
            case '└': return new PipeEnd(coord, PipePatterns.SOUTH);
            case '┘': return new PipeEnd(coord, PipePatterns.WEST);
            case '┌': return new PipeEnd(coord, PipePatterns.EAST);
            case '┐': return new PipeEnd(coord, PipePatterns.NORTH);
            case '│': return new PipeStraight(coord, PipePatterns.NORTH | PipePatterns.SOUTH);
            case '─': return new PipeStraight(coord, PipePatterns.EAST  | PipePatterns.WEST);
            case '┴': return new PipeTurn(coord, PipePatterns.SOUTH | PipePatterns.WEST);
            case '┤': return new PipeTurn(coord, PipePatterns.WEST  | PipePatterns.NORTH);
            case '┬': return new PipeTurn(coord, PipePatterns.NORTH | PipePatterns.EAST);
            case '├': return new PipeTurn(coord, PipePatterns.EAST  | PipePatterns.SOUTH);
            case '┼': return new PipeCross(coord);
            case ' ': return new Empty(coord);
            default:  return null;
        }
    }

    /**
     * @return the single‐character representation of this cell in the map display
     */
    public abstract char toSingleChar();

    // … other shared behavior …
}