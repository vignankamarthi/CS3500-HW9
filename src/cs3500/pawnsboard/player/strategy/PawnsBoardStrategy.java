package cs3500.pawnsboard.player.strategy;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.player.strategy.moves.Move;

import java.util.Optional;

/**
 * Interface for strategies in the Pawns Board game.
 * Strategies determine the next move for a player based on the current game state.
 *
 * <p>Strategies can be fallible or infallible:
 * <ul>
 *   <li>Fallible strategies may return an empty Optional even when legal moves exist</li>
 *   <li>Infallible strategies only return an empty Optional when no legal moves exist</li>
 * </ul>
 *
 * @param <C> the type of Card used in the game
 * @param <M> the type of Move this strategy produces
 */
public interface PawnsBoardStrategy<C extends Card, M extends Move> {

  /**
   * Determines the next move based on the current game state.
   *
   * @param model the current game state
   * @return an Optional containing the chosen move, or empty if no move was selected
   */
  Optional<M> chooseMove(ReadOnlyPawnsBoard<C, ?> model);
}
