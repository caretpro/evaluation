 be sure to handle invalid input to avoid invalid {@link Move}s.
     * <p>
     * <strong>Attention: Student should make sure the {@link Move} returned is valid.</strong>
     * <p>
     * <strong>Attention: {@link Place} object uses integer as index of x and y-axis, both starting from 0 to
     * facilitate programming.
     * This is VERY different from the coordinate used in console display.</strong>
     *
     * @param game           the current game object
     * @param availableMoves available moves for this player to choose from.
     * @return the chosen move
     */
    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (e.g., a1->b3): ");
            String input = scanner.nextLine().trim();
            Move move = parseMove(input);
            if (move == null) {
                System.out.println("Invalid format. Please enter move in the format 'a1->b3'.");
                continue;
            }
            // Check if move is among available moves
            boolean isValid = false;
            for (Move availableMove : availableMoves) {
                if (availableMove.equals(move)) {
                    isValid = true;
                    move = availableMove; // Use the actual move object from availableMoves
                    break;
                }
            }
            if (!isValid) {
                System.out.println("The move is not among available moves. Please try again.");
                continue;
            }
            // Validate move according to game rules
            String validationMsg = validateMove(game, move);
            if (validationMsg != null) {
                System.out.println("Invalid move: " + validationMsg);
                continue;
            }
            // Move is valid
            return move;
        }
    }

    private String validateMove(Game game, Move move) {
        Rule[] rules = new Rule[] {
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new VacantRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
            new ArcherMoveRule(),
            new KnightMoveRule(),
            new KnightBlockRule()
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return rule.getDescription();
            }
        }
        var piece = game.getPiece(move.getSource());
        if (piece == null) {
            return "No piece at " + move.getSource().toString();
        }
        if (!this.equals(piece.getPlayer())) {
            return "Cannot move a piece not belonging to you";
        }
        return null;
    }

    private static Place parsePlace(String str) {
        if (str.length() < 2) {
            return null;
        }
        try {
            int x = str.charAt(0) - 'a';
            int y = Integer.parseInt(str.substring(1)) - 1;
            return new Place(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Move parseMove(String str) {
        String[] segments = str.split("->");
        if (segments.length != 2) {
            return null;
        }
        Place source = parsePlace(segments[0].strip());
        if (source == null) {
            return null;
        }
        Place destination = parsePlace(segments[1].strip());
        if (destination == null) {
            return null;
        }
        return new Move(source, destination);
    }
}