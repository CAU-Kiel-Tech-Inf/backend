package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.framework.plugins.AbstractPlayer;
import sc.helpers.StringHelper;
import sc.protocol.responses.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias(value = "result")
public class GameResult implements ProtocolMessage {
  private final ScoreDefinition definition;
  
  @XStreamImplicit(itemFieldName = "score")
  private final List<PlayerScore> scores;
  
  @XStreamImplicit(itemFieldName = "winner")
  private List<AbstractPlayer> winners;
  
  /** might be needed by XStream */
  public GameResult() {
    definition = null;
    scores = null;
    winners = null;
  }
  
  public GameResult(ScoreDefinition definition, List<PlayerScore> scores, List<AbstractPlayer> winners) {
    this.definition = definition;
    this.scores = scores;
    this.winners = winners;
  }
  
  public ScoreDefinition getDefinition() {
    return this.definition;
  }
  
  public List<PlayerScore> getScores() {
    return this.scores;
  }
  
  public List<AbstractPlayer> getWinners() {
    if (this.winners == null) {
      this.winners = new ArrayList<AbstractPlayer>(2);
    }
    return this.winners;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    
    int playerIndex = 0;
    for (PlayerScore score : this.scores) {
      builder.append("--------------------------\n");
      builder.append(" Player " + playerIndex + "\n");
      builder.append("--------------------------\n");
      String[] scoreParts = score.toStrings();
      for (int i = 0; i < scoreParts.length; i++) {
        String key = StringHelper.pad(this.definition.get(i).getName(),
            10);
        String value = scoreParts[i];
        builder.append(" " + key + " = " + value + "\n");
      }
      playerIndex++;
    }
    
    return builder.toString();
  }
  
  public boolean isRegular() {
    for (PlayerScore score : this.scores) {
      if (score.getCause() != ScoreCause.REGULAR) {
        return false;
      }
    }
    
    return true;
  }
  
}