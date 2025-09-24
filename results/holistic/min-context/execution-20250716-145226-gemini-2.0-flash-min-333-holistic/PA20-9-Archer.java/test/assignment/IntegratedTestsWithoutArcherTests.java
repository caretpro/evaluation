package assignment;

import assignment.mock.MockPlayer;
import assignment.piece.Knight;
import assignment.protocol.*;
import assignment.util.IntegratedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@IntegratedTest
public class IntegratedTestsWithoutArcherTests {
    private Game game;
    private MockPlayer player1;
    private MockPlayer player2;

    @BeforeEach
    public void setUpGame() {
        var size = 5;
        var numMovesProtection = 0;
        this.player1 = new MockPlayer(Color.GREEN);
        this.player2 = new MockPlayer();
        Configuration configuration = new Configuration(size, new Player[]{player1, player2}, numMovesProtection);
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(new Knight(player2), i, size - 1);
            } else {
                configuration.addInitialPiece(new Knight(player2), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(new Knight(player1), i, 0);
            } else {
                configuration.addInitialPiece(new Knight(player1), i, 0);
            }
        }
        this.game = new JesonMor(configuration);
    }

    @Test
    public void testCaptureAllWin() {
        player1.setNextMoves(
                new Move[]{
                        new Move(0, 0, 1, 2),
                        new Move(1, 2, 0, 4),
                        new Move(4, 0, 3, 2),
                        new Move(3, 2, 4, 4),
                        new Move(1, 0, 0, 2),
                        new Move(3, 0, 4, 2)
                }
        );
        player2.setNextMoves(
                new Move[]{
                        new Move(3, 4, 4, 2),
                        new Move(2, 4, 3, 2),
                        new Move(4, 2, 3, 4),
                        new Move(1, 4, 0, 2),
                        new Move(3, 4, 4, 2)
                }
        );

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = this.game.start();
            assertEquals(player1, winner);
            assertEquals(18, player1.getScore());
            assertEquals(15, player2.getScore());
            assertEquals(11, game.getNumMoves());
        });
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testLeaveCentralPlaceWin() {
        player1.setNextMoves(
                new Move[]{
                        new Move(1, 0, 2, 2),
                        new Move(2, 0, 0, 1),
                        new Move(4, 0, 3, 2)
                }
        );
        player2.setNextMoves(
                new Move[]{
                        new Move(2, 4, 3, 2),
                        new Move(1, 4, 2, 2),
                        new Move(2, 2, 0, 3)
                }
        );

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = this.game.start();
            assertEquals(player2, winner);
            assertEquals(9, player1.getScore());
            assertEquals(9, player2.getScore());
            assertEquals(6, game.getNumMoves());
        });
    }
}
