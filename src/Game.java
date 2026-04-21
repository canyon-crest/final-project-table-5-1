import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Game {
    private static final String START_CARD = "start";
    private static final String GAME_CARD = "game";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("My Cafe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel root = new JPanel(cardLayout);
        root.setPreferredSize(new Dimension(1200, 800));

        GameMenuPanel gamePanel = new GameMenuPanel();
        StartMenuPanel startPanel = new StartMenuPanel(difficulty -> {
            gamePanel.setDifficulty(difficulty);
            cardLayout.show(root, GAME_CARD);
        });

        root.add(startPanel, START_CARD);
        root.add(gamePanel, GAME_CARD);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}

interface DifficultySelectionHandler {
    void onSelect(String difficulty);
}

class StartMenuPanel extends JPanel {
    private static final int BASE_WIDTH = 700;
    private static final int BASE_HEIGHT = 470;

    private final JButton easyButton;
    private final JButton mediumButton;
    private final JButton hardButton;
    private final JButton helpButton;
    private final DifficultySelectionHandler selectionHandler;

    StartMenuPanel(DifficultySelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;

        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setLayout(null);
        setBackground(new Color(232, 232, 232));

        easyButton = createButton("Easy", new Color(219, 193, 172));
        mediumButton = createButton("Medium", new Color(151, 115, 89));
        hardButton = createButton("Hard", new Color(99, 72, 50));
        helpButton = createButton("Help", new Color(56, 34, 15));

        easyButton.addActionListener(e -> selectionHandler.onSelect("Easy"));
        mediumButton.addActionListener(e -> selectionHandler.onSelect("Medium"));
        hardButton.addActionListener(e -> selectionHandler.onSelect("Hard"));

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

class GameMenuPanel extends JPanel {
    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 800;

    private static final Color BACKGROUND = new Color(236, 236, 236);
    private static final Color SOFT_BEIGE = new Color(214, 207, 194);
    private static final Color TAN = new Color(190, 149, 101);
    private static final Color MID_BROWN = new Color(151, 120, 92);
    private static final Color DARK_BROWN = new Color(118, 89, 63);
    private static final Color BORDER_BROWN = new Color(120, 90, 64);
    private static final Color BORDER_TAN = new Color(223, 193, 140);

    private final RoundedBlock openBlock;
    private final JLabel openLabel;

    private final RoundedBlock dayBlock;
    private final RoundedBlock moneyBlock;
    private final RoundedBlock timeBlock;
    private final RoundedBlock ratingBlock;
    private final RoundedBlock cleanlinessBlock;

    private final JLabel dayLabel;
    private final JLabel moneyLabel;
    private final JLabel timeLabel;
    private final JLabel ratingLabel;
    private final JLabel cleanlinessLabel;

    private final RoundedBlock actionsHeader;
    private final RoundedBlock customersHeader;
    private final RoundedBlock menuHeader;

    private final JButton checkInventoryButton;
    private final JButton cleanShopButton;
    private final JButton closeEarlyButton;
    private final JButton pauseGameButton;
    private final JButton helpButton;

    private final CustomerCard customerOne;
    private final CustomerCard customerTwo;
    private final CustomerCard customerThree;

    private final RoundedBlock menuListBlock;
    private final JLabel menuListLabel;
    private final ArrayList<String> drinkMenuItems = new ArrayList<>();

    private int currentDay = 1;
    private int totalDays = 14;
    private long currentMoneyCents = 0;
    private int elapsedMinutes = 0;
    private int elapsedSeconds = 0;
    private double currentRating = 0.0;
    private double shopCleanlinessRating = 10.0;
    private String difficulty = "Easy";

    GameMenuPanel() {
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setLayout(null);
        setBackground(BACKGROUND);

        openBlock = new RoundedBlock(SOFT_BEIGE, null, 0, 42);
        openBlock.setLayout(new GridBagLayout());
        openLabel = createLabel("OPEN", Color.BLACK, 64, true, SwingConstants.CENTER);
        openBlock.add(openLabel);

        dayBlock = createStatBlock();
        moneyBlock = createStatBlock();
        timeBlock = createStatBlock();
        ratingBlock = createStatBlock();
        cleanlinessBlock = new RoundedBlock(DARK_BROWN, null, 0, 40);

        dayLabel = createLabel("", Color.WHITE, 22, true, SwingConstants.LEFT);
        moneyLabel = createLabel("", Color.WHITE, 22, true, SwingConstants.LEFT);
        timeLabel = createLabel("", Color.WHITE, 22, true, SwingConstants.LEFT);
        ratingLabel = createLabel("", Color.WHITE, 22, true, SwingConstants.LEFT);
        cleanlinessLabel = createLabel("", Color.WHITE, 19, true, SwingConstants.CENTER);

        dayBlock.setLayout(new BorderLayout());
        moneyBlock.setLayout(new BorderLayout());
        timeBlock.setLayout(new BorderLayout());
        ratingBlock.setLayout(new BorderLayout());
        cleanlinessBlock.setLayout(new BorderLayout());

        dayBlock.add(dayLabel, BorderLayout.CENTER);
        moneyBlock.add(moneyLabel, BorderLayout.CENTER);
        timeBlock.add(timeLabel, BorderLayout.CENTER);
        ratingBlock.add(ratingLabel, BorderLayout.CENTER);
        cleanlinessBlock.add(cleanlinessLabel, BorderLayout.CENTER);

        actionsHeader = createHeaderBlock("ACTIONS");
        customersHeader = createHeaderBlock("CUSTOMERS");
        menuHeader = createHeaderBlock("MENU");

        checkInventoryButton = createActionButton("CHECK\nINVENTORY");
        cleanShopButton = createActionButton("CLEAN SHOP");
        closeEarlyButton = createActionButton("CLOSE EARLY");
        pauseGameButton = createActionButton("PAUSE GAME");
        helpButton = createActionButton("HELP");

        customerOne = new CustomerCard();
        customerTwo = new CustomerCard();
        customerThree = new CustomerCard();

        menuListBlock = new RoundedBlock(Color.WHITE, BORDER_TAN, 6, 28);
        menuListBlock.setLayout(new BorderLayout());
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");
        drinkMenuItems.add("Drink - Price");

        menuListLabel = createLabel("", Color.BLACK, 20, true, SwingConstants.CENTER);
        menuListBlock.add(menuListLabel, BorderLayout.CENTER);

        add(openBlock);
        add(dayBlock);
        add(moneyBlock);
        add(timeBlock);
        add(ratingBlock);
        add(cleanlinessBlock);
        add(actionsHeader);
        add(customersHeader);
        add(menuHeader);

        add(checkInventoryButton);
        add(cleanShopButton);
        add(closeEarlyButton);
        add(pauseGameButton);
        add(helpButton);

        add(customerOne);
        add(customerTwo);
        add(customerThree);

        add(menuListBlock);

        refreshStatLabels();
        refreshMenuListLabel();
    }

    void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        refreshStatLabels();
    }

    private void refreshStatLabels() {
        setLabelText(dayLabel, String.format("Day %02d/%02d", currentDay, totalDays));
        setLabelText(moneyLabel, String.format("$ %,d.%02d", currentMoneyCents / 100, currentMoneyCents % 100));
        setLabelText(timeLabel, String.format("Time: %d M %02d S", elapsedMinutes, elapsedSeconds));
        setLabelText(ratingLabel, String.format("Rating: %.1f/10 (%s)", currentRating, difficulty));
        setLabelText(cleanlinessLabel, String.format("Shop Cleanliness:\n%.1f/10", shopCleanlinessRating));
    }

    private void setLabelText(JLabel label, String text) {
        label.setText(toHtml(text));
    }

    private void refreshMenuListLabel() {
        StringBuilder menuText = new StringBuilder();
        for (String item : drinkMenuItems) {
            if (menuText.length() > 0) {
                menuText.append('\n');
            }
            menuText.append("• ").append(item);
        }
        setLabelText(menuListLabel, menuText.toString());
    }

    private RoundedBlock createStatBlock() {
        return new RoundedBlock(MID_BROWN, null, 0, 34);
    }

    private RoundedBlock createHeaderBlock(String text) {
        RoundedBlock block = new RoundedBlock(TAN, null, 0, 36);
        block.setLayout(new GridBagLayout());
        block.add(createLabel(text, Color.BLACK, 28, true, SwingConstants.CENTER));
        return block;
    }

    private JButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(text, Color.WHITE, 34, BORDER_TAN, 6, true);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    private JLabel createLabel(String text, Color color, int size, boolean bold, int horizontalAlignment) {
        JLabel label = new JLabel(toHtml(text));
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setHorizontalAlignment(horizontalAlignment);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private String toHtml(String text) {
        return "<html>" + text.replace("\n", "<br>") + "</html>";
    }

    @Override
    public void doLayout() {
        super.doLayout();
        layoutScaled();
    }

    private void layoutScaled() {
        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float sx = width / (float) BASE_WIDTH;
        float sy = height / (float) BASE_HEIGHT;
        float fontScale = Math.min(sx, sy);

        openBlock.setBounds(scaleRect(15, 15, 300, 140, sx, sy));

        dayBlock.setBounds(scaleRect(350, 25, 230, 58, sx, sy));
        moneyBlock.setBounds(scaleRect(595, 25, 260, 58, sx, sy));
        timeBlock.setBounds(scaleRect(350, 95, 230, 58, sx, sy));
        ratingBlock.setBounds(scaleRect(595, 95, 260, 58, sx, sy));
        cleanlinessBlock.setBounds(scaleRect(885, 25, 300, 128, sx, sy));

        actionsHeader.setBounds(scaleRect(15, 178, 300, 86, sx, sy));
        customersHeader.setBounds(scaleRect(350, 182, 505, 84, sx, sy));
        menuHeader.setBounds(scaleRect(885, 182, 300, 84, sx, sy));

        checkInventoryButton.setBounds(scaleRect(15, 286, 300, 100, sx, sy));
        cleanShopButton.setBounds(scaleRect(15, 408, 300, 86, sx, sy));
        closeEarlyButton.setBounds(scaleRect(15, 510, 300, 86, sx, sy));
        pauseGameButton.setBounds(scaleRect(15, 612, 300, 86, sx, sy));
        helpButton.setBounds(scaleRect(15, 714, 300, 72, sx, sy));

        customerOne.setBounds(scaleRect(350, 294, 505, 148, sx, sy));
        customerTwo.setBounds(scaleRect(350, 472, 505, 148, sx, sy));
        customerThree.setBounds(scaleRect(350, 650, 505, 148, sx, sy));

        menuListBlock.setBounds(scaleRect(885, 286, 300, 512, sx, sy));

        openLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(18, Math.round(64 * fontScale))));
        dayLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(22 * fontScale))));
        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(22 * fontScale))));
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(22 * fontScale))));
        ratingLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(22 * fontScale))));
        cleanlinessLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(11, Math.round(24 * fontScale))));

        Font actionFont = new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(24 * fontScale)));
        checkInventoryButton.setFont(actionFont);
        cleanShopButton.setFont(actionFont);
        closeEarlyButton.setFont(actionFont);
        pauseGameButton.setFont(actionFont);
        helpButton.setFont(actionFont);

        menuListLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(20 * fontScale))));
    }

    private Rectangle scaleRect(int x, int y, int w, int h, float sx, float sy) {
        return new Rectangle(Math.round(x * sx), Math.round(y * sy), Math.round(w * sx), Math.round(h * sy));
    }
}

