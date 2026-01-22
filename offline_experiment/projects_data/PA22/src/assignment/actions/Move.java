package assignment.actions;

import assignment.game.Position;
import assignment.actions.Action;
import assignment.utils.NotImplementedException;

/**
 * An actions of moving a player.
 */
public abstract class Move extends Action {
    protected Move(int initiator) {
        super(initiator);
    }

    /**
     * Generates the next position after the move based on the current position.
     *
     * @param currentPosition The current position.
     * @return The next position.
     */
    public abstract Position nextPosition(Position currentPosition);

    /**
     * The action of moving down.
     */
    public static final class Down extends Move {

        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Down(int initiator) {
            super(initiator);
        }

        @Override
        public Position nextPosition(Position currentPosition) {
            // TODO
            throw new NotImplementedException();
        }
    }

    /**
     * The action of moving left.
     */
    public static final class Left extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Left(int initiator) {
            super(initiator);
        }

        @Override
        public Position nextPosition(Position currentPosition) {
            // TODO
            throw new NotImplementedException();
        }
    }

    /**
     * The action of mocking right.
     */
    public static final class Right extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Right(int initiator) {
            super(initiator);
        }

        @Override
        public Position nextPosition(Position currentPosition) {
            // TODO
            throw new NotImplementedException();
        }
    }

    /**
     * The action of moving up.
     */
    public static final class Up extends Move {
        /**
         * @param initiator The id of the player who give the invalid input.
         */
        public Up(int initiator) {
            super(initiator);
        }

        @Override
        public Position nextPosition(Position currentPosition) {
            // TODO
            throw new NotImplementedException();
        }
    }
}

