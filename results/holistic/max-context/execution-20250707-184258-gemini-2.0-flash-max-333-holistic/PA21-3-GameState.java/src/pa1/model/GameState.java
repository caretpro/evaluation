 Do NOT throw an exception when {@code numLives} is zero. This will be used in our testing.
	 * </p>
	 *
	 * @param gameBoard The game board to be managed by this instance.
	 * @param numLives  Number of lives the player has. If the value is negative, treat as if the player has an
	 *                  unlimited number of lives.
	 */
	public GameState(final GameBoard gameBoard, final int numLives) {
		this.gameBoard = Objects.requireNonNull(gameBoard);
		this.numLives = numLives < 0 ? UNLIMITED_LIVES : numLives;
		this.initialNumOfGems = gameBoard.getNumGems();
	}

	/**
	 * Checks whether the game has been won.
	 *
	 * <p>
	 * The game is won when there are no gems left in the game board.
	 * </p>
	 *
	 * @return Whether the player has won the game.
	 */
	public boolean hasWon() {
		return gameBoard.getNumGems() == 0;
	}

	/**
	 * Checks whether the game has been lost.
	 *
	 * <p>
	 * The game is lost when the player has no lives remaining. For games which the player has unlimited lives, this
	 * condition should never trigger.
	 * </p>
	 *
	 * @return Whether the player has lost the game.
	 */
	public boolean hasLost() {
		return !hasUnlimitedLives() && numLives <= 0;
	}

	/**
	 * Increases the player's number of lives by the specified amount.
	 *
	 * @param delta The number of lives to give the player.
	 * @return The new number of lives of the player. If the player has unlimited number of lives, returns
	 * {@link Integer#MAX_VALUE}.
	 */
	public int increaseNumLives(final int delta) {
		if (hasUnlimitedLives()) {
			return Integer.MAX_VALUE;
		}

		numLives += delta;
		return numLives;
	}

	/**
	 * Decreases the player's number of lives by the specified amount.
	 *
	 * @param delta The number of lives to take from the player.
	 * @return The new number of lives of the player. If the player has unlimited number of lives, returns
	 * {@link Integer#MAX_VALUE}.
	 */
	public int decreaseNumLives(final int delta) {
		if (hasUnlimitedLives()) {
			return Integer.MAX_VALUE;
		}

		numLives = Math.max(0, numLives - delta);
		return numLives;
	}

	/**
	 * Decrements the player's number of lives by one.
	 *
	 * @return The new number of lives of the player. If the player has unlimited number of lives, returns
	 * {@link Integer#MAX_VALUE}.
	 */
	public int decrementNumLives() {
		return decreaseNumLives(1);
	}

	/**
	 * Increments the number of moves taken by the player.
	 *
	 * @return The new number of moves taken by the player.
	 */
	public int incrementNumMoves() {
		return ++numMoves;
	}

	/**
	 * Increments the number of deaths of the player.
	 *
	 * @return The new number of deaths of the player.
	 */
	public int incrementNumDeaths() {
		return ++numDeaths;
	}

	/**
	 * @return The current number of deaths of the player.
	 */
	public int getNumDeaths() {
		return numDeaths;
	}

	/**
	 * @return The current number of moves taken by the player.
	 */
	public int getNumMoves() {
		return numMoves;
	}

	/**
	 * @return Whether the player has unlimited lives.
	 */
	public boolean hasUnlimitedLives() {
		return numLives == UNLIMITED_LIVES;
	}

	/**
	 * @return The number of lives a player has. If the player has an unlimited number of lives, returns
	 * {@link Integer#MAX_VALUE}.
	 */
	public int getNumLives() {
		return hasUnlimitedLives() ? Integer.MAX_VALUE : numLives;
	}

	/**
	 * @return The number of gems that is still present on the game board.
	 */
	public int getNumGems() {
		return gameBoard.getNumGems();
	}

	/**
	 * <p>
	 * At any point of the game, the score should be computed using the following formula:
	 * </p>
	 * <ul>
	 * <li>The initial score of any game board is {@code gameBoardSize}.</li>
	 * <li>Each gem will be worth 10 points.</li>
	 * <li>Each valid move deducts one point.</li>
	 * <li>Each undo deducts two points.</li>
	 * <li>Each death deducts four points (on top of the one point deducted by a valid move).</li>
	 * </ul>
	 *
	 * @return The current score of this game.
	 */
	public int getScore() {
		int score = gameBoard.getNumRows() * gameBoard.getNumCols();
		score += getNumGems() * 10;
		score -= numMoves;
		score -= moveStack.getPopCount() * 2;
		score -= numDeaths * 4;
		return score;
	}

	/**
	 * @return A controller of the managed game board for mutation.
	 */
	public GameBoardController getGameBoardController() {
		return new GameBoardController(gameBoard);
	}

	/**
	 * @return A read-only view of the managed game board.
	 */
	public GameBoardView getGameBoardView() {
		return new GameBoardView(gameBoard);
	}

	/**
	 * @return The instance of the managed {@link GameBoard}.
	 */
	public GameBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * @return The instance of the managed {@link MoveStack}.
	 */
	public MoveStack getMoveStack() {
		return moveStack;
	}
}