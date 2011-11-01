Hier eine �berarbeitete Version des Delphi Simple Clients.
Die Kommunikation nutzt nun eine externe XML-Lib (OpenXML)
um das Protokoll zu �bersetzen.
Die Protokollschicht wurde in die Unit UProtocol ausgelagert.

Die Struktur der KI-Unit (UClient) wurde nicht stark ver�ndert.
Um die KI-Logik aus dem alten SimpleClient in den neuen zu
�bernehmen muss folgendes getan werden:

- Alle selbst geschriebenen Methoden aus dem alten in den neuen
  Client kopieren
- Den Inhalt der Methode zugAngefordert aus dem alten in den
  neuen Client kopieren
- Klassennamen m�ssen angepasst werden (um der Delphi
  Namenskonvention zu entsprechen wurden die Klassen Client, Board
  und MyBoard in TClient, TBoard und TMyBoard umbenannt).

Wenn etwas nicht klappt und ihr nicht weiter wisst, schreibt ins
Forum oder direkt mir eine E-Mail mit Beschreibung.
Meine E-Mail: fewking@paniccrew.de