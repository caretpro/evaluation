diff
--- a/src/pa1/model/GameBoard.java
+++ b/src/pa1/model/GameBoard.java
@@ -70,8 +70,8 @@ public final class GameBoard {
      * @throws IllegalArgumentException if the given dimensions are non-positive
      *                                  or {@code startRow}/{@code startCol} is
      *                                  out of bounds.
-    public GameBoard(int numRows, int numCols,
-                     int startRow, int startCol) {
+    public GameBoard(int numRows, int numCols,
+                     int startRow, int startCol) {
         if (numRows <= 0 || numCols <= 0) {
             throw new IllegalArgumentException("Board must have at least one row and one column");
         }
-        if (startRow < 0 || startRow >= numRows ||
-            startCol < 0 || startCol >= numCols) {
-            throw new IllegalArgumentException("Start position out of bounds");
-        }
+        // Fix: only reject start position out-of-bounds, but allow all valid board sizes.
+        if (startRow < 0 || startRow >= numRows ||
+            startCol < 0 || startCol >= numCols) {
+            throw new IllegalArgumentException("Start position out of bounds");
+        }
 
         this.numRows = numRows;
         this.numCols = numCols;