class CustomerCard extends RoundedBlock {
    private static final Color CARD_BORDER = new Color(120, 90, 64);
    private static final Color MAKE_FILL = new Color(118, 89, 63);

    private final JLabel infoLabel;
    private final JButton makeButton;

    CustomerCard() {
        super(Color.WHITE, CARD_BORDER, 6, 40);
        setLayout(null);

        infoLabel = new JLabel("<html>[NAME]<br>[ARRIVAL TIME]<br>[DRINK ORDER]</html>");
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);

        makeButton = new RoundedButton("MAKE\n->", MAKE_FILL, 50, new Color(75, 50, 30), 5, true);
        makeButton.setForeground(Color.BLACK);
        makeButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        makeButton.setFocusPainted(false);
        makeButton.setBorderPainted(false);
        makeButton.setContentAreaFilled(false);
        makeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(infoLabel);
        add(makeButton);
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float sx = width / 505f;
        float sy = height / 148f;
        float fontScale = Math.min(sx, sy);

        infoLabel.setBounds(Math.round(24 * sx), Math.round(20 * sy), Math.round(270 * sx), Math.round(108 * sy));
        makeButton.setBounds(Math.round(320 * sx), Math.round(19 * sy), Math.round(150 * sx), Math.round(110 * sy));

        infoLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(30 * fontScale))));
        makeButton.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(28 * fontScale))));
    }
}

