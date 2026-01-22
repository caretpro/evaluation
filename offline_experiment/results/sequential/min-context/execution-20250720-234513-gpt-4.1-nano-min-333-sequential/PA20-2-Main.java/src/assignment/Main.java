
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user player first to move first
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Player[] players = new Player[] { userPlayer, computerPlayer };
    
    // Initialize configuration with players and protection moves
    Configuration configuration = new Configuration(size, players, numMovesProtection);
    
    // Add initial pieces for user player
    Knight userKnight1 = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight1, 0, 0);
    // Add more user initial pieces if needed
    // configuration.addInitialPiece(new Archer(userPlayer), 0, 1);
    
    // Add initial pieces for computer player
    Knight computerKnight1 = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight1, size - 1, size - 1);
    // Add more computer initial pieces if needed
    
    // Return the game instance
    return new JesonMor(configuration);
}