import java.util.List;

public abstract class Piece implements ChessPiece {
    private final Colors color;
    private Position position;
    public abstract int getValue();
    public Piece(Colors color, Position position) {
        this.color = color;
        this.position = position;
    }
    public Colors getColor() {
        return color;
    }
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    @Override
    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> moves = this.getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
}
