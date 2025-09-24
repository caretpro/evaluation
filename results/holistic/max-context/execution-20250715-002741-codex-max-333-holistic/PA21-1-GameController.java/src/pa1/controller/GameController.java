
package pa1.controller;

import pa1.model.Direction;
import pa1.model.MoveResult;
import pa1.model.PositionOffset;
import pa1.model.Tile;
import pa1.model.Entity;
import pa1.model.Player;

/**
 * Controller for mutating a GameBoard.
 */
public class GameBoardController {
    // ...
    /** Slides the player (and entities) one cell at a time in the given direction until hitting an obstacle. */
    public MoveResult slidePlayerOneByOne(Direction dir) { … }

    /** Slides the player immediately to the last empty cell before an obstacle. */
    public MoveResult slidePlayerToEnd(Direction dir) { … }

    /** Performs the reverse of a prior slide (undo), using the MoveResult’s stored data. */
    public void undoSlide(MoveResult reverse) { … }
    // ...
}