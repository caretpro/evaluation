package assignment.game;

import assignment.actions.Action;

/**
 * An engine for getting inputs from players.
 */
public interface InputEngine {

    /**
     * Fetches an unprocessed action performed by the players.
     *
     * @return the action to process.
     */
    Action fetchAction();
}
