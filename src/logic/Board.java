package logic;

import pieces.King;
import pieces.Piece;
import pieces.PieceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Board{
    private TreeSet<ChessPair<Position, Piece>> pieces;
    public Board(){
        this.pieces = new TreeSet<>();
    }
    private void addPiece(Piece piece) {
        ChessPair<Position, Piece> pair = new ChessPair<>(piece.getPosition(), piece);
        pieces.add(pair);
    }
    public void initializeBoard() {
        pieces.clear();
        try {
            addPiece(PieceFactory.createPiece('R', Colors.WHITE, new Position('A', 1)));
            addPiece(PieceFactory.createPiece('N', Colors.WHITE, new Position('B', 1)));
            addPiece(PieceFactory.createPiece('B', Colors.WHITE, new Position('C', 1)));
            addPiece(PieceFactory.createPiece('Q', Colors.WHITE, new Position('D', 1)));
            addPiece(PieceFactory.createPiece('K', Colors.WHITE, new Position('E', 1)));
            addPiece(PieceFactory.createPiece('B', Colors.WHITE, new Position('F', 1)));
            addPiece(PieceFactory.createPiece('N', Colors.WHITE, new Position('G', 1)));
            addPiece(PieceFactory.createPiece('R', Colors.WHITE, new Position('H', 1)));

            for (char col = 'A'; col <= 'H'; col++) {
                addPiece(PieceFactory.createPiece('P', Colors.WHITE, new Position(col, 2)));
            }

            addPiece(PieceFactory.createPiece('R', Colors.BLACK, new Position('A', 8)));
            addPiece(PieceFactory.createPiece('N', Colors.BLACK, new Position('B', 8)));
            addPiece(PieceFactory.createPiece('B', Colors.BLACK, new Position('C', 8)));
            addPiece(PieceFactory.createPiece('Q', Colors.BLACK, new Position('D', 8)));
            addPiece(PieceFactory.createPiece('K', Colors.BLACK, new Position('E', 8)));
            addPiece(PieceFactory.createPiece('B', Colors.BLACK, new Position('F', 8)));
            addPiece(PieceFactory.createPiece('N', Colors.BLACK, new Position('G', 8)));
            addPiece(PieceFactory.createPiece('R', Colors.BLACK, new Position('H', 8)));

            for (char col = 'A'; col <= 'H'; col++) {
                addPiece(PieceFactory.createPiece('P', Colors.BLACK, new Position(col, 7)));
            }

        } catch (Exception e) {
            System.err.println("Error initializing board: " + e.getMessage());
        }
    }
    public Piece getPieceAt(Position p) {
        for (ChessPair<Position, Piece> pair : pieces) {
            if (pair.getKey().equals(p)) {
                return pair.getValue();
            }
        }
        return null;
    }
    public List<ChessPair<Position, Piece>> getPiecesByColor(Colors color) {
        List<ChessPair<Position, Piece>> coloredPieces = new ArrayList<>();
        for (ChessPair<Position, Piece> pair : pieces) {
            if (pair.getValue().getColor() == color) {
                coloredPieces.add(pair);
            }
        }
        return coloredPieces;
    }
    private Position whereIsKing(Colors color) {
        for(ChessPair<Position,Piece> pair : pieces){
            Piece piece = pair.getValue();
            if(piece instanceof King && piece.getColor().equals(color)){
                return piece.getPosition();
            }
        }
        return null;
    }
    public boolean isInCheck(Colors color) throws InvalidMoveException {
        Position kingPos =  whereIsKing(color);

        for(ChessPair<Position,Piece> pair : pieces){
            Piece enemy = pair.getValue();
            if(enemy.getColor()!=color){
                List<Position> enemyCanAttack = enemy.getPossibleMoves(this);
                if (enemyCanAttack.contains(kingPos)){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isValidMove(Position posFrom, Position posTo) throws InvalidMoveException {
        Piece pieceToMove = getPieceAt(posFrom);
        if(pieceToMove == null){
            throw new InvalidMoveException("No piece at starting position " + posFrom);
        }


        List<Position> possibleMoves = pieceToMove.getPossibleMoves(this);
        if(!possibleMoves.contains(posTo)){
            throw new InvalidMoveException("No move from " + posFrom + " to " + posTo);
        };

        //simulate move to see if king is ok
        //remove the pair at from cuz it's moving at 'to'
        ChessPair<Position, Piece> oldPair = new ChessPair<>(posFrom, pieceToMove);
        pieces.remove(oldPair);
        //remove the piece captured if it exists
        Piece pieceAtTo = getPieceAt(posTo);
        if (pieceAtTo != null) {
            ChessPair<Position, Piece> toCapturePair = new ChessPair<>(posTo, pieceAtTo);
            pieces.remove(toCapturePair);
        }
        //move piece at 'to' pos
        pieceToMove.setPosition(posTo);
        pieces.add(new ChessPair<>(posTo, pieceToMove));
        boolean kingIsSafe = !isInCheck(pieceToMove.getColor());


        //go back to start board before the from->to move
        pieces.remove(new ChessPair<>(posTo,pieceToMove));
        pieceToMove.setPosition(posFrom);
        pieces.add(oldPair);
        if (pieceAtTo!= null) {
            pieces.add( new ChessPair<>(posTo, pieceAtTo));
        }
        if (!kingIsSafe) {
            throw new InvalidMoveException("logic.Move leaves pieces.King in check!");
        }
        return true;
}
    public void movePiece(Position posFrom, Position posTo) throws InvalidMoveException {
       isValidMove(posFrom, posTo);
       Piece pieceAtFrom = getPieceAt(posFrom);
       Piece pieceAtTo = getPieceAt(posTo);
       pieces.remove(new ChessPair<>(posFrom, pieceAtFrom));
       if(pieceAtTo != null){
           pieces.remove(new ChessPair<>(posTo,pieceAtTo));
       }
       pieceAtFrom.setPosition(posTo);
       pieces.add(new ChessPair<>(posTo, pieceAtFrom));

    }
    public void printBoard() {
        System.out.println("   _______________________________________"); // Top border (optional)
        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " | ");
            for (char col = 'A'; col <= 'H'; col++) {
                try {
                    Position pos = new Position(col, row);
                    Piece piece = getPieceAt(pos);
                    if (piece == null) {
                        System.out.print("...  ");
                    } else {
                        char type = piece.type();
                        char colorChar = (piece.getColor() == Colors.WHITE) ? 'W' : 'B';
                        System.out.print(type + "-" + colorChar + "  ");
                    }
                } catch (InvalidMoveException e) {
                    //er shouldn't get here
                }}
            System.out.println("|");}
        System.out.println("   ---------------------------------------");
        System.out.println("    A    B    C    D    E    F    G    H");
    }
    public void clear() {
        pieces.clear();
    }
    public void setPiece(Piece piece) {
        try{
            pieces.add(new ChessPair<>(piece.getPosition(), piece));
        }catch(Exception e){
            System.err.println("Error placing piece from file: " + e.getMessage());
        }
    }
    public void promoteToQueen(Position pos, Colors color) {
        Piece pawn = getPieceAt(pos);
        if (pawn != null) {
            pieces.remove(new ChessPair<>(pos, pawn));
        }
        pieces.add(new ChessPair<>(pos, PieceFactory.createPiece('Q', color, pos)));
    }
}
