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

      System.out.println("============= AUGMENTED PAWNS BOARD GAME - BASIC DEMONSTRATION " +
              "=============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // ===== DEMONSTRATION 1: BASIC UPGRADING INFLUENCE =====
      System.out.println("============= FEATURE DEMONSTRATION: BASIC UPGRADING INFLUENCE " +
              "=============");
      System.out.println("RED plays 'Upgrade' card at (0,1) - This card has upgrading influences " +
              "(U)");
      System.out.println("We expect to see cells around the card get +1 to their value modifiers");
      System.out.println();
      
      executeMove(model, view, 3, 0, 1, 
              "RED places Upgrade at (0,1)");
      
      System.out.println();
      System.out.println("Notice the '__+1' marks in cells around the card - these are value " +
              "modifiers");
      System.out.println("Even empty cells can hold value modifiers for future cards");
      System.out.println();

      // ===== DEMONSTRATION 2: BASIC DEVALUING INFLUENCE =====
      System.out.println("============= FEATURE DEMONSTRATION: BASIC DEVALUING INFLUENCE " +
              "=============");
      System.out.println("BLUE plays 'Corrupt' card at (0,3) - This card has devaluing " +
              "influences (D)");
      System.out.println("We expect to see cells around the card get -1 to their value modifiers");
      System.out.println();
      
      executeMove(model, view, 3, 0, 3, 
              "BLUE places Corrupt at (0,3)");
      
      System.out.println();
      System.out.println("Notice the '__-1' marks in cells around the card - these are negative " +
              "modifiers");
      System.out.println("Devaluing influences decrease the effective value of cards");
      System.out.println();

      // ===== DEMONSTRATION 3: MODIFIER STACKING =====
      System.out.println("============= FEATURE DEMONSTRATION: VALUE MODIFIER STACKING " +
              "=============");
      System.out.println("RED plays 'Bless' card at (1,0) - Another card with upgrading " +
              "influences");
      System.out.println("When overlapping with existing modifiers, they will stack!");
      System.out.println();
      
      executeMove(model, view, 9, 1, 0, 
              "RED places Bless at (1,0)");
      
      System.out.println();
      System.out.println("Notice how some cells now have +2 modifiers - upgrading effects stack");
      System.out.println("This demonstrates that value modifiers can accumulate from multiple " +
              "sources");
      System.out.println();

      // ===== DEMONSTRATION 4: MIXED INFLUENCE CARD =====
      System.out.println("============= FEATURE DEMONSTRATION: MIXED INFLUENCE CARD =============");
      System.out.println("BLUE plays 'Harmony' card at (1,4) - This card has both U and D " +
              "influences");
      System.out.println("It will apply both upgrading and devaluing effects to different cells");
      System.out.println();
      
      executeMove(model, view, 7, 1, 4, 
              "BLUE places Harmony at (1,4)");
      
      System.out.println();
      System.out.println("Notice the mixed influence effects around the card - some cells are " +
              "upgraded");
      System.out.println("while others are devalued, showing the flexibility of the augmented " +
              "model");
      System.out.println();

      // ===== DEMONSTRATION 5: REGULAR INFLUENCE STILL WORKS =====
      System.out.println("============= FEATURE DEMONSTRATION: REGULAR INFLUENCE FUNCTIONALITY " +
              "=============");
      System.out.println("RED plays 'Spark' card at (2,1) - This card has regular influences (I)");
      System.out.println("It will add pawns to empty cells and modify existing pawns");
      System.out.println();
      
      executeMove(model, view, 13, 2, 1, 
              "RED places Spark at (2,1)");
      
      System.out.println();
      System.out.println("Notice the regular influence effect adding pawns to empty cells (1r__)");
      System.out.println("The augmented model preserves all regular influence functionality");
      System.out.println();

      // ===== DEMONSTRATION 6: PLACING CARD ON MODIFIED CELL =====
      System.out.println("============= FEATURE DEMONSTRATION: CARD ON MODIFIED CELL " +
              "=============");
      System.out.println("BLUE plays 'Flash' card at (2,3) - We're placing this on a cell with a " +
              "modifier");
      System.out.println("The card's value will be affected by the existing modifier");
      System.out.println();
      
      executeMove(model, view, 13, 2, 3, 
              "BLUE places Flash at (2,3)");
      
      System.out.println();
      System.out.println("Notice the card's displayed effective value reflects the modifier");
      System.out.println("Cards placed on pre-modified cells inherit those modifications");
      System.out.println();

      // ===== DEMONSTRATION 7: SCORING WITH MODIFIERS =====
      System.out.println("============= FEATURE DEMONSTRATION: SCORING WITH MODIFIERS " +
              "=============");
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
   * @param cardIndex the index of the card in the current player's hand
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
    } catch (IllegalAccessException e) {
      System.out.println("Move failed (access): " + e.getMessage());
    } catch (IllegalOwnerException e) {
      System.out.println("Move failed (ownership): " + e.getMessage());
    } catch (IllegalCardException e) {
      System.out.println("Move failed (card): " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Move failed: " + e.getMessage());
    }
    
    try {
      model.passTurn();
      System.out.println(view.renderGameState("PASS - " + description + " failed"));
    } catch (Exception ex) {
      System.err.println("Error passing turn: " + ex.getMessage());
    }
    
    return false;
  }
}
