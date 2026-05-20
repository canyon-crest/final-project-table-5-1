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

    private final CoffeeMakerPanel coffeeMaker;

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
    private CustomerCard pendingCard = null;

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

        coffeeMaker = new CoffeeMakerPanel(this::hideCoffeeMakerAndCheck, this::hideCoffeeMakerCancel);
        coffeeMaker.setVisible(false);

        customerOne   = new CustomerCard();
        customerTwo   = new CustomerCard();
        customerThree = new CustomerCard();

        customerOne  .setMakeAction(() -> showCoffeeMaker(customerOne));
        customerTwo  .setMakeAction(() -> showCoffeeMaker(customerTwo));
        customerThree.setMakeAction(() -> showCoffeeMaker(customerThree));

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
        add(coffeeMaker);

        refreshStatLabels();
        refreshMenuListLabel();
    }

    private void showCoffeeMaker(CustomerCard card) {
        if (!card.isActive()) return;
        pendingCard = card;
        coffeeMaker.reset();
        coffeeMaker.setVisible(true);
        coffeeMaker.revalidate();
        coffeeMaker.repaint();
        setComponentZOrder(coffeeMaker, 0);
    }

    private void hideCoffeeMakerAndCheck() {
        coffeeMaker.setVisible(false);
        if (pendingCard == null) return;
        CustomerCard card = pendingCard;
        pendingCard = null;

        if (!card.isActive()) {
            // customer left while coffee maker was open
            scheduleNewCustomer(card, 2000);
            return;
        }

        String madeCoffee             = coffeeMaker.getSelectedCoffee();
        java.util.Set<String> madeSyrups   = coffeeMaker.getSelectedSyrups();
        String madeMilk               = coffeeMaker.getSelectedMilk();
        java.util.Set<String> madeToppings = coffeeMaker.getSelectedToppings();

        if (card.matchesOrder(madeCoffee, madeSyrups, madeMilk, madeToppings)) {
            currentMoneyCents += Math.round(card.calcPrice() * 100);
            card.randomize(elapsedMinutes, elapsedSeconds);
        } else {
            currentRating = Math.max(0, currentRating - 0.5);
            card.markAbandoned("Wrong order...");
            scheduleNewCustomer(card, 2000);
        }
        refreshStatLabels();
    }

    private void hideCoffeeMakerCancel() {
        coffeeMaker.setVisible(false);
        pendingCard = null; // customer stays, patience keeps ticking
    }

    private void scheduleNewCustomer(CustomerCard card, int delayMs) {
        javax.swing.Timer t = new javax.swing.Timer(delayMs, e -> {
            card.randomize(elapsedMinutes, elapsedSeconds);
            ((javax.swing.Timer) e.getSource()).stop();
        });
        t.setRepeats(false);
        t.start();
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
        customerOne.randomize(0, 0);
        customerTwo.randomize(0, 0);
        customerThree.randomize(0, 0);
        refreshStatLabels();
    }

    private void incrementElapsedTime() {
        elapsedSeconds++;
        if (elapsedSeconds >= 60) {
            elapsedSeconds = 0;
            elapsedMinutes++;
        }
        tickPatience(customerOne);
        tickPatience(customerTwo);
        tickPatience(customerThree);
        refreshStatLabels();
    }

    private void tickPatience(CustomerCard card) {
        if (card.tick()) {
            currentRating = Math.max(0, currentRating - 0.5);
            if (card != pendingCard) {
                scheduleNewCustomer(card, 3000);
            }
        }
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

        coffeeMaker.setBounds(0, 0, width, height);
    }

    private Rectangle scaleRect(int x, int y, int w, int h, float sx, float sy) {
        return new Rectangle(Math.round(x * sx), Math.round(y * sy), Math.round(w * sx), Math.round(h * sy));
    }
}

class CustomerCard extends RoundedBlock {
    private static final Color CARD_BORDER = new Color(120, 90, 64);
    private static final Color MAKE_FILL   = new Color(118, 89, 63);

