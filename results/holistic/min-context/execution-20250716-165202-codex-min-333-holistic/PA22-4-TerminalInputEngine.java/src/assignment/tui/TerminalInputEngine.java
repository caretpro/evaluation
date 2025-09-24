
// assignment/actions/Move.java
public abstract class Move implements Action {
    private final int deltaRow, deltaCol;
    public Move(int dr, int dc) { … }
    public static Move up()    { return new Move(-1,  0){}; }
    public static Move down()  { return new Move( 1,  0){}; }
    public static Move left()  { return new Move( 0, -1){}; }
    public static Move right() { return new Move( 0,  1){}; }
}