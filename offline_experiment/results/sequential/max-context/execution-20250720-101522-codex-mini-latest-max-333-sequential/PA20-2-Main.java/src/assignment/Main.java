
    @Test
    void testInitialBoard1() {
        Game game = Main.createGame(size, 0);
        // every non‐central cell on the two “home rows” must have exactly one piece:
        for (Place place : List.of(new Place(mid - 1, bot), new Place(mid + 1, bot),
                                   new Place(mid - 2, bot), new Place(mid + 2, bot))) {
            assertNotNull(game.getPiece(place));
        }
        // ... same for top “home row”
    }