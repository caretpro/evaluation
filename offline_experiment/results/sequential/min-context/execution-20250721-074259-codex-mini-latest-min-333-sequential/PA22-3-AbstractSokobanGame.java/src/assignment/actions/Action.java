package assignment.actions;

/**
 * An action performed by a player.
 */
public abstract class Action {

    protected final int initiator;

    /**
     * @return The id of the player who performed the action.
     */
    public int getInitiator() {
        return initiator;
    }

    protected Action(int initiator) {
        this.initiator = initiator;
    }
}
