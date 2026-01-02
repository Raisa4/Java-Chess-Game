import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {
    private BufferedImage backgroundImage;
    private JLabel welcomeLabel;
    private JLabel pointsLabel;
    private JLabel gamesLabel;

    private final Color COLOR_BEIGE = new Color(245, 245, 220);//text color
    private final Color COLOR_BROWN_TRANSPARENT = new Color(100, 55, 35, 150);
    private final Color COLOR_HOVER = new Color(100, 55, 35, 200);

    public MenuPanel() {
        try {
            backgroundImage = ImageIO.read(new File("src/input/background.png"));
        } catch (IOException e) {
            backgroundImage = null;
            System.out.println("Background image not found in src/input/");
        }

        setLayout(new BorderLayout());

        //TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false); // Make it transparent
        topBar.setBorder(new EmptyBorder(20, 30, 20, 30));

        welcomeLabel = new JLabel("Welcome back");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 28));
        welcomeLabel.setForeground(COLOR_BEIGE);

        //stats container
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setOpaque(false);

        pointsLabel = createStatLabel("Points: 0");
        gamesLabel = createStatLabel("Active Games: 0");

        statsPanel.add(pointsLabel);
        statsPanel.add(Box.createHorizontalStrut(15)); // Space between labels
        statsPanel.add(gamesLabel);

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(statsPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        //CENTER BUTTONS
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Transparent so background shows

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);

        //button spacing
        addButton(buttonContainer, "START NEW GAME", e -> {
            Main.getInstance().startNewGame();
        });

        buttonContainer.add(Box.createVerticalStrut(25)); // Space

        addButton(buttonContainer, "MY GAMES", e -> {
            JOptionPane.showMessageDialog(this, "My Games Feature Coming Next!");
        });

        buttonContainer.add(Box.createVerticalStrut(25)); // Space

        addButton(buttonContainer, "LOGOUT", e -> {
            Main.getInstance().showScreen("LOGIN");
        });

        centerPanel.add(buttonContainer);
        add(centerPanel, BorderLayout.CENTER);
    }


    //updates the labels
    public void refreshStats() {
        User u = Main.getInstance().getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome, " + u.getEmail().split("@")[0]);
            pointsLabel.setText("Points: " + u.getPoints());
            gamesLabel.setText("Active Games: " + u.getActiveGames().size());
        }
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        MenuButton btn = new MenuButton(text);
        btn.addActionListener(action);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 60)); // Button size
        panel.add(btn);
    }

    private JLabel createStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(COLOR_BEIGE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 245, 220, 100), 1),
                new EmptyBorder(5, 15, 5, 15)
        ));
        return lbl;
    }

    //BACKGROUND
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(60, 40, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    //CUSTOM TRANSPARENT BUTTON
    private class MenuButton extends JButton {
        public MenuButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(COLOR_BEIGE);
            setFont(new Font("Serif", Font.BOLD, 22));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isRollover()) {
                g2.setColor(COLOR_HOVER);
            } else {
                g2.setColor(COLOR_BROWN_TRANSPARENT);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            g2.setColor(new Color(255, 255, 255, 50)); // Very subtle border
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}