package sc.plugin2017;

public enum Direction {

  RIGHT(0), UP_RIGHT(1), UP_LEFT(2), LEFT(3), DOWN_LEFT(4), DOWN_RIGHT(5);

  private final int value;

  Direction(final int value) {
    this.value = value;
  }

  public int getValue() { return value; }

  private Direction getForValue(int val) {
    for (Direction dir : values()) {
      if (val == dir.getValue()) {
        return dir;
      }
    }
    throw new IllegalArgumentException("no direction for value "+val);
  }

  /**
   * Calculates the opposite direction (i.e. turning by 180 degrees).
   * @return opposite direction
   */
  public Direction getOpposite() {
    return getTurnedDirection(3);
  }

  /**
   * Calculates the direction when turning turn steps (positive values counterclockwise, negative values clockwise).
   * @return turned direction
   */
  public Direction getTurnedDirection(int turn) {
    return getForValue((value + turn + 6) % 6);
  }

  /**
   * Gibt die Anzahl der Drehungen bei einer Drehung von der aktuellen Richtung zu toDir zurück
   * @param toDir Endrichtung
   * @return Anzahl der Drehungen
   */
  public int turnToDir(Direction toDir) {
    int direction = ((value - toDir.getValue()) + 6) % 6;
    if(direction >= 3) {
      direction = 6 - direction;
    } else {
      direction = - direction;
    }
    return direction;
  }
}