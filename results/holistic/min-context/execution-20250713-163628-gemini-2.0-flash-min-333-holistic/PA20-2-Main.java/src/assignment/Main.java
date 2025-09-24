 The code in this method is only an example of using {@link Configuration} to initialize
     * gameboard, Students should remove them and implement on their own.</strong>
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection) {
        ConsolePlayer userPlayer = new ConsolePlayer("UserPlayer");
        RandomPlayer computerPlayer = new RandomPlayer("ComputerPlayer");

        Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer},
                numMovesProtection);

        // Initialize user pieces (Archers) in the first row
        for (int i = 0; i < size; i++) {
            Archer archer = new Archer(userPlayer);
            configuration.addInitialPiece(archer, i, 0);
        }

        // Initialize computer pieces (Archers) in the last row
        for (int i = 0; i < size; i++) {
            Archer archer = new Archer(computerPlayer);
            configuration.addInitialPiece(archer, i, size - 1);
        }

        // Initialize Knights for both players. Placing them at specific locations.
        Knight userKnight = new Knight(userPlayer);
        configuration.addInitialPiece(userKnight, size / 2, 1); // User Knight in the middle of second row

        Knight computerKnight = new Knight(computerPlayer);
        configuration.addInitialPiece(computerKnight, size / 2, size - 2); // Computer Knight in the middle of second to last row


        return new JesonMor(configuration);
    }

    public static void main(String[] args) {
        var helper = "two integer arguments are required specifying size of gameboard and number of moves with capturing protection ";
        if (args.length < 2) {
            throw new IllegalArgumentException(helper);
        }
        int size;
        int numMovesProtection;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the first argument is not a number");
        }
        try {
            numMovesProtection = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("the second argument is not a number");
        }
        createGame(size, numMovesProtection).start();
    }
}