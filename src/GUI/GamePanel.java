package GUI;

import logic.*;
import pieces.Piece;
import main.Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel implements GameObserver {
    private GameTopBar topBar;
    private GameSideBar sideBar;
    private GameBoard gameBoard;
    private JLabel statusLabel;

    private Game currentGame;
    private Position selectedPosition = null;

    public GamePanel() {
        setLayout(new BorderLayout());

        topBar = new GameTopBar();
        add(topBar, BorderLayout.NORTH);

        sideBar = new GameSideBar();
        add(sideBar, BorderLayout.EAST);

        gameBoard = new GameBoard(this::handleSquareClickWrapper);
        add(gameBoard, BorderLayout.CENTER);

        statusLabel = new JLabel("Select a piece to move.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10,0,10,0));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(statusLabel, BorderLayout.SOUTH);
    }

    // Wrapper to catch exception from lambda
    private void handleSquareClickWrapper(int row, int col) {
        try {
            handleSquareClick(row, col);
        } catch (InvalidMoveException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onMoveMade(Move move) {
        refreshBoard();
        topBar.updateTurnLabel("Turn: " + currentGame.getCurrentPlayer().getColor() +
                " | Last move: " + move.toString());
    }

    @Override
    public void onTurnChanged(Colors newColor) {
    }

    @Override
    public void onGameEnd(String result, int points) {
        Main.getInstance().endGame(currentGame,result, points);
    }

    private void handleSquareClick(int row, int col) throws InvalidMoveException {
        if (currentGame == null) return;

        //(0,0) e top left and i need them to be as A1 bottom left
        char posCol = (char) ('A' + col);
        int posRow = 8 - row;
        Position clickedPos = new Position(posCol, posRow);

        //select a piece
        if (selectedPosition == null) {
            Piece p = currentGame.getBoard().getPieceAt(clickedPos);

            //piece can be only the current player color
            if (p != null && p.getColor() == currentGame.getCurrentPlayer().getColor()) {
                selectedPosition = clickedPos;
                statusLabel.setText("Selected " + p.type() + " at " + clickedPos);
                gameBoard.highlightSquare(row, col);
                gameBoard.highlightPossibleMoves(selectedPosition, currentGame);
            }
        }
        //move piece
        else {
            //deselection
            if (clickedPos.equals(selectedPosition)) {
                selectedPosition = null;
                refreshBoard();
                statusLabel.setText("Deselected.");
                return;
            }

            try {
                Player player = currentGame.getCurrentPlayer();
                Board board = currentGame.getBoard();

                //make move
                Piece capturedPiece = board.getPieceAt(clickedPos);
                player.makeMove(selectedPosition, clickedPos, board);

                //check for promotion
                Piece movedPiece = board.getPieceAt(clickedPos);
                int promoRow = (player.getColor() == Colors.WHITE) ? 8 : 1;

                if (movedPiece != null && movedPiece.type() == 'P' && clickedPos.getRow() == promoRow) {
                    board.promoteToQueen(clickedPos, player.getColor());
                    JOptionPane.showMessageDialog(this, "pieces.Pawn promoted to pieces.Queen!");
                }

                //add move to history
                Move move = new Move(player.getColor(), selectedPosition, clickedPos, capturedPiece);
                currentGame.addMove(move);

                currentGame.updateTurn();

                //back to initial
                selectedPosition = null;
                statusLabel.setText("logic.Move successful! Waiting for computer...");
                //refreshBoard();

                //checkmate?
                if (currentGame.checkForMate()) {
                    int points = currentGame.getCurrentPlayer().getPoints();
                    Main.getInstance().endGame(currentGame,"VICTORY", points);
                    return;
                }
                if (currentGame.isStalemate() || currentGame.isThereDrawByRepetiton()) {
                    Main.getInstance().endGame(currentGame, "DRAW", 0); // Bonus +150
                    return;
                }

                //computer move
                if (currentGame.getCurrentPlayer().getName().equals("Computer")) {
                    //delay so you can notice it
                    Timer timer = new Timer(500, e -> {
                        try {
                            makeComputerMove();
                        } catch (InvalidMoveException ex) {
                            throw new RuntimeException(ex);
                        }
                        ((Timer)e.getSource()).stop(); // Run once
                    });
                    timer.setRepeats(false);
                    timer.start();
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid logic.Move: " + ex.getMessage());
                selectedPosition = null; //reset on error
                refreshBoard();
            }
        }
    }

    private void makeComputerMove() throws InvalidMoveException {
        Player currentPlayer = currentGame.getCurrentPlayer();
        if (!currentPlayer.getName().equals("Computer")) return;

        Board board = currentGame.getBoard();
        List<ChessPair<Position, Piece>> myPieces = board.getPiecesByColor(currentPlayer.getColor());

        class ValidMove {
            Position from;
            Position to;
            Piece piece;
            public ValidMove(Position f, Position t, Piece p) { from = f; to = t; piece = p; }
        }

        java.util.List<ValidMove> allMoves = new java.util.ArrayList<>();

        //find all moves
        for (ChessPair<Position, Piece> pair : myPieces) {
            Piece p = pair.getValue();
            Position start = p.getPosition();
            List<Position> candidates = p.getPossibleMoves(board);

            for (Position target : candidates) {
                try {
                    //check if this specific move is legal
                    if (board.isValidMove(start, target)) {
                        allMoves.add(new ValidMove(start, target, p));
                    }
                } catch (InvalidMoveException _) {
                }
            }
        }

        //pick random move
        if (!allMoves.isEmpty()) {
            java.util.Random rand = new java.util.Random();
            ValidMove chosenMove = allMoves.get(rand.nextInt(allMoves.size()));

            try {
                Piece capturedPiece = board.getPieceAt(chosenMove.to); // Save for history
                currentPlayer.makeMove(chosenMove.from, chosenMove.to, board);

                //promote
                Piece movedPiece = board.getPieceAt(chosenMove.to);
                int compPromoRow = (currentPlayer.getColor() == Colors.WHITE) ? 8 : 1;

                if (movedPiece != null && movedPiece.type() == 'P' && chosenMove.to.getRow() == compPromoRow) {
                    board.promoteToQueen(chosenMove.to, currentPlayer.getColor());
                }

                Move moveRecord = new Move(currentPlayer.getColor(), chosenMove.from, chosenMove.to, capturedPiece);
                currentGame.addMove(moveRecord);

                currentGame.updateTurn();

                if (currentGame.checkForMate()) {
                    int points = currentGame.getPlayer1().getPoints();
                    Main.getInstance().endGame(currentGame,"DEFEAT", points);
                } else if (board.isInCheck(Colors.WHITE)) {
                    statusLabel.setText("WARNING: You are in Check!");
                } else {
                    statusLabel.setText("Your turn.");
                }
                if (currentGame.isStalemate() || currentGame.isThereDrawByRepetiton()) {
                    Main.getInstance().endGame(currentGame, "DRAW", 0); // Bonus +150
                    return;
                }

            } catch (Exception e) {
                System.err.println("Computer failed to move: " + e.getMessage());
            }
        } else {
            if (board.isInCheck(Colors.BLACK)) {
                JOptionPane.showMessageDialog(this, "You Won! Computer is in Checkmate.");
            } else {
                Main.getInstance().endGame(currentGame, "DRAW", 0);
            }
        }
    }

    private void refreshBoard() {
        Player current = currentGame.getCurrentPlayer();
        topBar.updateTurnLabel("Turn: " + current.getColor() + " (" + current.getName() + ")" + "  Last move:" + currentGame.getLastMove());

        sideBar.refreshCapturedPanel(currentGame);
        gameBoard.refreshBoard(currentGame);
    }

    public void setGame(Game game) throws InvalidMoveException {
        if(this.currentGame != null) {
            this.currentGame.removeObserver(this);
        }
        this.currentGame = game;
        this.currentGame.addObserver(this);
        this.selectedPosition = null;
        topBar.setGame(game);
        refreshBoard();

        Player current = currentGame.getCurrentPlayer();
        if (current.getName().equals("Computer")) {
            Timer timer = new Timer(500, e -> {
                try {
                    makeComputerMove();
                } catch (InvalidMoveException ex) {
                    throw new RuntimeException(ex);
                }
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}