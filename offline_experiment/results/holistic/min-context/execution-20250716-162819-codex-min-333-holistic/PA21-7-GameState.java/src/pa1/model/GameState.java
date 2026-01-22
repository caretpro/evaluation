
package pa1.model;

import pa1.controller.GameBoardController;
import pa1.view.GameBoardView;

/**
 * Represents the game board and provides read‐only accessors
 * plus hooks for obtaining its controller and view.
 */
public interface GameBoard {

    /**
     * @return The number of gems currently on this board.
     */
    int getNumGems();

    /**
     * @return The width of the board.
     */
    int getWidth();

    /**
     * @return The height of the board.
     */
    int getHeight();

    /**
     * @return A controller for mutating this board.
     */
    GameBoardController getController();

    /**
     * @return A read‐only view of this board.
     */
    GameBoardView getView();
}