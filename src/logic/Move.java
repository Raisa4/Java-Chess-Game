package logic;

import pieces.Piece;

public class Move{
    private Colors moveColor;
    private Position posFrom;
    private Position posTo;
    private Piece capturedPiece;

    public Move(Colors moveColor, Position posFrom, Position posTo, Piece capturedPiece) {
        this.moveColor = moveColor;
        this.posFrom = posFrom;
        this.posTo = posTo;
        this.capturedPiece = capturedPiece;
    }

    public Colors getMoveColor() { return moveColor; }
    public Position getPosFrom() { return posFrom; }
    public Position getPosTo() { return posTo; }
    public Piece getCapturedPiece() { return capturedPiece; }
    @Override
    public String toString() {

        String captureInfo = (capturedPiece != null) ? " (Captured " + capturedPiece.type() + ")" : "";

        return moveColor + ": " + posFrom + " -> " + posTo + captureInfo;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return moveColor == move.moveColor &&
                posFrom.equals(move.posFrom) &&
                posTo.equals(move.posTo);
    }
}

