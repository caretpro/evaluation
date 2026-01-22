
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Tracks the game state: player position, score, lives, and move history.
 */
public class GameState {

    private final MoveStack moveStack = new MoveStack();
    private final int deathPenalty;
    private final int gemValue;
    private final int extraLifeValue;

    private int score = 0;
    private int lives = 1;
    private Position currentPosition;

    public GameState(@NotNull Position startPosition,
                     int deathPenalty,
                     int gemValue,
                     int extraLifeValue) {
        this.currentPosition = Objects.requireNonNull(startPosition);
        this.deathPenalty = deathPenalty;
        this.gemValue = gemValue;
        this.extraLifeValue = extraLifeValue;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public Position getPlayerPosition() {
        return currentPosition;
    }

    public void move(@NotNull MoveResult move) {
        Objects.requireNonNull(move);
        moveStack.push(move);

        if (move instanceof MoveResult.Valid.Alive alive) {
            score += gemValue * alive.collectedGems.size();
            score += extraLifeValue * alive.collectedExtraLives.size();
        } else if (move instanceof MoveResult.Valid.Dead dead) {
            lives--;
            score -= deathPenalty;
        }
        currentPosition = move.newPosition;
    }

    public Optional<MoveResult> undo() {
        if (moveStack.isEmpty()) {
            return Optional.empty();
        }
        MoveResult lastMove = moveStack.pop();
        if (lastMove instanceof MoveResult.Valid.Alive alive) {
            // reverse gem/extra‐life gains
            score -= gemValue * alive.collectedGems.size();
            score -= extraLifeValue * alive.collectedExtraLives.size();
        } else if (lastMove instanceof MoveResult.Valid.Dead dead) {
            // restore life and re‐apply death penalty deduction on undo
            lives++;
            score -= deathPenalty;      // <--- fix: subtract deathPenalty again
        }
        // reset position to before move
        if (lastMove instanceof MoveResult.Valid valid) {
            currentPosition = valid.origPosition;
        } else {
            currentPosition = lastMove.newPosition;
        }
        return Optional.of(lastMove);
    }
}