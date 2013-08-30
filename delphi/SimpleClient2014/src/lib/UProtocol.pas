unit UProtocol;

interface

uses
  AdomCore_4_3, SysUtils, UPlayer, UBoard, UClient, UMove, UDefines, Classes;

type
  TProtocol = class
  private
    FXmlDomImpl : TDomImplementation;
    FXmlParser : TXmlToDomParser;
    FBaseNetworkClass : TObject;

    FBoard : TBoard;
    FClient : TClient;

    FRoomID : String;

  public
    constructor Create(network : TObject; board : TBoard; client : TClient);
    destructor Destroy(); override;

    procedure processString(text : String);
end;

implementation
uses UNetwork;

procedure TProtocol.processString(text : String);
var
  XmlInputSource : TXmlInputSource;
  network : TNetwork;
  XmlRequest : TDomDocument;
  i, j, k, m: Integer;
  XmlBlock : TDomNode;
  XmlSubNodei, XmlSubNodej, XmlSubNodek, XmlSubNodel, XmlSubNodem : TDomNode;
  playerId : Integer;
  player : array [0..1] of TPlayer;
  move : TMove;
  xmlDoc : TDomDocument;
  xmlElement : TDomElement;
begin
  XmlSubNodei := nil;
  XmlSubNodej := nil;
  XmlSubNodek := nil;
  XmlSubNodel := nil;
  XmlSubNodem := nil;

  network := FBaseNetworkClass as TNetwork;

  // Convert string to XML document
  XmlInputSource := TXmlInputSource.Create('<message>' + text + '</message>', '', '', 1024, False, 0, 0, 0, 0, 1);

  try
    XmlRequest := FXmlParser.Parse(XmlInputSource);
  except
    // No complete valid XML block received
    exit;
  end;

  FreeAndNil(XmlInputSource);

  // Process XML document
  if(XmlRequest = nil) then exit;

  // Handle every child node
  for i := 0 to XmlRequest.ChildNodes.Item(0).ChildNodes.length - 1 do begin
    XmlBlock := XmlRequest.ChildNodes.Item(0).ChildNodes.Item(i);

    // Error encountered
    if(XmlBlock.NodeName = 'error') then begin
      writeln;
      writeln('Error occured:');
      writeln(XmlBlock.attributes.getNamedItem('message').NodeValue);
      network.sendString('</protocol>');
      network.disconnect;
    end;

    // Successfully joined a room
    if(XmlBlock.NodeName = 'joined') then begin
      FRoomID := XmlBlock.Attributes.getNamedItem('roomId').NodeValue;
      writeln;
      writeln('Joined room: ' + FRoomID);
      network.log('Joined room: ' + FRoomID);
    end;

    // Left the room
    if(XmlBlock.NodeName = 'left') then begin
      writeln('Left room: ' + FRoomID);
      network.log('Left room: ' + FRoomID);
      network.sendString('</protocol>');
      network.disconnect;
      break;
    end;

    // Room information received
    if(XmlBlock.NodeName = 'room') then begin
      for j := 0 to XmlBlock.ChildNodes.Length - 1 do begin
        XmlSubNodei := xmlBlock.ChildNodes.Item(j);
        if(XmlSubNodei.NodeName = 'data') then begin
          // Welcome message
          // Contains player color for this client
          if(XmlSubNodei.Attributes.GetNamedItem('class').NodeValue = 'welcome') then begin
            if(XmlSubNodei.Attributes.GetNamedItem('color').NodeValue = 'red') then begin
              FClient.setId(PLAYER_RED);
            end
            else begin
              FClient.setId(PLAYER_BLUE);
            end;
            writeln;
            writeln('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
            network.log('My id is: ' + XmlSubNodei.Attributes.getNamedItem('color').NodeValue);
          end;

          // Received game state
          if(XmlSubNodei.Attributes.getNamedItem('class').NodeValue = 'memento') then begin
            for k := 0 to XmlSubNodei.ChildNodes.Length - 1 do begin
              XmlSubNodej := xmlSubNodei.ChildNodes.Item(k);
              if(XmlSubNodej.NodeName = 'state') then begin
                // Read current turn number and player color
                FClient.CurrentTurn := StrToInt(XmlSubNodej.Attributes.getNamedItem('turn').NodeValue);
                if(XmlSubNodej.Attributes.getNamedItem('current').NodeValue = 'red') then begin
                  FBoard.CurrentPlayer := PLAYER_RED;
                end
                else begin
                  FBoard.CurrentPlayer := PLAYER_BLUE;
                end;
                for m := 0 to XmlSubNodej.ChildNodes.Length - 1 do begin
                  XmlSubNodel := XmlSubNodej.ChildNodes.Item(m);
                  // Read player data
                  if((XmlSubNodel.NodeName = 'red') or (XmlSubNodel.NodeName = 'blue')) then begin
                    if(XmlSubNodel.NodeName = 'red') then begin
                      playerId := PLAYER_RED;
                    end
                    else begin
                      playerId := PLAYER_BLUE;
                    end;
                    player[playerId] := FClient.getPlayer(playerId);
                    player[playerId].fromXml(XmlSubNodel); // Evaluate player XML
                  end;
                  // Read stones in shared stash
                  if(XmlSubNodel.NodeName = 'nextStones') then begin
                    FBoard.updateNextStones(XmlSubNodel);
                  end;
                  // Read latest move
                  if(XmlSubNodel.NodeName = 'move') then begin
                    FBoard.updateLastMove(XmlSubNodel);
                  end;
                  // Read board
                  if(XmlSubNodel.NodeName = 'board') then begin
                    FBoard.updateBoard(XmlSubNodel);
                  end;
                end;
                // Update board with player information
                FBoard.updatePlayers(player[0], player[1]);
              end;
            end;
          end;

          // Move request received
          if(XmlSubNodei.Attributes.GetNamedItem('class').NodeValue = 'sc.framework.plugins.protocol.MoveRequest') then begin
            move := FClient.zugAngefordert; // Let the client logic find a move to perform
            // Now construct the XML response document
            xmlDoc := TDomDocument.Create(FXmlDomImpl);
            xmlElement := TDomElement.Create(xmlDoc, 'room');
            xmlElement.SetAttribute('roomId', FRoomID);
            xmlElement.AppendChild(move.toXml(xmlDoc));
            xmlDoc.AppendChild(xmlElement);
            network.sendXml(xmlDoc);
            FreeAndNil(xmlDoc);
            FreeAndNil(move);
          end;
        end;
      end;
    end;
  end;
  network.emptyReceiveBuffer;
  if(XmlSubNodei <> nil) then FreeAndNil(XmlSubNodei);
  if(XmlSubNodej <> nil) then FreeAndNil(XmlSubNodej);
  if(XmlSubNodek <> nil) then FreeAndNil(XmlSubNodek);
  if(XmlSubNodel <> nil) then FreeAndNil(XmlSubNodel);
  if(XmlSubNodem <> nil) then FreeAndNil(XmlSubNodem);
  FreeAndNil(XmlRequest);
  if(move <> nil) then FreeAndNil(move);
end;

constructor TProtocol.Create(network : TObject; board : TBoard; client : TClient);
begin
  inherited Create();
  FXmlDomImpl := TDomImplementation.Create(nil);
  FXmlParser := TXmlToDomParser.Create(nil);
  FXmlParser.DOMImpl := FXmlDomImpl;
  FBaseNetworkClass := network;
  FBoard := board;
  FClient := client;
end;

destructor TProtocol.Destroy;
begin
  if(FXmlDomImpl <> nil) then FXmlDomImpl.Free;
  if(FXmlParser <> nil) then FXmlParser.Free;

  inherited;
end;

end.
