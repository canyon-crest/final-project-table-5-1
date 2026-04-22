package screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class RoundedButton extends JButton {
    private final Color fillColor;
    private final int cornerArc;
    private final Color borderColor;
    private final int borderWidth;
    private final boolean hoverShrinkEnabled;
    private boolean hovered;

    public RoundedButton(String text, Color fillColor, int cornerArc) {
        this(text, fillColor, cornerArc, null, 0, true);
    }

    public RoundedButton(String text, Color fillColor, int cornerArc, Color borderColor, int borderWidth,
            boolean hoverShrinkEnabled) {
        super(text);
        this.fillColor = fillColor;
        this.cornerArc = cornerArc;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.hoverShrinkEnabled = hoverShrinkEnabled;
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        float scale = hovered && hoverShrinkEnabled ? 0.95f : 1.0f;
        int drawWidth = Math.max(1, Math.round(getWidth() * scale));
        int drawHeight = Math.max(1, Math.round(getHeight() * scale));
        int drawX = (getWidth() - drawWidth) / 2;
        int drawY = (getHeight() - drawHeight) / 2;
        int arc = Math.max(4, Math.round(cornerArc * scale));

        g2.setColor(fillColor);
        g2.fillRoundRect(drawX, drawY, drawWidth, drawHeight, arc, arc);

        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            int inset = Math.max(1, borderWidth / 2);
            g2.drawRoundRect(drawX + inset, drawY + inset, drawWidth - (inset * 2) - 1,
                    drawHeight - (inset * 2) - 1, arc, arc);
        }

        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics();
        String[] lines = getText().split("\\n");
        int totalHeight = lines.length * metrics.getHeight();
        int startY = drawY + ((drawHeight - totalHeight) / 2) + metrics.getAscent();

        for (int i = 0; i < lines.length; i++) {
            int textX = drawX + (drawWidth - metrics.stringWidth(lines[i])) / 2;
            int textY = startY + (i * metrics.getHeight());
            g2.drawString(lines[i], textX, textY);
        }

        g2.dispose();
    }
}
