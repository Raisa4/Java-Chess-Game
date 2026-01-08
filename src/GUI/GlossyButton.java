package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GlossyButton extends JButton {
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
