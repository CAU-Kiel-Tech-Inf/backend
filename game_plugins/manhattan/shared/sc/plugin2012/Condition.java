package sc.plugin2012;
/**
 * Beinhaltet Informationen zum Spielende:
 * Farbe des Gewinners und Gewinngrund.
 *
 */
class Condition {

	public final PlayerColor winner;

	public final String reason;

         /**
         * XStream benötigt eventuell einen parameterlosen Konstruktor
         * bei der Deserialisierung von Objekten aus XML-Nachrichten.
         */
        public Condition() {
            winner = null;
            reason = null;
        }

        /**
	 * erzeugt eine neue Condition mit Sieger und Geiwnngrund
	 * @param winner Farbe des Siegers
	 * @param reason TExt, der Sieg beschreibt
	 */
	public Condition(PlayerColor winner, String reason) {
		this.winner = winner;
		this.reason = reason;
	}

}
