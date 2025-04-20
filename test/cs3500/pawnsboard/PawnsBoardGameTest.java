package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.PawnsBoardController;
import cs3500.pawnsboard.controller.PawnsBoardStubController;
import cs3500.pawnsboard.model.PawnsBoardBase;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.view.PawnsBoardGUIView;
import cs3500.pawnsboard.view.PawnsBoardGraphicalView;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Test class for the Pawns Board GUI view.
 * This class demonstrates three different game states for testing:
 * 1. Initial game start
 * 2. Blue player's turn with a selected card
 * 3. Mid-game state with cards played by both players
 */
public class PawnsBoardGameTest {

  /**
   * Main method that creates and displays three test cases.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    String[] options = {"Game Start", "Blue's Turn with Selected Card", "Mid-Game State"};

    int selectedOption = JOptionPane.showOptionDialog(null,
            "Which test case would you like to run?",
            "Pawns Board GUI Test",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

    switch (selectedOption) {
      case 0:
        runGameStart();
        break;
      case 1:
        runBlueTurnWithSelection();
        break;
      case 2:
        runMidGameState();
        break;
      default:
        System.exit(0);
    }
  }

  /**
   * Runs the first test case: Initial game start.
   * This shows the board at the beginning of the game with Red's turn.
   */
  private static void runGameStart() {
    try {
      // Create the model
      PawnsBoardBase<PawnsBoardBaseCard> model = new PawnsBoardBase<>();

      // Get the deck configuration file paths
      String redDeckPath = "docs" + File.separator + "RED5x7PawnsBoardBaseCompleteDeck.config";
      String blueDeckPath = "docs" + File.separator + "BLUE5x7PawnsBoardBaseCompleteDeck.config";

      // Initialize the game with 5 rows, 7 columns, and 7 cards per hand
      model.startGame(5, 7, redDeckPath, blueDeckPath, 7);

      // Create the view with the model as a ReadOnlyPawnsBoard
      ReadOnlyPawnsBoard<PawnsBoardBaseCard, ?> readOnlyModel = model;
      PawnsBoardGUIView view = new PawnsBoardGraphicalView(readOnlyModel);

      // Create the stub controller
      PawnsBoardController controller = new PawnsBoardStubController();
      controller.initialize(readOnlyModel, view);

      // Set the view title and make it visible
      view.setTitle("Test Case 1: 5x7 Game Start - Red's Turn");
      view.refresh();
      view.setVisible(true);

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error starting game: " + e.getMessage());
    }
  }

  /**
   * Runs the second test case: Blue player's turn with a selected card.
   * This shows the board from Blue's perspective during their first turn,
   * with one of their cards already selected.
   */
  private static void runBlueTurnWithSelection() {
    try {
      // Create the model
      PawnsBoardBase<PawnsBoardBaseCard> model = new PawnsBoardBase<>();

      // Get the deck configuration file paths
      String redDeckPath = "docs" + File.separator + "RED5x7PawnsBoardBaseCompleteDeck.config";
      String blueDeckPath = "docs" + File.separator + "BLUE5x7PawnsBoardBaseCompleteDeck.config";

      // Initialize the game with 5 rows, 7 columns, and 7 cards per hand
      model.startGame(5, 7, redDeckPath, blueDeckPath, 7);

      // Create the view
      ReadOnlyPawnsBoard<PawnsBoardBaseCard, ?> readOnlyModel = model;
      PawnsBoardGUIView view = new PawnsBoardGraphicalView(readOnlyModel);

      // Create the stub controller
      PawnsBoardStubController controller = new PawnsBoardStubController();
      controller.initialize(readOnlyModel, view);

      // Red places a card to make it Blue's turn
      try {
        model.placeCard(1, 0, 0);
      } catch (IllegalAccessException | IllegalOwnerException | IllegalCardException e) {
        System.err.println("Error placing card: " + e.getMessage());
      }

      // Set the view title
      view.setTitle("Test Case 2: 5x7 Blue's Turn with Selected Card");

      // Refresh the view to reflect the updated state
      view.refresh();

      // Make the view visible
      view.setVisible(true);

      // Simulate Blue selecting a card (after a slight delay to ensure the view is visible)
      SwingUtilities.invokeLater(() -> {
        try {
          Thread.sleep(500); // Small delay to ensure the view is ready
          view.highlightCard(0); // Select the first card in Blue's hand
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error starting game: " + e.getMessage());
    }
  }

  /**
   * Runs the third test case: Mid-game state with cards played by both players.
   * This shows the board after several moves, with cards from both players on the board.
   */
  private static void runMidGameState() {
    try {
      // Create the model
      PawnsBoardBase<PawnsBoardBaseCard> model = new PawnsBoardBase<>();

      // Get the deck configuration file paths
      String redDeckPath = "docs" + File.separator + "RED5x7PawnsBoardBaseCompleteDeck.config";
      String blueDeckPath = "docs" + File.separator + "BLUE5x7PawnsBoardBaseCompleteDeck.config";

      // Initialize the game with 5 rows, 7 columns, and 7 cards per hand
      model.startGame(5, 7, redDeckPath, blueDeckPath, 7);

      // Create the view
      ReadOnlyPawnsBoard<PawnsBoardBaseCard, ?> readOnlyModel = model;
      PawnsBoardGUIView view = new PawnsBoardGraphicalView(readOnlyModel);

      // Create the stub controller
      PawnsBoardStubController controller = new PawnsBoardStubController();
      controller.initialize(readOnlyModel, view);

      // Simulate a series of moves to create a mid-game state
      try {
        // RED plays first move
        model.placeCard(1, 0, 0);
        // BLUE plays first move
        model.placeCard(4, 0, 6);
        // RED plays second move
        model.placeCard(3, 2, 0);
        // BLUE plays second move
        model.placeCard(1, 2, 6);
        // RED plays third move
        model.placeCard(5, 4, 0);
        // BLUE plays third move
        model.placeCard(5, 4, 6);
        // Now it's BLUE's turn
      } catch (IllegalAccessException | IllegalOwnerException | IllegalCardException e) {
        System.err.println("Error setting up mid-game state: " + e.getMessage());
      }

      // Set the view title
      view.setTitle("Test Case 3: 5x7 Non-Trivial, Mid-Game State - Blue's Turn");

      // Refresh the view to reflect the updated state
      view.refresh();

      // Make the view visible
      view.setVisible(true);

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error starting game: " + e.getMessage());
    }
  }
}
