
// ...
public class Pipe {
    private final int    holePosition;
    private final char   symbol;

    private static final char[] SYMBOLS = { 'A', 'B', 'C' };
    private static final int[]  POSITIONS = { 2, 4, 6 };

    public Pipe(int holePosition, char symbol) {
        this.holePosition = holePosition;
        this.symbol       = symbol;
    }

    public static Pipe of(int holePosition, char symbol) {
        return new Pipe(holePosition, symbol);
    }

    public static char[] holeSymbols() {
        return SYMBOLS.clone();
    }

    public static int[] holePositions() {
        return POSITIONS.clone();
    }

    public char toSingleChar() { return symbol; }
    public int  holePosition()  { return holePosition; }
    // ...
}