package cs3500.pawnsboard;

import cs3500.pawnsboard.model.PawnsBoardAugmented;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.deckbuilder.PawnsBoardAugmentedDeckBuilder;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import cs3500.pawnsboard.view.PawnsBoardAugmentedTextualView;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import java.io.File;

/**
 * Demonstration of the Augmented Pawns Board game with basic features.
 * This class showcases the core mechanics of the enhanced model:
 * - Basic upgrading/devaluing influences
 * - Value modifier stacking (both positive and negative)
 * - Mixed influence cards (all possible combinations)
 * - Scoring with modifiers
 */
//TODO: Perfectly review demonstrations
public class PawnsBoardGameLevel1BasicDemo {

  private static final int BOARD_ROWS = 3;
  private static final int BOARD_COLS = 5;
  private static final int HAND_SIZE = 5;

  /**
   * Main method that runs the demonstration.
   * Each move demonstrates a specific feature of the augmented model.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    try {
      // Initialize model and view
      PawnsBoardAugmented<PawnsBoardAugmentedCard> model = new PawnsBoardAugmented<>(
              new PawnsBoardAugmentedDeckBuilder<>(),
              new InfluenceManager()
      );

      // Set up deck paths
      String redDeckPath = "docs" + File.separator + "RED3x5PawnsBoardAugmentedDeck.config";
      String blueDeckPath = "docs" + File.separator + "BLUE3x5PawnsBoardAugmentedDeck.config";

      // Start the game
      model.startGame(BOARD_ROWS, BOARD_COLS, redDeckPath, blueDeckPath, HAND_SIZE);

      // Create the augmented view
      PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view =
              new PawnsBoardAugmentedTextualView<>(model);

      System.out.println("============= AUGMENTED PAWNS BOARD GAME - BASIC DEMONSTRATION " +
              "=============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Add pawns for demonstrations
      try {
        // For RED positions (top to bottom, left to right)
        for (int i = 0; i < 1; i++) {
          model.getCell(0, 0).addPawn(PlayerColors.RED);
        }
        for (int i = 0; i < 1; i++) {
          model.getCell(1, 0).addPawn(PlayerColors.RED);
        }
        for (int i = 0; i < 1; i++) {
          model.getCell(2, 0).addPawn(PlayerColors.RED);
        }
        for (int i = 0; i < 2; i++) {
          model.getCell(0, 2).addPawn(PlayerColors.RED);
        }
        for (int i = 0; i < 3; i++) {
          model.getCell(2, 2).addPawn(PlayerColors.RED);
        }

        // For BLUE positions
        for (int i = 0; i < 1; i++) {
          model.getCell(0, 4).addPawn(PlayerColors.BLUE);
        }
        for (int i = 0; i < 1; i++) {
          model.getCell(1, 4).addPawn(PlayerColors.BLUE);
        }
        for (int i = 0; i < 2; i++) {
          model.getCell(2, 4).addPawn(PlayerColors.BLUE);
        }

        for (int i = 0; i < 3; i++) {
          model.getCell(1, 2).addPawn(PlayerColors.BLUE);
        }

        System.out.println("Pawns set up successfully for the demonstration");
        System.out.println(view.renderGameState("After Setting Up Pawns"));
        System.out.println();
      } catch (Exception e) {
        System.out.println("Setup issue: " + e.getMessage());
      }

      // ===== DEMONSTRATION 1: PURE UPGRADING INFLUENCE =====
      System.out.println("============= DEMONSTRATION 1: PURE UPGRADING INFLUENCE =============");
      System.out.println("RED plays 'Upgrade' card at (0,0) - This card has only upgrading " +
              "influences (U)");
      System.out.println("We expect to see cells around the card get +1 to their value modifiers");
      System.out.println();

      executeMove(model, view, 0, 0, 0,
              "RED places Upgrade at (0,0)");

      System.out.println();
      System.out.println("Notice the '__+1' marks in cells around the card - these are value " +
              "modifiers");
      System.out.println("Even empty cells can hold value modifiers for future cards");
      System.out.println();

      // ===== DEMONSTRATION 2: PURE DEVALUING INFLUENCE =====
      System.out.println("============= DEMONSTRATION 2: PURE DEVALUING INFLUENCE =============");
      System.out.println("BLUE plays 'Corrupt' card at (0,4) - This card has only devaluing " +
              "influences (D)");
      System.out.println("We expect to see cells around the card get -1 to their value modifiers");
      System.out.println();

      executeMove(model, view, 0, 0, 4,
              "BLUE places Corrupt at (0,4)");

      System.out.println();
      System.out.println("Notice the '__-1' marks in cells around the card - these are negative " +
              "modifiers");
      System.out.println("Devaluing influences decrease the effective value of cards");
      System.out.println();

      // ===== DEMONSTRATION 3: POSITIVE STACKING (+2) =====
      System.out.println("============= DEMONSTRATION 3: POSITIVE STACKING (+2) =============");
      System.out.println("RED plays 'Bless' card at (1,0) - Another card with pure upgrading " +
              "influences");
      System.out.println("This will overlap with the previous 'Upgrade' card's influence area");
      System.out.println("We should see +2 modifiers where both influences overlap");
      System.out.println();

      executeMove(model, view, 0, 1, 0,
              "RED places Bless at (1,0)");

      System.out.println();
      System.out.println("Look at cell (0,1) - it now shows a +2 modifier!");
      System.out.println("This proves that upgrading effects stack additively");
      System.out.println();

      // ===== DEMONSTRATION 4: NEGATIVE STACKING (-2) =====
      System.out.println("============= DEMONSTRATION 4: NEGATIVE STACKING (-2) =============");
      System.out.println("BLUE plays 'Wither' card at (1,4) - Another card with pure devaluing " +
              "influences");
      System.out.println("This will overlap with the previous 'Corrupt' card's influence area");
      System.out.println("We should see -2 modifiers where both influences overlap");
      System.out.println();

      executeMove(model, view, 0, 1, 4,
              "BLUE places Wither at (1,4)");

      System.out.println();
      System.out.println("Look at cell (1,3) - it now shows a -2 modifier!");
      System.out.println("This proves that devaluing effects stack additively");
      System.out.println();

      // ===== DEMONSTRATION 5: PURE REGULAR INFLUENCE =====
      System.out.println("============= DEMONSTRATION 5: PURE REGULAR INFLUENCE =============");
      System.out.println("RED plays 'Spark' card at (2,0) - This card has only regular " +
              "influences (I)");
      System.out.println("It will add pawns to empty cells and convert opponent pawns");
      System.out.println();

      executeMove(model, view, 0, 2, 0,
              "RED places Spark at (2,0)");

      System.out.println();
      System.out.println("Notice the additional pawns added to the board by the regular influence");
      System.out.println("Regular influence works alongside the new upgrading/devaluing effects");
      System.out.println();

      // ===== DEMONSTRATION 6: MIXED UI INFLUENCE (UPGRADING + REGULAR) =====
      System.out.println("============= DEMONSTRATION 6: MIXED UI INFLUENCE (UPGRADING + " +
              "REGULAR) =============");
      System.out.println("BLUE plays 'Empower' card at (2,4) - This card has both upgrading and " +
              "regular influences");
      System.out.println("It will add pawns AND add value modifiers to different cells " +
              "simultaneously");
      System.out.println();

      executeMove(model, view, 0, 2, 4,
              "BLUE places Empower at (2,4)");

      System.out.println();
      System.out.println("Notice this card adds both pawns AND value modifiers in its " +
              "influence area");
      System.out.println("Cards can have multiple influence types affecting different cells");
      System.out.println();

      // ===== DEMONSTRATION 7: MIXED DI INFLUENCE (DEVALUING + REGULAR) =====
      System.out.println("============= DEMONSTRATION 7: MIXED DI INFLUENCE (DEVALUING + " +
              "REGULAR) =============");
      System.out.println("RED plays 'Curse' card at (0,2) - This card has both devaluing and " +
              "regular influences");
      System.out.println("It will add pawns AND add negative value modifiers to different cells " +
              "simultaneously");
      System.out.println();

      executeMove(model, view, 0, 0, 2,
              "RED places Curse at (0,2)");

      System.out.println();
      System.out.println("Look at how this card combines regular and devaluing influences in one " +
              "pattern");
      System.out.println("Some cells get pawns, while others get negative value modifiers");
      System.out.println();

      // ===== DEMONSTRATION 8: MIXED UD INFLUENCE (UPGRADING + DEVALUING) =====
      System.out.println("============= DEMONSTRATION 8: MIXED UD INFLUENCE (UPGRADING + " +
              "DEVALUING) =============");
      System.out.println("BLUE plays 'Harmony' card at (1,2) - This card has both upgrading and " +
              "devaluing influences");
      System.out.println("It has NO regular influence, but combines the two new influence types");
      System.out.println();

      executeMove(model, view, 0, 1, 2,
              "BLUE places Harmony at (1,2)");

      System.out.println();
      System.out.println("Notice this card applies BOTH positive and negative modifiers in " +
              "different cells!");
      System.out.println("This demonstrates the full flexibility of the augmented influence " +
              "system");
      System.out.println();

      // ===== DEMONSTRATION 9: FULLY MIXED INFLUENCE (U+D+I) =====
      System.out.println("============= DEMONSTRATION 9: FULLY MIXED INFLUENCE (U+D+I) " +
              "=============");
      System.out.println("RED plays 'Balance' card at (2,2) - This card has ALL THREE " +
              "influence types");
      System.out.println("It combines upgrading, devaluing, AND regular influences in one pattern");
      System.out.println();

      executeMove(model, view, 0, 2, 2,
              "RED places Balance at (2,2)");

      System.out.println();
      System.out.println("This card demonstrates the most complex influence pattern possible:");
      System.out.println("- Upgrading: Some cells get positive modifiers");
      System.out.println("- Devaluing: Some cells get negative modifiers");
      System.out.println("- Regular: Some cells get pawns added or converted");
      System.out.println();

      // ===== DEMONSTRATION 10: SCORING WITH MODIFIERS =====
      System.out.println("============= DEMONSTRATION 10: SCORING WITH MODIFIERS =============");
      System.out.println("Let's examine how all these modifiers affect the game score");
      System.out.println();

      // Display the current score
      int[] scores = model.getTotalScore();
      int[] row0Scores = model.getRowScores(0);
      int[] row1Scores = model.getRowScores(1);
      int[] row2Scores = model.getRowScores(2);

      System.out.println("Row 0 scores: RED = " + row0Scores[0] + ", BLUE = " + row0Scores[1]);
      System.out.println("Row 1 scores: RED = " + row1Scores[0] + ", BLUE = " + row1Scores[1]);
      System.out.println("Row 2 scores: RED = " + row2Scores[0] + ", BLUE = " + row2Scores[1]);
      System.out.println("Total scores: RED = " + scores[0] + ", BLUE = " + scores[1]);
      System.out.println();
      System.out.println("The scoring system accounts for all modifiers when calculating scores:");
      System.out.println("- Positive modifiers INCREASE a card's contribution to score");
      System.out.println("- Negative modifiers DECREASE a card's contribution to score");
      System.out.println("- Cards with effective value 0 contribute nothing to scoring");
      System.out.println();

      // ===== DEMONSTRATION SUMMARY =====
      System.out.println("============= BASIC DEMONSTRATION SUMMARY =============");
      System.out.println("This demonstration has showcased the complete augmented influence " +
              "system:");
      System.out.println();
      System.out.println("INFLUENCE TYPES:");
      System.out.println("1. Pure upgrading influence (U): Increases cell values (+1)");
      System.out.println("2. Pure devaluing influence (D): Decreases cell values (-1)");
      System.out.println("3. Pure regular influence (I): Adds or converts pawns");
      System.out.println();
      System.out.println("MIXED INFLUENCE COMBINATIONS:");
      System.out.println("4. Upgrading + Regular (UI): Adds both pawns and positive modifiers");
      System.out.println("5. Devaluing + Regular (DI): Adds both pawns and negative modifiers");
      System.out.println("6. Upgrading + Devaluing (UD): Adds both positive and negative " +
              "modifiers");
      System.out.println("7. All three types (UDI): Complete mix of all influence types");
      System.out.println();
      System.out.println("STACKING EFFECTS:");
      System.out.println("8. Positive stacking: Multiple +1 modifiers combine to +2 or higher");
      System.out.println("9. Negative stacking: Multiple -1 modifiers combine to -2 or lower");
      System.out.println("10. Scoring: Cards' effective values = original value + modifiers");
      System.out.println();

      // Display final score
      int[] finalScores = model.getTotalScore();
      System.out.println("Final scores: RED = " + finalScores[0] + ", BLUE = " + finalScores[1]);

      System.out.println();
      System.out.println("Final game state:");
      System.out.println(view.renderGameState("End of Basic Demonstration"));

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error with deck configuration: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Helper method to execute a move in the game.
   * Simply places the card at the specified position and prints the result.
   *
   * @param model the game model
   * @param view the game view
   * @param cardIndex the index of the card in the current player's hand (0-based)
   * @param row the row to place the card
   * @param col the column to place the card
   * @param description a description of the move for the output
   * @return true if the move was successful, false otherwise
   */
  private static boolean executeMove(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                     PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view,
                                     int cardIndex, int row, int col, String description) {
    try {
      // Place the card
      model.placeCard(cardIndex, row, col);
      System.out.println(view.renderGameState(description));
      return true;
    } catch (IllegalAccessException | IllegalOwnerException | IllegalCardException e) {
      // Just log the error
      System.out.println("Move failed: " + e.getMessage());
      System.out.println(view.renderGameState("Failed: " + description));
      return false;
    } catch (Exception e) {
      System.out.println("Unexpected error: " + e.getMessage());
      return false;
    }
  }
}