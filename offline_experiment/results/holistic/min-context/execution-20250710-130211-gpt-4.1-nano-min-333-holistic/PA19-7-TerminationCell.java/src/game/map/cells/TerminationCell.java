
package util;

import org.jetbrains.annotations.NotNull;
import util.Direction;

/**
 * Utility class for pipe pattern characters.
 */
public class PipePatterns {

    /**
     * Returns the character pattern for a source cell based on the direction.
     *
     * @param direction the direction the source is pointing to
     * @return character representing the source pattern
     */
    public static char getSourcePattern(@NotNull Direction direction) {
        return switch (direction) {
            case UP -> '↑';
            case DOWN -> '↓';
            case LEFT -> '←';
            case RIGHT -> '→';
        };
    }

    /**
     * Returns the character pattern for a sink cell based on the direction.
     *
     * @param direction the direction the sink is pointing to
     * @return character representing the sink pattern
     */
    public static char getSinkPattern(@NotNull Direction direction) {
        return switch (direction) {
            case UP -> '∩';
            case DOWN -> '∪';
            case LEFT -> '⊣';
            case RIGHT -> '⊢';
        };
    }
}