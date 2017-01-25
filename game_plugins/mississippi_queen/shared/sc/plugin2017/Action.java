package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "action")
public abstract class Action implements Comparable<Action> {

  /**
   * Zeigt an welche Nummer die Aktion hat. Die Aktion eines Zuges mit der
   * niedrigsten Nummer wird als erstes ausgeführt.
   */
  @XStreamAsAttribute
  public int order;

  public abstract void perform(GameState state, Player player) throws InvalidMoveException;

  @Override
  public int compareTo(Action o) {
    return Integer.compare(this.order, o.order);
  }
}
