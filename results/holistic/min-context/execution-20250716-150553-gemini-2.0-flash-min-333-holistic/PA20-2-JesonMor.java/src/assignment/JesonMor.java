 %s%s%s\n", winner.getColor(), winner.getName(), Color.DEFAULT);
			return winner;
		}
		return null;
	}

	/**
	 * Get the winner of the game. If there is no winner yet, return null;
	 * This method will be called every time after a player makes a move and after
	 * {@link JesonMor#updateScore(Player, Piece, Move)} is called, in order to
	 * check whether any {@link Player} wins.
	 * If this method returns null, next player will be asked to make a move.
	 *
	 * @param lastPlayer the last player who makes a move
	 * @param lastMove   the last move made by lastPlayer
	 * @param lastPiece  the last piece that is moved by the player
	 * @return the winner if it exists, otherwise return null
	 */
	@Override
	public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				Piece piece = board[i][j];
				if (piece instanceof Knight) {
					Knight knight = (Knight) piece;
					if (piece.getColor() == lastPlayer.getColor()) {
						return lastPlayer;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Update the score of a player according to the {@link Piece} and corresponding move made by him just now.
	 * This method will be called every time after a player makes a move, in order to update the corresponding score
	 * of this player.
	 * <p>
	 * The score of a player is the cumulative score of each move he makes.
	 * The score of each move is calculated with the Manhattan distance between the source and destination {@link Place}.
	 * <p>
	 * Student can use {@link Player#getScore()} to get the current score of a player before updating.
	 * {@link Player#setScore(int)} can be used to update the score of a player.
	 * <p>
	 * <strong>Attention: do not need to validate move in this method.</strong>
	 *
	 * @param player the player who just makes a move
	 * @param piece  the piece that is just moved
	 * @param move   the move that is just made
	 */
	public void updateScore(Player player, Piece piece, Move move) {
		int manhattanDistance = Math.abs(move.destination().x() - move.source().x()) + Math.abs(move.destination().y() - move.source().y());
		player.setScore(player.getScore() + manhattanDistance);
	}

	/**
	 * Make a move.
	 * This method performs moving a {@link Piece} from source to destination {@link Place} according {@link Move} object.
	 * Note that after the move, there will be no {@link Piece} in source {@link Place}.
	 * <p>
	 * Positions of all {@link Piece}s on the gameboard are stored in {@link JesonMor#board} field as a 2-dimension array of
	 * {@link Piece} objects.
	 * The x and y coordinate of a {@link Place} on the gameboard are used as index in {@link JesonMor#board}.
	 * E.g. {@code board[place.x()][place.y()]}.
	 * If one {@link Place} does not have a piece on it, it will be null in {@code board[place.x()][place.y()]}.
	 * Student may modify elements in {@link JesonMor#board} to implement moving a {@link Piece}.
	 * The {@link Move} object can be considered valid on present gameboard.
	 *
	 * @param move the move to make
	 */
	public void movePiece(Move move) {
		board[move.destination().x()][move.destination().y()] = board[move.source().x()][move.source().y()];
		board[move.source().x()][move.source().y()] = null;
	}

	/**
	 * Get all available moves of one player.
	 * This method is called when it is the {@link Player}'s turn to make a move.
	 * It will iterate all {@link Piece}s belonging to the {@link Player} on board and obtain available moves of
	 * each of the {@link Piece}s through method {@link Piece#getAvailableMoves(Game, Place)} of each {@link Piece}.
	 * <p>
	 * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
	 *
	 * @param player the player whose available moves to get
	 * @return an array of available moves
	 */
	public Move[] getAvailableMoves(Player player) {
		List<Move> moves = new ArrayList<>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				Piece piece = board[i][j];
				if (piece != null && piece.getColor() == player.getColor()) {
					Place place = new Place(i, j);
					Move[] pieceMoves = piece.getAvailableMoves(this, place);
					for (Move move : pieceMoves) {
						moves.add(move);
					}
				}
			}
		}
		return moves.toArray(new Move[0]);
	}
}