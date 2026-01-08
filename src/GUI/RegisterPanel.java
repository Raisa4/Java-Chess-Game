package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.Main;

public class RegisterPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private BufferedImage backgroundImage;

    private final Color COLOR_PRIMARY_GREEN = new Color(40, 85, 35);
    private final Color COLOR_DARK_BROWN = new Color(100, 55, 35);
    private final Color COLOR_BEIGE = new Color(245, 245, 220);
    private final Color COLOR_TRANSPARENT_BOX = new Color(30, 20, 10, 200);

    public RegisterPanel() {
        try {
            backgroundImage = ImageIO.read(new File("src/input/background.png"));
        } catch (IOException e) {
            backgroundImage = null;
        }

        setLayout(new GridBagLayout());

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(COLOR_TRANSPARENT_BOX);
        box.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //title
        JLabel titleLabel = new JLabel("Join Chess Master");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_BEIGE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        box.add(titleLabel, gbc);

        //email
        gbc.gridy++; gbc.gridwidth = 2;
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setForeground(Color.WHITE);
        box.add(emailLbl, gbc);

        emailField = new JTextField(20);
        styleTextField(emailField);
        gbc.gridy++;
        box.add(emailField, gbc);

        //password
        gbc.gridy++;
        JLabel passLbl = new JLabel("Password");
        passLbl.setForeground(Color.WHITE);
        box.add(passLbl, gbc);

        passField = new JPasswordField(20);
        styleTextField(passField);
        gbc.gridy++;
        box.add(passField, gbc);

        //confirm password
        gbc.gridy++;
        JLabel confirmLbl = new JLabel("Confirm Password");
        confirmLbl.setForeground(Color.WHITE);
        box.add(confirmLbl, gbc);

        confirmPassField = new JPasswordField(20);
        styleTextField(confirmPassField);
        gbc.gridy++;
        box.add(confirmPassField, gbc);

        //register btn
        JButton registerBtn = new GlossyButton("CREATE ACCOUNT", COLOR_PRIMARY_GREEN, Color.WHITE);
        gbc.gridy++; gbc.insets = new Insets(20, 10, 5, 10);
        box.add(registerBtn, gbc);

        //back btn
        JButton backBtn = new GlossyButton("Back to Login", COLOR_DARK_BROWN, COLOR_BEIGE);
        gbc.gridy++; gbc.insets = new Insets(5, 10, 10, 10);
        box.add(backBtn, gbc);

        add(box);

        registerBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmPassField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            if (Main.getInstance().register(email, pass)) {
                JOptionPane.showMessageDialog(this, "Account created successfully! Please login.");
                Main.getInstance().showScreen("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "An account with this email already exists.");
            }
        });

        backBtn.addActionListener(e -> Main.getInstance().showScreen("LOGIN"));
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
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private static class GlossyButton extends JButton {
        private Color baseColor, textColor;
        public GlossyButton(String text, Color base, Color textCol) {
            super(text);
            this.baseColor = base; this.textColor = textCol;
            setContentAreaFilled(false); setFocusPainted(false);
            setBorderPainted(false); setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