    // Must match CoffeeMakerPanel arrays exactly (including \n in toppings)
    private static final String[] NAMES    = {
        "Alice", "Bob", "Carol", "Dan", "Eve", "Frank",
        "Grace", "Henry", "Iris", "Jack", "Karen", "Leo",
        "Mia", "Noah", "Olivia", "Paul", "Quinn", "Rose"
    };
    private static final String[] COFFEES  = {"Cold Brew", "Espresso", "Drip"};
    private static final String[] SYRUPS   = {"Vanilla", "Caramel", "Hazelnut", "Lavender"};
    private static final String[] MILKS    = {"Regular", "Oat", "Almond", "Soy"};
    private static final String[] TOPPINGS = {"Whipped\nCream", "Cinnamon", "Sea Salt", "Cold\nFoam"};

    private static final double BASE_COLD_BREW    = 5.50;
    private static final double BASE_ESPRESSO     = 4.00;
    private static final double BASE_DRIP         = 3.50;
    private static final double SYRUP_COST        = 0.75;
    private static final double SPECIAL_MILK_COST = 1.00;
    private static final double TOPPING_COST      = 0.50;

    private static final java.util.Random RNG = new java.util.Random();

    private final JLabel  infoLabel;
    private final JButton makeButton;

    // Current order
    private String customerName = "";
    private String orderCoffee  = null;
    private final java.util.List<String> orderSyrups   = new java.util.ArrayList<>();
    private String orderMilk    = null;
    private final java.util.List<String> orderToppings = new java.util.ArrayList<>();
    private double maxPatience     = 90;
    private double currentPatience = 0;
    private boolean active         = false;

    CustomerCard() {
        super(Color.WHITE, CARD_BORDER, 6, 40);
        setLayout(null);

        infoLabel = new JLabel("<html>— no customer —</html>");
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setVerticalAlignment(SwingConstants.TOP);

        makeButton = new RoundedButton("MAKE\n->", MAKE_FILL, 50, new Color(75, 50, 30), 5, true);
        makeButton.setForeground(Color.BLACK);
        makeButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        makeButton.setFocusPainted(false);
        makeButton.setBorderPainted(false);
        makeButton.setContentAreaFilled(false);
        makeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        makeButton.setEnabled(false);

        add(infoLabel);
        add(makeButton);
    }

    // ── Order generation ──────────────────────────────────────────────────────

    void randomize(int arrivalMin, int arrivalSec) {
        customerName = NAMES[RNG.nextInt(NAMES.length)];
        orderCoffee  = COFFEES[RNG.nextInt(COFFEES.length)];
        orderSyrups.clear();
        if (RNG.nextBoolean()) orderSyrups.add(SYRUPS[RNG.nextInt(SYRUPS.length)]);
        orderMilk = MILKS[RNG.nextInt(MILKS.length)];
        orderToppings.clear();
        if (RNG.nextInt(3) == 0) orderToppings.add(TOPPINGS[RNG.nextInt(TOPPINGS.length)]);
        maxPatience     = 60 + RNG.nextInt(60); // 60–120 seconds
        currentPatience = maxPatience;
        active = true;
        makeButton.setEnabled(true);
        refreshInfo(arrivalMin, arrivalSec);
        repaint();
    }

    void clear() {
        active = false;
        orderCoffee = null;
        orderSyrups.clear();
        orderMilk = null;
        orderToppings.clear();
        makeButton.setEnabled(false);
        infoLabel.setText("<html>— no customer —</html>");
        repaint();
    }

