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
 * - Value modifier stacking
 * - Card-specific influences
 * - Simple scoring demonstrations
 */
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

      System.out.println("============= AUGMENTED PAWNS BOARD GAME - BASIC DEMONSTRATION =============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Add pawns as needed for our demonstration
      try {
        // Add a second RED pawn to position (0,0) to support cost-2 card placement
        model.getCell(0, 0).addPawn(PlayerColors.RED);
        
        // Add a second BLUE pawn to position (0,4) to support cost-2 card placement
        model.getCell(0, 4).addPawn(PlayerColors.BLUE);
      } catch (Exception e) {
        System.err.println("Error adding initial pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 1: BASIC UPGRADING INFLUENCE =====
      System.out.println("============= FEATURE DEMONSTRATION: BASIC UPGRADING INFLUENCE =============");
      System.out.println("RED plays 'Upgrade' card at (0,0) - This card has upgrading influences (U)");
      System.out.println("We expect to see cells around the card get +1 to their value modifiers");
      System.out.println();
      
      // Use position (0,0) which has RED pawns initially
      executeMove(model, view, 3, 0, 0, 
              "RED places Upgrade at (0,0)");
      
      System.out.println();
      System.out.println("Notice the '__+1' marks in cells around the card - these are value modifiers");
      System.out.println("Even empty cells can hold value modifiers for future cards");
      System.out.println();

      // ===== DEMONSTRATION 2: BASIC DEVALUING INFLUENCE =====
      System.out.println("============= FEATURE DEMONSTRATION: BASIC DEVALUING INFLUENCE =============");
      System.out.println("BLUE plays 'Corrupt' card at (0,4) - This card has devaluing influences (D)");
      System.out.println("We expect to see cells around the card get -1 to their value modifiers");
      System.out.println();
      
      // Use position (0,4) which has BLUE pawns initially
      executeMove(model, view, 4, 0, 4, 
              "BLUE places Corrupt at (0,4)");
      
      System.out.println();
      System.out.println("Notice the '__-1' marks in cells around the card - these are negative modifiers");
      System.out.println("Devaluing influences decrease the effective value of cards");
      System.out.println();

      // ===== DEMONSTRATION 3: MORE BASIC GAMEPLAY =====
      System.out.println("============= FEATURE DEMONSTRATION: BUILDING THE BOARD =============");
      System.out.println("RED plays 'Valor' card at (1,0) - This adds more elements to the board");
      System.out.println("This card uses regular influence to add pawns to adjacent cells");
      System.out.println();
      
      // Use position (1,0) which has RED pawns initially
      executeMove(model, view, 0, 1, 0, 
              "RED places Valor at (1,0)");
      
      System.out.println();
      System.out.println("Notice the additional pawns added to the board by the regular influence");
      System.out.println("The game board is building up with various elements and modifiers");
      System.out.println();

      // ===== DEMONSTRATION 4: MORE BASIC GAMEPLAY =====
      System.out.println("============= FEATURE DEMONSTRATION: CONTINUING GAMEPLAY =============");
      System.out.println("BLUE plays 'Barrier' card at (1,4) - Adding more elements to the board");
      System.out.println("This helps us develop the game state for later demonstrations");
      System.out.println();
      
      // Use position (1,4) which has BLUE pawns initially
      executeMove(model, view, 0, 1, 4, 
              "BLUE places Barrier at (1,4)");
      
      System.out.println();
      System.out.println("The board now has various cards and influence effects");
      System.out.println("This provides a rich environment to observe influence interactions");
      System.out.println();

      // Add more pawns for next demonstrations
      try {
        // Add pawns to support card placement at (2,0)
        model.getCell(2, 0).addPawn(PlayerColors.RED);
        model.getCell(2, 0).addPawn(PlayerColors.RED);
        
        // Add pawns to support card placement at (2,4)
        model.getCell(2, 4).addPawn(PlayerColors.BLUE);
        model.getCell(2, 4).addPawn(PlayerColors.BLUE);
        model.getCell(2, 4).addPawn(PlayerColors.BLUE);  // Added one more to ensure cost-3 cards can be placed
      } catch (Exception e) {
        System.err.println("Error adding pawns: " + e.getMessage());
      }

      // ===== DEMONSTRATION 5: MODIFIER STACKING =====
      System.out.println("============= FEATURE DEMONSTRATION: VALUE MODIFIER STACKING =============");
      System.out.println("RED plays 'Enchant' card at (2,0) - Another card with upgrading influences");
      System.out.println("When overlapping with existing modifiers, they will stack!");
      System.out.println();
      
      // Use position (2,0) which now has RED pawns
      executeMove(model, view, 4, 2, 0, 
              "RED places Enchant at (2,0)");
      
      System.out.println();
      System.out.println("Notice how some cells now have +2 modifiers - upgrading effects stack");
      System.out.println("This demonstrates that value modifiers can accumulate from multiple sources");
      System.out.println();

      // ===== DEMONSTRATION 6: MIXED INFLUENCE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: MIXED INFLUENCE CARD =============");
      System.out.println("BLUE plays 'Harmony' card at (2,4) - This card has both U and D influences");
      System.out.println("It will apply both upgrading and devaluing effects to different cells");
      System.out.println();
      
      // Use position (2,4) which now has BLUE pawns
      executeMove(model, view, 7, 2, 4, 
              "BLUE places Harmony at (2,4)");
      
      System.out.println();
      System.out.println("Notice the mixed influence effects around the card - some cells are upgraded");
      System.out.println("while others are devalued, showing the flexibility of the augmented model");
      System.out.println();

      // ===== DEMONSTRATION 7: SCORING WITH MODIFIERS =====
      System.out.println("============= FEATURE DEMONSTRATION: SCORING WITH MODIFIERS =============");
      System.out.println("Let's examine how the score is affected by value modifiers");
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
      System.out.println("The scoring system automatically accounts for value modifiers!");
      System.out.println("A card's contribution to the score is its base value + any modifiers");
      System.out.println();

      // Complete any remaining cells if needed to finish the game
      try {
        // Fill any remaining cells that have pawns to ensure game completion
        // Row 0 center cells
        if (model.getCellContent(0, 1) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 0, 1, "RED completes the board at (0,1)");
        }
        if (model.getCellContent(0, 2) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 0, 2, "BLUE completes the board at (0,2)");
        }
        if (model.getCellContent(0, 3) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 0, 3, "RED completes the board at (0,3)");
        }
        
        // Row 1 center cells
        if (model.getCellContent(1, 1) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 1, 1, "BLUE completes the board at (1,1)");
        }
        if (model.getCellContent(1, 2) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 1, 2, "RED completes the board at (1,2)");
        }
        if (model.getCellContent(1, 3) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 1, 3, "BLUE completes the board at (1,3)");
        }
        
        // Row 2 center cells
        if (model.getCellContent(2, 1) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 2, 1, "RED completes the board at (2,1)");
        }
        if (model.getCellContent(2, 2) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 2, 2, "BLUE completes the board at (2,2)");
        }
        if (model.getCellContent(2, 3) == cs3500.pawnsboard.model.enumerations.CellContent.PAWNS) {
          executeMove(model, view, 0, 2, 3, "RED completes the board at (2,3)");
        }
      } catch (Exception e) {
        // Ignore exceptions here as we're just trying to complete the game
      }
      
      // If the game is not over, pass turns to end it
      if (!model.isGameOver()) {
        try {
          System.out.println("Passing final turns to complete the game...");
          model.passTurn();
          System.out.println(view.renderGameState("First player passes"));
          model.passTurn();
          System.out.println(view.renderGameState("Second player passes - Game ends"));
        } catch (Exception e) {
          System.err.println("Error passing turn: " + e.getMessage());
        }
      }

      // ===== DEMONSTRATION SUMMARY =====
      System.out.println("============= BASIC DEMONSTRATION SUMMARY =============");
      System.out.println("This demonstration has showcased:");
      System.out.println("1. Upgrading influences that increase cell values");
      System.out.println("2. Devaluing influences that decrease cell values");
      System.out.println("3. Stacking of multiple modifiers from different cards");
      System.out.println("4. Cards with mixed influence types");
      System.out.println("5. Regular influence functionality still working");
      System.out.println("6. Effect of placing cards on pre-modified cells");
      System.out.println("7. Scoring system accounting for value modifiers");
      System.out.println();
      
      // Display final score
      if (model.isGameOver()) {
        int[] finalScores = model.getTotalScore();
        PlayerColors winner = model.getWinner();
        System.out.println("Final scores: RED = " + finalScores[0] + ", BLUE = " + finalScores[1]);
        if (winner != null) {
          System.out.println("Winner: " + winner);
        } else {
          System.out.println("Game ended in a tie!");
        }
      }
      
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
      // Place the card - using the index directly, assuming setup is correct
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
