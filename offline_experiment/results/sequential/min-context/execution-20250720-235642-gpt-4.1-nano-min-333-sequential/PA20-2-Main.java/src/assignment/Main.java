
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user and computer
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    // Initialize configuration with user first to ensure user moves first
    Configuration configuration = new Configuration(size, new Player[] { userPlayer, computerPlayer }, numMovesProtection);
    // Create initial pieces for each player
    Knight userKnight = new Knight(userPlayer);
    Knight computerKnight = new Knight(computerPlayer);
    // Place the user's knight at position (0,0)
    configuration.addInitialPiece(userKnight, 0, 0);
    // Place the computer's knight at position (1,0)
    configuration.addInitialPiece(computerKnight, 1, 0);
    // Additional pieces can be added here if needed for tests
    // For example, adding an Archer for testHalfArcher
    Archer userArcher = new Archer(userPlayer);
    configuration.addInitialPiece(userArcher, 2, 0);
    // Ensure all initial pieces are added before creating the game
    return new JesonMor(configuration);
}