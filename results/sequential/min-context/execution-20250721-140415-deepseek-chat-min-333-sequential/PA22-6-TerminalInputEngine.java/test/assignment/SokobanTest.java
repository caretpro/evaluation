package assignment;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SokobanTest {
    @Test
    void mainHelp() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Sokoban.main(new String[] {});
        assertEquals("Map is not provided.\n", errContent.toString());
    }

    @Test
    void mainLoop() {
        String in = "exit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(in.getBytes());
        System.setIn(inContent);

        String path = Objects.requireNonNull(SokobanTest.class.getClassLoader().getResource("map00.map")).getPath();
        Sokoban.main(new String[] { path });
    }
}
