
package pa1.model;

/**
 * A human‐controlled player on the board.
 */
public class Player extends Entity {

    // existing one‐arg constructor:
    public Player() {
        super();  // chains to Entity’s zero‐arg constructor
    }

    // other constructors you already have…
    public Player(EntityCell startCell) {
        super(startCell);
    }

    // … rest of Player’s code …
}