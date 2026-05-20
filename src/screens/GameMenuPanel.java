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
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GameMenuPanel extends javax.swing.JPanel {
    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 800;
    private static final int OPEN_DURATION_SECONDS = 6 * 60;
    private static final int CLOSED_DURATION_SECONDS = 4 * 60;
    private static final long STARTING_MONEY_CENTS = 5000;
    private static final long RESTOCK_COST_CENTS = 2000;
    private static final int RESTOCK_TIME_SECONDS = 45;
    private static final int CLEAN_TIME_SECONDS = 30;

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
    private final LinkedHashMap<String, DrinkRule> drinkRules = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> inventory = new LinkedHashMap<>();

    private int currentDay = 1;
    private int totalDays = 14;
    private long currentMoneyCents = STARTING_MONEY_CENTS;
    private long totalProfitCents = 0;
    private long targetProfitCents = 12500;
    private int elapsedMinutes = 0;
    private int elapsedSeconds = 0;
    private double currentRating = 10.0;
    private double shopCleanlinessRating = 10.0;
    private String difficulty = "Easy";
    private CustomerCard pendingCard = null;
    private boolean openPhase = true;
    private boolean paused = false;
    private boolean gameEnded = false;
    private int reviewCount = 0;

    public GameMenuPanel() {
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setLayout(null);
        setBackground(BACKGROUND);
        setupDrinkRules();

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

        coffeeMaker = new CoffeeMakerPanel(this::finishCoffeeMaker, this::cancelCoffeeMaker);
        coffeeMaker.setVisible(false);

        customerOne   = new CustomerCard();
        customerTwo   = new CustomerCard();
        customerThree = new CustomerCard();

        customerOne  .setMakeAction(() -> showCoffeeMaker(customerOne));
        customerTwo  .setMakeAction(() -> showCoffeeMaker(customerTwo));
        customerThree.setMakeAction(() -> showCoffeeMaker(customerThree));

        menuListBlock = new RoundedBlock(Color.WHITE, BORDER_TAN, 6, 28);
        menuListBlock.setLayout(new BorderLayout());
        for (DrinkRule rule : drinkRules.values()) {
            drinkMenuItems.add(rule.name + " - " + formatMoney(rule.priceCents) + "\n  " + rule.getShortGuideText());
        }

        menuListLabel = createLabel("", Color.BLACK, 20, true, SwingConstants.CENTER);
        menuListBlock.add(menuListLabel, BorderLayout.CENTER);

        gameTimer = new Timer(1000, e -> incrementElapsedTime());
        gameTimer.setInitialDelay(1000);
        checkInventoryButton.addActionListener(e -> handleInventoryAction());
        cleanShopButton.addActionListener(e -> cleanShop());
        closeEarlyButton.addActionListener(e -> handleCloseOrNextDay());
        pauseGameButton.addActionListener(e -> togglePause());
        helpButton.addActionListener(e -> showHelp());

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

        resetGameTimer();
        refreshMenuListLabel();
    }

    private void showCoffeeMaker(CustomerCard card) {
        if(!openPhase || gameEnded || !card.isActive()) {
            return;
        }
        pendingCard = card;
        coffeeMaker.reset();
        DrinkRule rule = drinkRules.get(card.getDrinkOrder());
        if(rule != null) {
            coffeeMaker.setOrderGuide(rule.name, rule.getGuideText());
        }
        coffeeMaker.setVisible(true);
        coffeeMaker.revalidate();
        coffeeMaker.repaint();
        setComponentZOrder(coffeeMaker, 0);
    }

    private void cancelCoffeeMaker() {
        coffeeMaker.setVisible(false);
        pendingCard = null;
    }

    private void finishCoffeeMaker() {
        if(pendingCard == null || !pendingCard.isActive()) {
            cancelCoffeeMaker();
            return;
        }

        DrinkRule rule = drinkRules.get(pendingCard.getDrinkOrder());
        if(rule == null) {
            JOptionPane.showMessageDialog(this, "This order is not on the menu yet.");
            cancelCoffeeMaker();
            return;
        }

        if(coffeeMaker.getSelectedCoffee() == null) {
            JOptionPane.showMessageDialog(this, "Choose a coffee type before finishing the drink.");
            return;
        }

        if(!rule.matches(coffeeMaker)) {
            recordServiceResult(pendingCard, rule, false);
            cancelCoffeeMaker();
            return;
        }

        if(!hasInventoryFor(rule)) {
            JOptionPane.showMessageDialog(this,
                    "Not enough inventory for " + rule.name + ". Restock after closing.");
            return;
        }

        consumeInventoryFor(rule);
        recordServiceResult(pendingCard, rule, true);
        cancelCoffeeMaker();
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        if("Medium".equals(difficulty)) {
            targetProfitCents = 17500;
        }
        else if("Hard".equals(difficulty)) {
            targetProfitCents = 25000;
        }
        else {
            targetProfitCents = 12500;
        }
        refreshStatLabels();
    }

    public void startGameTimer() {
        if (!gameTimer.isRunning() && !gameEnded && !paused) {
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
        currentDay = 1;
        currentMoneyCents = STARTING_MONEY_CENTS;
        totalProfitCents = 0;
        currentRating = 10.0;
        shopCleanlinessRating = 10.0;
        openPhase = true;
        paused = false;
        gameEnded = false;
        reviewCount = 0;
        elapsedMinutes = 0;
        elapsedSeconds = 0;
        openLabel.setText("OPEN");
        pauseGameButton.setText("PAUSE GAME");
        closeEarlyButton.setText("CLOSE EARLY");
        cleanShopButton.setEnabled(false);
        checkInventoryButton.setText("CHECK\nINVENTORY");
        prepareStartingInventory();
        customerOne.randomize(0, 0);
        customerTwo.randomize(0, 0);
        customerThree.randomize(0, 0);
        refreshStatLabels();
    }

    private void incrementElapsedTime() {
        if(gameEnded) {
            stopGameTimer();
            return;
        }
        elapsedSeconds++;
        if (elapsedSeconds >= 60) {
            elapsedSeconds = 0;
            elapsedMinutes++;
        }
        if(openPhase) {
            shopCleanlinessRating = Math.max(0, shopCleanlinessRating - 0.01);
            removeExpiredCustomers();
            if(getElapsedPhaseSeconds() % 20 == 0) {
                fillEmptyCustomerSlots();
            }
            if(getElapsedPhaseSeconds() >= OPEN_DURATION_SECONDS) {
                beginClosingPhase();
            }
        }
        else if(getElapsedPhaseSeconds() >= CLOSED_DURATION_SECONDS) {
            startNextDay();
        }
        refreshStatLabels();
    }

    private void refreshStatLabels() {
        setLabelText(dayLabel, String.format("Day %02d/%02d", currentDay, totalDays));
        setLabelText(moneyLabel, "Cash: " + formatMoney(currentMoneyCents));
        int remainingSeconds = Math.max(0, getPhaseDurationSeconds() - getElapsedPhaseSeconds());
        setLabelText(timeLabel, String.format("%s: %d M %02d S",
                openPhase ? "Open" : "Close", remainingSeconds / 60, remainingSeconds % 60));
        setLabelText(ratingLabel, String.format("Rating: %.1f/10 (%s)", currentRating, difficulty));
        setLabelText(cleanlinessLabel, String.format("Shop Cleanliness:\n%.1f/10", shopCleanlinessRating));
        updateCustomerTimers();
        updateActionStates();
    }

    private void setLabelText(JLabel label, String text) {
        label.setText(toHtml(text));
    }

    private void refreshMenuListLabel() {
        StringBuilder menuText = new StringBuilder();
        for (String item : drinkMenuItems) {
            if (menuText.length() > 0) menuText.append('\n');
            menuText.append("- ").append(item);
        }
        setLabelText(menuListLabel, menuText.toString());
    }

    private void setupDrinkRules() {
        drinkRules.clear();
        addDrinkRule(new DrinkRule("Espresso", 300, "Espresso", false, null));
        addDrinkRule(new DrinkRule("Americano", 350, "Espresso", false, null));
        addDrinkRule(new DrinkRule("Latte", 450, "Espresso", true, null));
        addDrinkRule(new DrinkRule("Cappuccino", 475, "Espresso", true, null));
        addDrinkRule(new DrinkRule("Mocha", 525, "Espresso", true, "Caramel"));
        addDrinkRule(new DrinkRule("Flat White", 425, "Espresso", true, null));
    }

    private void addDrinkRule(DrinkRule rule) {
        drinkRules.put(rule.name, rule);
    }

    private void prepareStartingInventory() {
        inventory.clear();
        inventory.put("beans", 12);
        inventory.put("milk", 10);
        inventory.put("vanilla syrup", 6);
        inventory.put("caramel syrup", 6);
        inventory.put("cups", 12);
    }

    private void clearDailyInventory() {
        for (String item : inventory.keySet()) {
            inventory.put(item, 0);
        }
    }

    private void restockInventoryBundle() {
        addInventory("beans", 12);
        addInventory("milk", 10);
        addInventory("vanilla syrup", 6);
        addInventory("caramel syrup", 6);
        addInventory("cups", 12);
    }

    private void addInventory(String item, int amount) {
        inventory.put(item, getInventoryAmount(item) + amount);
    }

    private int getInventoryAmount(String item) {
        Integer amount = inventory.get(item);
        return amount == null ? 0 : amount;
    }

    private boolean hasInventoryFor(DrinkRule rule) {
        for (Map.Entry<String, Integer> ingredient : rule.ingredients.entrySet()) {
            if (getInventoryAmount(ingredient.getKey()) < ingredient.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void consumeInventoryFor(DrinkRule rule) {
        for (Map.Entry<String, Integer> ingredient : rule.ingredients.entrySet()) {
            inventory.put(ingredient.getKey(), getInventoryAmount(ingredient.getKey()) - ingredient.getValue());
        }
    }

    private void handleInventoryAction() {
        if(openPhase) {
            JOptionPane.showMessageDialog(this, buildInventoryMessage(), "Inventory", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                buildInventoryMessage() + "\nRestock bundle costs " + formatMoney(RESTOCK_COST_CENTS)
                        + " and 45 seconds.\nBuy it for tomorrow?",
                "Restock Inventory",
                JOptionPane.YES_NO_OPTION);

        if(choice == JOptionPane.YES_OPTION) {
            currentMoneyCents -= RESTOCK_COST_CENTS;
            totalProfitCents -= RESTOCK_COST_CENTS;
            restockInventoryBundle();
            advanceTime(RESTOCK_TIME_SECONDS);
            checkGameEnd();
        }
    }

    private String buildInventoryMessage() {
        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, Integer> item : inventory.entrySet()) {
            if(message.length() > 0) {
                message.append('\n');
            }
            message.append(item.getKey()).append(": ").append(item.getValue());
        }
        return message.toString();
    }

    private void cleanShop() {
        if(openPhase) {
            JOptionPane.showMessageDialog(this, "Cleaning is available after closing.");
            return;
        }
        shopCleanlinessRating = Math.min(10.0, shopCleanlinessRating + 2.5);
        advanceTime(CLEAN_TIME_SECONDS);
        refreshStatLabels();
    }

    private void handleCloseOrNextDay() {
        if(gameEnded) {
            return;
        }
        if(openPhase) {
            beginClosingPhase();
        }
        else {
            startNextDay();
        }
    }

    private void togglePause() {
        if(gameEnded) {
            return;
        }
        paused = !paused;
        if(paused) {
            stopGameTimer();
            pauseGameButton.setText("RESUME");
        }
        else {
            pauseGameButton.setText("PAUSE GAME");
            startGameTimer();
        }
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Open: serve customers before patience runs out.\n"
                + "Close: clean and restock before the next day.\n"
                + "Unused inventory is thrown out when the shop closes.\n"
                + "Wrong drinks lower ratings. Debt ends the game.",
                "How to Play",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void recordServiceResult(CustomerCard card, DrinkRule rule, boolean correctDrink) {
        if(correctDrink) {
            double satisfaction = card.calculateSatisfaction(getElapsedPhaseSeconds(), shopCleanlinessRating);
            long tipCents = calculateTip(rule.priceCents, satisfaction);
            long saleCents = rule.priceCents + tipCents;
            currentMoneyCents += saleCents;
            totalProfitCents += saleCents;
            updateRating(satisfaction);
            shopCleanlinessRating = Math.max(0, shopCleanlinessRating - 0.15);
            JOptionPane.showMessageDialog(this,
                    "Served " + rule.name + " for " + formatMoney(rule.priceCents)
                            + "\nTip: " + formatMoney(tipCents)
                            + "\nSatisfaction: " + String.format("%.1f/10", satisfaction));
        }
        else {
            updateRating(2.0);
            JOptionPane.showMessageDialog(this,
                    "That drink did not match the " + card.getDrinkOrder()
                            + " order.\nThe customer left unhappy.");
        }

        spawnReplacementCustomer(card);
        refreshStatLabels();
        checkGameEnd();
    }

    private long calculateTip(long priceCents, double satisfaction) {
        if(satisfaction >= 9.0) {
            return Math.round(priceCents * 0.22);
        }
        if(satisfaction >= 6.0) {
            return Math.round(priceCents * 0.15);
        }
        if(satisfaction >= 3.0) {
            return Math.round(priceCents * 0.05);
        }
        return 0;
    }

    private void updateRating(double satisfaction) {
        currentRating = ((currentRating * reviewCount) + satisfaction) / (reviewCount + 1);
        reviewCount++;
        if(currentRating < 0) {
            currentRating = 0;
        }
        if(currentRating > 10) {
            currentRating = 10;
        }
    }

    private void spawnReplacementCustomer(CustomerCard card) {
        if(openPhase && shouldSpawnCustomer()) {
            card.randomize(elapsedMinutes, elapsedSeconds);
        }
        else {
            card.clear();
        }
    }

    private boolean shouldSpawnCustomer() {
        double chance = 0.55 + (currentRating / 25.0);
        return Math.random() < chance;
    }

    private void removeExpiredCustomers() {
        removeExpiredCustomer(customerOne);
        removeExpiredCustomer(customerTwo);
        removeExpiredCustomer(customerThree);
    }

    private void updateCustomerTimers() {
        int currentSeconds = getElapsedPhaseSeconds();
        customerOne.updatePatienceBar(currentSeconds);
        customerTwo.updatePatienceBar(currentSeconds);
        customerThree.updatePatienceBar(currentSeconds);
    }

    private void fillEmptyCustomerSlots() {
        fillCustomerSlot(customerOne);
        fillCustomerSlot(customerTwo);
        fillCustomerSlot(customerThree);
    }

    private void fillCustomerSlot(CustomerCard card) {
        if(!card.isActive() && shouldSpawnCustomer()) {
            card.randomize(elapsedMinutes, elapsedSeconds);
        }
    }

    private void removeExpiredCustomer(CustomerCard card) {
        if(card.isActive() && card.isExpired(getElapsedPhaseSeconds())) {
            updateRating(1.5);
            card.clear();
        }
    }

    private void beginClosingPhase() {
        openPhase = false;
        elapsedMinutes = 0;
        elapsedSeconds = 0;
        openLabel.setText("CLOSED");
        closeEarlyButton.setText("START\nNEXT DAY");
        checkInventoryButton.setText("RESTOCK");
        cleanShopButton.setEnabled(true);
        cancelCoffeeMaker();
        customerOne.clear();
        customerTwo.clear();
        customerThree.clear();
        clearDailyInventory();
        refreshStatLabels();
        JOptionPane.showMessageDialog(this,
                "The shop is closed. Leftover inventory was thrown out.");
    }

    private void startNextDay() {
        if(currentDay >= totalDays) {
            gameEnded = true;
            stopGameTimer();
            String result;
            if(totalProfitCents >= targetProfitCents) {
                result = "You win! Net profit: " + formatMoney(totalProfitCents);
            }
            else {
                result = "Game over. Net profit: " + formatMoney(totalProfitCents)
                        + "\nTarget: " + formatMoney(targetProfitCents);
            }
            JOptionPane.showMessageDialog(this, result);
            refreshStatLabels();
            return;
        }

        currentDay++;
        openPhase = true;
        elapsedMinutes = 0;
        elapsedSeconds = 0;
        openLabel.setText("OPEN");
        closeEarlyButton.setText("CLOSE EARLY");
        checkInventoryButton.setText("CHECK\nINVENTORY");
        cleanShopButton.setEnabled(false);
        customerOne.randomize(0, 0);
        customerTwo.randomize(0, 0);
        customerThree.randomize(0, 0);
        refreshStatLabels();
    }

    private void advanceTime(int seconds) {
        int newElapsedSeconds = getElapsedPhaseSeconds() + seconds;
        elapsedMinutes = newElapsedSeconds / 60;
        elapsedSeconds = newElapsedSeconds % 60;
        if(!openPhase && getElapsedPhaseSeconds() >= CLOSED_DURATION_SECONDS) {
            startNextDay();
        }
        else {
            refreshStatLabels();
        }
    }

    private void checkGameEnd() {
        if(currentMoneyCents < 0) {
            gameEnded = true;
            stopGameTimer();
            JOptionPane.showMessageDialog(this, "You went into debt. Game over.");
        }
    }

    private int getElapsedPhaseSeconds() {
        return elapsedMinutes * 60 + elapsedSeconds;
    }

    private int getPhaseDurationSeconds() {
        return openPhase ? OPEN_DURATION_SECONDS : CLOSED_DURATION_SECONDS;
    }

    private String formatMoney(long cents) {
        long absoluteCents = Math.abs(cents);
        String sign = cents < 0 ? "-" : "";
        return String.format("%s$%,d.%02d", sign, absoluteCents / 100, absoluteCents % 100);
    }

    private void updateActionStates() {
        cleanShopButton.setEnabled(!openPhase && !gameEnded);
        closeEarlyButton.setEnabled(!gameEnded);
        checkInventoryButton.setEnabled(!gameEnded);
        customerOne.setServingEnabled(openPhase && !gameEnded);
        customerTwo.setServingEnabled(openPhase && !gameEnded);
        customerThree.setServingEnabled(openPhase && !gameEnded);
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

        menuListLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, Math.round(13 * fontScale))));

        coffeeMaker.setBounds(0, 0, width, height);
    }

    private Rectangle scaleRect(int x, int y, int w, int h, float sx, float sy) {
        return new Rectangle(Math.round(x * sx), Math.round(y * sy), Math.round(w * sx), Math.round(h * sy));
    }

    private static class DrinkRule {
        private final String name;
        private final long priceCents;
        private final String requiredCoffee;
        private final boolean requiresMilk;
        private final String requiredSyrup;
        private final LinkedHashMap<String, Integer> ingredients = new LinkedHashMap<>();

        DrinkRule(String name, long priceCents, String requiredCoffee, boolean requiresMilk, String requiredSyrup) {
            this.name = name;
            this.priceCents = priceCents;
            this.requiredCoffee = requiredCoffee;
            this.requiresMilk = requiresMilk;
            this.requiredSyrup = requiredSyrup;

            ingredients.put("beans", 1);
            ingredients.put("cups", 1);
            if(requiresMilk) {
                ingredients.put("milk", 1);
            }
            if(requiredSyrup != null) {
                ingredients.put(requiredSyrup.toLowerCase() + " syrup", 1);
            }
        }

        boolean matches(CoffeeMakerPanel maker) {
            if(!requiredCoffee.equals(maker.getSelectedCoffee())) {
                return false;
            }
            if(requiresMilk && maker.getSelectedMilk() == null) {
                return false;
            }
            if(!requiresMilk && maker.getSelectedMilk() != null) {
                return false;
            }

            Set<String> selectedSyrups = maker.getSelectedSyrups();
            if(requiredSyrup == null) {
                return selectedSyrups.isEmpty();
            }
            return selectedSyrups.size() == 1 && selectedSyrups.contains(requiredSyrup);
        }

        String getGuideText() {
            String milkText = requiresMilk ? "Any milk" : "No milk";
            String syrupText = requiredSyrup == null ? "No syrup" : requiredSyrup + " syrup";
            return "Coffee: " + requiredCoffee + " | Milk: " + milkText + " | Syrup: " + syrupText;
        }

        String getShortGuideText() {
            String milkText = requiresMilk ? "milk" : "no milk";
            String syrupText = requiredSyrup == null ? "no syrup" : requiredSyrup.toLowerCase();
            return requiredCoffee + " + " + milkText + " + " + syrupText;
        }
    }
}

class CustomerCard extends RoundedBlock {
    private static final Color CARD_BORDER = new Color(120, 90, 64);
    private static final Color MAKE_FILL = new Color(118, 89, 63);

    private static final String[] NAMES = {
        "Alice", "Bob", "Carol", "Dan", "Eve", "Frank",
        "Grace", "Henry", "Iris", "Jack", "Karen", "Leo",
        "Mia", "Noah", "Olivia", "Paul", "Quinn", "Rose"
    };
    private static final String[] DRINKS = {
        "Espresso", "Latte", "Cappuccino",
        "Americano", "Mocha", "Flat White"
    };
    private static final java.util.Random RNG = new java.util.Random();

    private final JLabel infoLabel;
    private final JLabel recipeLabel;
    private final JLabel patienceLabel;
    private final JProgressBar patienceBar;
    private final JButton makeButton;
    private String customerName;
    private String drinkOrder;
    private String recipeHint;
    private int arrivalSeconds;
    private int patienceSeconds;
    private boolean active;

    CustomerCard() {
        super(Color.WHITE, CARD_BORDER, 6, 40);
        setLayout(null);

        infoLabel = new JLabel("<html>[NAME]<br>[ARRIVAL TIME]<br>[DRINK ORDER]</html>");
        infoLabel.setForeground(Color.BLACK);
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);

        recipeLabel = new JLabel("<html>Recipe hint</html>");
        recipeLabel.setForeground(new Color(75, 50, 30));
        recipeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        recipeLabel.setVerticalAlignment(SwingConstants.CENTER);

        patienceLabel = new JLabel("Patience");
        patienceLabel.setForeground(new Color(75, 50, 30));
        patienceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        patienceBar = new JProgressBar(0, 100);
        patienceBar.setValue(100);
        patienceBar.setStringPainted(false);
        patienceBar.setBorderPainted(false);
        patienceBar.setForeground(new Color(84, 140, 92));
        patienceBar.setBackground(new Color(226, 215, 203));

        makeButton = new RoundedButton("MAKE\n->", MAKE_FILL, 50, new Color(75, 50, 30), 5, true);
        makeButton.setForeground(Color.BLACK);
        makeButton.setFont(new Font("SansSerif", Font.BOLD, 28));
        makeButton.setFocusPainted(false);
        makeButton.setBorderPainted(false);
        makeButton.setContentAreaFilled(false);
        makeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(infoLabel);
        add(recipeLabel);
        add(patienceLabel);
        add(patienceBar);
        add(makeButton);
    }

    void randomize(int arrivalMin, int arrivalSec) {
        customerName = NAMES[RNG.nextInt(NAMES.length)];
        drinkOrder = DRINKS[RNG.nextInt(DRINKS.length)];
        recipeHint = getRecipeHint(drinkOrder);
        arrivalSeconds = arrivalMin * 60 + arrivalSec;
        patienceSeconds = 60 + RNG.nextInt(61);
        active = true;
        infoLabel.setText(String.format(
            "<html>%s<br>%d M %02d S<br>Order: %s</html>",
            customerName, arrivalMin, arrivalSec, drinkOrder));
        recipeLabel.setText("<html>" + recipeHint + "</html>");
        recipeLabel.setVisible(true);
        patienceLabel.setVisible(true);
        patienceBar.setVisible(true);
        updatePatienceBar(arrivalSeconds);
        makeButton.setEnabled(true);
    }

    void clear() {
        customerName = "";
        drinkOrder = "";
        recipeHint = "";
        active = false;
        infoLabel.setText("<html>-- no customer --</html>");
        recipeLabel.setVisible(false);
        patienceLabel.setVisible(false);
        patienceBar.setVisible(false);
        makeButton.setEnabled(false);
    }

    boolean isActive() {
        return active;
    }

    String getDrinkOrder() {
        return drinkOrder;
    }

    boolean isExpired(int currentSeconds) {
        return currentSeconds - arrivalSeconds > patienceSeconds;
    }

    double calculateSatisfaction(int currentSeconds, double cleanliness) {
        int waitSeconds = Math.max(0, currentSeconds - arrivalSeconds);
        double speedScore = 10.0 - ((waitSeconds / (double) patienceSeconds) * 6.0);
        double cleanScore = Math.max(0, Math.min(10, cleanliness));
        double satisfaction = (speedScore * 0.7) + (cleanScore * 0.3);
        if(satisfaction < 0) {
            satisfaction = 0;
        }
        if(satisfaction > 10) {
            satisfaction = 10;
        }
        return satisfaction;
    }

    void setServingEnabled(boolean enabled) {
        makeButton.setEnabled(enabled && active);
    }

    void updatePatienceBar(int currentSeconds) {
        if(!active) {
            patienceBar.setVisible(false);
            patienceLabel.setVisible(false);
            return;
        }

        int remainingSeconds = Math.max(0, patienceSeconds - (currentSeconds - arrivalSeconds));
        int percent = Math.max(0, Math.min(100, Math.round((remainingSeconds * 100f) / patienceSeconds)));
        patienceBar.setValue(percent);
        patienceLabel.setText(String.format("Patience: %d:%02d", remainingSeconds / 60, remainingSeconds % 60));

        if(percent <= 25) {
            patienceBar.setForeground(new Color(174, 72, 58));
        }
        else if(percent <= 50) {
            patienceBar.setForeground(new Color(198, 139, 50));
        }
        else {
            patienceBar.setForeground(new Color(84, 140, 92));
        }
    }

    private String getRecipeHint(String drink) {
        if("Mocha".equals(drink)) {
            return "Pick: Espresso + any milk + Caramel syrup";
        }
        if("Latte".equals(drink) || "Cappuccino".equals(drink) || "Flat White".equals(drink)) {
            return "Pick: Espresso + any milk + no syrup";
        }
        return "Pick: Espresso + no milk + no syrup";
    }

    void setMakeAction(Runnable action) {
        for (java.awt.event.ActionListener l : makeButton.getActionListeners())
            makeButton.removeActionListener(l);
        makeButton.addActionListener(e -> action.run());
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int width = Math.max(1, getWidth());
        int height = Math.max(1, getHeight());
        float sx = width / 505f;
        float sy = height / 148f;
        float fontScale = Math.min(sx, sy);

        infoLabel.setBounds(Math.round(24 * sx), Math.round(12 * sy), Math.round(278 * sx), Math.round(62 * sy));
        recipeLabel.setBounds(Math.round(24 * sx), Math.round(74 * sy), Math.round(285 * sx), Math.round(36 * sy));
        patienceLabel.setBounds(Math.round(24 * sx), Math.round(111 * sy), Math.round(170 * sx), Math.round(18 * sy));
        patienceBar.setBounds(Math.round(24 * sx), Math.round(132 * sy), Math.round(278 * sx), Math.round(10 * sy));
        makeButton.setBounds(Math.round(330 * sx), Math.round(24 * sy), Math.round(145 * sx), Math.round(98 * sy));

        infoLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(21 * fontScale))));
        recipeLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, Math.round(13 * fontScale))));
        patienceLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, Math.round(12 * fontScale))));
        makeButton.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(26 * fontScale))));
    }
}
