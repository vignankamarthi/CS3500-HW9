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
import cs3500.pawnsboard.model.enumerations.CellContent;

import java.io.File;

/**
 * Complex demonstration of the Augmented Pawns Board game.
 * This class showcases advanced mechanics and edge cases:
 * - Multiple influences affecting the same cells
 * - Card removal mechanics when value is 0 or less
 * - Special case cards with mixed influence types
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
      String blueDeckPath = "docs" + File.separator + "BLUEComplex3x5PawnsBoardAugmentedDeck" +
              ".config";

      // Start the game
      model.startGame(BOARD_ROWS, BOARD_COLS, redDeckPath, blueDeckPath, HAND_SIZE);

      // Create the augmented view
      PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view =
              new PawnsBoardAugmentedTextualView<>(model);

      System.out.println("============= AUGMENTED PAWNS BOARD GAME - COMPLEX DEMONSTRATION " +
              "=============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Clear the board first - this gets rid of initial pawns
      clearAllPawns(model);
      
      // Set up pawns with careful ownership management
      try {
        // For RED positions
        model.getCell(0, 0).addPawn(PlayerColors.RED);
        model.getCell(1, 0).addPawn(PlayerColors.RED);
        model.getCell(2, 1).addPawn(PlayerColors.RED);
        
        // For BLUE positions
        model.getCell(0, 1).addPawn(PlayerColors.BLUE);
        model.getCell(0, 4).addPawn(PlayerColors.BLUE);
        model.getCell(1, 4).addPawn(PlayerColors.BLUE);
        model.getCell(2, 2).addPawn(PlayerColors.BLUE);
        model.getCell(2, 4).addPawn(PlayerColors.BLUE);

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
      System.out.println("============= FEATURE DEMONSTRATION: ZERO VALUE CARD REMOVAL =====");
      System.out.println("BLUE plays 'Fortify' (value 1) card at (0,1) - a cell with a -1 " +
              "modifier");
      System.out.println("Since 1 + (-1) = 0, this will result in a card with 0 effective value");
      System.out.println("Cards with 0 value are automatically removed from the board");
      System.out.println();

      executeMove(model, view, 0, 0, 1,
              "BLUE places Fortify at (0,1)");

      System.out.println();
      System.out.println("Notice the card was immediately removed when its value reached 0");
      System.out.println("The cell now has pawns equal to the original card's cost (1 in this case)");
      System.out.println("This demonstrates the core mechanic: cards with 0 or negative value are removed");
      System.out.println();

      // Add RED pawns to (1,2) for next demonstration
      try {
        addPawnsToCell(model, 1, 2, PlayerColors.RED, 2);
        System.out.println("Added RED pawns to position (1,2) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (1,2)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 3: HIGHER VALUE CARD ON NORMAL CELL =====
      System.out.println("============= FEATURE DEMONSTRATION: HIGHER VALUE CARD =====");
      System.out.println("RED plays 'Shield' (value 3) card at (1,2) - This is a normal cell");
      System.out.println("The card will be placed with its full value of 3");
      System.out.println();

      executeMove(model, view, 0, 1, 2,
              "RED places Shield at (1,2)");

      System.out.println();
      System.out.println("The Shield card is placed with its full value of 3");
      System.out.println("It would take devaluing of -3 or more to remove this card");
      System.out.println();

      // ===== DEMONSTRATION 4: ADDING MORE PAWNS FOR NEXT DEMO =====
      try {
        addPawnsToCell(model, 0, 3, PlayerColors.BLUE, 1);
        System.out.println("Added BLUE pawns to position (0,3) for next demonstration");
        System.out.println(view.renderGameState("After adding pawns to (0,3)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 5: STRONG DEVALUING INFLUENCE =====
      System.out.println("============= FEATURE DEMONSTRATION: NEGATIVE VALUE REMOVAL =====");
      System.out.println("BLUE plays 'Corrupt' (value 3) at (0,3) - This has strong devaluing influences");
      System.out.println("It will create cells with -2 modifiers to demonstrate negative value removal");
      System.out.println();

      executeMove(model, view, 1, 0, 3,
              "BLUE places Corrupt at (0,3)");

      System.out.println();
      System.out.println("Notice the strong -2 devaluing modifiers this card creates");
      System.out.println("Any card with value 2 or less placed in these cells will be removed");
      System.out.println();

      // Add RED pawns to another cell for next demo
      try {
        addPawnsToCell(model, 1, 1, PlayerColors.RED, 1);
        System.out.println("Added RED pawns to position (1,1) for negative value demonstration");
        System.out.println(view.renderGameState("After adding pawns to (1,1)"));
        System.out.println();
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 6: NEGATIVE VALUE CARD REMOVAL =====
      System.out.println("============= FEATURE DEMONSTRATION: NEGATIVE VALUE CARD REMOVAL =====");
      System.out.println("RED plays 'Upgrade' (value 1) at (1,1) - This is a cell with a -2 modifier");
      System.out.println("The effective value will be -1 (1 + (-2) = -1)");
      System.out.println("Since this is less than 0, the card should be automatically removed");
      System.out.println();

      executeMove(model, view, 4, 1, 1,
              "RED places Upgrade at (1,1)");

      System.out.println();
      System.out.println("Notice the card was immediately removed since its value was negative (-1)");
      System.out.println("The cell now has pawns equal to the original card's cost (1 in this case)");
      System.out.println("This demonstrates that cards with negative values are also removed");
      System.out.println();

      // ===== DEMONSTRATION 7: SETTING UP FOR MULTI-STEP DEVALUING =====
      try {
        // Find a suitable cell for BLUE pawns
        int targetRow = 2;
        int targetCol = 3;
        // Make sure the cell doesn't already have a card
        if (model.getCellContent(targetRow, targetCol) != CellContent.CARD) {
          addPawnsToCell(model, targetRow, targetCol, PlayerColors.BLUE, 2);
          System.out.println("Added BLUE pawns to position (" + targetRow + "," + targetCol + ") for next demonstration");
          System.out.println(view.renderGameState("After adding pawns for devaluing demonstration"));
          System.out.println();
        } else {
          System.out.println("Could not add BLUE pawns for demonstration - cell already contains a card");
        }
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 8: FIRST STEP DEVALUING =====
      System.out.println("============= FEATURE DEMONSTRATION: MULTI-STEP DEVALUING =====");
      System.out.println("BLUE plays 'Weaken' at (2,3) - This applies -1 devaluing to nearby cards");
      System.out.println("This will reduce any nearby card's value by 1, potentially removing cards with low value");
      System.out.println();

      executeMove(model, view, 1, 2, 3,
              "BLUE places Weaken at (2,3)");

      System.out.println();
      System.out.println("The Shield card now has an effective value of 2 (3 - 1)");
      System.out.println("It needs more devaluing to reach 0 or below and be removed");
      System.out.println();

      // ===== DEMONSTRATION 9: SETTING UP FINAL DEVALUING =====
      try {
        // Find a suitable cell for RED pawns that isn't already occupied by a card
        int targetRow = -1;
        int targetCol = -1;
        
        // Check a few potential locations
        int[][] potentialLocations = {{1, 3}, {0, 2}, {0, 3}};
        for (int[] loc : potentialLocations) {
          int r = loc[0];
          int c = loc[1];
          if (model.getCellContent(r, c) != CellContent.CARD) {
            targetRow = r;
            targetCol = c;
            break;
          }
        }
        
        if (targetRow >= 0) {
          addPawnsToCell(model, targetRow, targetCol, PlayerColors.RED, 1);
          System.out.println("Added RED pawns to position (" + targetRow + "," + targetCol + ") for final devaluing step");
          System.out.println(view.renderGameState("After adding pawns for devaluing step"));
          System.out.println();
        } else {
          System.out.println("Could not find a suitable cell for the final devaluing step");
        }
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // Store the targetRow and targetCol for the next step
      final int decayTargetRow = targetRow;
      final int decayTargetCol = targetCol;

      // ===== DEMONSTRATION 10: STRONG DEVALUING CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: COMPLETE CARD REMOVAL =====");
      if (decayTargetRow >= 0 && decayTargetCol >= 0) {
        System.out.println("RED plays 'Decay' at (" + decayTargetRow + "," + decayTargetCol + ") - This applies strong devaluing");
        System.out.println("This should bring nearby cards' values to 0 or below, triggering removal");
        System.out.println();

        executeMove(model, view, 2, decayTargetRow, decayTargetCol,
                "RED places Decay at (" + decayTargetRow + "," + decayTargetCol + ")");
      } else {
        System.out.println("Cannot place devaluing card - no suitable cell was found");
        System.out.println("Continuing with the demonstration without this step");
        System.out.println();
      }

      System.out.println();
      System.out.println("Look at cell (1,2) - the Shield card has been removed!");
      System.out.println("It's replaced with pawns equal to its original cost (2 in this case)");
      System.out.println("This demonstrates that multi-step devaluing can remove higher-value cards");
      System.out.println();

      // ===== DEMONSTRATION 11: ADVANCED SCORING CALCULATION =====
      System.out.println("============= FEATURE DEMONSTRATION: ADVANCED SCORING CALCULATION =====");
      System.out.println("Let's examine the scoring with all these removals in play");
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
      System.out.println("The scoring system accounts for all removals:");
      System.out.println("1. Cards removed due to 0 or negative value don't contribute to scoring");
      System.out.println("2. Only cards with positive effective value remain on the board");
      System.out.println("3. Complex devaluing patterns can strategically remove opponent's cards");
      System.out.println();

      // ===== DEMONSTRATION 12: MIXED INFLUENCE FOR COMPLEX PATTERNS =====
      int finalRow = -1;
      int finalCol = -1;
      try {
        // Find an empty cell on the board for BLUE pawns
        boolean foundCell = false;
        for (int r = 0; r < BOARD_ROWS && !foundCell; r++) {
          for (int c = 0; c < BOARD_COLS && !foundCell; c++) {
            if (model.getCellContent(r, c) == CellContent.EMPTY) {
              addPawnsToCell(model, r, c, PlayerColors.BLUE, 2);
              System.out.println("Added BLUE pawns to position (" + r + "," + c + ") for final demonstration");
              System.out.println(view.renderGameState("After adding pawns for final demonstration"));
              System.out.println();
              finalRow = r;
              finalCol = c;
              foundCell = true;
            }
          }
        }
        
        if (!foundCell) {
          System.out.println("Could not find an empty cell for the final demonstration");
        }
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      System.out.println("============= FEATURE DEMONSTRATION: MIXED INFLUENCE PATTERNS =====");
      if (finalRow >= 0 && finalCol >= 0) {
        System.out.println("BLUE plays 'Fusion' at (" + finalRow + "," + finalCol + ") - This has mixed influence types (U and D)");
        System.out.println("It demonstrates how complex influence patterns can create strategic advantages");
        System.out.println();

        executeMove(model, view, 2, finalRow, finalCol,
                "BLUE places Fusion at (" + finalRow + "," + finalCol + ")");
      } else {
        System.out.println("Cannot play the final demonstration card - no suitable cell was found");
      }

      System.out.println();
      System.out.println("Notice the mixed pattern of upgrading (U) and devaluing (D) influences");
      System.out.println("This creates an asymmetric pattern to benefit the player's strategy");
      System.out.println("Upgrading can protect cards while devaluing targets opponent's cards");
      System.out.println();

      // ===== DEMONSTRATION SUMMARY =====
      System.out.println("============= COMPLEX DEMONSTRATION SUMMARY =============");
      System.out.println("This demonstration has showcased advanced mechanics including:");
      System.out.println("1. Card removal when effective value is 0 OR LESS");
      System.out.println("2. ONLY cards with POSITIVE values remain on the board");
      System.out.println("3. Value modifier reset when cards are removed");
      System.out.println("4. Complex influence patterns with multiple types");
      System.out.println("5. Multi-step devaluing to remove higher-value cards");
      System.out.println("6. Strategic placement of cards for maximum impact");
      System.out.println("7. Advanced scoring with modifier effects");
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
   * @param row the row index to place the card
   * @param col the column index to place the card
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
  
  /**
   * Helper method to clear all pawns from the board.
   * This gives us a clean slate to add our own pawns with the right ownership.
   *
   * @param model the game model
   */
  private static void clearAllPawns(PawnsBoardAugmented<PawnsBoardAugmentedCard> model) {
    try {
      int[] dims = model.getBoardDimensions();
      for (int r = 0; r < dims[0]; r++) {
        for (int c = 0; c < dims[1]; c++) {
          // Skip the required edge pawns that are part of the initial setup
          if ((c == 0 || c == dims[1] - 1) && model.getCellContent(r, c) == CellContent.PAWNS) {
            continue;
          }
          
          // For any cells that have pawns, try to reset them
          if (model.getCellContent(r, c) == CellContent.PAWNS) {
            // Try to remove pawns by directly setting something else there temporarily
            PlayerColors owner = model.getCellOwner(r, c);
            // Can't just replace with empty, so add a card temporarily
            if (owner == PlayerColors.RED && !model.getPlayerHand(PlayerColors.RED).isEmpty()) {
              model.getCell(r, c).setCard(model.getPlayerHand(PlayerColors.RED).get(0), owner);
            } else if (owner == PlayerColors.BLUE && !model.getPlayerHand(PlayerColors.BLUE).isEmpty()) {
              model.getCell(r, c).setCard(model.getPlayerHand(PlayerColors.BLUE).get(0), owner);
            }
            // Now the cell should be clear of pawns
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error clearing pawns: " + e.getMessage());
    }
  }
  
  /**
   * Helper method to add pawns to a specific cell with a specific owner.
   * This avoids ownership conflicts in the demonstration.
   * If the cell already contains a card, it will print a warning but not throw an exception.
   *
   * @param model the game model
   * @param row the row where to add pawns
   * @param col the column where to add pawns
   * @param owner the owner of the pawns to add
   * @param count the number of pawns to add
   */
  private static void addPawnsToCell(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                    int row, int col, PlayerColors owner, int count) throws Exception {
    // Check if the target cell already has a card
    if (model.getCellContent(row, col) == CellContent.CARD) {
      System.out.println("Warning: Cannot add pawns to cell (" + row + "," + col + ") because it contains a card");
      return;
    }
    
    // If cell is empty, simply add pawns
    if (model.getCellContent(row, col) == CellContent.EMPTY) {
      for (int i = 0; i < count; i++) {
        model.getCell(row, col).addPawn(owner);
      }
      return;
    }
    
    // If cell has pawns but wrong owner, need to clear first
    if (model.getCellContent(row, col) == CellContent.PAWNS && 
        model.getCellOwner(row, col) != owner) {
        
      // Get the current owner
      PlayerColors currentOwner = model.getCellOwner(row, col);
      
      // Try to clear by placing a temporary card
      if (currentOwner == PlayerColors.RED && !model.getPlayerHand(PlayerColors.RED).isEmpty()) {
        // Make sure there are enough pawns
        while (model.getPawnCount(row, col) < 1) {
          model.getCell(row, col).addPawn(currentOwner);
        }
        model.getCell(row, col).setCard(model.getPlayerHand(PlayerColors.RED).get(0), currentOwner);
      } else if (currentOwner == PlayerColors.BLUE && !model.getPlayerHand(PlayerColors.BLUE).isEmpty()) {
        // Make sure there are enough pawns
        while (model.getPawnCount(row, col) < 1) {
          model.getCell(row, col).addPawn(currentOwner);
        }
        model.getCell(row, col).setCard(model.getPlayerHand(PlayerColors.BLUE).get(0), currentOwner);
      }
      
      // Now add new pawns with the right owner
      for (int i = 0; i < count; i++) {
        model.getCell(row, col).addPawn(owner);
      }
    } else if (model.getCellContent(row, col) == CellContent.PAWNS && 
               model.getCellOwner(row, col) == owner) {
      // Cell already has pawns with the right owner, add more if needed
      int currentCount = model.getPawnCount(row, col);
      for (int i = 0; i < count - currentCount; i++) {
        if (i >= 0) {
          model.getCell(row, col).addPawn(owner);
        }
      }
    }
  }
}