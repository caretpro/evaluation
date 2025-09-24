
/**
 * Get the maximum height of the game map.
 *
 * @return maximum height.
 */
public int getMaxHeight() {
    if (maxHeight > 0) {
        return maxHeight;
    }
    return map.keySet().stream()
            .mapToInt(Position::y)
            .max()
            .orElse(-1) + 1;
}