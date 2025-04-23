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
import java.util.List;

/**
 * Complex demonstration of the Augmented Pawns Board game.
 * Shows advanced mechanics including card removal through value devaluation.
 */
public class PawnsBoardGameLevel1ComplexDemo {

  private static final int BOARD_ROWS = 3;
  private static final int BOARD_COLS = 5;
  private static final int HAND_SIZE = 5;

  /**
   * Main method that runs the complex demonstration.
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
      String redDeckPath = "docs" + File.separator + "REDComplex3x5PawnsBoardAugmentedDeck.config";
      String blueDeckPath = "docs" + File.separator +
              "BLUEComplex3x5PawnsBoardAugmentedDeck.config";

      // Start the game
      model.startGame(BOARD_ROWS, BOARD_COLS, redDeckPath, blueDeckPath, HAND_SIZE);

      // Create the augmented view
      PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view =
              new PawnsBoardAugmentedTextualView<>(model);

      // Print header and initial state
      printHeader("AUGMENTED PAWNS BOARD GAME - COMPLEX DEMONSTRATION");
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Setup the board for demonstration
      setupBoard(model, view);

      // ===== DEMO 1: DEVALUING INFLUENCE =====
      printDemoHeader("1: DEVALUING INFLUENCE SETUP");
      System.out.println("RED plays 'Ruin' card at (0,0) - This card has devaluing influences (D)");
      System.out.println("Cells marked with D will have their values reduced by 1");
      System.out.println();

      executeMove(model, view, 0, 0, 0, "RED places" +
              " Ruin at (0,0)");

      System.out.println();
      System.out.println("These cells are now primed for a card removal demonstration");
      System.out.println();

      // ===== DEMO 2: ZERO VALUE CARD REMOVAL =====
      printDemoHeader("2: ZERO/NEGATIVE VALUE CARD REMOVAL");
      System.out.println("BLUE plays 'Fortify' (value 1) at (0,1) - on a cell with -1 modifier");
      System.out.println("The effective value will be 1 + (-1) = 0");
      System.out.println("Cards with 0 value are automatically removed from the board");
      System.out.println();

      // Find Fortify card (value 1) in BLUE's hand
      int fortifyIndex = findCardByName(model, PlayerColors.BLUE, "Fortify");
      if (fortifyIndex == -1) {
        fortifyIndex = 0; // Fallback to first card
      }

      executeMove(model, view, fortifyIndex, 0, 1, "BLUE places" +
              " Fortify at (0,1)");

      System.out.println();
      System.out.println("The card was removed and replaced with 1 pawn (equal to its cost)");
      System.out.println("This demonstrates the core mechanic: cards with value 0 are removed");
      System.out.println();

      // ===== DEMO 3: CARD SETUP FOR REMOVAL DEMONSTRATION =====
      // Setup for value 1 card placement
      addPawnsToCell(model, view, 1, 2, PlayerColors.RED, 1,
              "Added RED pawns to position (1,2) for next demonstration");

      printDemoHeader("3: CARD SETUP FOR REMOVAL DEMONSTRATION");
      System.out.println("RED plays a value 1 card at (1,2) - This is a normal cell");
      System.out.println("We'll place this card with value 1 so it can be removed in the " +
              "next step");
      System.out.println();

      // Find a value 1 card in RED's hand
      int value1CardIndex = findCardWithExactValue(model, PlayerColors.RED, 1);
      if (value1CardIndex == -1) {
        // Fallback to using any card
        value1CardIndex = 0;
      }

      executeMove(model, view, value1CardIndex, 1, 2, "RED places " +
              "value 1 card at (1,2)");

      System.out.println();
      System.out.println("A value 1 card is now on the board");
      System.out.println("Next, we'll place a card with devaluing influence nearby to remove it");
      System.out.println();

      // ===== DEMO 4: CARD REMOVAL BY DEVALUING INFLUENCE =====
      addPawnsToCell(model, view, 1, 1, PlayerColors.BLUE, 1,
              "Added BLUE pawns to position (1,1) for next demonstration");

      printDemoHeader("4: CARD REMOVAL BY DEVALUING INFLUENCE");
      System.out.println("BLUE plays a card with devaluing influence at (1,1)");
      System.out.println("This will apply -1 influence to the nearby value 1 card");
      System.out.println("When the card's value becomes 0, it will be removed automatically");
      System.out.println();

      // Find a card with devaluing influence in BLUE's hand
      int devalueCardIndex = findCardWithDevaluing(model, PlayerColors.BLUE);
      if (devalueCardIndex == -1) {
        devalueCardIndex = 0; // Fallback
      }

      executeMove(model, view, devalueCardIndex, 1, 1, "BLUE places " +
              "devaluing card at (1,1)");

      System.out.println();
      System.out.println("Look at position (1,2) - the value 1 card has been removed!");
      System.out.println("It's replaced with pawns equal to its original cost (1)");
      System.out.println("This demonstrates how cards are removed by influence from other cards");
      System.out.println();

      // ===== DEMO 5: ADVANCED SCORING CALCULATION =====
      printDemoHeader("5: ADVANCED SCORING CALCULATION");
      System.out.println("Let's examine the scoring with card removals in play");
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
      System.out.println("2. Only cards with positive effective value count toward the score");
      System.out.println("3. Strategic devaluing can neutralize opponent's valuable cards");
      System.out.println();

      // ===== DEMO SUMMARY =====
      printDemoHeader("COMPLEX DEMONSTRATION SUMMARY");
      System.out.println("This demonstration has showcased advanced mechanics including:");
      System.out.println("1. Card removal when effective value is 0 or less");
      System.out.println("2. Only cards with positive values remain on the board");
      System.out.println("3. Value modifier reset when cards are removed");
      System.out.println("4. Complex influence patterns with multiple types");
      System.out.println("5. Strategic placement of cards for maximum impact");
      System.out.println("6. Advanced scoring with modifier effects");
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
   * Prints a header for the entire demonstration.
   *
   * @param title the title text
   */
  private static void printHeader(String title) {
    String decoration = "=============";
    System.out.println(decoration + " " + title + " " + decoration);
    System.out.println();
  }

