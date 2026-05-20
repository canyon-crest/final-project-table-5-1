package screens;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CoffeeMakerPanel extends JPanel {

    private static final int BASE_WIDTH  = 1200;
    private static final int BASE_HEIGHT = 800;

    private static final Color BACKGROUND  = new Color(236, 236, 236);
    private static final Color SOFT_BEIGE  = new Color(214, 207, 194);
    private static final Color MID_BROWN   = new Color(151, 120, 92);
    private static final Color DARK_BROWN  = new Color(118, 89, 63);
    private static final Color BORDER_TAN  = new Color(223, 193, 140);
    private static final Color TAB_IDLE    = new Color(196, 186, 172);

    // ── Order tabs ──────────────────────────────────────────────────────────
    private int activeTab = 0;
    private final JLabel[] tabLabels = new JLabel[4];

    // ── Selections ──────────────────────────────────────────────────────────
    private String selectedCoffee = null;
    private final Set<String> selectedSyrups   = new LinkedHashSet<>();
    private String selectedMilk = null;
    private final Set<String> selectedToppings = new LinkedHashSet<>();

    // ── Step 1 – Coffee type ─────────────────────────────────────────────────
    private final JLabel  coffeeTitleLbl;
    private final JButton coldBrewBtn, espressoBtn, dripBtn;

    // ── Step 2 – Syrups + Milk ───────────────────────────────────────────────
    private static final String[] SYRUPS = {"Vanilla", "Caramel", "Hazelnut", "Lavender"};
    private static final String[] MILKS  = {"Regular", "Oat",     "Almond",   "Soy"};
    private final JLabel  syrupsTitleLbl, milkTitleLbl;
    private final JButton[] syrupBtns = new JButton[4];
    private final JButton[] milkBtns  = new JButton[4];

    // ── Step 3 – Toppings ────────────────────────────────────────────────────
    private static final String[] TOPPINGS = {"Whipped\nCream", "Cinnamon", "Sea Salt", "Cold\nFoam"};
    private final JLabel  toppingsTitleLbl;
    private final JButton[] toppingBtns = new JButton[4];

    // ── Navigation ───────────────────────────────────────────────────────────
    private int currentStep = 0;
    private final JButton nextBtn, backBtn, closeBtn;
    private final Image mugImage;
    private final Image espressoImage;
    private final Image milkImage;

    // ─────────────────────────────────────────────────────────────────────────

    public CoffeeMakerPanel(Runnable onSubmit, Runnable onCancel) {
        java.net.URL mugUrl      = getClass().getResource("/images/mug.png");
        java.net.URL espressoUrl = getClass().getResource("/images/espresso.png");
        java.net.URL milkUrl     = getClass().getResource("/images/milk.png");
        mugImage      = (mugUrl      != null) ? new javax.swing.ImageIcon(mugUrl).getImage()      : null;
        espressoImage = (espressoUrl != null) ? new javax.swing.ImageIcon(espressoUrl).getImage() : null;
        milkImage     = (milkUrl     != null) ? new javax.swing.ImageIcon(milkUrl).getImage()     : null;
        setLayout(null);
        setBackground(BACKGROUND);

        // Order tabs (clickable labels painted over custom tab shapes)
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            tabLabels[i] = makeLabel("Order " + (i + 1), Color.BLACK, 20, true);
            tabLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            tabLabels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabLabels[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { activeTab = idx; repaint(); }
            });
            add(tabLabels[i]);
        }

        // Step 1
        coffeeTitleLbl = makeLabel("Coffee", DARK_BROWN, 42, true);
        coldBrewBtn = makeTypeButton("Cold Brew");
        espressoBtn = makeTypeButton("Espresso");
        dripBtn     = makeTypeButton("Drip");
        coldBrewBtn.addActionListener(e -> selectCoffee("Cold Brew", coldBrewBtn, espressoBtn, dripBtn));
        espressoBtn.addActionListener(e -> selectCoffee("Espresso",  coldBrewBtn, espressoBtn, dripBtn));
        dripBtn    .addActionListener(e -> selectCoffee("Drip",      coldBrewBtn, espressoBtn, dripBtn));
        add(coffeeTitleLbl); add(coldBrewBtn); add(espressoBtn); add(dripBtn);

        // Step 2
        syrupsTitleLbl = makeLabel("Syrups", DARK_BROWN, 38, true);
        milkTitleLbl   = makeLabel("Milk",   DARK_BROWN, 38, true);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            syrupBtns[i] = makeCircleButton(SYRUPS[i]);
            milkBtns[i]  = makeCircleButton(MILKS[i]);
            syrupBtns[i].addActionListener(e -> toggleSet(selectedSyrups, SYRUPS[idx], syrupBtns[idx]));
            milkBtns[i].addActionListener(e -> {
                selectedMilk = MILKS[idx];
                for (int j = 0; j < milkBtns.length; j++) {
                    milkBtns[j].putClientProperty("selected", j == idx);
                    milkBtns[j].repaint();
                }
            });
            add(syrupBtns[i]); add(milkBtns[i]);
        }
        add(syrupsTitleLbl); add(milkTitleLbl);

        // Step 3
        toppingsTitleLbl = makeLabel("Toppings", DARK_BROWN, 38, true);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            toppingBtns[i] = makeCircleButton(TOPPINGS[i]);
            toppingBtns[i].addActionListener(e -> toggleSet(selectedToppings, TOPPINGS[idx], toppingBtns[idx]));
            add(toppingBtns[i]);
        }
        add(toppingsTitleLbl);

        // Nav
        nextBtn  = makeNavButton("Next >");
        backBtn  = makeNavButton("< Back");
        closeBtn = makeNavButton("✕");
        nextBtn .addActionListener(e -> { if (currentStep < 2) showStep(currentStep + 1); else onSubmit.run(); });
        backBtn .addActionListener(e -> { if (currentStep > 0) showStep(currentStep - 1); });
        closeBtn.addActionListener(e -> onCancel.run());
        add(nextBtn); add(backBtn); add(closeBtn);

        showStep(0);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void selectCoffee(String type, JButton... btns) {
        selectedCoffee = type;
        String[] types = {"Cold Brew", "Espresso", "Drip"};
        for (int i = 0; i < btns.length; i++) {
            btns[i].putClientProperty("selected", types[i].equals(type));
            btns[i].repaint();
        }
    }

    private void toggleSet(Set<String> set, String value, JButton btn) {
        if (set.contains(value)) set.remove(value); else set.add(value);
        btn.putClientProperty("selected", set.contains(value));
        btn.repaint();
    }

    private void showStep(int step) {
        currentStep = step;

        coffeeTitleLbl.setVisible(step == 0);
        coldBrewBtn.setVisible(step == 0);
        espressoBtn.setVisible(step == 0);
        dripBtn    .setVisible(step == 0);

        syrupsTitleLbl.setVisible(step == 1);
        milkTitleLbl  .setVisible(step == 1);
        for (JButton b : syrupBtns) b.setVisible(step == 1);
        for (JButton b : milkBtns)  b.setVisible(step == 1);

        toppingsTitleLbl.setVisible(step == 2);
        for (JButton b : toppingBtns) b.setVisible(step == 2);

        nextBtn.setText(step == 2 ? "Finish" : "Next >");
        backBtn.setVisible(step > 0);

        repaint();
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    private JLabel makeLabel(String text, Color color, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    /** Rectangular toggle button for coffee type selection. */
    private JButton makeTypeButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                boolean sel = Boolean.TRUE.equals(getClientProperty("selected"));
                boolean hov = getModel().isRollover();
                Color fill  = sel ? DARK_BROWN : (hov ? SOFT_BEIGE : Color.WHITE);
                Color txt   = sel ? Color.WHITE : DARK_BROWN;
                int arc = Math.max(8, Math.round(22 * Math.min(getWidth() / 310f, getHeight() / 80f)));

                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(BORDER_TAN);
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, arc, arc);

                g2.setColor(txt);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btn.setOpaque(false); btn.setFocusPainted(false);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("selected", false);
        return btn;
    }

    /** Circular toggle button for syrups, milk, and toppings. */
    private JButton makeCircleButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                boolean sel = Boolean.TRUE.equals(getClientProperty("selected"));
                boolean hov = getModel().isRollover();
                Color fill  = sel ? DARK_BROWN : (hov ? SOFT_BEIGE : Color.WHITE);
                Color txt   = sel ? Color.WHITE : DARK_BROWN;
                Color brd   = sel ? MID_BROWN   : BORDER_TAN;

                int d = Math.min(getWidth(), getHeight()) - 6;
                int ox = (getWidth()  - d) / 2;
                int oy = (getHeight() - d) / 2;

                g2.setColor(fill);
                g2.fillOval(ox, oy, d, d);
                g2.setColor(brd);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(ox, oy, d, d);

                g2.setColor(txt);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String[] lines = getText().split("\\n");
                int lh = fm.getHeight();
                int total = lines.length * lh;
                int startY = oy + (d - total) / 2 + fm.getAscent();
                for (int i = 0; i < lines.length; i++) {
                    int lx = ox + (d - fm.stringWidth(lines[i])) / 2;
                    g2.drawString(lines[i], lx, startY + i * lh);
                }
                g2.dispose();
            }
        };
        btn.setOpaque(false); btn.setFocusPainted(false);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("selected", false);
        return btn;
    }

    private JButton makeNavButton(String text) {
        RoundedButton btn = new RoundedButton(text, DARK_BROWN, 28, null, 0, true);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        float sx = w / (float) BASE_WIDTH;
        float sy = h / (float) BASE_HEIGHT;

        paintTabs(g2, sx, sy, w, h);
        paintMainArea(g2, sx, sy, w, h);
        paintCup(g2, sx, sy);

        g2.dispose();
    }

    private void paintTabs(Graphics2D g2, float sx, float sy, int w, int h) {
        int tabW = Math.round(220 * sx);
        int tabH = Math.round(88 * sy);
        int arc  = Math.max(6, Math.round(22 * Math.min(sx, sy)));
        for (int i = 0; i < 4; i++) {
            int tx = Math.round((20 + i * 240) * sx);
            Color fill = (i == activeTab) ? SOFT_BEIGE : TAB_IDLE;
            g2.setColor(fill);
            // Round only top corners: draw full rounded rect then fill bottom overlap
            g2.fillRoundRect(tx, 0, tabW, tabH + arc, arc, arc);
            g2.fillRect(tx, tabH, tabW, arc);
        }
    }

    private void paintMainArea(Graphics2D g2, float sx, float sy, int w, int h) {
        int tabH = Math.round(88 * sy);
        int arc  = Math.max(8, Math.round(32 * Math.min(sx, sy)));
        int mx   = Math.round(10 * sx);
        g2.setColor(SOFT_BEIGE);
        g2.fillRoundRect(mx, tabH, w - mx * 2, h - tabH - Math.round(8 * sy), arc, arc);
    }

    private void paintCup(Graphics2D g2, float sx, float sy) {
        if (mugImage == null) return;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int cx    = Math.round(600 * sx);
        int maxW  = Math.round(300 * sx);
        int maxH  = Math.round(400 * sy);
        int imgY  = Math.round(160 * sy);

        int natW = mugImage.getWidth(null);
        int natH = mugImage.getHeight(null);
        float scale = Math.min(maxW / (float) natW, maxH / (float) natH);
        int drawW = Math.round(natW * scale);
        int drawH = Math.round(natH * scale);
        int drawX = cx - drawW / 2;

        g2.drawImage(mugImage, drawX, imgY, drawW, drawH, null);

        if ("Espresso".equals(selectedCoffee) && espressoImage != null) {
            g2.drawImage(espressoImage, drawX, imgY, drawW, drawH, null);
        }

        if (selectedMilk != null && milkImage != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g2.drawImage(milkImage, drawX, imgY, drawW, drawH, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutScaled();
    }

    private void layoutScaled() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        float sx = w / (float) BASE_WIDTH;
        float sy = h / (float) BASE_HEIGHT;
        float fs = Math.min(sx, sy);

        // Tabs
        for (int i = 0; i < 4; i++) {
            tabLabels[i].setBounds(sr(20 + i * 240, 20, 220, 56, sx, sy));
            tabLabels[i].setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(20 * fs))));
        }

        // Step 1 – Coffee
        coffeeTitleLbl.setBounds(sr(55, 138, 260, 52, sx, sy));
        coffeeTitleLbl.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, Math.round(42 * fs))));
        coldBrewBtn.setBounds(sr(55, 210, 310, 78, sx, sy));
        espressoBtn.setBounds(sr(55, 312, 310, 78, sx, sy));
        dripBtn    .setBounds(sr(55, 414, 310, 78, sx, sy));
        Font typeF = new Font("SansSerif", Font.BOLD, Math.max(12, Math.round(26 * fs)));
        coldBrewBtn.setFont(typeF); espressoBtn.setFont(typeF); dripBtn.setFont(typeF);

        // Step 2 – Syrups + Milk
        syrupsTitleLbl.setBounds(sr(55, 138, 260, 50, sx, sy));
        syrupsTitleLbl.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, Math.round(38 * fs))));
        milkTitleLbl.setBounds(sr(790, 138, 200, 50, sx, sy));
        milkTitleLbl.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, Math.round(38 * fs))));
        int[][] sg = {{55, 205}, {205, 205}, {55, 365}, {205, 365}};
        int[][] mg = {{790, 205}, {950, 205}, {790, 365}, {950, 365}};
        Font cirF  = new Font("SansSerif", Font.BOLD, Math.max(9,  Math.round(15 * fs)));
        Font milkF = new Font("SansSerif", Font.BOLD, Math.max(9,  Math.round(17 * fs)));
        for (int i = 0; i < 4; i++) {
            syrupBtns[i].setBounds(sr(sg[i][0], sg[i][1], 135, 135, sx, sy));
            syrupBtns[i].setFont(cirF);
            milkBtns[i].setBounds(sr(mg[i][0], mg[i][1], 135, 135, sx, sy));
            milkBtns[i].setFont(milkF);
        }

        // Step 3 – Toppings
        toppingsTitleLbl.setBounds(sr(55, 138, 310, 50, sx, sy));
        toppingsTitleLbl.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, Math.round(38 * fs))));
        int[][] tg = {{55, 205}, {215, 205}, {55, 370}, {215, 370}};
        Font topF = new Font("SansSerif", Font.BOLD, Math.max(9, Math.round(15 * fs)));
        for (int i = 0; i < 4; i++) {
            toppingBtns[i].setBounds(sr(tg[i][0], tg[i][1], 145, 145, sx, sy));
            toppingBtns[i].setFont(topF);
        }

        // Nav
        nextBtn .setBounds(sr(1010, 702, 170, 68, sx, sy));
        nextBtn .setFont(new Font("SansSerif", Font.BOLD, Math.max(12, Math.round(26 * fs))));
        backBtn .setBounds(sr(20,   702, 150, 68, sx, sy));
        backBtn .setFont(new Font("SansSerif", Font.BOLD, Math.max(12, Math.round(26 * fs))));
        closeBtn.setBounds(sr(1140,  14,  56, 56, sx, sy));
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, Math.round(22 * fs))));
    }

    private Rectangle sr(int x, int y, int w, int h, float sx, float sy) {
        return new Rectangle(Math.round(x * sx), Math.round(y * sy), Math.round(w * sx), Math.round(h * sy));
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void reset() {
        selectedCoffee = null;
        selectedSyrups.clear();
        selectedMilk = null;
        selectedToppings.clear();
        for (JButton b : new JButton[]{coldBrewBtn, espressoBtn, dripBtn})
            b.putClientProperty("selected", false);
        for (JButton b : syrupBtns)   b.putClientProperty("selected", false);
        for (JButton b : milkBtns)    b.putClientProperty("selected", false);
        for (JButton b : toppingBtns) b.putClientProperty("selected", false);
        showStep(0);
    }

    public String getSelectedCoffee()          { return selectedCoffee; }
    public Set<String> getSelectedSyrups()     { return new LinkedHashSet<>(selectedSyrups); }
    public String getSelectedMilk()            { return selectedMilk; }
    public Set<String> getSelectedToppings()   { return new LinkedHashSet<>(selectedToppings); }
}
