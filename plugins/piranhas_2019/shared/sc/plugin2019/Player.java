package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.framework.plugins.AbstractPlayer;
import sc.shared.PlayerColor;

/** Ein Spieler aus Hase- und Igel. */
@XStreamAlias(value = "player")
public class Player extends AbstractPlayer implements Cloneable {

  /** Farbe der Spielfiguren */
  @XStreamAsAttribute
  private PlayerColor color;

  /** only for XStream */
  protected Player() {
  }

  protected Player(PlayerColor color) {
    this.color = color;
  }

  public final PlayerColor getPlayerColor() {
    return color;
  }

  public void setPlayerColor(PlayerColor playerColor) {
    this.color = playerColor;
  }

  public Player clone() {
    return new Player(color);
  }

  @Override
  public String toString() {
    return String.format("Player %s{color: %s}", getDisplayName(), color);
  }

}
