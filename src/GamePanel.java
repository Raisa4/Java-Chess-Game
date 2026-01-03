import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GamePanel extends JPanel {
    private JPanel boardPanel;
    private JButton[][] squares = new JButton[8][8];
    private JLabel statusLabel;
    private JLabel turnLabel;

    private final Color COLOR_LIGHT = new Color(179, 147, 119); // Beige
    private final Color COLOR_DARK = new Color(88, 57, 39);    // Dark Brown
    private final Color COLOR_HIGHLIGHT = new Color(56, 142, 60, 150); // Green for selection

    private Game currentGame;
    private Position selectedPosition = null;

    public GamePanel() {
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(60, 40, 20));
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        turnLabel = new JLabel("Turn: WHITE");
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton backButton = new JButton("Back to Menu");
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> Main.getInstance().showScreen("MENU"));

        infoPanel.add(turnLabel, BorderLayout.WEST);
        infoPanel.add(backButton, BorderLayout.EAST);
        add(infoPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(8, 8));
        add(boardPanel, BorderLayout.CENTER);

        initializeBoardGrid();

        statusLabel = new JLabel("Select a piece to move.");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10,0,10,0));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void initializeBoardGrid() {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Serif", Font.PLAIN, 50)); // Large font for pieces
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);

                if ((row + col) % 2 == 0) {
                    btn.setBackground(COLOR_LIGHT);
                } else {
                    btn.setBackground(COLOR_DARK);
                }

                final int r = row;
                final int c = col;
                btn.addActionListener(e -> {
                    try {
                        handleSquareClick(r, c);
                    } catch (InvalidMoveException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                squares[row][col] = btn;
                boardPanel.add(btn);
            }
        }
    }

    public void setGame(Game game) throws InvalidMoveException {
        this.currentGame = game;
        this.selectedPosition = null;
        refreshBoard();
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
                highlightSquare(row, col);
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

                // 2.check for promotion
                Piece movedPiece = board.getPieceAt(clickedPos);
                if (movedPiece != null && movedPiece.type() == 'P' && clickedPos.getRow() == 8) {
                    board.promoteToQueen(clickedPos, Colors.WHITE);
                    JOptionPane.showMessageDialog(this, "Pawn promoted to Queen!");
                }

                //add move to history
                Move move = new Move(player.getColor(), selectedPosition, clickedPos, capturedPiece);
                currentGame.addMove(move);

                currentGame.updateTurn();

                //back to initial
                selectedPosition = null;
                statusLabel.setText("Move successful! Waiting for computer...");
                refreshBoard();

                //checkmate?
                if (currentGame.checkForMate()) {
                    JOptionPane.showMessageDialog(this, "CHECKMATE! You won!");
                    return; //stop game
                }

                //computer move
                if (currentGame.getCurrentPlayer().getColor() == Colors.BLACK) {
                    //delay so you can notice it
                    Timer timer = new Timer(500, e -> {
                        makeComputerMove();
                        ((Timer)e.getSource()).stop(); // Run once
                    });
                    timer.setRepeats(false);
                    timer.start();
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid Move: " + ex.getMessage());
                selectedPosition = null; //reset on error
                refreshBoard();
            }
        }
    }

    private void makeComputerMove() {
        Player currentPlayer = currentGame.getCurrentPlayer();
        if (currentPlayer.getColor() != Colors.BLACK) return;

        Board board = currentGame.getBoard();
        List<ChessPair<Position, Piece>> myPieces = board.getPiecesByColor(Colors.BLACK);

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
                if (movedPiece != null && movedPiece.type() == 'P' && chosenMove.to.getRow() == 1) {
                    board.promoteToQueen(chosenMove.to, Colors.BLACK);
                }

                Move moveRecord = new Move(Colors.BLACK, chosenMove.from, chosenMove.to, capturedPiece);
                currentGame.addMove(moveRecord);

                currentGame.updateTurn();
                refreshBoard();

                if (currentGame.checkForMate()) {
                    JOptionPane.showMessageDialog(this, "You lost! Checkmate.");
                } else if (board.isInCheck(Colors.WHITE)) {
                    statusLabel.setText("WARNING: You are in Check!");
                } else {
                    statusLabel.setText("Your turn.");
                }

            } catch (Exception e) {
                System.err.println("Computer failed to move: " + e.getMessage());
            }
        } else {
            if (board.isInCheck(Colors.BLACK)) {
                JOptionPane.showMessageDialog(this, "You Won! Computer is in Checkmate.");
            } else {
                JOptionPane.showMessageDialog(this, "Stalemate! It's a draw.");
            }
        }
    }

    private void refreshBoard() {
        Board b = currentGame.getBoard();
        Player current = currentGame.getCurrentPlayer();

        turnLabel.setText("Turn: " + current.getColor() + " (" + current.getName() + ")");

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) squares[row][col].setBackground(COLOR_LIGHT);
                else squares[row][col].setBackground(COLOR_DARK);

                try {
                    char posCol = (char) ('A' + col);
                    int posRow = 8 - row;
                    Position pos = new Position(posCol, posRow);

                    Piece p = b.getPieceAt(pos);
                    if (p != null) {
                        squares[row][col].setText(getUnicodePiece(p));

                        //contrast so it's okay to see
                        if (p.getColor() == Colors.WHITE) {
                            squares[row][col].setForeground(Color.WHITE);
                        } else {
                            squares[row][col].setForeground(Color.BLACK);
                        }
                    } else {
                        squares[row][col].setText("");
                    }
                } catch (InvalidMoveException e) {
                    System.err.println("Board refresh error: " + e.getMessage());
                }
            }
        }
    }

    private void highlightSquare(int row, int col) {
        squares[row][col].setBackground(COLOR_HIGHLIGHT);
    }

    private String getUnicodePiece(Piece p) {
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
        return "?";
    }
}
