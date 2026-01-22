
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameBoard {
    private final int numRows;
    private final int numCols;
    @NotNull
    private final Cell[][] board;
    @NotNull
    private final Player player;

    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (numRows != cells.length || numCols != cells[0].length) {
            throw new IllegalArgumentException("Dimensions don't match cell array");
        }
        
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(cells[i], 0, this.board[i], 0, numCols);
        }
        
        this.player = getSinglePlayer();
        
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("No gems on board");
        }
        
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable");
        }
    }

    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allReachablePos = new ArrayList<>();
        final List<Position> allStoppablePos = getAllStoppablePositions(initialPosition);

        for (final var reachablePos : allStoppablePos) {
            for (final var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    final var posToAdd = getEntityCellByOffset(reachablePos, posOffset);
                    if (posToAdd == null) {
                        break;
                    }

                    if (!allReachablePos.contains(posToAdd)) {
                        allReachablePos.add(posToAdd);
                    }
                }
            }
        }

        return Collections.unmodifiableList(allReachablePos);
    }

    private boolean isAllGemsReachable() {
        final int expectedNumOfGems = getNumGems();
        final Position initialPosition = Objects.requireNonNull(player.getOwner()).getPosition();
        final List<Position> playerReachableCells = getAllReachablePositions(initialPosition);

        int actualNumOfGems = 0;
        for (final Position pos : playerReachableCells) {
            final Cell cell = getCell(pos);
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                ++actualNumOfGems;
            }
        }

        return expectedNumOfGems == actualNumOfGems;
    }

    @NotNull
    private Player getSinglePlayer() {
        Player player = null;
        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (player != null) {
                        throw new IllegalArgumentException("Multiple players found");
                    }
                    player = p;
                }
            }
        }

        if (player == null) {
            throw new IllegalArgumentException("No player found");
        }
        return player;
    }

    // ... (rest of the methods remain the same as in previous implementation)
}