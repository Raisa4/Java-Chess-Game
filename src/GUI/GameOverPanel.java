package GUI;

import logic.User;
import main.Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameOverPanel extends JPanel {
    private BufferedImage backgroundImage;
    private JLabel resultLabel;
    private JLabel pointsDeltaLabel;
    private JLabel totalPointsLabel;

    private final Color COLOR_BEIGE = new Color(245, 245, 220);
    private final Color COLOR_TRANSPARENT_BOX = new Color(60, 40, 20, 220); // Darker box
    private final Color COLOR_GREEN = new Color(56, 142, 60);
    private final Color COLOR_RED = new Color(180, 40, 40);
    private final Color COLOR_BROWN = new Color(100, 55, 35);

    public GameOverPanel() {
        setLayout(new GridBagLayout());

        try {
            backgroundImage = ImageIO.read(new File("src/input/background.jpg"));
        } catch (IOException e) {
            backgroundImage = null;
        }

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(COLOR_TRANSPARENT_BOX);
        box.setBorder(new EmptyBorder(40, 60, 40, 60));

        //result
        resultLabel = new JLabel("GAME OVER");
        resultLabel.setFont(new Font("Serif", Font.BOLD, 48));
        resultLabel.setForeground(COLOR_BEIGE);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(resultLabel);

        box.add(Box.createVerticalStrut(20));

        //points stats
        pointsDeltaLabel = new JLabel("+0 Points");
        pointsDeltaLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        pointsDeltaLabel.setForeground(Color.GREEN);
        pointsDeltaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(pointsDeltaLabel);

        box.add(Box.createVerticalStrut(10));

        //score
        totalPointsLabel = new JLabel("Total Score: 0");
        totalPointsLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        totalPointsLabel.setForeground(Color.LIGHT_GRAY);
        totalPointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(totalPointsLabel);

        box.add(Box.createVerticalStrut(40));

        //btns
        JButton playAgainBtn = new JButton("Play Again");
        styleButton(playAgainBtn, COLOR_GREEN);
        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainBtn.addActionListener(e -> Main.getInstance().startNewGame());

        box.add(playAgainBtn);
        box.add(Box.createVerticalStrut(15));

        JButton menuBtn = new JButton("Back to Menu");
        styleButton(menuBtn, COLOR_BROWN);
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBtn.addActionListener(e -> Main.getInstance().showScreen("MENU"));

        box.add(menuBtn);

        add(box);
    }

    public void setResults(String result, int pointsChanged) {
        resultLabel.setText(result);

        if (pointsChanged > 0) {
            pointsDeltaLabel.setText("+" + pointsChanged + " Points");
            pointsDeltaLabel.setForeground(Color.GREEN);
            resultLabel.setForeground(COLOR_BEIGE);
        } else if (pointsChanged < 0) {
            pointsDeltaLabel.setText(pointsChanged + " Points");
            pointsDeltaLabel.setForeground(Color.RED);
            resultLabel.setForeground(Color.RED);
        } else {
            pointsDeltaLabel.setText("0 Points");
            pointsDeltaLabel.setForeground(Color.WHITE);
            resultLabel.setForeground(COLOR_BEIGE);
        }

        //update points
        User u = Main.getInstance().getCurrentUser();
        if (u != null) {
            totalPointsLabel.setText("Total Score: " + u.getPoints());
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(40, 30, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}