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
}

