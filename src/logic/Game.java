package logic;

import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int id;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int playerToMove;
    private List<GameObserver> observers = new ArrayList<>();
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }
    public void triggerGameEnd(String result, int points) {
        for (GameObserver obs : observers) {
            obs.onGameEnd(result, points);
        }
    }
    public Game(Player player1, Player player2) {
        this.id = 1;
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board();
        this.moves = new ArrayList<>();
    }
    public int getID() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void updateTurn() {
        this.playerToMove = (this.playerToMove == 0) ? 1 : 0;
        for (GameObserver observer : observers) {
            observer.onTurnChanged(getCurrentPlayer().getColor());
        }
    }
    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public Board getBoard() {
        return board;
    }
    public Player getCurrentPlayer() {
        return (playerToMove == 0) ? player1 : player2;
    }
    public void setCurrentPlayerIndex(int i) {
        this.playerToMove = i;
    }
    public void start() {
        this.board.initializeBoard();
        if (this.moves != null) {
            this.moves.clear();
        }
        if (player1.getColor() == Colors.WHITE) {
            this.playerToMove = 0;
        } else {
            this.playerToMove = 1;
        }
    }
    public boolean checkForMate() throws InvalidMoveException {
        Player currentPlayer = getCurrentPlayer();
        Colors color = currentPlayer.getColor();
        if (!board.isInCheck(color)) {
            return false;
        }
        List<ChessPair<Position, Piece>> myPieces = board.getPiecesByColor(color);
        for (ChessPair<Position, Piece> pair : myPieces) {
            Piece p = pair.getValue();
            List<Position> potentialMoves = p.getPossibleMoves(board);
            for (Position potentialMove : potentialMoves) {
                try {
                    if (board.isValidMove(p.getPosition(), potentialMove)) {
                        return false; //practic aici gaseste o mutare care salveaza king ul
                    }
                } catch (InvalidMoveException _) {
                }
            }
        }
        return true;//practic a trecut prin toate si e tot in sah deci e mat
    }
    public void addMove(Move move) {
        this.moves.add(move);
        for(GameObserver observer : observers) {
            observer.onMoveMade(move);
        }
    }
    public List<Move> getMoves() {
        return moves;
    }
    public Move getLastMove() {
        if (moves.isEmpty()) {
            return null;
        }
        return moves.get(moves.size() - 1);
    }
    public boolean isStalemate() throws InvalidMoveException {
        Player currentPlayer = getCurrentPlayer();
        Colors color = currentPlayer.getColor();
        if (board.isInCheck(color)) {
            return false;
        }
        List<ChessPair<Position, Piece>> myPieces = board.getPiecesByColor(color);
        for (ChessPair<Position, Piece> pair : myPieces) {
            if (!pair.getValue().getPossibleMoves(board).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public void setTurnByColor(Colors color) {
        if (player1.getColor() == color) {
            this.playerToMove = 0;
        } else {
            this.playerToMove = 1;
        }
    }
    public void setMoves(List<Move> loadedMoves) {
        this.moves = loadedMoves;
    }
    public boolean isThereDrawByRepetiton() {
        int n = moves.size();
        if (n < 10) return false;
        Move lastMove = moves.get(n - 1);
        Move moveMinus4 = moves.get(n - 5);
        Move moveMinus8 = moves.get(n - 9);
        boolean playerRepeated = lastMove.equals(moveMinus4) && lastMove.equals(moveMinus8);

        Move oppLast = moves.get(n - 2);
        Move oppMinus4 = moves.get(n - 6);
        Move oppMinus8 = moves.get(n - 10);

        boolean opponentRepeated = oppLast.equals(oppMinus4) && oppLast.equals(oppMinus8);

        return playerRepeated && opponentRepeated;

    }
}
