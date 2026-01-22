package pa1;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    void mainHelp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[] {});
        assertEquals("Usage: Main [--unicode] [GAME_FILE]\n\n", outContent.toString());
    }

    @Test
    void mainGameCreation() {
        String in = "undo\nquit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(in.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setIn(inContent);
        System.setOut(new PrintStream(outContent));

        String path = Objects.requireNonNull(MainTest.class.getClassLoader().getResource("01-simple.game")).getPath();
        Main.main(new String[] { path });

        assertTrue(outContent.toString().contains("No more steps to undo!"));
    }
}
