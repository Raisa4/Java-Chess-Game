package GUI;

import logic.Board;
import logic.Game;
import logic.InvalidMoveException;
import logic.Position;
import pieces.Piece;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class GameBoard extends JPanel {
    private JButton[][] squares = new JButton[8][8];
    private final Color COLOR_LIGHT = new Color(179, 147, 119); // Beige
    private final Color COLOR_DARK = new Color(88, 57, 39);    // Dark Brown
    private final Color COLOR_HIGHLIGHT = new Color(86, 160, 90);
    private final Color COLOR_MOVE_HIGHLIGHT = new Color(240, 230, 100);
    private BiConsumer<Integer, Integer> clickHandler;

    public GameBoard(BiConsumer<Integer, Integer> clickHandler) {
        this.clickHandler = clickHandler;
        setLayout(new GridLayout(8, 8));
        initializeBoardGrid();
    }

    private void initializeBoardGrid() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Serif", Font.PLAIN, 50)); // Large font for pieces
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);

                if ((row + col) % 2 == 0) {
                    btn.setBackground(COLOR_LIGHT);
                } else {
                    btn.setBackground(COLOR_DARK);
                }

                final int r = row;
                final int c = col;
                btn.addActionListener(e -> {
                    if (clickHandler != null) {
                        clickHandler.accept(r, c);
                    }
                });

                squares[row][col] = btn;
                add(btn);
            }
        }
    }

    public void refreshBoard(Game currentGame) {
        Board b = currentGame.getBoard();
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
                        squares[row][col].setText(PieceUIHelper.getUnicodePiece(p));

                        //contrast so it's okay to see
                        squares[row][col].setForeground(PieceUIHelper.getPieceColor(p));
                    } else {
                        squares[row][col].setText("");
                    }
                } catch (InvalidMoveException e) {
                    System.err.println("logic.Board refresh error: " + e.getMessage());
                }
            }
        }
    }

    public void highlightSquare(int row, int col) {
        squares[row][col].setBackground(COLOR_HIGHLIGHT);
    }

    public void highlightPossibleMoves(Position fromPos, Game currentGame) throws InvalidMoveException {
        Board board = currentGame.getBoard();
        Piece piece = board.getPieceAt(fromPos);
        if (piece == null) return;

        List<Position> toHighlight = piece.getPossibleMoves(board);

        for (Position target : toHighlight) {
            try {
                int row = 8 - target.getRow();
                int col = target.getColumn() - 'A';
                squares[row][col].setBackground(COLOR_MOVE_HIGHLIGHT);

            } catch (Exception e) {
                System.out.println("highlight possible moves error");
            }
        }
    }
}
