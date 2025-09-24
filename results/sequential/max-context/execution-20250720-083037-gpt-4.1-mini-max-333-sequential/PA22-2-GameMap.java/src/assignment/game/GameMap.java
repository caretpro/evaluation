
/**
 * Get the zero-based player IDs of the game map.
 * Player characters 'A', 'B', 'C', ... are mapped to 0, 1, 2, ...
 *
 * @return a set of zero-based player IDs.
 */
public Set<Integer> getPlayerIds() {
    return map.values().stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> ((Player) entity).getId())
            .map(idChar -> idChar - 'A')  // Convert 'A'->0, 'B'->1, etc.
            .collect(Collectors.toSet());
}