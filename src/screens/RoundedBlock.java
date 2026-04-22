package screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class RoundedBlock extends JPanel {
    private final Color fillColor;
    private final Color borderColor;
    private final int borderWidth;
    private final int arc;

    public RoundedBlock(Color fillColor, Color borderColor, int borderWidth, int arc) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            int inset = Math.max(1, borderWidth / 2);
            g2.drawRoundRect(inset, inset, getWidth() - (inset * 2) - 1, getHeight() - (inset * 2) - 1, arc, arc);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
