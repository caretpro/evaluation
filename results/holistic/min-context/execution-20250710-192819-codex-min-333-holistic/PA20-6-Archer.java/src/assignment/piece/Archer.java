
// assignment/protocol/Place.java
package assignment.protocol;

public record Place(int row, int column) {
    public static Place of(int row, int column) {
        return new Place(row, column);
    }
}