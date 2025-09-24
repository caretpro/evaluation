
package util;

/**
 * Common single‐character representations used for pipes and walls in the game map.
 */
public interface PipePatterns {
    /** wall */
    char WALL = 'W';

    /** empty/fillable cell */
    char EMPTY = '.';

    /** pipe pointing up */
    char PIPE_UP = '^';

    /** pipe pointing down */
    char PIPE_DOWN = 'v';

    /** pipe pointing left */
    char PIPE_LEFT = '<';

    /** pipe pointing right */
    char PIPE_RIGHT = '>';
}