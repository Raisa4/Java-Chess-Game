import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;
    private BufferedImage backgroundImage;

    private final Color COLOR_PRIMARY_GREEN = new Color(40, 85, 35);
    private final Color COLOR_DARK_BROWN = new Color(100, 55, 35);
    private final Color COLOR_BEIGE = new Color(245, 245, 220);
    private final Color COLOR_TRANSPARENT_BOX = new Color(30, 20, 10, 200);

    public LoginPanel() {
        try {
            backgroundImage = ImageIO.read(new File("src/input/background.png"));
        } catch (IOException e) {
            backgroundImage = null;
        }

        setLayout(new GridBagLayout());

        JPanel loginBox = new JPanel();
        loginBox.setLayout(new GridBagLayout());
        loginBox.setBackground(COLOR_TRANSPARENT_BOX);
        loginBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Chess Master");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_BEIGE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginBox.add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 2;
        loginBox.add(emailLabel, gbc);

        emailField = new JTextField(20);
        styleTextField(emailField);
        gbc.gridy = 2;
        loginBox.add(emailField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        loginBox.add(passLabel, gbc);

        passField = new JPasswordField(20);
        styleTextField(passField);
        gbc.gridy = 4;
        loginBox.add(passField, gbc);

        loginButton = new GlossyButton("LOGIN", COLOR_PRIMARY_GREEN, Color.WHITE);
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginBox.add(loginButton, gbc);

        registerButton = new GlossyButton("Create New Account", COLOR_DARK_BROWN, COLOR_BEIGE);
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 10, 10, 10);
        loginBox.add(registerButton, gbc);

        add(loginBox);

        setupActions();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(COLOR_DARK_BROWN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(COLOR_BEIGE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupActions() {
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            if (Main.getInstance().login(email, pass)) {
                Main.getInstance().showScreen("MENU");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) return;

            if (Main.getInstance().register(email, pass)) {
                JOptionPane.showMessageDialog(this, "Account Created!");
            } else {
                JOptionPane.showMessageDialog(this, "User already exists.");
            }
        });
    }

    private static class GlossyButton extends JButton {
        private Color baseColor;
        private Color textColor;

        public GlossyButton(String text, Color base, Color textCol) {
            super(text);
            this.baseColor = base;
            this.textColor = textCol;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int h = getHeight();
            int w = getWidth();

            g2.setColor(baseColor);
            g2.fillRoundRect(0, 0, w, h, 15, 15);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 100),
                    0, h / 2, new Color(255, 255, 255, 0)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 15, 15);

            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}