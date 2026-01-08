package pieces;

import logic.*;
import java.util.List;


public interface ChessPiece {
    List<Position> getPossibleMoves(Board board) throws InvalidMoveException;
    boolean checkForCheck(Board board, Position kingPosition) throws InvalidMoveException;
    char type();
}