class RoundedBlock extends JPanel {
    private final Color fillColor;
    private final Color borderColor;
    private final int borderWidth;
    private final int arc;

    RoundedBlock(Color fillColor, Color borderColor, int borderWidth, int arc) {
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

class RoundedButton extends JButton {
    private final Color fillColor;
    private final int cornerArc;
    private final Color borderColor;
    private final int borderWidth;
    private final boolean hoverShrinkEnabled;
    private boolean hovered;

    RoundedButton(String text, Color fillColor, int cornerArc) {
        this(text, fillColor, cornerArc, null, 0, true);
    }

    RoundedButton(String text, Color fillColor, int cornerArc, Color borderColor, int borderWidth, boolean hoverShrinkEnabled) {
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
            g2.drawRoundRect(drawX + inset, drawY + inset, drawWidth - (inset * 2) - 1, drawHeight - (inset * 2) - 1, arc, arc);
        }

        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics();
        String text = getText();
        String[] lines = text.split("\\n");
        int totalHeight = lines.length * metrics.getHeight();
        int startY = drawY + ((drawHeight - totalHeight) / 2) + metrics.getAscent();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int textX = drawX + (drawWidth - metrics.stringWidth(line)) / 2;
            int textY = startY + (i * metrics.getHeight());
            g2.drawString(line, textX, textY);
        }

        g2.dispose();
    }
}