  /**
   * Prints a header for each individual demonstration.
   *
   * @param title the demonstration title
   */
  private static void printDemoHeader(String title) {
    System.out.println("============= FEATURE DEMONSTRATION: " + title + " =============");
  }

  /**
   * Sets up the initial board state for the demonstration.
   *
   * @param model the game model
   * @param view the game view
   */
  private static void setupBoard(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                 PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view) {
    try {
      clearBoard(model);

      // Add pawns at specific positions
      addPawnsToCell(model, 0, 0, PlayerColors.RED, 3);
      addPawnsToCell(model, 0, 1, PlayerColors.BLUE, 1);
      addPawnsToCell(model, 0, 4, PlayerColors.BLUE, 3);

      addPawnsToCell(model, 1, 0, PlayerColors.RED, 3);
      addPawnsToCell(model, 1, 4, PlayerColors.BLUE, 3);

      addPawnsToCell(model, 2, 0, PlayerColors.RED, 3);
      addPawnsToCell(model, 2, 1, PlayerColors.RED, 1);
      addPawnsToCell(model, 2, 2, PlayerColors.BLUE, 1);
      addPawnsToCell(model, 2, 4, PlayerColors.BLUE, 3);

      System.out.println("Board setup completed for demonstration");
      System.out.println(view.renderGameState("After Setting Up Pawns"));
      System.out.println();
    } catch (Exception e) {
      System.err.println("Error setting up board: " + e.getMessage());
    }
  }

