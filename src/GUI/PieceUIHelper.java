package GUI;

import logic.Colors;
import pieces.Piece;

import java.awt.Color;

public class PieceUIHelper {
    public static String getUnicodePiece(Piece p) {
        if (p.getColor() == Colors.WHITE) {
            switch(p.type()) {
                case 'K': return "♔"; case 'Q': return "♕";
                case 'R': return "♖"; case 'B': return "♗";
                case 'N': return "♘"; case 'P': return "♙";
            }
        } else {
            switch(p.type()) {
                case 'K': return "♚"; case 'Q': return "♛";
                case 'R': return "♜"; case 'B': return "♝";
                case 'N': return "♞"; case 'P': return "♟";
            }
        }
        return "?";//just in case
    }
    public static Color getPieceColor(Piece p) {
        if (p.getColor() == Colors.WHITE) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
