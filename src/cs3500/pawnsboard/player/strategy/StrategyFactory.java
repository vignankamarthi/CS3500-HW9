package cs3500.pawnsboard.player.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;
import cs3500.pawnsboard.player.strategy.types.ControlBoardStrategy;
import cs3500.pawnsboard.player.strategy.types.FillFirstStrategy;
import cs3500.pawnsboard.player.strategy.types.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.player.strategy.types.MinimaxStrategy;

/**
 * A factory for creating and chaining infallible strategies for the PawnsBoard game.
 * Implements {@link PawnsBoardStrategy} to be used as a strategy.
 * Use {@code create*} methods to start a chain, and {@code add*} methods to build the chain.
 */
public class StrategyFactory<C extends Card> implements PawnsBoardStrategy<C, PawnsBoardMove> {
  private final List<PawnsBoardStrategy<C, PawnsBoardMove>> strategies;
  private PawnsBoardStrategy<C, PawnsBoardMove> fallbackStrategy;

  /**
   * Constructs a new StrategyFactory with an empty list of strategies.
   */
  public StrategyFactory() {
    this.strategies = new ArrayList<>();
    this.fallbackStrategy = new MaximizeRowScoreStrategy<>();
  }

  /**
   * Creates a new {@link MaximizeRowScoreStrategy} as the start of a chain.
   *
   * @return this factory, with the chain started
   * @throws IllegalStateException if strategies have already been added to the chain
   */
  public StrategyFactory<C> createMaximizeRowScoreStrategy() {
    if (!strategies.isEmpty()) {
      throw new IllegalStateException("Cannot call createStrategy after strategies have " +
              "been added. Use addStrategy methods instead.");
    }
    this.strategies.add(new MaximizeRowScoreStrategy<>());
    return this;
  }

  /**
   * Creates a new {@link FillFirstStrategy} as the start of a chain.
   *
   * @return this factory, with the chain started
   * @throws IllegalStateException if strategies have already been added to the chain
   */
  public StrategyFactory<C> createFillFirstStrategy() {
    if (!strategies.isEmpty()) {
      throw new IllegalStateException("Cannot call createStrategy after strategies have " +
              "been added. Use addStrategy methods instead.");
    }
    this.strategies.add(new FillFirstStrategy<>());
    return this;
  }

  /**
   * Creates a new {@link ControlBoardStrategy} as the start of a chain.
   *
   * @return this factory, with the chain started
   * @throws IllegalStateException if strategies have already been added to the chain
   */
  public StrategyFactory<C> createControlBoardStrategy() {
    if (!strategies.isEmpty()) {
      throw new IllegalStateException("Cannot call createStrategy after strategies have " +
              "been added. Use addStrategy methods instead.");
    }
    this.strategies.add(new ControlBoardStrategy<>());
    return this;
  }

  /**
   * Creates a new {@link MinimaxStrategy} as the start of a chain.
   *
   * @param opponentStrategy the strategy to use for simulating opponent moves
   * @return this factory, with the chain started
   * @throws IllegalArgumentException if opponentStrategy is null
   * @throws IllegalStateException if strategies have already been added to the chain
   */
  public StrategyFactory<C> createMinimaxStrategy(
          PawnsBoardStrategy<C, PawnsBoardMove> opponentStrategy) {
    if (!strategies.isEmpty()) {
      throw new IllegalStateException("Cannot call createStrategy after strategies have" +
              " been added. Use addStrategy methods instead.");
    }
    this.strategies.add(new MinimaxStrategy<>(opponentStrategy));
    return this;
  }

  /**
   * Adds a {@link MaximizeRowScoreStrategy} to the end of the current chain.
   *
   * @return this factory, with the updated chain
   * @throws IllegalStateException if no chain has been started
   */
  public StrategyFactory<C> addMaximizeRowScore() {
    if (strategies.isEmpty()) {
      throw new IllegalStateException("No chain started; use a create* method first");
    }
    strategies.add(new MaximizeRowScoreStrategy<>());
    return this;
  }

  /**
   * Adds a {@link FillFirstStrategy} to the end of the current chain.
   *
   * @return this factory, with the updated chain
   * @throws IllegalStateException if no chain has been started
   */
  public StrategyFactory<C> addFillFirst() {
    if (strategies.isEmpty()) {
      throw new IllegalStateException("No chain started; use a create* method first");
    }
    strategies.add(new FillFirstStrategy<>());
    return this;
  }

  /**
   * Adds a {@link ControlBoardStrategy} to the end of the current chain.
   *
   * @return this factory, with the updated chain
   * @throws IllegalStateException if no chain has been started
   */
  public StrategyFactory<C> addControlBoard() {
    if (strategies.isEmpty()) {
      throw new IllegalStateException("No chain started; use a create* method first");
    }
    strategies.add(new ControlBoardStrategy<>());
    return this;
  }

  /**
   * Adds a {@link MinimaxStrategy} to the end of the current chain.
   *
   * @param opponentStrategy the strategy to use for simulating opponent moves
   * @return this factory, with the updated chain
   * @throws IllegalStateException if no chain has been started
   * @throws IllegalArgumentException if opponentStrategy is null
   */
  public StrategyFactory<C> addMinimax(PawnsBoardStrategy<C, PawnsBoardMove> opponentStrategy) {
    if (strategies.isEmpty()) {
      throw new IllegalStateException("No chain started; use a create* method first");
    }
    strategies.add(new MinimaxStrategy<>(opponentStrategy));
    return this;
  }

  /**
   * Sets the fallback strategy that will be used if all other strategies fail.
   *
   * @param fallbackStrategy the fallback strategy
   * @return this factory, with the updated fallback
   * @throws IllegalArgumentException if fallbackStrategy is null
   */
  public StrategyFactory<C> setFallbackStrategy(
          PawnsBoardStrategy<C, PawnsBoardMove> fallbackStrategy) {
    if (fallbackStrategy == null) {
      throw new IllegalArgumentException("Fallback strategy cannot be null");
    }
    this.fallbackStrategy = fallbackStrategy;
    return this;
  }

  /**
   * Gets the list of strategies in this factory.
   *
   * @return the list of strategies
   */
  public List<PawnsBoardStrategy<C, PawnsBoardMove>> getStrategies() {
    return new ArrayList<>(strategies); // Return a defensive copy
  }

  /**
   * Gets the fallback strategy.
   *
   * @return the fallback strategy
   */
  public PawnsBoardStrategy<C, PawnsBoardMove> getFallbackStrategy() {
    return fallbackStrategy;
  }

  /**
   * This method is not supported in StrategyFactory. Use ChainedStrategy instead.
   *
   * @param model the current game state
   * @return never returns
   * @throws UnsupportedOperationException always
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    throw new UnsupportedOperationException(
            "StrategyFactory does not support chooseMove. Use ChainedStrategy instead.");
  }
}