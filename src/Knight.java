import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Colors color, Position pos) {
        super(color, pos);
    }

    @Override
    public char type() {
        return 'N';
    }

    @Override
    public int getValue() {
        return 30;
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, -1}, {-2, 1},
                {1, 2}, {-1, 2}, {1, -2}, {-1, -2}
        };

        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];

            int targetRow = getPosition().getRow() + dRow;
            int targetCol = getPosition().getColumn() + dCol;

            try {
                Position target = new Position((char) targetCol, targetRow);
                Piece pieceAtTarget = board.getPieceAt(target);
                if (pieceAtTarget == null) {
                    moves.add(target);
                }
                else {
                    if (pieceAtTarget.getColor() != this.getColor()) {
                        moves.add(target);
                    }
                }
            } catch (InvalidMoveException e) {
            }
        }
        return moves;
    }
}