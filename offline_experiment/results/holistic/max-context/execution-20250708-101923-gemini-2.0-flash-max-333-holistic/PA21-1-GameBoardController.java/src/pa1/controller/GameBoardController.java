 aliveMove.collectedGems) {
					final var cell = gameBoard.getEntityCell(gemPosition);
					((EntityCell) cell).setEntity(null); // Remove gem from board
				}

				// Collect extra lives
				for (final var extraLifePosition : aliveMove.collectedExtraLives) {
					final var cell = gameBoard.getEntityCell(extraLifePosition);
					((EntityCell) cell).setEntity(null); // Remove extra life from board
					player.setNumLives(player.getNumLives() + 1); // Increment player lives
				}
			} else if (validMove instanceof MoveResult.Valid.Dead) {
				player.setNumLives(player.getNumLives() - 1);
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
		Objects.requireNonNull(prevMove);

		final var player = gameBoard.getPlayer();

		if (prevMove instanceof MoveResult.Valid validMove) {
			// Revert player position
			final Position origPosition = validMove instanceof MoveResult.Valid.Dead ?
					validMove.newPosition : ((MoveResult.Valid.Alive) validMove).origPosition;

			final Position newPosition = validMove.newPosition;

			final var originalCell = gameBoard.getEntityCell(newPosition);
			originalCell.setEntity(null);

			final var revertedCell = gameBoard.getEntityCell(origPosition);
			revertedCell.setEntity(player);

			if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
				// Revert gems
				for (final var gemPosition : aliveMove.collectedGems) {
					final var cell = gameBoard.getEntityCell(gemPosition);
					cell.setEntity(new Gem()); // Restore gem to board
				}

				// Revert extra lives
				for (final var extraLifePosition : aliveMove.collectedExtraLives) {
					final var cell = gameBoard.getEntityCell(extraLifePosition);
					cell.setEntity(new ExtraLife()); // Restore extra life to board
					player.setNumLives(player.getNumLives() - 1); // Decrement player lives
				}
			} else if (validMove instanceof MoveResult.Valid.Dead) {
				player.setNumLives(player.getNumLives() + 1);
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

		final var collectedGems = new ArrayList<Position>();
		final var collectedExtraLives = new ArrayList<Position>();
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

		final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(),
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