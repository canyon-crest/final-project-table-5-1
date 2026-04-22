package screens;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

public class StartMenuPanel extends JPanel {
    private static final int BASE_WIDTH = 700;
    private static final int BASE_HEIGHT = 470;

    private final JButton easyButton;
    private final JButton mediumButton;
    private final JButton hardButton;
    private final JButton helpButton;
    private final Consumer<String> selectionHandler;

    public StartMenuPanel(Consumer<String> selectionHandler) {
        this.selectionHandler = selectionHandler;

        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setLayout(null);
        setBackground(new Color(232, 232, 232));

        easyButton = createButton("Easy", new Color(219, 193, 172));
        mediumButton = createButton("Medium", new Color(151, 115, 89));
        hardButton = createButton("Hard", new Color(99, 72, 50));
        helpButton = createButton("Help", new Color(56, 34, 15));

        easyButton.addActionListener(e -> selectionHandler.accept("Easy"));
        mediumButton.addActionListener(e -> selectionHandler.accept("Medium"));
        hardButton.addActionListener(e -> selectionHandler.accept("Hard"));

        add(easyButton);
        add(mediumButton);
        add(hardButton);
        add(helpButton);
    }

    private JButton createButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text, color, 28);
        button.setFont(new Font("Cantarell", Font.BOLD, 28));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateScaledLayout();
    }

    private void updateScaledLayout() {
        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float scaleX = width / (float) BASE_WIDTH;
        float scaleY = height / (float) BASE_HEIGHT;
        float fontScale = Math.min(scaleX, scaleY);

        easyButton.setBounds(scaleRect(183, 173, 313, 58, scaleX, scaleY));
        mediumButton.setBounds(scaleRect(183, 247, 313, 58, scaleX, scaleY));
        hardButton.setBounds(scaleRect(183, 321, 313, 58, scaleX, scaleY));
        helpButton.setBounds(scaleRect(15, 390, 120, 58, scaleX, scaleY));

        int buttonFontSize = Math.max(12, Math.round(28 * fontScale));
        Font buttonFont = new Font("SansSerif", Font.BOLD, buttonFontSize);
        easyButton.setFont(buttonFont);
        mediumButton.setFont(buttonFont);
        hardButton.setFont(buttonFont);
        helpButton.setFont(buttonFont);
    }

    private Rectangle scaleRect(int x, int y, int w, int h, float sx, float sy) {
        return new Rectangle(Math.round(x * sx), Math.round(y * sy), Math.round(w * sx), Math.round(h * sy));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float scaleX = width / (float) BASE_WIDTH;
        float scaleY = height / (float) BASE_HEIGHT;
        float fontScale = Math.min(scaleX, scaleY);

        int bannerX = Math.round(80 * scaleX);
        int bannerY = Math.round(26 * scaleY);
        int bannerW = Math.round(540 * scaleX);
        int bannerH = Math.round(105 * scaleY);
        int bannerArc = Math.max(8, Math.round(24 * fontScale));

        g2.setColor(new Color(111, 78, 55));
        g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, bannerArc, bannerArc);

        g2.setColor(Color.WHITE);
        int titleFontSize = Math.max(14, Math.round(38 * fontScale));
        g2.setFont(new Font("SansSerif", Font.BOLD, titleFontSize));
        String title = "Barista Simulation";
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (width - metrics.stringWidth(title)) / 2;
        int textY = bannerY + ((bannerH - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(title, textX, textY);

        g2.dispose();
    }
}
