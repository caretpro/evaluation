
/**
 * Create and initialize a game.
 * Student should first construct a {@link Configuration} object with two players.
 * Then add initial pieces to gameboard in the {@link Configuration} object.
 * After that, use this {@link Configuration} object to construct a {@link JesonMor} game object.
 * This method should return the created {@link JesonMor} object.
 *
 * The initialized gameboard should comply to the requirements of the assignment.
 * The user player is put first in the player array in the configuration and moves first.
 *
 * @param size               size of gameboard (odd number ≥ 3)
 * @param numMovesProtection number of moves with capture protection (non‑negative)
 * @return the initialized JesonMor game
 */
public static Game createGame(int size, int numMovesProtection) {
    // 1. Create the two players, user moves first (GREEN), computer second (BLUE)
    ConsolePlayer userPlayer     = new ConsolePlayer("UserPlayer", assignment.protocol.Color.GREEN);
    RandomPlayer  computerPlayer = new RandomPlayer("ComputerPlayer", assignment.protocol.Color.BLUE);

    // 2. Build the configuration
    Configuration cfg = new Configuration(size,
            new Player[]{ userPlayer, computerPlayer },
            numMovesProtection);

    // 3. Determine home rows
    int userRow = 0;
    int compRow = size - 1;
    int half    = (size - 1) / 2;

    // 4. Fill both home rows completely:
    //    Place exactly one Archer in the left half, one Archer in the right half,
    //    knights everywhere else.
    for (int x = 0; x < size; x++) {
        // Decide piece for user home row
        assignment.protocol.Piece userPiece =
            (x == half)             ? new Archer(userPlayer)  // center (counts as right half when x>half?)
          : (x == 0)                ? new Archer(userPlayer)  // one at extreme left
          : (x == size - 1)         ? new Archer(userPlayer)  // one at extreme right
          : new Knight(userPlayer);
        cfg.addInitialPiece(userPiece, x, userRow);

        // Mirror for computer home row
        assignment.protocol.Piece compPiece =
            (x == half)             ? new Archer(computerPlayer)
          : (x == 0)                ? new Archer(computerPlayer)
          : (x == size - 1)         ? new Archer(computerPlayer)
          : new Knight(computerPlayer);
        cfg.addInitialPiece(compPiece, x, compRow);
    }

    // 5. Return the new JesonMor game
    return new JesonMor(cfg);
}