package game;

import java.util.List;

public class GameSession {
    private List<Integer> scores;

    public GameSession(List<Integer> scores) {
        this.scores = scores;
    }

    public List<Integer> getScores() { return scores; }
}