unit USegment;

(*
Ein Segment enth�lt Informationen �ber Bauteile einer bestimmten
Gr��e f�r einen Spieler.
*)
interface
  type
    TSegment = class
      private
        FSize : Integer;
        FUsable : Integer;
        FRetained : Integer;
      public
        property Size : Integer read FSize write FSize;
        property Usable : Integer read FUsable write FUsable;
        property Retained : Integer read FRetained write FRetained;

        constructor Create(size : Integer; usable : Integer; retained : Integer);
    end;

implementation
  constructor TSegment.Create(size : Integer; usable : Integer; retained : Integer);
    begin
      inherited Create;
      FSize := size;
      FUsable := usable;
      FRetained := retained;
    end;
end.

