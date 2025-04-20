package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.Optional;

/**
 * A strategy that selects the first legal move it finds.
 * This is an INFALLIBLE strategy that systematically searches through every possible combination
 * of card and board position until it finds a valid move.
 *
 * <p>The search pattern is:
 * <ol>
 *   <li>Iterate through each card in the player's hand</li>
 *   <li>For each card, search the board differently based on player color:</li>
 *   <ul>
 *     <li>RED player: Check from top-left to bottom-right</li>
 *     <li>BLUE player: Check from top-right to bottom-left</li>
 *   </ul>
 *   <li>Return the first legal move found</li>
 * </ol>
 * </p>
 *
 * <p>This strategy is INFALLIBLE: if no move is found, it will return a pass move.</p>
 *
 * @param <C> the type of Card used in the game
 */
public class FillFirstStrategy<C extends Card> extends AbstractPawnsBoardStrategy<C> {

  /**
   * Determines the next move based on the current game state.
   * Systematically checks every possible move and returns the first legal one found.
   * If no legal move is found, returns a pass move.
   *
   * @param model the current game state
   * @return Optional containing the first legal move found, or a pass move if no legal one exist
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    this.validateGameStart(model);
    // Initialize context with common game state information
    StrategyContext<C> context = initializeContext(model);

    // If context is null, the game isn't in a valid state for moves
    if (context == null) {
      return Optional.of(PawnsBoardMove.pass());
    }

    // For each card in hand (cards are always left to right in hand)
    for (int cardIndex = 0; cardIndex < context.hand.size(); cardIndex++) {
      // Search direction depends on player color but always row by row, top to bottom
      for (int row = 0; row < context.rows; row++) {
        if (context.currentPlayer == PlayerColors.RED) {
          // RED: left to right within each row
          for (int col = 0; col < context.cols; col++) {
            if (model.isLegalMove(cardIndex, row, col)) {
              return Optional.of(new PawnsBoardMove(cardIndex, row, col));
            }
          }
        } else {
          // BLUE: right to left within each row
          for (int col = context.cols - 1; col >= 0; col--) {
            if (model.isLegalMove(cardIndex, row, col)) {
              return Optional.of(new PawnsBoardMove(cardIndex, row, col));
            }
          }
        }
      }
    }

    // No legal moves found, return a pass move
    return Optional.of(PawnsBoardMove.pass());
  }
}