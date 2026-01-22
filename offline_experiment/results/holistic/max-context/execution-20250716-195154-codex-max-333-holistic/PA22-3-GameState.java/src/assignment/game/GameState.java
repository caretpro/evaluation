
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.utils.ShouldNotReachException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An abstract skeleton for a Sokoban game that handles Move actions,
 * undos, and win/lose checks against a backing {@link GameState}.
 */
public abstract class AbstractSokobanGame {

    private final GameState state;
    private final TerminalRenderingEngine renderer = new TerminalRenderingEngine();

    protected AbstractSokobanGame(GameState initialState) {
        this.state = initialState;
    }

    /**
     * Process one Move action (possibly multi-step like pushing a box).
     * Throws on invalid player id or illegal move (e.g. into wall or box).
     */
    public final void processAction(@NotNull Move action) {
        int pid = action.getPlayerId();
        // 1) find the player
        Position pos = state.getPlayerPositionById(pid)
                .orElseThrow(() -> new IllegalArgumentException("Player " + pid + " not found"));

        // 2) target position for the player
        Position nxt = action.nextPosition(pos);

        Entity atNext = state.getEntity(nxt);
        // If it's a wall (entity==null), we hit a wall
        if (atNext == null) {
            throw new IllegalStateException("Hit wall at " + nxt);
        }
        // If it's a Box, we attempt to push
        if (atNext instanceof Box) {
            pushBox((Box) atNext, pid, pos, nxt, action);
            return;
        }
        // If it's Empty or Player, just move the player
        if (atNext instanceof Empty || atNext instanceof Player) {
            state.move(pos, nxt);
            return;
        }
        // Should never get here
        throw new ShouldNotReachException("Unexpected entity at move: " + atNext);
    }

    private void pushBox(Box box, int pid, Position playerFrom, Position boxPos, Move action) {
        // Only boxes with matching playerId can be pushed
        if (box.getPlayerId() != pid) {
            throw new IllegalStateException("Player " + pid + " cannot push box of player " + box.getPlayerId());
        }
        // compute where the box would go
        Position boxTo = action.nextPosition(boxPos);
        if (state.getEntity(boxTo) == null || state.getEntity(boxTo) instanceof Box) {
            throw new IllegalStateException("Cannot push box into " + state.getEntity(boxTo));
        }
        state.move(playerFrom, boxPos);
        state.move(boxPos, boxTo);
    }

    /** Undo one full push‐or‐move operation. */
    public final void undo() {
        state.undo();
    }

    /** Render the current state as text rows. */
    public final @NotNull List<String> render() {
        List<String> rows = new ArrayList<>();
        int h = state.getMapMaxHeight();
        int w = state.getMapMaxWidth();
        for (int y = 0; y < h; y++) {
            Map<Integer, Entity> row = new HashMap<>();
            for (int x = 0; x < w; x++) {
                Position p = Position.of(x, y);
                Entity e = state.getEntity(p);
                row.put(x, e instanceof Empty ? e : e); // missing map means wall
            }
            rows.add(renderer.renderRow(row));
        }
        return rows;
    }
}