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

        Configuration configuration = new Configuration(size, new Player[]{userPlayer, computerPlayer}, numMovesProtection);

        // Initialize user's pieces (Knight at 0,0, then Archers on the first half of the first row)
        configuration.addInitialPiece(new Knight(userPlayer), 0, 0);
        for (int i = 1; i < size / 2 + (size % 2); i++) {
            configuration.addInitialPiece(new Archer(userPlayer), i, 0);
        }


        // Initialize computer's pieces (Knight at size-1, size-1, then Archers on the second half of the last row)
        configuration.addInitialPiece(new Knight(computerPlayer), size - 1, size - 1);
        for (int i = size / 2; i < size - 1; i++) {
            configuration.addInitialPiece(new Archer(computerPlayer), i, size - 1);
        }

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