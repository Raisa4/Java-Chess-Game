package pieces;

import logic.Colors;
import logic.Position;

public class PieceFactory {

    public static Piece createPiece(char type, Colors color, Position position) {
        return switch (Character.toUpperCase(type)) {
            case 'R' -> new Rook(color, position);
            case 'N' -> new Knight(color, position);
            case 'B' -> new Bishop(color, position);
            case 'Q' -> new Queen(color, position);
            case 'K' -> new King(color, position);
            case 'P' -> new Pawn(color, position);
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        };
    }
}