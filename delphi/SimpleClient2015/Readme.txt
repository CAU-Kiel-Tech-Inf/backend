
--- Delphi-Client ---

Version: 2014_r2

Einleitung
===========

Dies ist der Delphi SimpleClient fur das Spiel "Sixpack" 
der Software Challenge 2014.

Der Client ist in Delphi 7 geschrieben, sollte aber auch unter Delphi 6 und
evtl. Delphi 5 problemlos funktionieren.


Entwicklung
============

Das Projekt �ffnen
-------------------

Die Projektdatei src/simpleClient/simpleClient2014.dpr kann einfach in
Borland Delphi ge�ffnet werden. Der Client kann dann direkt aus der
Entwicklungsumgebung heraus gestartet werden.


WICHTIG: 
---------

Bevor der Client ausgef�hrt werden kann, muss Borland Delphi noch
so konfiguriert werden, dass der Debugger die Exception "EParserException"
ignoriert.
Unter Borland Delphi 7 findet man diese Einstellung unter 
Tools->Debugger Options->Language Exceptions
Dort f�gt man per "Add" den Exception Type "EParserException" (ohne Anf�hrungszeichen)
hinzu und best�tigt alles mit "OK".


Bibliotheken
-------------

Im Ordner "src/lib" befinden sich die Units, die der Umsetzung der XML-Kommunikation
mit dem Server und der Darstellung des Spielzustandes und der Spielelemente
(Felder, Steine, ...) dienen.

In der Regel sollten diese Dateien nicht bearbeitet werden.


Eigene Logik
-----------------

Die Dateien, in denen die Schulen ihre eigene Logik implementieren sollen,
liegen im Ordner "src/simpleClient".

Die Klasse TMyBoard in der Unit UMyBoard ist eine Erweiterung der Board-Klasse
TBoard (src/lib/UBoard.pas). Hier kann das Board von den Sch�lern um weitere Funktionen
erg�nzt werden.

In der Klasse TClient (src/simpleClient/UClient.pas) wird die KI umgesetzt.
Jedesmal, wenn der Client am Zug ist, wird hier die Methode TClient.zugAngefordert
aufgerufen, die dann einen Zug finden und zuruckgeben muss. In dem Beispielclient
w�hlt die Strategie lediglich einen zuf�lligen Zug aus. Diese einfache "Strategie" dient nur der 
Veranschaulichung und sollte nat�rlich durch eine eigene, bessere Strategie ersetzt 
werden.


Bei Problemen oder Fragen
==========================

Wenn etwas nicht klappen sollte, steht der Support der Software-Challenge 
jederzeit gerne im Forum oder per E-Mail zur Verf�gung.

Bei Fragen, sollten Fehler auftreten oder Sie Informationen/Funktionen vermissen, 
k�nnen Sie mich auch jederzeit gerne direkt kontaktieren per E-Mail unter

fewking@paniccrew.de (Sven Casimir)