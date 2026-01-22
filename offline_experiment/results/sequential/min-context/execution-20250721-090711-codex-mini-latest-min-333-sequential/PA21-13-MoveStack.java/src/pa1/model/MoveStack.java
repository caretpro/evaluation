
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Tracks the state of a roguelike-like game, including current score, depth, lives, etc.
 */
public class GameState {

    private int score = 0;
    private int depth = 1;
    private int lives = 3;
    private boolean dead = false;

    /** 
     * A simple stack recording each state-changing move so that undo() can restore prior values.
     */
    @NotNull
    private final MoveStack history = new MoveStack();

    /**
     * Returns the player's current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * The player traversed one tile; score increases by 1.
     */
    public void move() {
        record(); 
        score += 1;
    }

    /**
     * The player fell into a trap (died), score loses 10 points, and loses a life.
     */
    public void die() {
        record();
        dead = true;
        score -= 10;
        lives -= 1;
    }

    /**
     * Undo the last move (whether move() or die()), restoring score, depth, lives, and dead flag.
     *
     * @throws IllegalStateException if there is no move to undo
     */
    public void undo() {
        Objects.requireNonNull(history, "history must not be null");
        MoveResult last = history.pop();  // throws if empty
        this.score = last.score();
        this.depth = last.depth();
        this.lives = last.lives();
        this.dead = last.dead();
    }

    /**
     * Record the current state onto the history stack.
     */
    private void record() {
        history.push(new MoveResult(score, depth, lives, dead));
    }
}