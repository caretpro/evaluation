
// assignment/protocol/Game.java
package assignment.protocol;

import java.util.List;

public interface Game {
    /**
     * @return list of all moves applied so far, in order
     */
    List<Move> getMoveHistory();
}