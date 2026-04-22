import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import screens.GameMenuPanel;
import screens.StartMenuPanel;

public class Game {
    private static final String START_CARD = "start";
    private static final String GAME_CARD = "game";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("My Cafe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        java.awt.CardLayout cardLayout = new java.awt.CardLayout();
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
