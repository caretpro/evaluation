
public static Game createGame(int size, int numMovesProtection) {
    // Create players: user player first, computer second
    ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
    RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");
    Player[] players = new Player[] { userPlayer, computerPlayer };

    // Initialize configuration with players and protection moves
    Configuration configuration = new Configuration(size, players, numMovesProtection);

    // Add initial pieces for the user player at top-left corner
    Knight userKnight = new Knight(userPlayer);
    configuration.addInitialPiece(userKnight, 0, 0);

    // Add initial pieces for the computer player at bottom-right corner
    Knight computerKnight = new Knight(computerPlayer);
    configuration.addInitialPiece(computerKnight, size - 1, size - 1);

    // Create the game instance
    JesonMor game = new JesonMor(configuration);

    // Populate the gameboard with initial pieces
    for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
            Piece piece = configuration.getInitialBoard()[x][y];
            if (piece != null) {
                game.getConfiguration().addInitialPiece(piece, x, y);
            }
        }
    }

    return game;
}