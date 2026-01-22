package game;

import java.util.List;
import java.util.ArrayList;

public class ScoreService implements IScoreService {

    @Override
    public int calculateFinalScore(List<Integer> data, int factor, User user) {
        int sum = 0;
        double adjustment = 1.0;

        for (int value : data) {
            if (value > 0) {
                sum += value;
            }
        }

        if (user.getLevel().equals("Low")) {
            adjustment = 0.8;
        } else if (user.getLevel().equals("Medium")) {
            adjustment = 1.0;
        } else if (user.getLevel().equals("High")) {
            adjustment = 1.2;
        }

        return (int) (sum * factor * adjustment);
    }

    @Override
    public ScoreReport generateReport(GameSession session, User user) {
        int finalScore = calculateFinalScore(session.getScores(), 2, user);
        return new ScoreReport(user.getName(), finalScore);
    }

    @Override
    public List<Integer> generateSquares(List<Integer> numbers) {
        List<Integer> squares = new ArrayList<>();
        for (int n : numbers) {
            squares.add(n * n);
        }
        return squares;
    }

    @Override
    public int findMaxValue(List<Integer> numbers) {
        int max = Integer.MIN_VALUE;
        for (int n : numbers) {
            if (n > max) max = n;
        }
        return max;
    }
}
