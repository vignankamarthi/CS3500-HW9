package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.Optional;

/**
 * A strategy that tries to maximize the player's row scores.
 * This is an INFALLIBLE strategy that visits rows from top to bottom, looking for moves that would
 * make the player's row score higher than or equal to the opponent's in that row.
 *
 * <p>The search pattern is:
 * <ol>
 *   <li>Visit rows from top to bottom</li>
 *   <li>For each row, check if player's row score is lower than or equal to opponent's</li>
 *   <li>If so, find moves that would increase player's score to be greater than opponent's</li>
 *   <li>Return the first such move found, or move on to the next row if none exists</li>
 *   <li>If no optimal row-improving moves are found, return a pass move</li>
 * </ol>
 * </p>
 *
 * <p>This strategy is INFALLIBLE: if no move is found, it will return a pass move.</p>
 *
 * @param <C> the type of Card used in the game
 */
public class MaximizeRowScoreStrategy<C extends Card> extends AbstractPawnsBoardStrategy<C> {

  /**
   * Determines the next move based on the current game state.
   * Visits rows from top to bottom, looking for moves that would maximize row scores.
   *
   * @param model the current game state
   * @return an Optional containing a move that improves a row score, or a pass move if none found
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    validateGameStart(model);
    // Initialize context with common game state information
    StrategyContext<C> context = initializeContext(model);

    // If context is null, the game isn't in a valid state for moves
    if (context == null) {
      return Optional.of(PawnsBoardMove.pass());
    }

    // Visit rows from top to bottom
    for (int rowIndex = 0; rowIndex < context.rows; rowIndex++) {
      Optional<PawnsBoardMove> move = findMoveToImproveRow(model, context, rowIndex);
      if (move.isPresent()) {
        return move;
      }
    }

    // If no optimal moves were found, return a pass move
    return Optional.of(PawnsBoardMove.pass());
  }

  /**
   * Finds a move that would improve the player's score in the specified row.
   *
   * @param model the current game state
   * @param context the strategy context containing game state information
   * @param rowIndex the index of the row to check
   * @return an Optional containing a move that improves the row score, or empty if none found
   */
  private Optional<PawnsBoardMove> findMoveToImproveRow(ReadOnlyPawnsBoard<C, ?> model,
                                                        StrategyContext<C> context,
                                                        int rowIndex) {
    // Get current row scores
    int[] rowScores = model.getRowScores(rowIndex);
    int playerScore = rowScores[context.playerIndex];
    int opponentScore = rowScores[context.opponentIndex];

    // Only consider improving rows where player score <= opponent score
    if (playerScore <= opponentScore) {
      return findMoveToBeatOpponentScore(model, context, rowIndex, opponentScore);
    }

    // Player already has higher score in this row
    return Optional.empty();
  }

  /**
   * Finds a move that would make the player's score higher than the opponent's in the specified
   * row.
   *
   * @param model the current game state
   * @param context the strategy context containing game state information
   * @param rowIndex the index of the row to check
   * @param opponentScore the opponent's current score in the row
   * @return an Optional containing a move that beats the opponent's score, or empty if none found
   */
  private Optional<PawnsBoardMove> findMoveToBeatOpponentScore(ReadOnlyPawnsBoard<C, ?> model,
                                                               StrategyContext<C> context,
                                                               int rowIndex,
                                                               int opponentScore) {
    // Try each card in hand
    for (int cardIndex = 0; cardIndex < context.hand.size(); cardIndex++) {

      // Try each cell in this row
      for (int col = 0; col < context.cols; col++) {
        // Check if move is legal
        if (model.isLegalMove(cardIndex, rowIndex, col)) {
          Optional<PawnsBoardMove> move = simulateMoveAndCheckScore(model, context,
                  cardIndex, rowIndex, col);
          if (move.isPresent()) {
            return move;
          }
        }
      }
    }

    // No move found that beats opponent's score in this row
    return Optional.empty();
  }

  /**
   * Simulates placing a card and checks if it improves the player's score relative to the opponent.
   *
   * @param model the current game state
   * @param context the strategy context containing game state information
   * @param cardIndex the index of the card to place
   * @param rowIndex the row index where the card would be placed
   * @param col the column index where the card would be placed
   * @return an Optional containing the move if it improves the score, or empty otherwise
   */
  private Optional<PawnsBoardMove> simulateMoveAndCheckScore(ReadOnlyPawnsBoard<C, ?> model,
                                                             StrategyContext<C> context,
                                                             int cardIndex, int rowIndex, int col) {
    try {
      // Create a copy of the model to simulate this move
      PawnsBoard<C, ?> modelCopy = model.copy();

      // Simulate placing the card
      modelCopy.placeCard(cardIndex, rowIndex, col);

      // Check if this improved our row score relative to opponent
      int[] newRowScores = modelCopy.getRowScores(rowIndex);
      int newPlayerScore = newRowScores[context.playerIndex];
      int newOpponentScore = newRowScores[context.opponentIndex];

      // If new score > opponent score, return this move
      if (newPlayerScore > newOpponentScore) {
        return Optional.of(new PawnsBoardMove(cardIndex, rowIndex, col));
      }
    } catch (Exception e) {
      // Simulation failed, ignore this move
    }

    return Optional.empty();
  }
}