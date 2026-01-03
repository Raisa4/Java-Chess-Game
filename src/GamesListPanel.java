import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamesListPanel extends JPanel {
    private BufferedImage backgroundImage;
    private JPanel listContainer;
    private JScrollPane scrollPane;

    // --- THEME COLORS ---
    private final Color COLOR_BEIGE = new Color(245, 245, 220);
    private final Color COLOR_DARK_BROWN = new Color(100, 55, 35);
    private final Color COLOR_TRANSPARENT_BOX = new Color(60, 40, 20, 200);
    private final Color COLOR_GREEN = new Color(56, 142, 60);
    private final Color COLOR_RED = new Color(180, 40, 40);

    public GamesListPanel() {
        setLayout(new BorderLayout());

        try {
            backgroundImage = ImageIO.read(new File("src/input/background.png"));
        } catch (IOException e) {
            backgroundImage = null;
        }

        // 1. Title Bar
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Your Active Games");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_BEIGE);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // 2. The List Area
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        // Scroll Pane for the list
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(20, 50, 20, 50)); // Margins

        // Remove scroll bars visuals for cleaner look (optional)
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // 3. Back Button Area
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton backBtn = new JButton("Back to Menu");
        styleButton(backBtn, COLOR_DARK_BROWN);
        backBtn.addActionListener(e -> Main.getInstance().showScreen("MENU"));

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Called every time we switch to this screen
    public void refreshGameList() {
        listContainer.removeAll();
        User currentUser = Main.getInstance().getCurrentUser();

        if (currentUser == null || currentUser.getActiveGames().isEmpty()) {
            JLabel emptyLabel = new JLabel("No active games found.");
            emptyLabel.setForeground(COLOR_BEIGE);
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(emptyLabel);
        } else {
            for (Game g : currentUser.getActiveGames()) {
                listContainer.add(createGameRow(g));
                listContainer.add(Box.createVerticalStrut(10)); // Gap between items
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createGameRow(Game game) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(COLOR_TRANSPARENT_BOX);
        row.setBorder(new EmptyBorder(10, 20, 10, 20));
        row.setMaximumSize(new Dimension(600, 80)); // Fixed height per row

        // Game Info Text
        String vsInfo = "Game #" + game.getID() + " | vs Computer";
        // Assuming getting moves count is safe
        int movesCount = (game.getMoves() != null) ? game.getMoves().size() : 0;
        String subInfo = "Moves played: " + movesCount;

        JLabel textLabel = new JLabel("<html><b style='font-size:14px'>" + vsInfo + "</b><br>" + subInfo + "</html>");
        textLabel.setForeground(COLOR_BEIGE);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton resumeBtn = new JButton("Resume");
        styleButton(resumeBtn, COLOR_GREEN);
        resumeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        resumeBtn.addActionListener(e -> {
            Main.getInstance().resumeGame(game);
        });

        JButton deleteBtn = new JButton("Delete");
        styleButton(deleteBtn, COLOR_RED);
        deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Game #" + game.getID() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Main.getInstance().deleteGame(game);
                refreshGameList(); // Refresh list immediately
            }
        });

        btnPanel.add(resumeBtn);
        btnPanel.add(deleteBtn);

        row.add(textLabel, BorderLayout.CENTER);
        row.add(btnPanel, BorderLayout.EAST);

        return row;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

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
}
