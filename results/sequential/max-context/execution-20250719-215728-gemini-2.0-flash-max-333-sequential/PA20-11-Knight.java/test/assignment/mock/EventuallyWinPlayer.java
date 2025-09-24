package assignment.mock;

import assignment.protocol.Color;
import assignment.protocol.Game;
import assignment.protocol.Move;

public class EventuallyWinPlayer extends MockPlayer {
    public EventuallyWinPlayer(Color color) {
        super(color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        var minDistance = Integer.MAX_VALUE;
        var best = availableMoves[0];
        for (var move :
                availableMoves) {
            var dist = Math.abs(move.getDestination().x() - move.getSource().x()) +
                    Math.abs(move.getDestination().y() - move.getSource().y());
            if (dist <= minDistance) {
                minDistance = dist;
                best = move;
            }
        }
        return best;
    }
}