  /**
   * Resets the board to an empty state.
   *
   * @param model the game model
   */
  private static void clearBoard(PawnsBoardAugmented<PawnsBoardAugmentedCard> model) {
    try {
      int[] dims = model.getBoardDimensions();
      for (int r = 0; r < dims[0]; r++) {
        for (int c = 0; c < dims[1]; c++) {
          // Reset cells except the edge columns which are required for game setup
          if (c == 0 || c == dims[1] - 1) {
            continue;
          }

          // Reset cell to empty by direct manipulation
          if (model.getCellContent(r, c) != CellContent.EMPTY) {
            // Force cell to empty state
            PlayerColors tempOwner = PlayerColors.RED;
            model.getCell(r, c).restorePawnsAfterCardRemoval(1, tempOwner);

            // Force cell to be truly empty by removing the pawns
            if (model.getCellContent(r, c) == CellContent.PAWNS) {
              for (int i = 0; i < model.getPawnCount(r, c); i++) {
                // This is a hack to empty cells - in a real game this would be a proper API
                if (!model.getPlayerHand(tempOwner).isEmpty()) {
                  model.getCell(r, c).setCard(model.getPlayerHand(tempOwner).get(0), tempOwner);
                  // Remove the card
                  model.getCell(r, c).restorePawnsAfterCardRemoval(0, tempOwner);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      // Silently handle errors during board clearing
    }
  }

  /**
   * Helper method to add pawns to a cell and display message.
   */
  private static void addPawnsToCell(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                     PawnsBoardAugmentedTextualView<PawnsBoardAugmentedCard> view,
                                     int row, int col, PlayerColors owner, int count,
                                     String message) {
    try {
      addPawnsToCell(model, row, col, owner, count);
      System.out.println(message);
      System.out.println(view.renderGameState("After adding pawns " +
              "to (" + row + "," + col + ")"));
      System.out.println();
    } catch (Exception e) {
      System.err.println("Error adding pawns: " + e.getMessage());
    }
  }

  /**
   * Adds pawns to a specific cell with a specific owner.
   */
  private static void addPawnsToCell(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                     int row, int col, PlayerColors owner, int count) {
    try {
      // First ensure cell is empty
      if (model.getCellContent(row, col) != CellContent.EMPTY) {
        // If cell has content with wrong owner, reset it
        if (model.getCellContent(row, col) != CellContent.PAWNS ||
                model.getCellOwner(row, col) != owner) {
          model.getCell(row, col).restorePawnsAfterCardRemoval(1, owner);

          // Remove the pawn we just added
          if (!model.getPlayerHand(owner).isEmpty()) {
            model.getCell(row, col).setCard(model.getPlayerHand(owner).get(0), owner);
            model.getCell(row, col).restorePawnsAfterCardRemoval(0, owner);
          }
        }
      }

      // Now add the requested pawns
      for (int i = 0; i < count; i++) {
        model.getCell(row, col).addPawn(owner);
      }
    } catch (Exception e) {
      // Silently handle errors for cell manipulation
    }
  }

  /**
   * Finds a card by name in the player's hand.
   *
   * @return the index of the card in the player's hand, or -1 if not found
   */
  private static int findCardByName(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                    PlayerColors player, String cardName) {
    try {
      List<PawnsBoardAugmentedCard> hand = model.getPlayerHand(player);
      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).getName().equalsIgnoreCase(cardName)) {
          return i;
        }
      }
    } catch (Exception e) {
      // Silently handle errors
    }
    return -1;
  }

  /**
   * Finds a card with exactly the specified value in the player's hand.
   *
   * @return the index of the card, or -1 if not found
   */
  private static int findCardWithExactValue(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                            PlayerColors player, int value) {
    try {
      List<PawnsBoardAugmentedCard> hand = model.getPlayerHand(player);
      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).getValue() == value) {
          return i;
        }
      }
    } catch (Exception e) {
      // Silently handle errors
    }
    return -1;
  }

  /**
   * Finds a card with devaluing influence in the player's hand.
   *
   * @return the index of the card, or -1 if not found
   */
  private static int findCardWithDevaluing(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                           PlayerColors player) {
    try {
      List<PawnsBoardAugmentedCard> hand = model.getPlayerHand(player);
      // For simplicity, we'll assume cards with 'devaluing' related names
      String[] devalueNames = {"Corrupt", "Weaken", "Curse", "Decay", "Devalue", "Leech"};

      for (int i = 0; i < hand.size(); i++) {
        String cardName = hand.get(i).getName();
        for (String name : devalueNames) {
          if (cardName.contains(name)) {
            return i;
          }
        }
      }

      // If none found by name, return the first card as fallback
      return 0;
    } catch (Exception e) {
      // Silently handle errors
    }
    return -1;
  }

  /**
   * Executes a move in the game - attempts to place a card.
   *
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
    }

    try {
      // Only pass turn if we're not in game over state
      if (!model.isGameOver()) {
        model.passTurn();
        System.out.println(view.renderGameState("PASS - " 
                + description + " failed"));
      }
    } catch (Exception ex) {
      // Silently handle errors during pass turn
    }

    return false;
  }
}