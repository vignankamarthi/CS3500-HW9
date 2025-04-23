package cs3500.pawnsboard;

import cs3500.pawnsboard.model.AugmentedPawnsBoard;
import cs3500.pawnsboard.model.PawnsBoardAugmented;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.deckbuilder.PawnsBoardAugmentedDeckBuilder;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import cs3500.pawnsboard.view.PawnsBoardAugmentedGUIView;
import cs3500.pawnsboard.view.PawnsBoardAugmentedGraphicalView;
import cs3500.pawnsboard.controller.HumanPawnsBoardController;
import cs3500.pawnsboard.controller.PawnsBoardController;
import cs3500.pawnsboard.controller.listeners.ModelStatusListener;
import cs3500.pawnsboard.player.HumanPlayer;
import cs3500.pawnsboard.player.Player;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Demonstration class for the Augmented Pawns Board game.
 * This class provides a complete example of creating an augmented model,
 * connecting it to an augmented view, and setting up a controller for a human vs. human game.
 */
public class PawnsBoardGameLevel2 {

  // Constants for board dimensions and hand size
  private static final int DEFAULT_ROWS = 3;
  private static final int DEFAULT_COLUMNS = 5;
  private static final int DEFAULT_HAND_SIZE = 5;

  /**
   * Main method to run the demonstration.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) throws InvalidDeckConfigurationException {
    // Create components
    PawnsBoardAugmented<PawnsBoardAugmentedCard> model = createModel();
    setupGame(model);
    createAndShowGUI(model);
  }

  /**
   * Creates an augmented model with the necessary components.
   *
   * @return the configured model
   */
  private static PawnsBoardAugmented<PawnsBoardAugmentedCard> createModel() {
    // Create an influence manager for handling different influence types
    InfluenceManager influenceManager = new InfluenceManager();
    
    // Create a deck builder using the influence manager
    PawnsBoardAugmentedDeckBuilder<PawnsBoardAugmentedCard> deckBuilder =
            new PawnsBoardAugmentedDeckBuilder<>(influenceManager);
    
    // Create the model with the deck builder and influence manager
    return new PawnsBoardAugmented<>(deckBuilder, influenceManager);
  }

  /**
   * Sets up the game with initial configuration.
   *
   * @param model the model to set up
   * @throws InvalidDeckConfigurationException if there's an issue with the deck configuration
   */
  private static void setupGame(AugmentedPawnsBoard<PawnsBoardAugmentedCard, ?> model)
          throws InvalidDeckConfigurationException {
    // Define the paths to the deck configuration files
    String redDeckConfigPath = "docs" + File.separator + "RED3x5PawnsBoardAugmentedDeck.config";
    String blueDeckConfigPath = "docs" + File.separator + "BLUE3x5PawnsBoardAugmentedDeck.config";
    
    // Check if the files exist and display an error if they don't
    File redDeckFile = new File(redDeckConfigPath);
    File blueDeckFile = new File(blueDeckConfigPath);
    
    if (!redDeckFile.exists() || !blueDeckFile.exists()) {
      throw new InvalidDeckConfigurationException(
              "One or both deck configuration files not found: \n" +
              redDeckConfigPath + "\n" + 
              blueDeckConfigPath + "\n" +
              "Please ensure these files exist in the docs directory.");
    }
    
    // Start the game with the defined configuration
    model.startGame(DEFAULT_ROWS, DEFAULT_COLUMNS, redDeckConfigPath, blueDeckConfigPath, 
            DEFAULT_HAND_SIZE);
    
    // Add a status listener to log game events
    model.addModelStatusListener(new ModelStatusListener() {
      @Override
      public void onTurnChange(PlayerColors newPlayer) {
        System.out.println("Turn changed to: " + newPlayer);
      }

      @Override
      public void onGameOver(PlayerColors winner, int[] finalScores) {
        String winnerStr = (winner != null) ? winner.toString() : "TIE";
        System.out.println("Game over! Winner: " + winnerStr +
                ", Scores: RED=" + finalScores[0] + ", BLUE=" + finalScores[1]);
        
        // Show game over dialog
        SwingUtilities.invokeLater(() -> {
          JOptionPane.showMessageDialog(null,
                  "Game over!\n" +
                  "Winner: " + winnerStr + "\n" +
                  "Scores: RED=" + finalScores[0] + ", BLUE=" + finalScores[1],
                  "Game Over", JOptionPane.INFORMATION_MESSAGE);
        });
      }

      @Override
      public void onInvalidMove(String errorMessage) {
        System.out.println("Invalid move: " + errorMessage);
      }
    });
  }

