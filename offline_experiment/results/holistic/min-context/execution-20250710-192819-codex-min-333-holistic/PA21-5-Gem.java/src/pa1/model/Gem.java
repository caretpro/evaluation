
package pa1.model;

/** A generic entity on the board, possibly owned by a cell. */
public abstract class Entity {
  private EntityCell owner;

  protected Entity() {
    this(null);
  }

  protected Entity(EntityCell owner) {
    this.owner = owner;
  }

  public EntityCell getOwner() { return owner; }
  public void setOwner(EntityCell owner) { this.owner = owner; }

  public abstract char toUnicodeChar();
  public abstract char toASCIIChar();
}