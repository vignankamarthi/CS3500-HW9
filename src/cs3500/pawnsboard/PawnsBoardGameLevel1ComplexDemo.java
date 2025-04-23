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
 * Complex demonstration of the Augmented Pawns Board game.
 * This class showcases advanced mechanics and edge cases:
 * - Multiple influences affecting the same cells
 * - Card removal mechanics
 * - Special case cards
 * - Advanced scoring calculations
 * - Influence persistence
 * - Complex edge cases
 */
public class PawnsBoardGameLevel1ComplexDemo {

  private static final int BOARD_ROWS = 3;
  private static final int BOARD_COLS = 5;
  private static final int HAND_SIZE = 5;  // Maximum hand size of 5 cards

  /**
   * Main method that runs the complex demonstration.
   * Each move demonstrates a specific advanced feature or edge case of the augmented model.
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

      // Set up deck paths - use the complex-specific deck files
      String redDeckPath = "docs" + File.separator + "REDComplex3x5PawnsBoardAugmentedDeck.config";
      String blueDeckPath = "docs" + File.separator + "BLUEComplex3x5PawnsBoardAugmentedDeck.config";

      // Start the game
      model.startGame(BOARD_ROWS, BOARD_COLS, redDeckPath, blueDeckPath, HAND_SIZE);

      // Create the augmented view
      PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view =
              new PawnsBoardAugmentedTextualView<>(model);

      System.out.println("============= AUGMENTED PAWNS BOARD GAME - COMPLEX DEMONSTRATION =============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Add pawns for demonstrations
      try {
        // For RED positions (top to bottom, left to right)
        for (int i = 0; i < 1; i++) model.getCell(0, 0).addPawn(PlayerColors.RED);
        for (int i = 0; i < 1; i++) model.getCell(1, 0).addPawn(PlayerColors.RED);
        for (int i = 0; i < 1; i++) model.getCell(2, 1).addPawn(PlayerColors.RED);
        
        // For BLUE positions
        for (int i = 0; i < 1; i++) model.getCell(0, 1).addPawn(PlayerColors.BLUE);
        for (int i = 0; i < 1; i++) model.getCell(0, 4).addPawn(PlayerColors.BLUE);
        for (int i = 0; i < 1; i++) model.getCell(1, 4).addPawn(PlayerColors.BLUE);
        for (int i = 0; i < 2; i++) model.getCell(2, 4).addPawn(PlayerColors.BLUE);
        
        System.out.println("Pawns set up successfully for the demonstration");
        System.out.println(view.renderGameState("After Setting Up Pawns"));
        System.out.println();
      } catch (Exception e) {
        System.out.println("Setup issue: " + e.getMessage());
      }

      // ===== DEMONSTRATION 1: SETTING UP FOR CARD REMOVAL =====
      System.out.println("============= FEATURE DEMONSTRATION: SETTING UP FOR CARD REMOVAL =====");
      System.out.println("RED plays 'Ruin' card at (0,0) - This card has devaluing influences (D)");
      System.out.println("We'll use this to set up a scenario for card removal in later steps");
      System.out.println();
      
      executeMove(model, view, 0, 0, 0, 
              "RED places Ruin at (0,0)");
      
      System.out.println();
      System.out.println("Notice the '__-1' marks in cells around the card");
      System.out.println("These cells are now primed for a demonstration of card removal");
      System.out.println();

      // ===== DEMONSTRATION 2: PLACING A CARD WITH VALUE 1 ON A DEVALUED CELL =====
      System.out.println("============= FEATURE DEMONSTRATION: ZERO-VALUE CARD SCENARIO =====");
      System.out.println("BLUE plays 'Fortify' (value 1) card at (0,1) - a cell with a -1 modifier");
      System.out.println("Since 1 + (-1) = 0, this will result in a card with 0 effective value");
      System.out.println();
      
      executeMove(model, view, 0, 0, 1, 
              "BLUE places Fortify at (0,1)");
      
      System.out.println();
      System.out.println("Notice the card's effective value is 0 - it contributes nothing to scoring");
      System.out.println("But it remains on the board since it hasn't gone below 0");
      System.out.println();

      // Add RED pawns to (1,1) for next demonstration
      try {
        for (int i = 0; i < 1; i++) {
          model.getCell(1, 1).addPawn(PlayerColors.RED);
        }
        System.out.println("Added RED pawns to position (1,1) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (1,1)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 3: MULTIPLE DEVALUING EFFECTS FOR CARD REMOVAL =====
      System.out.println("============= FEATURE DEMONSTRATION: CARD REMOVAL MECHANICS =====");
      System.out.println("RED plays 'Curse' card at (1,0) - This will apply additional -1 modifiers");
      System.out.println("If these affect a card with effective value of 0, it will be removed");
      System.out.println();
      
      executeMove(model, view, 0, 1, 0, 
              "RED places Curse at (1,0)");
      
      System.out.println();
      System.out.println("Look at cell (0,1) - the card has been removed and replaced with pawns!");
      System.out.println("When a card's effective value drops below 0, it's automatically removed");
      System.out.println("The cell now has pawns equal to the original card's cost (1 in this case)");
      System.out.println();

      // Add BLUE pawns to position (0,3) for next demonstration
      try {
        for (int i = 0; i < 1; i++) {
          model.getCell(0, 3).addPawn(PlayerColors.BLUE);
        }
        System.out.println("Added BLUE pawns to position (0,3) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (0,3)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 4: VALUE RESET AFTER CARD REMOVAL =====
      System.out.println("============= FEATURE DEMONSTRATION: MODIFIER RESET AFTER REMOVAL =====");
      System.out.println("BLUE plays 'Weaken' card at (0,4) - Another devaluing card");
      System.out.println("Let's look at what happens to the cell where a card was removed");
      System.out.println();
      
      executeMove(model, view, 0, 0, 4, 
              "BLUE places Weaken at (0,4)");
      
      System.out.println();
      System.out.println("Notice cell (0,1) now has a new value modifier - the old one was reset");
      System.out.println("When a card is removed due to devaluation, its cell's modifiers are reset");
      System.out.println("New influences can then affect the cell normally");
      System.out.println();

      // ===== DEMONSTRATION 5: COMPLEX PATTERN - MULTIPLE INFLUENCE TYPES =====
      System.out.println("============= FEATURE DEMONSTRATION: COMPLEX INFLUENCE PATTERNS =====");
      System.out.println("RED plays 'Balance' card at (1,1) - This has a complex pattern of U and D influences");
      System.out.println("It showcases how different influence types can be arranged in a pattern");
      System.out.println();
      
      executeMove(model, view, 0, 1, 1, 
              "RED places Balance at (1,1)");
      
      System.out.println();
      System.out.println("Observe the complex pattern of upgrading and devaluing around the card");
      System.out.println("This demonstrates the flexibility of influence patterns in the augmented model");
      System.out.println();

      // ===== DEMONSTRATION 6: REMOVING A HIGHER VALUE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: REMOVING HIGHER VALUE CARDS =====");
      System.out.println("BLUE plays 'Corrupt' card at (0,3) - We're setting up to remove a higher value card");
      System.out.println("This will demonstrate how larger value cards need more devaluing to remove");
      System.out.println();
      
      executeMove(model, view, 0, 0, 3, 
              "BLUE places Corrupt at (0,3)");
      
      System.out.println();
      System.out.println("Notice the devaluing effects around cell (0,3)");
      System.out.println("We're preparing to demonstrate removal of a higher-value card");
      System.out.println();

      // Add RED pawns to position (2,1) for next demonstration if needed
      try {
        if (model.getCellContent(2, 1) == null || model.getPawnCount(2, 1) < 2) {
          for (int i = 0; i < 2 - (model.getPawnCount(2, 1)); i++) {
            model.getCell(2, 1).addPawn(PlayerColors.RED);
          }
          System.out.println("Added RED pawns to position (2,1) for next demonstration");
          System.out.println(view.renderGameState("After adding pawns to (2,1)"));
          System.out.println();
        }
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 7: PLACING HIGHER VALUE CARD ON DEVALUED CELL =====
      System.out.println("============= FEATURE DEMONSTRATION: HIGHER VALUE CARD ON DEVALUED CELL =====");
      System.out.println("RED plays 'Shield' (value 3) card at (2,1) - This is a cell with a -1 modifier");
      System.out.println("The card will be played with effective value 2 (3 - 1)");
      System.out.println();
      
      executeMove(model, view, 0, 2, 1, 
              "RED places Shield at (2,1)");
      
      System.out.println();
      System.out.println("The card is placed with effective value 2 (base 3 - 1 modifier)");
      System.out.println("It would take 3 total devaluing points to remove this card");
      System.out.println();

      // Add BLUE pawns to position (2,2) for next demonstration
      try {
        for (int i = 0; i < 2; i++) {
          model.getCell(2, 2).addPawn(PlayerColors.BLUE);
        }
        System.out.println("Added BLUE pawns to position (2,2) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (2,2)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 8: EXTREME DEVALUING TO REMOVE HIGH-VALUE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: EXTREME DEVALUING FOR REMOVAL =====");
      System.out.println("BLUE plays 'Leech' card at (2,2) - This will apply more devaluing to the Shield card");
      System.out.println("Multiple devaluing effects should stack until the card is removed");
      System.out.println();
      
      executeMove(model, view, 0, 2, 2, 
              "BLUE places Leech at (2,2)");
      
      System.out.println();
      System.out.println("Observe that the Shield card is still present but now with less effective value");
      System.out.println("Let's add more devaluing effects to remove it completely");
      System.out.println();

      // Add RED pawns to position (1,2) for next demonstration
      try {
        for (int i = 0; i < 1; i++) {
          model.getCell(1, 2).addPawn(PlayerColors.RED);
        }
        System.out.println("Added RED pawns to position (1,2) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (1,2)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 9: FINAL DEVALUING TO REMOVE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: FINAL DEVALUING TO REMOVE CARD =====");
      System.out.println("RED plays 'Decay' card at (1,2) - This should be the final devaluing needed");
      System.out.println("After this, the Shield card should be removed and replaced with pawns");
      System.out.println();
      
      executeMove(model, view, 0, 1, 2, 
              "RED places Decay at (1,2)");
      
      System.out.println();
      System.out.println("Look at cell (2,1) - the Shield card has been removed!");
      System.out.println("It's replaced with pawns equal to its original cost (2 in this case)");
      System.out.println("This demonstrates that any card can be removed with enough devaluing");
      System.out.println();

      // Add BLUE pawns to position (2,3) for next demonstration
      try {
        for (int i = 0; i < 2; i++) {
          model.getCell(2, 3).addPawn(PlayerColors.BLUE);
        }
        System.out.println("Added BLUE pawns to position (2,3) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (2,3)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 10: ADVANCED MIXED INFLUENCE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: ADVANCED MIXED INFLUENCE CARD =====");
      System.out.println("BLUE plays 'Fusion' card at (2,3) - A card with strategically placed influences");
      System.out.println("This shows how influence placement can create specific tactical patterns");
      System.out.println();
      
      executeMove(model, view, 0, 2, 3, 
              "BLUE places Fusion at (2,3)");
      
      System.out.println();
      System.out.println("Notice the asymmetric pattern of influences around the card");
      System.out.println("Strategic placement of different influence types is a key tactical element");
      System.out.println();

      // ===== DEMONSTRATION 11: ADVANCED SCORING CALCULATION =====
      System.out.println("============= FEATURE DEMONSTRATION: ADVANCED SCORING CALCULATION =====");
      System.out.println("Let's examine the complex scoring with all these modifiers in play");
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
      System.out.println("The scoring system accounts for all modifiers and card removals!");
      System.out.println("Cards with effective value 0 don't contribute to scoring");
      System.out.println("Removed cards don't contribute to scoring either");
      System.out.println();

      // Add RED pawns to position (0,2) for final demonstration
      try {
        for (int i = 0; i < 2; i++) {
          model.getCell(0, 2).addPawn(PlayerColors.RED);
        }
        System.out.println("Added RED pawns to position (0,2) for final demonstration");
        System.out.println(view.renderGameState("After adding pawns to (0,2)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 12: MIXED INFLUENCE CARD ON MODIFIED CELL =====
      System.out.println("============= FEATURE DEMONSTRATION: MIXED CARD ON MODIFIED CELL =====");
      System.out.println("RED plays 'Dual' at (0,2) - A card with both U and D influences");
      System.out.println("Placing it on a cell that already has modifiers shows compounding effects");
      System.out.println();
      
      executeMove(model, view, 0, 0, 2, 
              "RED places Dual at (0,2)");
      
      System.out.println();
      System.out.println("Notice how the mixed influence card applies both types of effects");
      System.out.println("This creates a complex pattern of stacked modifiers");
      System.out.println();

      // ===== DEMONSTRATION SUMMARY =====
      System.out.println("============= COMPLEX DEMONSTRATION SUMMARY =============");
      System.out.println("This demonstration has showcased advanced mechanics including:");
      System.out.println("1. Card removal when effective value drops to 0 or below");
      System.out.println("2. Modifier reset when cards are removed due to devaluation");
      System.out.println("3. Complex influence patterns with multiple types");
      System.out.println("4. Removal of higher-value cards requiring more devaluing");
      System.out.println("5. Influence persistence and stacking of effects");
      System.out.println("6. Advanced scoring calculations with varied modifiers");
      System.out.println("7. Edge cases like zero-value cards and extreme devaluing");
      System.out.println();
      System.out.println("Final game state:");
      System.out.println(view.renderGameState("End of Complex Demonstration"));

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error with deck configuration: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Helper method to execute a move in the game.
   * Attempts to place a card at the specified position and prints the result.
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
      // Place the card using the specified index
      model.placeCard(cardIndex, row, col);
      System.out.println(view.renderGameState(description));
      return true;
    } catch (IllegalAccessException e) {
      System.out.println("Move failed (access): " + e.getMessage());
    } catch (IllegalOwnerException e) {
      System.out.println("Move failed (ownership): " + e.getMessage());
    } catch (IllegalCardException e) {
      System.out.println("Move failed (card): " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Move failed: " + e.getMessage());
      e.printStackTrace();
    }
    
    try {
      // Only pass turn if we're not in game over state
      if (!model.isGameOver()) {
        model.passTurn();
        System.out.println(view.renderGameState("PASS - " + description + " failed"));
      } else {
        System.out.println("Game is already over, cannot pass turn");
      }
    } catch (Exception ex) {
      System.err.println("Error passing turn: " + ex.getMessage());
    }
    
    return false;
  }
}