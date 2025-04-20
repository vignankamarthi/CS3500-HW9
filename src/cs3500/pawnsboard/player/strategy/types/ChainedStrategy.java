package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.StrategyFactory;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.Optional;

/**
 * A strategy that chains multiple strategies from a StrategyFactory together.
 * It tries each strategy in sequence, from the added strategies to the fallback strategy,
 * until one produces a valid move.
 *
 * @param <C> the type of Card used in the game
 */
public class ChainedStrategy<C extends Card> extends AbstractPawnsBoardStrategy<C> {
  private final StrategyFactory<C> strategyFactory;

  /**
   * Constructs a ChainedStrategy with the given StrategyFactory.
   *
   * @param strategyFactory the factory containing the strategies to run
   * @throws IllegalArgumentException if strategyFactory is null
   */
  public ChainedStrategy(StrategyFactory<C> strategyFactory) {
    if (strategyFactory == null) {
      throw new IllegalArgumentException("Strategy factory cannot be null");
    }
    this.strategyFactory = strategyFactory;
  }

  /**
   * Parses through the given list of strategies in the given Factory of strategies, and
   * runs moves from the added ones to the fallback.
   * @param model the current game state
   * @return the determined move
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    validateGameStart(model);

    // Run strategies from the added ones to the fallback
    for (PawnsBoardStrategy<C, PawnsBoardMove> strategy : strategyFactory.getStrategies()) {
      Optional<PawnsBoardMove> move = strategy.chooseMove(model);
      if (move.isPresent()) {
        return move;
      }
    }

    // If no strategy produces a move, run the fallback strategy
    if (strategyFactory.getFallbackStrategy() != null) {
      return strategyFactory.getFallbackStrategy().chooseMove(model);
    }

    // If there's no fallback strategy, return an empty Optional, but that should never be the case
    else {
      throw new IllegalStateException("No strategy found, check for " +
              "existence of fallback strategy");
    }
  }
}