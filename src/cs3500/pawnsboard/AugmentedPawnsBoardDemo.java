package cs3500.pawnsboard;

import cs3500.pawnsboard.model.AugmentedPawnsBoard;
import cs3500.pawnsboard.model.PawnsBoardAugmented;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.deckbuilder.PawnsBoardAugmentedDeckBuilder;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import cs3500.pawnsboard.view.PawnsBoardAugmentedGUIView;
import cs3500.pawnsboard.view.PawnsBoardAugmentedGraphicalView;
import cs3500.pawnsboard.controller.PawnsBoardController;
import cs3500.pawnsboard.controller.PawnsBoardControllerImpl;
import cs3500.pawnsboard.controller.listeners.ModelStatusListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Demonstration class for the Augmented Pawns Board game.
 * This class provides a complete example of creating an augmented model,
 * connecting it to an augmented view, and setting up a controller.
 */
public class AugmentedPawnsBoardDemo {

  /**
   * Main method to run the demonstration.
   * Creates and initializes the game components and displays the GUI.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Create components
        AugmentedPawnsBoard<PawnsBoardAugmentedCard, PawnsBoardAugmentedCell> model = createModel();
        setupGame(model);
        createAndShowGUI(model);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
                "Error starting game: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
      }
    });
  }

  /**
   * Creates an augmented model with the necessary components.
   *
   * @return the configured model
   */
  private static AugmentedPawnsBoard<PawnsBoardAugmentedCard> createModel() {
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
  private static void setupGame(AugmentedPawnsBoard<PawnsBoardAugmentedCard> model)
          throws InvalidDeckConfigurationException {
    // Define the paths to the deck configuration files
    // These should be created or available in the resources directory
    String redDeckConfigPath = "resources/augmented_red_deck.txt";
    String blueDeckConfigPath = "resources/augmented_blue_deck.txt";
    
    // Define game parameters
    int rows = 5;
    int cols = 5;
    int startingHandSize = 3;
    
    // Start the game with the defined configuration
    model.startGame(rows, cols, redDeckConfigPath, blueDeckConfigPath, startingHandSize);
    
    // Add a status listener to log game events (optional)
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
  private static void createAndShowGUI(AugmentedPawnsBoard<PawnsBoardAugmentedCard> model) {
    // Create a view for RED player
    PawnsBoardAugmentedGUIView redView = new PawnsBoardAugmentedGraphicalView(model);
    redView.setTitle("Augmented Pawns Board - RED Player");
    ((PawnsBoardAugmentedGraphicalView) redView).setViewPlayer(PlayerColors.RED);
    
    // Create a view for BLUE player
    PawnsBoardAugmentedGUIView blueView = new PawnsBoardAugmentedGraphicalView(model);
    blueView.setTitle("Augmented Pawns Board - BLUE Player");
    ((PawnsBoardAugmentedGraphicalView) blueView).setViewPlayer(PlayerColors.BLUE);
    
    // Position the views side by side
    redView.setPosition(100, 100);
    blueView.setPosition(redView.getAugmentedBoardPanel().getPanel().getWidth() + 150, 100);
    
    // Create controllers for both views
    PawnsBoardController redController = new PawnsBoardControllerImpl(model, redView);
    PawnsBoardController blueController = new PawnsBoardControllerImpl(model, blueView);
    
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
            "Augmented Pawns Board Game\n\n" +
                    "- Select a card from your hand\n" +
                    "- Select a cell on the board to place it\n" +
                    "- Press ENTER to confirm the move\n" +
                    "- Press P to pass your turn\n\n" +
                    "Augmented features:\n" +
                    "- Green cells: Upgrading influence (+1 to card value)\n" +
                    "- Purple cells: Devaluing influence (-1 to card value)\n" +
                    "- Cyan cells: Regular influence\n" +
                    "- Cards with value ≤ 0 are removed and replaced with pawns",
            "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
  }
}
