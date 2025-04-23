package cs3500.pawnsboard;

import cs3500.pawnsboard.model.PawnsBoardAugmented;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.deckbuilder.PawnsBoardAugmentedDeckBuilder;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;
import cs3500.pawnsboard.player.strategy.types.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.view.PawnsBoardAugmentedTextualView;

import java.io.File;
import java.util.Optional;

/**
 * Demonstration of the MaximizeRowScoreStrategy with the Augmented Pawns Board game.
 * This demonstration shows how the strategy prioritizes row-by-row optimization
 * when making decisions in a game with value modifiers and influence effects.
 */
//TODO: Perfectly review demonstrations
public class PawnsBoardStrategyMaximizeRowDemo {

  private static final int BOARD_ROWS = 3;
  private static final int BOARD_COLS = 5;
  private static final int HAND_SIZE = 5;

  /**
   * Main method that runs the strategy demonstration.
   * Shows step-by-step decision making of the MaximizeRowScoreStrategy.
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

      // Create the strategy
      MaximizeRowScoreStrategy<PawnsBoardAugmentedCard> strategy =
              new MaximizeRowScoreStrategy<>();

      System.out.println("============= MAXIMIZE ROW SCORE STRATEGY DEMONSTRATION =============");
      System.out.println();
      System.out.println("Initial game state:");
      System.out.println(view.renderGameState("Game Start"));
      System.out.println();

      // Add pawns for demonstrations
      try {
        // For RED positions (top to bottom, left to right)
        model.getCell(0, 0).addPawn(PlayerColors.RED);
        model.getCell(0, 1).addPawn(PlayerColors.RED);
        model.getCell(1, 2).addPawn(PlayerColors.RED);
        model.getCell(2, 0).addPawn(PlayerColors.RED);
        model.getCell(2, 1).addPawn(PlayerColors.RED);

        // For BLUE positions
        model.getCell(0, 4).addPawn(PlayerColors.BLUE);
        model.getCell(1, 4).addPawn(PlayerColors.BLUE);
        model.getCell(2, 3).addPawn(PlayerColors.BLUE);
        model.getCell(2, 4).addPawn(PlayerColors.BLUE);

        System.out.println("Pawns set up successfully for the demonstration");
        System.out.println(view.renderGameState("After Setting Up Pawns"));
        System.out.println();
      } catch (Exception e) {
        System.out.println("Setup issue: " + e.getMessage());
      }

      // Display initial row scores
      printRowScores(model);

      // ===== DEMONSTRATION 1: STRATEGY INITIAL ANALYSIS =====
      System.out.println("============= STEP 1: STRATEGY INITIAL ANALYSIS =============");
      System.out.println("Let's see what move the MaximizeRowScoreStrategy recommends for RED:");

      // Get strategy's move recommendation
      Optional<PawnsBoardMove> recommendedMove = strategy.chooseMove(model);

      if (recommendedMove.isPresent()) {
        PawnsBoardMove move = recommendedMove.get();
        if (move.getMoveType() == MoveType.PASS) {
          System.out.println("Strategy recommends PASSING - no row-improving moves found");
        } else {
          System.out.println("Strategy recommends playing card " + move.getCardIndex() +
                  " at position (" + move.getRow() + "," + move.getCol() + ")");

          // Explain the reasoning
          explainStrategyReasoning(model, move);

          // Execute the move
          executeMove(model, view, move.getCardIndex(), move.getRow(), move.getCol(),
                  "RED plays card based on strategy recommendation");
        }
      } else {
        System.out.println("Strategy did not return a move - this should not happen");
      }

      printRowScores(model);

      // ===== DEMONSTRATION 2: VALUE MODIFIERS AFFECTING STRATEGY =====
      System.out.println("============= STEP 2: VALUE MODIFIERS AFFECTING STRATEGY =============");
      System.out.println("Now that RED has placed a card, it's BLUE's turn.");
      System.out.println("BLUE will place a card with devaluing influences to affect row scores:");

      // BLUE makes a strategic move to devalue RED's card in the top row
      // Using cell (0,4) which has BLUE pawns
      executeMove(model, view, 0, 0, 4,
              "BLUE plays a devaluing influence card at (0,4)");

      printRowScores(model);
      System.out.println("Notice how the devaluing influence has changed the row scores!");
      System.out.println("This affects the strategy's decision-making for the next move.");

      // ===== DEMONSTRATION 3: STRATEGY RESPONSE TO CHANGED CONDITIONS =====
      System.out.println("============= STEP 3: STRATEGY RESPONSE TO CHANGED CONDITIONS " +
              "=============");
      System.out.println("Now let's see how the strategy responds to the changed scores:");

      // Get strategy's updated move recommendation
      recommendedMove = strategy.chooseMove(model);

      if (recommendedMove.isPresent()) {
        PawnsBoardMove move = recommendedMove.get();
        if (move.getMoveType() == MoveType.PASS) {
          System.out.println("Strategy recommends PASSING - no row-improving moves found");
        } else {
          System.out.println("Strategy recommends playing card " + move.getCardIndex() +
                  " at position (" + move.getRow() + "," + move.getCol() + ")");

          // Explain the reasoning
          explainStrategyReasoning(model, move);

          // Execute the move
          executeMove(model, view, move.getCardIndex(), move.getRow(), move.getCol(),
                  "RED plays card based on strategy's updated recommendation");
        }
      } else {
        System.out.println("Strategy did not return a move - this should not happen");
      }

      printRowScores(model);

      // ===== DEMONSTRATION 4: COMPLEX SCENARIO WITH MULTIPLE INFLUENCES =====
      System.out.println("============= STEP 4: COMPLEX SCENARIO WITH MULTIPLE INFLUENCES " +
              "=============");
      System.out.println("BLUE creates a more complex scenario with upgrading influences:");

      // BLUE makes another move to create a more complex board state
      // Using cell (1,4) which has BLUE pawns
      executeMove(model, view, 0, 1, 4,
              "BLUE plays an upgrading influence card at (1,4)");

      printRowScores(model);

      // ===== DEMONSTRATION 5: STRATEGY HANDLING COMPLEX BOARD STATE =====
      System.out.println("============= STEP 5: STRATEGY HANDLING COMPLEX BOARD STATE " +
              "=============");
      System.out.println("Now the strategy must analyze a board with multiple value modifiers:");

      // Get strategy's recommendation for the complex scenario
      recommendedMove = strategy.chooseMove(model);

      if (recommendedMove.isPresent()) {
        PawnsBoardMove move = recommendedMove.get();
        if (move.getMoveType() == MoveType.PASS) {
          System.out.println("Strategy recommends PASSING - no row-improving moves found");
        } else {
          System.out.println("Strategy recommends playing card " + move.getCardIndex() +
                  " at position (" + move.getRow() + "," + move.getCol() + ")");

          // Explain the reasoning
          explainStrategyReasoning(model, move);

          // Execute the move
          executeMove(model, view, move.getCardIndex(), move.getRow(), move.getCol(),
                  "RED plays card based on strategy's complex analysis");
        }
      } else {
        System.out.println("Strategy did not return a move - this should not happen");
      }

      printRowScores(model);

      // ===== DEMONSTRATION SUMMARY =====
      System.out.println("============= STRATEGY DEMONSTRATION SUMMARY =============");
      System.out.println("The MaximizeRowScoreStrategy demonstration has showcased:");
      System.out.println("1. How the strategy analyzes rows from top to bottom");
      System.out.println("2. How value modifiers influence the strategy's decisions");
      System.out.println("3. How the strategy adapts to changing board conditions");
      System.out.println("4. How it handles complex scenarios with multiple influence types");
      System.out.println("5. The strategy's focus on improving row scores relative to the " +
              "opponent");
      System.out.println();
      System.out.println("Key insights:");
      System.out.println("- The strategy works 'as-is' with the augmented model because it " +
              "relies on");
      System.out.println("  the model's methods to calculate scores, which already account for " +
              "modifiers");
      System.out.println("- No special adaptation was needed for the augmented features because" +
              " the");
      System.out.println("  strategy is abstracted from the specific scoring implementation");
      System.out.println("- This demonstrates good separation of concerns in the design");
      System.out.println();
      System.out.println("Final game state:");
      System.out.println(view.renderGameState("End of Strategy Demonstration"));

    } catch (InvalidDeckConfigurationException e) {
      System.err.println("Error with deck configuration: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Helper method to execute a move in the game.
   * Places the card at the specified position and prints the result.
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

  /**
   * Prints the current row scores for both players.
   *
   * @param model the game model
   */
  private static void printRowScores(PawnsBoardAugmented<PawnsBoardAugmentedCard> model) {
    System.out.println("Current Row Scores:");
    for (int row = 0; row < BOARD_ROWS; row++) {
      int[] rowScores = model.getRowScores(row);
      System.out.println("Row " + row + ": RED = " + rowScores[0] + ", BLUE = " + rowScores[1]);
    }

    int[] totalScores = model.getTotalScore();
    System.out.println("Total scores: RED = " + totalScores[0] + ", BLUE = " + totalScores[1]);
    System.out.println();
  }

