unit UMyBoard;

// Hier k�nnen eigene Funktionen implementiert werden, um die Funktionalit�t
// des Boards zu erweitern

interface
  uses UBoard, UDefines, UInteger, UPlayer, UMove, UStone, UField, Classes, UUtil, SysUtils;

  type
    TMyBoard = class(TBoard)
     public

    end;
implementation

uses Math;


end.
