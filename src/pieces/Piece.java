package pieces;

import logic.*;

import java.util.ArrayList;
import java.util.List;


public abstract class Piece implements ChessPiece {
    private final Colors color;
    private Position position;
    protected MoveStrategy moveStrategy;
    public abstract int getValue();
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
    public boolean checkForCheck(Board board, Position kingPosition) throws InvalidMoveException {
        List<Position> moves = this.getPossibleMoves(board);
        return moves.contains(kingPosition);
    }
    public Piece(Colors color, Position position) {
        this.color = color;
        this.position = position;
    }
    public List<Position> getPossibleMoves(Board board) throws InvalidMoveException {
        if (moveStrategy == null) {
            return new ArrayList<>(); // Sau aruncă excepție
        }
        return moveStrategy.getPossibleMoves(board, this);
    }

    public abstract char type();
}
