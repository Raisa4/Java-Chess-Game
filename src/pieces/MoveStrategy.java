package pieces;

import logic.*;

import java.util.List;

public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException;
}
