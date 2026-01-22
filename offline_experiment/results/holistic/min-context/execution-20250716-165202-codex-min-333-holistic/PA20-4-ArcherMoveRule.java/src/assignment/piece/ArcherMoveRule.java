
// in assignment/protocol/Move.java
package assignment.protocol;

public interface Move {
    /** source row */
    int getFromRow();
    /** source column */
    int getFromColumn();
    /** target row */
    int getToRow();
    /** target column */
    int getToColumn();
}