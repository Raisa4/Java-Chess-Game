package GUI;

import logic.Colors;
import logic.Game;
import logic.Player;
import pieces.Piece;
import main.Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameSideBar extends JPanel {
    private JPanel p1CapturesPanel;
    private JPanel p2CapturesPanel;

    public GameSideBar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(45, 30, 15)); // Un maro foarte închis pentru contrast
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(160, 0)); // Lățime fixă

        JLabel p1Label = new JLabel("You Captured:");
        p1Label.setForeground(Color.LIGHT_GRAY);
        p1Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        p1CapturesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1CapturesPanel.setOpaque(false);
        p1CapturesPanel.setMaximumSize(new Dimension(140, 200));

        JLabel p2Label = new JLabel("Opponent Captured:");
        p2Label.setForeground(Color.LIGHT_GRAY);
        p2Label.setAlignmentX(Component.CENTER_ALIGNMENT);

        p2CapturesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2CapturesPanel.setOpaque(false);
        p2CapturesPanel.setMaximumSize(new Dimension(140, 200));

        add(p1Label);
        add(Box.createVerticalStrut(5));
        add(p1CapturesPanel);
        add(Box.createVerticalStrut(20)); //spațiu între jucători
        add(p2Label);
        add(Box.createVerticalStrut(5));
        add(p2CapturesPanel);
    }

    public void refreshCapturedPanel(Game currentGame) {
        p1CapturesPanel.removeAll();
        p2CapturesPanel.removeAll();

        if (currentGame == null) return;

        Player user = currentGame.getplayerHuman();
        Player computer = currentGame.getplayerComputer();


        for (Piece p : user.getCapturedPieces()) {
            JLabel pieceLbl = new JLabel(PieceUIHelper.getUnicodePiece(p));
            pieceLbl.setFont(new Font("Serif", Font.PLAIN, 24));

            if (p.getColor() == Colors.WHITE) {
                pieceLbl.setForeground(Color.WHITE);
            } else {
                pieceLbl.setForeground(Color.LIGHT_GRAY);
            }
            p1CapturesPanel.add(pieceLbl);
        }

        for (Piece p : computer.getCapturedPieces()) {
            JLabel pieceLbl = new JLabel(PieceUIHelper.getUnicodePiece(p));
            pieceLbl.setFont(new Font("Serif", Font.PLAIN, 24));

            if (p.getColor() == Colors.WHITE) {
                pieceLbl.setForeground(Color.WHITE);
            } else {
                pieceLbl.setForeground(Color.LIGHT_GRAY);
            }
            p2CapturesPanel.add(pieceLbl);
        }

        p1CapturesPanel.revalidate();
        p1CapturesPanel.repaint();
        p2CapturesPanel.revalidate();
        p2CapturesPanel.repaint();
    }
}