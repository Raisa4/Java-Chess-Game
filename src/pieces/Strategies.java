package pieces;

import logic.*;

import java.util.ArrayList;
import java.util.List;

class RookStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newRow = piece.getPosition().getRow() + dir[0] * i;
                int newCol = piece.getPosition().getColumn() + dir[1] * i;

                if (newRow < 1 || newRow > 8 || newCol < 'A' || newCol > 'H') break;

                Position target = new Position((char) newCol, newRow);
                Piece targetPiece = board.getPieceAt(target);

                if (targetPiece == null) {
                    moves.add(target);
                } else {
                    if (targetPiece.getColor() != piece.getColor()) {
                        moves.add(target);
                    }
                    break;
                }
            }
        }
        return moves;
    }
}

class BishopStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Diagonale

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int newRow = piece.getPosition().getRow() + dir[0] * i;
                int newCol = piece.getPosition().getColumn() + dir[1] * i;

                if (newRow < 1 || newRow > 8 || newCol < 'A' || newCol > 'H') break;

                Position target = new Position((char) newCol, newRow);
                Piece targetPiece = board.getPieceAt(target);

                if (targetPiece == null) {
                    moves.add(target);
                } else {
                    if (targetPiece.getColor() != piece.getColor()) {
                        moves.add(target);
                    }
                    break;
                }
            }
        }
        return moves;
    }
}

class QueenStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        moves.addAll(new RookStrategy().getPossibleMoves(board, piece));
        moves.addAll(new BishopStrategy().getPossibleMoves(board, piece));
        return moves;
    }
}

class KnightStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] off : offsets) {
            int newRow = piece.getPosition().getRow() + off[0];
            int newCol = piece.getPosition().getColumn() + off[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 'A' && newCol <= 'H') {
                Position target = new Position((char) newCol, newRow);
                Piece p = board.getPieceAt(target);
                if (p == null || p.getColor() != piece.getColor()) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}

class KingStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] off : offsets) {
            int newRow = piece.getPosition().getRow() + off[0];
            int newCol = piece.getPosition().getColumn() + off[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 'A' && newCol <= 'H') {
                Position target = new Position((char) newCol, newRow);
                Piece p = board.getPieceAt(target);
                if (p == null || p.getColor() != piece.getColor()) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}

class PawnStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Piece piece) throws InvalidMoveException {
        List<Position> moves = new ArrayList<>();
        int direction = (piece.getColor() == Colors.WHITE) ? 1 : -1;
        int startRow = (piece.getColor() == Colors.WHITE) ? 2 : 7;

        int currentRow = piece.getPosition().getRow();
        char currentCol = piece.getPosition().getColumn();

        int nextRow = currentRow + direction;
        if (nextRow >= 1 && nextRow <= 8) {
            Position forward = new Position(currentCol, nextRow);
            if (board.getPieceAt(forward) == null) {
                moves.add(forward);

                if (currentRow == startRow) {
                    Position forwardTwo = new Position(currentCol, currentRow + 2 * direction);
                    if (board.getPieceAt(forwardTwo) == null) {
                        moves.add(forwardTwo);
                    }
                }
            }
        }

        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            char capCol = (char)(currentCol + offset);
            if (capCol >= 'A' && capCol <= 'H' && nextRow >= 1 && nextRow <= 8) {
                Position target = new Position(capCol, nextRow);
                Piece p = board.getPieceAt(target);
                if (p != null && p.getColor() != piece.getColor()) {
                    moves.add(target);
                }
            }
        }

        return moves;
    }
}