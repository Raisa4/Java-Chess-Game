package logic;

import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Colors color;
    private int points;
    private List<Piece> capturedPieces;
    public Player(String name, Colors color) {
        this.name = name;
        this.color = color;
        this.points = 0;
        this.capturedPieces = new ArrayList<>();
    }
    public List<ChessPair<Position, Piece>> getOwnedPieces(Board board) {
        List<ChessPair<Position, Piece>> myPieces = board.getPiecesByColor(color);
        return myPieces;
    }
    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public Colors getColor() {
        return color;
    }
    public void makeMove(Position posFrom, Position posTo,Board board) throws InvalidMoveException {
        Piece pieceAtTo = board.getPieceAt(posTo);
        //chestia asta imi face throw deci imi opreste daca invalid
        board.movePiece(posFrom, posTo);
        //daca ajung aici deci sunt ok s-a facut mutarea
        if(pieceAtTo != null) {
            capturedPieces.add(pieceAtTo);
            int value = pieceAtTo.getValue();
            this.points += value;
        }

    }
    public String getName(){
        return this.name;
    }
}
