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


        char posCol = (char) ('A' + col);
        int posRow = 8 - row;

        Position clickedPos = new Position(posCol, posRow);

        //SELECTION LOGIC
        if (selectedPosition == null) {

            Piece p = currentGame.getBoard().getPieceAt(clickedPos);
            if (p != null && p.getColor() == currentGame.getCurrentPlayer().getColor()) {
                selectedPosition = clickedPos;
                statusLabel.setText("Selected " + p.type() + " at " + clickedPos);
                highlightSquare(row, col);
            }
        } else {
            try {
                // Attempt move
                currentGame.getCurrentPlayer().makeMove(selectedPosition, clickedPos, currentGame.getBoard());

                // Add to history
                currentGame.updateTurn();

                statusLabel.setText("Move successful!");
                selectedPosition = null;
                refreshBoard();

                //Check for game over
                if (currentGame.checkForMate()) {
                    JOptionPane.showMessageDialog(this, "Checkmate! Game Over.");
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid Move: " + ex.getMessage());
                selectedPosition = null;
                refreshBoard();
            }
        }
    }

    private void refreshBoard() {
        Board b = currentGame.getBoard();
        Player current = currentGame.getCurrentPlayer();

        // Update top text
        turnLabel.setText("Turn: " + current.getColor() + " (" + current.getName() + ")");

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // 1. Reset Colors
                if ((row + col) % 2 == 0) squares[row][col].setBackground(COLOR_LIGHT);
                else squares[row][col].setBackground(COLOR_DARK);

                // 2. Place Pieces
                try {
                    // We wrap this in try-catch because Position constructor throws Exception
                    // even though we know logic (0-7) is safe.
                    char posCol = (char) ('A' + col);
                    int posRow = 8 - row;
                    Position pos = new Position(posCol, posRow);

                    Piece p = b.getPieceAt(pos);
                    if (p != null) {
                        squares[row][col].setText(getUnicodePiece(p));

                        // Visual contrast fix
                        if (p.getColor() == Colors.WHITE) {
                            squares[row][col].setForeground(Color.WHITE);
                            // If white text on beige background is hard to see, try Color.DARK_GRAY
                        } else {
                            squares[row][col].setForeground(Color.BLACK);
                        }
                    } else {
                        squares[row][col].setText("");
                    }
                } catch (InvalidMoveException e) {
                    // This block should never be reached given the for-loop bounds
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