    private void refreshInfo(int arrivalMin, int arrivalSec) {
        String coffeeStr = orderCoffee;
        if (orderMilk != null && !"Regular".equals(orderMilk))
            coffeeStr += " + " + orderMilk + " milk";
        StringBuilder extras = new StringBuilder();
        for (String s : orderSyrups) {
            if (extras.length() > 0) extras.append(", ");
            extras.append(s);
        }
        for (String t : orderToppings) {
            if (extras.length() > 0) extras.append(", ");
            extras.append(t.replace("\n", " "));
        }
        String extLine = extras.length() > 0 ? extras + "<br>" : "";
        infoLabel.setText(String.format(
            "<html><b>%s</b> (%dM %02dS)<br>%s<br>%s$%.2f</html>",
            customerName, arrivalMin, arrivalSec, coffeeStr, extLine, calcPrice()));
    }

    // ── Patience ──────────────────────────────────────────────────────────────

    /** Called every second. Returns true if customer just left. */
    boolean tick() {
        if (!active) return false;
        currentPatience = Math.max(0, currentPatience - 1);
        repaint();
        if (currentPatience <= 0) {
            active = false;
            makeButton.setEnabled(false);
            infoLabel.setText("<html><i>Left without ordering...</i></html>");
            return true;
        }
        return false;
    }

    boolean isActive() { return active; }

    // ── Order checking ────────────────────────────────────────────────────────

    boolean matchesOrder(String coffee, java.util.Set<String> syrups,
                         String milk, java.util.Set<String> toppings) {
        if (!active || orderCoffee == null) return false;
        return java.util.Objects.equals(orderCoffee, coffee)
            && new java.util.HashSet<>(orderSyrups)
                   .equals(syrups   != null ? syrups   : new java.util.HashSet<>())
            && java.util.Objects.equals(orderMilk, milk)
            && new java.util.HashSet<>(orderToppings)
                   .equals(toppings != null ? toppings : new java.util.HashSet<>());
    }

    double calcPrice() {
        if (orderCoffee == null) return 0;
        double base;
        if      ("Cold Brew".equals(orderCoffee)) base = BASE_COLD_BREW;
        else if ("Espresso" .equals(orderCoffee)) base = BASE_ESPRESSO;
        else                                      base = BASE_DRIP;
        base += orderSyrups.size()   * SYRUP_COST;
        if (orderMilk != null && !"Regular".equals(orderMilk)) base += SPECIAL_MILK_COST;
        base += orderToppings.size() * TOPPING_COST;
        return base;
    }

    void markAbandoned(String reason) {
        active = false;
        makeButton.setEnabled(false);
        infoLabel.setText("<html><i>" + reason + "</i></html>");
        repaint();
    }

    void setMakeAction(Runnable action) {
        for (java.awt.event.ActionListener l : makeButton.getActionListeners())
            makeButton.removeActionListener(l);
        makeButton.addActionListener(e -> action.run());
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (!active || maxPatience <= 0) return;

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        double ratio = currentPatience / maxPatience;
        Color barColor = ratio > 0.5  ? new Color(76, 175, 80)
                       : ratio > 0.25 ? new Color(255, 193, 7)
                       : new Color(244, 67, 54);

        int barH = Math.max(5, getHeight() / 16);
        int barX = 12;
        int barW = getWidth() - 24;
        int barY = getHeight() - barH - 5;

        g2.setColor(new Color(210, 210, 210));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);
        g2.setColor(barColor);
        g2.fillRoundRect(barX, barY, (int) (barW * ratio), barH, barH, barH);
        g2.dispose();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();

        int width  = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float sx = width  / 505f;
        float sy = height / 148f;
        float fontScale = Math.min(sx, sy);

        // Leave bottom 16px for patience bar
        infoLabel.setBounds(Math.round(12 * sx), Math.round(8  * sy),
                            Math.round(278 * sx), Math.round(118 * sy));
        makeButton.setBounds(Math.round(308 * sx), Math.round(14 * sy),
                             Math.round(175 * sx), Math.round(110 * sy));

        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, Math.max(9, Math.round(14 * fontScale))));
        makeButton.setFont(new Font("SansSerif", Font.BOLD,  Math.max(9, Math.round(22 * fontScale))));
    }
}
