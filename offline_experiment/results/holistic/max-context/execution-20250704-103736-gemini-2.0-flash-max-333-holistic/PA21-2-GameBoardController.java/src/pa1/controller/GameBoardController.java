 aliveMove.collectedGems) {
                    final Cell cell = gameBoard.getCell(gemPosition);
                    if (cell instanceof EntityCell entityCell && entityCell.getEntity() instanceof Gem) {
                        entityCell.setEntity(null); // Remove the gem from the board
                    }
                }

                // Collect extra lives
                for (final Position extraLifePosition : aliveMove.collectedExtraLives) {
                    final Cell cell = gameBoard.getCell(extraLifePosition);
                    if (cell instanceof EntityCell entityCell && entityCell.getEntity() instanceof ExtraLife) {
                        entityCell.setEntity(null); // Remove the extra life from the board
                        player.setLives(player.getLives() + 1); // Increase player lives
                    }
                }
            }

            if (validMove instanceof MoveResult.Valid.Dead) {
                player.setLives(player.getLives() - 1);
            }

            return validMove;
        }

        return moveResult;
    }

    /**
     * Undoes a move by reverting all changes performed by the specified move.
     *
     * <p>
     * Hint: Undoing a move is effectively the same as reversing everything you have done to make a move.
     * </p>
     *
     * @param prevMove The {@link MoveResult} object to revert.
     */
    public void undoMove(final MoveResult prevMove) {
        if (prevMove instanceof MoveResult.Valid validMove) {
            final Player player = gameBoard.getPlayer();
            final Position currentPosition = validMove.newPosition;
            final Position originalPosition = validMove.origPosition;

            // Revert player position
            player.setPosition(originalPosition);

            // Revert gems
            if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
                for (final Position gemPosition : aliveMove.collectedGems) {
                    final Cell cell = gameBoard.getCell(gemPosition);
                    if (cell instanceof EntityCell entityCell && entityCell.getEntity() == null) {
                        entityCell.setEntity(new Gem()); // Restore the gem to the board
                    }
                }

                // Revert extra lives
                for (final Position extraLifePosition : aliveMove.collectedExtraLives) {
                    final Cell cell = gameBoard.getCell(extraLifePosition);
                    if (cell instanceof EntityCell entityCell && entityCell.getEntity() == null) {
                        entityCell.setEntity(new ExtraLife()); // Restore the extra life to the board
                        player.setLives(player.getLives() - 1); // Decrease player lives
                    }
                }
            }

            if (validMove instanceof MoveResult.Valid.Dead) {
                player.setLives(player.getLives() + 1);
            }
        }
    }

    /**
     * Tries to move the player from a position in the specified direction as far as possible.
     *
     * <p>
     * Note that this method does <b>NOT</b> actually move the player. It just tries to move the player and return
     * the state of the player as-if it has been moved.
     * </p>
     *
     * @param position  The original position of the player.
     * @param direction The direction to move the player in.
     * @return An instance of {@link MoveResult} representing the type of the move and the position of the player after
     * moving.
     */
    @NotNull
    private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final ArrayList<Position> collectedGems = new ArrayList<>();
        final ArrayList<Position> collectedExtraLives = new ArrayList<>();
        Position lastValidPosition = position;
        do {
            final Position newPosition = offsetPosition(lastValidPosition, direction);
            if (newPosition == null) {
                break;
            }

            lastValidPosition = newPosition;

            if (gameBoard.getCell(newPosition) instanceof StopCell) {
                break;
            }

            if (gameBoard.getCell(newPosition) instanceof EntityCell entityCell) {
                if (entityCell.getEntity() instanceof Mine) {
                    return new MoveResult.Valid.Dead(lastValidPosition, newPosition);
                }

                if (entityCell.getEntity() instanceof Gem) {
                    collectedGems.add(newPosition);
                } else if (entityCell.getEntity() instanceof ExtraLife) {
                    collectedExtraLives.add(newPosition);
                }
            }
        } while (true);

        if (lastValidPosition.equals(position)) {
            return new MoveResult.Invalid(position);
        }

        return new MoveResult.Valid.Alive(lastValidPosition, position, collectedGems, collectedExtraLives);
    }

    /**
     * Offsets the {@link Position} in the specified {@link Direction} by one step.
     *
     * @param position  The original position.
     * @param direction The direction to offset.
     * @return The given position offset by one in the specified direction. If the new position is outside of the game
     * board, or contains a non-{@link EntityCell}, returns {@code null}.
     */
    @Nullable
    private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);

        final Position newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(),
                gameBoard.getNumCols());

        if (newPos == null) {
            return null;
        }
        if (!(gameBoard.getCell(newPos) instanceof EntityCell)) {
            return null;
        }

        return newPos;
    }
    
}