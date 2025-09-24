
// assignment/protocol/Place.java
package assignment.protocol;

public record Place(int column, int row) {
    public static Place of(int column, int row) {
        return new Place(column, row);
    }
}