import java.util.ArrayList;
import java.util.List;

public class Game {
    private int id;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int playerToMove;
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
        this.playerToMove = 0;
    }
    public boolean checkForMate() {
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
    }
    public List<Move> getMoves() {
        return moves;
    }
    public boolean isStalemate() {
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

}