  /**
   * Creates and displays the GUI for both RED and BLUE players.
   *
   * @param model the model to connect to the view
   */
  private static void createAndShowGUI(AugmentedPawnsBoard<PawnsBoardAugmentedCard, ?> model) {
    // Create human players
    Player<PawnsBoardAugmentedCard> redPlayer = new HumanPlayer<>(PlayerColors.RED);
    Player<PawnsBoardAugmentedCard> bluePlayer = new HumanPlayer<>(PlayerColors.BLUE);
    
    // Create a view for RED player
    PawnsBoardAugmentedGUIView redView = new PawnsBoardAugmentedGraphicalView(model);
    redView.setTitle("Augmented Pawns Board - RED Player (Human)");
    ((PawnsBoardAugmentedGraphicalView) redView).setViewPlayer(PlayerColors.RED);
    
    // Create a view for BLUE player
    PawnsBoardAugmentedGUIView blueView = new PawnsBoardAugmentedGraphicalView(model);
    blueView.setTitle("Augmented Pawns Board - BLUE Player (Human)");
    ((PawnsBoardAugmentedGraphicalView) blueView).setViewPlayer(PlayerColors.BLUE);
    
    // Position the views side by side
    redView.setPosition(100, 100);
    blueView.setPosition(1300, 100);
    
    // Create controllers for both views
    PawnsBoardController redController = new HumanPawnsBoardController<>(
            model, (HumanPlayer<PawnsBoardAugmentedCard>) redPlayer, redView);
    PawnsBoardController blueController = new HumanPawnsBoardController<>(
            model, (HumanPlayer<PawnsBoardAugmentedCard>) bluePlayer, blueView);
    
    // Initialize controllers
    redController.initialize(model, redView);
    blueController.initialize(model, blueView);
    
    // Set view sizes
    redView.setSize(1200, 900);
    blueView.setSize(1200, 900);
    
    // Add window listeners to exit when both windows are closed
    ((PawnsBoardAugmentedGraphicalView) redView).addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if (!((PawnsBoardAugmentedGraphicalView) blueView).isVisible()) {
          System.exit(0);
        }
      }
    });
    
    ((PawnsBoardAugmentedGraphicalView) blueView).addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if (!((PawnsBoardAugmentedGraphicalView) redView).isVisible()) {
          System.exit(0);
        }
      }
    });
    
    // Show the views
    redView.setVisible(true);
    blueView.setVisible(true);
    
    // Initial refresh
    redView.refresh();
    blueView.refresh();
    
    // Display instructions to the user
    JOptionPane.showMessageDialog(null,
            "Augmented Pawns Board Game - Human vs. Human\n\n" +
                    "How to play:\n" +
                    "- Select a card from your hand\n" +
                    "- Select a cell on the board to place it\n" +
                    "- Press ENTER to confirm the move\n" +
                    "- Press P to pass your turn\n\n" +
                    "Augmented features:\n" +
                    "- Green cells: Upgrading influence (+1 to card value)\n" +
                    "- Purple cells: Devaluing influence (-1 to card value)\n" +
                    "- Cyan cells: Regular influence\n" +
                    "- Cards with value ≤ 0 are removed and replaced with \n pawns equal to the " +
                    "cost of the original card.",
            "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
  }
}