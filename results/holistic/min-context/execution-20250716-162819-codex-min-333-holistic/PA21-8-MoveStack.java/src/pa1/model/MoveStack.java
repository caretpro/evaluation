diff
--- a/src/main/java/pa1/model/MoveStack.java
+++ b/src/main/java/pa1/model/MoveStack.java
@@ public class MoveStack {
-   public void push(final MoveResult move) {
-       Objects.requireNonNull(move, "move cannot be null");
-       moves.add(move);
-   }
+   public void push(final MoveResult move) {
+       Objects.requireNonNull(move, "move cannot be null");
+       moves.add(move);
+   }
@@
-   public boolean isEmpty() {
-       // TODO
-       return false;
-   }
+   public boolean isEmpty() {
+       return moves.isEmpty();
+   }
@@
-   public MoveResult pop() {
-       // TODO
-       return null;
-   }
+   public MoveResult pop() {
+       if (moves.isEmpty()) {
+           throw new NoSuchElementException("Cannot pop from empty MoveStack");
+       }
+       popCount++;
+       return moves.remove(moves.size() - 1);
+   }
@@
-   public int getPopCount() {
-       // TODO
-       return 0;
-   }
+   public int getPopCount() {
+       return popCount;
+   }
@@
-   public MoveResult peek() {
-       // TODO
-       return null;
-   }
+   public MoveResult peek() {
+       if (moves.isEmpty()) {
+           throw new NoSuchElementException("Cannot peek empty MoveStack");
+       }
+       return moves.get(moves.size() - 1);
+   }