//en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int srcRow = source.getRow();
        int srcCol = source.getColumn();
        int destRow = destination.getRow();
        int destCol = destination.getColumn();

        // Calculate the number of pieces between the source and destination
        int piecesBetween = 0;

        if (srcRow == destRow) { // Horizontal movement
            int startCol = Math.min(srcCol, destCol) + 1;
            int endCol = Math.max(srcCol, destCol);
            for (int col = startCol; col < endCol; col++) {
                Place between = new Place(srcRow, col);
                if (game.getPiece(between) != null) {
                    piecesBetween++;
                }
            }
        } else if (srcCol == destCol) { // Vertical movement
            int startRow = Math.min(srcRow, destRow) + 1;
            int endRow = Math.max(srcRow, destRow);
            for (int row = startRow; row < endRow; row++) {
                Place between = new Place(row, srcCol);
                if (game.getPiece(between) != null) {
                    piecesBetween++;
                }
            }
        } else {
            return false; // Invalid movement: must be horizontal or vertical
        }

        // Validate the move based on the number of pieces between
        if (game.getPiece(destination) == null) {
            // If the destination is empty, there should be no pieces between
            return piecesBetween == 0;
        } else {
            // If the destination is not empty, there should be exactly one piece between
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}