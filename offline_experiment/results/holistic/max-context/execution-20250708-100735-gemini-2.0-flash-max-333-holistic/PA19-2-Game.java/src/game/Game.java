 Undoing a step counts will increment the number of steps by one.
     * </p>
     * <p>
     * Hint: Remember to check whether there are cells to undo.
     * </p>
     *
     * @return {@code false} if there are no steps to undo, otherwise {@code true}.
     */
    public boolean undoStep() {
        FillableCell cell = cellStack.pop();
        if (cell == null) {
            return false;
        }

        map.undo(cell.coord);
        pipeQueue.undo(cell.getPipe().orElseThrow());
        numOfSteps++;
        return true;
    }

    /**
     * Displays the current game state.
     */
    public void display() {
        map.display();
        System.out.println();
        pipeQueue.display();
        cellStack.display();
        System.out.println();
        delayBar.display();
    }

    /**
     * Updates the game state. More specifically, it fills pipes according to the distance the water should flow.
     *
     * <p>
     * This method should invoke {@link Map#fillTiles(int)} up to a certain distance.
     * </p>
     */
    public void updateState() {
        map.fillBeginTile();
        map.fillTiles(delayBar.distance());
    }

    /**
     * Checks whether the game is won.
     *
     * @return {@code true} if the game is won.
     */
    public boolean hasWon() {
        return map.checkPath();
    }

    /**
     * Checks whether the game is lost.
     *
     * <p>
     * Hint: This method should invoke {@link Map#hasLost()}. You may also need other judgements.
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return map.hasLost();
    }

    /**
     * @return Number of steps the player has taken.
     */
    public int getNumOfSteps() {
        return numOfSteps;
    }
}