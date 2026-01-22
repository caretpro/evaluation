
    /**
     * @return  True if the game should stop running. For example when the user specified to exit the game or the user won the game.
     */
    @Override
    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }