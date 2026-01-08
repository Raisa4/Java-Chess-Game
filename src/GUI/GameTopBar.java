package GUI;

import logic.Game;
import main.Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameTopBar extends JPanel {
    private JLabel turnLabel;
    private Game currentGame;

    public GameTopBar() {
        setLayout(new BorderLayout());
        setBackground(new Color(60, 40, 20));
        setBorder(new EmptyBorder(10, 20, 10, 20));
        //round label
        turnLabel = new JLabel("Turn: WHITE");
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        //butoane din dreapta
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton resignBtn = new JButton("Resign");
        resignBtn.setBackground(new Color(180, 40, 40)); // Roșu închis
        resignBtn.setForeground(Color.WHITE);
        resignBtn.setFocusPainted(false);
        resignBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        resignBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ești sigur că vrei să renunți? Vei pierde 150 de puncte.",
                    "Confirmare Resign", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION && currentGame != null) {
                int points = currentGame.getCurrentPlayer().getPoints();
                Main.getInstance().endGame(currentGame, "RESIGN", points);
            }
        });

        JButton saveExitBtn = new JButton("Save & Exit");
        saveExitBtn.setFocusPainted(false);
        saveExitBtn.setBackground(new Color(100, 55, 35)); // Maro
        saveExitBtn.setForeground(Color.WHITE);
        saveExitBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveExitBtn.addActionListener(e -> {
            Main.getInstance().write();
            Main.getInstance().showScreen("MENU");
        });

        buttonsPanel.add(resignBtn);
        buttonsPanel.add(saveExitBtn);

        add(turnLabel, BorderLayout.WEST);
        add(buttonsPanel, BorderLayout.EAST);
    }

    public void setGame(Game game) {
        this.currentGame = game;
    }

    public void updateTurnLabel(String text) {
        turnLabel.setText(text);
    }
}
