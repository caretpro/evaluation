
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {

    private final Stack<Transition> history = new Stack<>();
    private final Map<Position, Entity> entities;
    private final int boardWidth;
    private final int boardHeight;
    private final Set<Position> destinations;
    private int undoQuota;
    private Transition currentTransition = new Transition();

    public GameState(GameMap map) {
        this.entities = new HashMap<>();
        for (int x = 0; x < map.getMaxWidth(); x++) {
            for (int y = 0; y < map.getMaxHeight(); y++) {
                Position pos = new Position(x, y);
                Entity entity = map.getEntity(pos);
                this.entities.put(pos, entity != null ? entity : new Empty());
            }
        }
        this.boardWidth = map.getMaxWidth();
        this.boardHeight = map.getMaxHeight();
        this.destinations = new HashSet<>(map.getDestinations());
        this.undoQuota = map.getUndoLimit().orElse(-1);
    }

    public Position getPlayerPositionById(int id) {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player && ((Player) e.getValue()).getId() == id)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public Set<Position> getAllPlayerPositions() {
        return entities.entrySet().stream()
                .filter(e -> e.getValue() instanceof Player)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Entity getEntity(Position position) {
        return entities.get(position);
    }

    public Set<Position> getDestinations() {
        return Collections.unmodifiableSet(destinations);
    }

    public Optional<Integer> getUndoQuota() {
        return undoQuota < 0 ? Optional.empty() : Optional.of(undoQuota);
    }

    public boolean isWin() {
        return destinations.stream()
                .allMatch(pos -> entities.get(pos) instanceof Box);
    }

    public void move(Position from, Position to) {
        Entity entity = entities.get(from);
        currentTransition.add(from, to);
        entities.put(from, new Empty());
        entities.put(to, entity);
    }

    public void checkpoint() {
        if (!currentTransition.moves.isEmpty()) {
            history.push(currentTransition);
            currentTransition = new Transition();
            if (undoQuota > 0) {
                undoQuota--;
            }
        }
    }

    private void applyTransition(Transition transition) {
        transition.moves.forEach((from, to) -> {
            Entity entity = entities.remove(from);
            entities.put(from, new Empty());
            entities.put(to, entity);
        });
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }
        Transition lastTransition = history.pop();
        applyTransition(lastTransition.reverse());
        if (undoQuota >= 0) {
            undoQuota++;
        }
    }

    public int getMapMaxWidth() {
        return boardWidth;
    }

    public int getMapMaxHeight() {
        return boardHeight;
    }

    private static class Transition {
        private final Map<Position, Position> moves;

        private void add(Position from, Position to) {
            final Position key = this.moves.entrySet().stream().filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey).findFirst().orElse(from);
            this.moves.put(key, to);
        }

        private Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        private Transition() {
            this.moves = new HashMap<>();
        }

        private Transition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

        @Override
        public String toString() {
            final List<String> moves = this.moves.entrySet().stream().map(e -> String.format("(%d,%d)->(%d,%d)",
                    e.getKey().x(), e.getKey().y(), e.getValue().x(), e.getValue().y())).toList();
            return String.join(",", moves);
        }
    }
}