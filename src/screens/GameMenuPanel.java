package screens;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.util.ArrayList;

public class GameMenuPanel extends javax.swing.JPanel {
    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 800;

    private static final Color BACKGROUND = new Color(236, 236, 236);
    private static final Color SOFT_BEIGE = new Color(214, 207, 194);
    private static final Color TAN = new Color(190, 149, 101);
    private static final Color MID_BROWN = new Color(151, 120, 92);
    private static final Color DARK_BROWN = new Color(118, 89, 63);
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
    private final Timer gameTimer;

    private int currentDay = 1;
    private int totalDays = 14;
    private long currentMoneyCents = 0;
    private int elapsedMinutes = 0;
    private int elapsedSeconds = 0;
    private double currentRating = 0.0;
    private double shopCleanlinessRating = 10.0;
    private String difficulty = "Easy";

    public GameMenuPanel() {
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
        for (int i = 0; i < 12; i++) drinkMenuItems.add("Drink - Price");

        menuListLabel = createLabel("", Color.BLACK, 20, true, SwingConstants.CENTER);
        menuListBlock.add(menuListLabel, BorderLayout.CENTER);

        gameTimer = new Timer(1000, e -> incrementElapsedTime());
        gameTimer.setInitialDelay(1000);

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

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        refreshStatLabels();
    }

    public void startGameTimer() {
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
    }

    public void stopGameTimer() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    public void resetGameTimer() {
        stopGameTimer();
        elapsedMinutes = 0;
        elapsedSeconds = 0;
        refreshStatLabels();
    }

    private void incrementElapsedTime() {
        elapsedSeconds++;
        if (elapsedSeconds >= 60) {
            elapsedSeconds = 0;
            elapsedMinutes++;
        }
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
            if (menuText.length() > 0) menuText.append('\n');
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
