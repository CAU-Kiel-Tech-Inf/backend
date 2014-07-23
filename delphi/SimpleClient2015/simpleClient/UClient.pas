unit UClient;

(*
 * Diese Unit enth�lt die Spiellogik
 *
 * Im Auslieferungszustand ist hier beispielhaft eine einfache Spiellogik implementiert, die das Spiel
 * fehlerfrei spielen kann. Die Z�ge werden allerdings gr��tenteils zuf�llig ausgef�hrt.
 *)

interface
  uses UPlayer, UBoard, UDebugHint, UMyBoard, UMove, UField, USetMove, URunMove, UNullMove, UUtil, SysUtils, Classes, UInteger, Contnrs;
  type
    TClient = class
      protected
        FMyId : Integer;                        // Die SpielerID dieses Clients (0 oder 1)
        FPlayers : array [0..1] of TPlayer;     // Die beiden teilnehmenden Spieler
        FBoard : TMyBoard;                      // Das Board des Spiels
        FActivePlayer : String;                 // Der Spieler, der gerade an der Reihe ist ("red" oder "blue")
        FTurn : Integer;                        // Nummer des aktuellen Zuges
        Me : TPlayer;                           // Der Spieler, der von diesem Client gesteuert wird
        Opponent : TPlayer;                     // Der Spieler, der vom Client des Gegenspielers gesteuert wird
        LastMove : TMove;                       // Der in diesem Spiel zuletzt get�tigte Zug oder nil
      public
        function getPlayer(playerNum : Integer) : TPlayer; overload;
        function getPlayer(displayName : String) : TPlayer; overload;
        function getBoard : TBoard;
        function zugAngefordert : TMove;
        function macheZufallszug : TMove;

        procedure setId(playerId : Integer);

        property MyId : Integer read FMyId write FMyId;
        property CurrentTurn : Integer read FTurn write FTurn;
        constructor Create(board : TMyBoard);
        destructor destroy; override;
    end;

implementation

uses UNetwork, Math;
  destructor TClient.destroy;
  begin
    if(FPlayers[0] <> nil) then FPlayers[0].Free;
    if(FPlayers[1] <> nil) then FPlayers[1].Free;
    inherited;
  end;

  (*
   * Macht einen zuf�llig ausgew�hlten Zug.
   *
   * 1. Fall: Mache einen Laufzug
   *
   * 2. Fall: Mache einen Setzzug
   *
   * Falls kein Zug m�glich ist, wird ein Aussetzzug gesendet.
   *)
  function TClient.macheZufallszug : TMove;
    var
      possibleSetMoves : TObjectList; // Liste der m�glichen Setzz�ge
      possibleRunMoves : TObjectList; // Liste der m�glichen Laufz�ge
    begin
      Result := TNullMove.Create;
      writeln('');

      if FBoard.IsRunMove then begin
        possibleRunMoves := FBoard.getPossibleRunMoves();
        if possibleRunMoves.Count > 0 then begin
          Result := TRunMove(possibleRunMoves.Items[Random(possibleRunMoves.Count)]);
        end;
      end else begin
        possibleSetMoves := FBoard.getPossibleSetMoves();
        if possibleSetMoves.Count > 0 then begin
          Result := TSetMove(possibleSetMoves.Items[Random(possibleSetMoves.Count)]);
        end;
      end;
    end;

  (*
  Wird aufgerufen, wenn ein Zug angefordert wurde.
  Soll einen g�ltigen Zug zur�ckliefern. Gibt diese Funktion keinen
  oder einen ung�ltigen Zug zur�ck, ist das Spiel verloren.
  *)
  function TClient.zugAngefordert : TMove;
    var
      mov : TMove;
    begin
      // Die beiden Spieler zur �bersicht als Ich und Gegner ordnen
      if(FBoard.getPlayer(0).PlayerID = FMyId) then begin
        Me := FBoard.getPlayer(0);
        Opponent := FBoard.getPlayer(1);
      end
      else begin
        me := FBoard.getPlayer(1);
        opponent := FBoard.getPlayer(0);
      end;

      write('(Punkte, Felder) vor dem Zug: ');
      writeln('(' + IntToStr(Me.Points) + ', ' + IntToStr(Me.Fields) + ')' + sLineBreak);

      if FBoard.LastMove <> nil then begin
        LastMove := FBoard.LastMove;
        write('Letzter Zug: ');
        writeln(FBoard.LastMove.toString() + sLineBreak);
      end;

      writeln(FBoard.toString());

      // Zuf�lligen Zug berechnen lassen
      mov := macheZufallszug;

      if (mov <> nil) then begin
        writeln('Zug gefunden: ');
        if mov is TSetMove then begin
          writeln('Setzzug');
          mov.addHint(TDebugHint.create('Setzzug'));
        end else if mov is TRunMove then begin
          writeln('Laufzug');
          mov.addHint(TDebugHint.create('Laufzug'));
        end else begin
          writeln('Aussetzzug');
          mov.addHint(TDebugHint.create('Aussetzzug'));
        end;
        writeln(mov.toString);
      end;

      Result := mov;
    end;

  procedure TClient.setId(playerId : Integer);
    begin
      FMyId := playerId;
    end;

  function TClient.getPlayer(playerNum : Integer) : TPlayer;
    begin
      Result := FPlayers[playerNum];
    end;

  function TClient.getPlayer(displayName : String) : TPlayer;
    begin
      Result := nil;
      if(FPlayers[0].DisplayName = displayName) then begin
        Result := FPlayers[0];
      end
      else if(FPlayers[1].DisplayName = displayName) then begin
        Result := FPlayers[1];
      end;
    end;

  function TClient.getBoard : TBoard;
    begin
      Result := FBoard;
    end;

  constructor TClient.Create(board : TMyBoard);
    begin
      inherited Create;
      // Die zwei Spieler werden schonmal erstellt.
      FPlayers[0] := TPlayer.Create('Spieler 1');
      FPlayers[1] := TPlayer.Create('Spieler 2');

      Randomize;
      FBoard := board;
    end;
end.