  /**
   * Explains the reasoning behind the strategy's move recommendation.
   *
   * @param model the game model
   * @param move the recommended move
   */
  private static void explainStrategyReasoning(PawnsBoardAugmented<PawnsBoardAugmentedCard> model,
                                               PawnsBoardMove move) {
    if (move.getMoveType() == MoveType.PASS) {
      System.out.println("The strategy found no moves that would improve any row scores.");
      return;
    }

    int targetRow = move.getRow();
    int[] currentRowScores = model.getRowScores(targetRow);

    System.out.println("Strategy reasoning:");
    System.out.println("- Examining row " + targetRow + " with current scores: RED = " +
            currentRowScores[0] + ", BLUE = " + currentRowScores[1]);

    try {
      // Create a copy to simulate the move
      PawnsBoardAugmented<PawnsBoardAugmentedCard> modelCopy =
              (PawnsBoardAugmented<PawnsBoardAugmentedCard>) model.copy();

      // Simulate the move
      modelCopy.placeCard(move.getCardIndex(), move.getRow(), move.getCol());

      // Check new scores
      int[] newRowScores = modelCopy.getRowScores(targetRow);

      System.out.println("- After playing this card, row scores would be: RED = " +
              newRowScores[0] + ", BLUE = " + newRowScores[1]);
      System.out.println("- This improves RED's position in the row from " +
              (currentRowScores[0] > currentRowScores[1] ? "winning" :
                      currentRowScores[0] == currentRowScores[1] ? "tied" : "losing") +
              " to " +
              (newRowScores[0] > newRowScores[1] ? "winning" :
                      newRowScores[0] == newRowScores[1] ? "tied" : "losing"));

      if (newRowScores[0] > newRowScores[1]) {
        System.out.println("- This is a good move because it gives RED a higher score than BLUE " +
                "in this row");
      }

    } catch (Exception e) {
      System.out.println("Error in strategy explanation: " + e.getMessage());
    }

    System.out.println();
  }
}