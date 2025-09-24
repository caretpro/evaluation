
/**
     * Create and initialize a game.
     * Student should first construct a {@link Configuration} object with two players.
     * Then add initial pieces to gameboard in the {@link Configuration} object.
     * After that, use this {@link Configuration} object to construct a {@link JesonMor} game object
     * This method should return the created {@link JesonMor} object.
     * <p>
     * The initialized gameboard should comply to the requirements of the assignment.
     * The user player should be put
     * first in the player array in the configuration and user player should moves first.
     *
     * <strong>Attention: The code in this method is only an example of using {@link Configuration} to initialize
     * gameboard, Students should remove them and implement on their own.</strong>
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        // User's pieces (Knights) at (0,0) and (size-1, size-1)
        configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
        configuration.addInitialPiece(new Knight(userPlayer), size - 1, size - 1);

        // Computer's pieces (Archers) at (0, size-1) and (size-1, 0)
        configuration.addInitialPiece(new Archer(computerPlayer), 0, size - 1);
        configuration.addInitialPiece(new Archer(computerPlayer), size - 1, 0);

        return new JesonMor(configuration);
    }