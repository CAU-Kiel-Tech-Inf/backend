unit UClient;

(*
 * Diese Unit enth�lt die Spiellogik
 *
 * Im Auslieferungszustand ist hier beispielhaft eine einfache Spiellogik implementiert, die das Spiel
 * fehlerfrei spielen kann. Die Z�ge werden allerdings gr��tenteils zuf�llig ausgef�hrt.
 *)

interface
  uses UPlayer, UBoard, UDebugHint, UMyBoard, UMove, UField, ULayMove, UExchangeMove, UStone, UUtil, SysUtils, Classes, UInteger;
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
        function platziereStein(stone : TStone) : Boolean;

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
   * Platziert den gegebenen Stein an einer zuf�lligen, g�ltigen Position
   * (Probiert alle freien Felder aus, die an bereits belegte Felder angrenzen)
   *)
  function TClient.platziereStein(stone : TStone) : Boolean;
  var
    x, y : Integer;
  begin
    Result := true;
    for x := 0 to 15 do begin
      for y := 0 to 15 do begin
        if FBoard.isOccupied(x, y) then begin  // Finde belegtes Feld
          if FBoard.layStoneAt(stone, x - 1, y) then exit; // Probiere links davon
          if FBoard.layStoneAt(stone, x + 1, y) then exit; // Probiere rechts davon
          if FBoard.layStoneAt(stone, x, y - 1) then exit; // Probiere oberhalb
          if FBoard.layStoneAt(stone, x, y + 1) then exit; // Probiere unterhalb
        end;
      end;
    end;
    Result := false;
  end;

  (*
   * Macht einen mehr oder weniger zuf�llig ausgew�hlten Zug.
   *
   * 1. Fall: Mache den allerersten Zug des Spiels:
   * Versuche, zwei Steine zu finden, die zusammen zuf�llig auf dem Brett platziert
   * werden k�nnen.
   *
   * 2. Fall:
   * Probiere der Reihe nach alle Steine durch. Versuche, f�r jeden Stein eine
   * g�ltige Position zum Anlegen zu finden.
   *
   * In beiden F�llen wird, falls kein Legezug gefunden wird, eine zuf�llige Anzahl
   * an Steinen eingetauscht.
   *)
  function TClient.macheZufallszug : TMove;
    var
      layMove : TLayMove;               // Der Legezug, der gemacht wird, wenn einer gefunden wird
      exchangeMove : TExchangeMove;     // Der Tauschzug, der gemacht wird, wenn kein Legezug gefunden wurde
      n, o, x, y: Integer;
      stone, stone2 : TStone;
    begin
      writeln('');
      write('Punkte danach: ');
      writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));

      layMove := nil;
      exchangeMove := TExchangeMove.create;
      // Wenn Anfangszug: Zwei Steine finden, die zusammenpassen und diese zuf�llig positionieren
      if LastMove = nil then begin
        for n := 0 to Me.Stones.Count - 1 do begin
          if layMove <> nil then Break; // Wenn bereits ein Zug gefunden wurde, breche ab
          stone := TStone(Me.Stones[n]);
          if (RandomRange(0, 2) = 0) then exchangeMove.addStoneToExchange(stone);  // F�ge Stein m�glicherweise zum Tauschzug hinzu
          for o := n + 1 to Me.Stones.Count - 1 do begin
            if layMove <> nil then Break; // Wenn bereits ein Zug gefunden wurde, breche ab
            stone2 := TStone(Me.Stones[o]);
            // Pr�fe, ob die beiden Steine zusammen liegen d�rfen
            if stone.canBeInSameRowWith(stone2) then begin
              // Passende Steine gefunden, positioniere sie irgendwo horizontal nebeneinander
              y := RandomRange(0, 16);
              x := RandomRange(0, 15);
              layMove := TLayMove.create;
              layMove.addStoneToField(stone, FBoard.getField(x, y));
              layMove.addStoneToField(stone2, FBoard.getField(x + 1, y));
            end;
          end;
        end;
        if layMove = nil then begin
          // Sicherstellen, dass der Tauschzug mindestens einen Stein enth�lt
          if exchangeMove.stonesToExchange.Count = 0 then exchangeMove.addStoneToExchange(TStone(Me.Stones[0]));
          Result := exchangeMove;
        end else begin
          Result := layMove;
        end;
      end else begin
        // Wenn kein Anfangszug, dann alle liegenden Steine durchgehen
        // Jeden Stein zuf�llig f�r einen m�glichen Tauschzug ausw�hlen
        // Au�erdem f�r jeden Stein pr�fen, ob er irgendwo angelegt werden kann
        // Wenn ein Stein zum Anlegen gefunden wurde, diesen Zug ausf�hren.
        for n := 0 to Me.Stones.Count - 1 do begin
          stone := TStone(Me.Stones[n]);
          if (RandomRange(0, 2) = 0) then exchangeMove.addStoneToExchange(stone);  // F�ge Stein m�glicherweise zum Tauschzug hinzu
          if platziereStein(stone) then Break; // Versuche, den Stein anzulegen
        end;

        // Wenn ein m�glicher Anlegezug gefunden wurde, diesen ausf�hren, ansonsten den Tauschzug ausf�hren
        layMove := FBoard.createLayMove;
        if (layMove = nil) then begin
          // Sicherstellen, dass der Tauschzug mindestens einen Stein enth�lt
          if exchangeMove.stonesToExchange.Count = 0 then exchangeMove.addStoneToExchange(TStone(Me.Stones[0]));
          Result := exchangeMove;
        end else begin
          exchangeMove.Free;
          Result := layMove;
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
      if(FPlayers[0].PlayerID = FMyId) then begin
        Me := FPlayers[0];
        Opponent := FPlayers[1];
      end
      else begin
        me := FPlayers[1];
        opponent := FPlayers[0];
      end;

      write('Punkte vor dem Zug: ');
      writeln(IntToStr(FBoard.getScoresForPlayer(Me.PlayerID)));

      if FBoard.LastMove <> nil then begin
        LastMove := FBoard.LastMove;
        write('Letzter Zug: ');
        writeln(FBoard.LastMove.toString());
      end;

      writeln(FBoard.toString());

      // Zuf�lligen Zug berechnen lassen
      mov := macheZufallszug;

      if (mov <> nil) then begin
        writeln('Zug gefunden: ');
        if mov is TLayMove then begin
          writeln('Legezug');
          mov.addHint(TDebugHint.create('Legezug'));
        end else if mov is TExchangeMove then begin
          writeln('Tauschzug');
          mov.addHint(TDebugHint.create('Tauschzug'));
        end;
        writeln(mov.toString);
      end else begin
        writeln('KEIN ZUG GEFUNDEN!');
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
