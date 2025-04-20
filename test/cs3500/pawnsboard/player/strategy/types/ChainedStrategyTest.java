package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.mocks.PawnsBoardMockTracker;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.StrategyFactory;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for the ChainedStrategy class.
 * Tests the strategy chaining mechanism and various scenario behaviors.
 */
public class ChainedStrategyTest {

  private PawnsBoardMockTracker<PawnsBoardBaseCard, ?> mockTracker;
  private StrategyFactory<PawnsBoardBaseCard> strategyFactory;
  private PawnsBoardBaseCard testCard;

  @Before
  public void setUp() {
    mockTracker = new PawnsBoardMockTracker<>();
    strategyFactory = new StrategyFactory<>();
    boolean[][] emptyInfluence = new boolean[5][5];
    testCard = new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence);
    mockTracker.setupInitialBoard();
  }

  /**
   * Tests that the ChainedStrategy constructor throws an exception for null strategy factory.
   */
  @Test
  public void testConstructor_NullStrategyFactory() {
    try {
      new ChainedStrategy<>(null);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Strategy factory cannot be null", e.getMessage());
    }
  }

  /**
   * Tests that ChainedStrategy returns a move from the first valid strategy in the chain.
   */
  @Test
  public void testChooseMove_FirstStrategySucceeds() {
    // Set up model with a legal move
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, hand)
            .setReturnAllLegalMoves(true);

    // Create a chained strategy that will use the first strategy successfully
    ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(
            strategyFactory.createFillFirstStrategy()
                    .addMaximizeRowScore()
    );

    // Choose move
    Optional<PawnsBoardMove> move = chainedStrategy.chooseMove(mockTracker);

    // Verify the move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
  }

  /**
   * Tests that ChainedStrategy moves to the next strategy if the first one fails.
   */
  @Test
  public void testChooseMove_FirstStrategyFails_SecondStrategySucceeds() {
    // Set up a scenario where the first strategy fails, but the second succeeds
    mockTracker.setPlayerHand(PlayerColors.RED, List.of(testCard))
            .setReturnAllLegalMoves(false);

    // Second strategy will have legal moves
    mockTracker.setLegalMoveCoordinates(List.of(new int[]{1, 1, 0}));

    ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(
            strategyFactory.createMaximizeRowScoreStrategy()
                    .addFillFirst()
    );

    Optional<PawnsBoardMove> move = chainedStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests that ChainedStrategy returns the fallback strategy's move when all strategies fail.
   */
  @Test
  public void testChooseMove_AllStrategiesFail_UseFallbackStrategy() {
    // Set up scenario where no strategies find a legal move
    mockTracker.setPlayerHand(PlayerColors.RED, List.of(testCard))
            .setReturnAllLegalMoves(false);

    // Create a fallback strategy
    strategyFactory.createFillFirstStrategy()
            .addMaximizeRowScore()
            .setFallbackStrategy(new MaximizeRowScoreStrategy<>());

    ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(strategyFactory);

    Optional<PawnsBoardMove> move = chainedStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests that ChainedStrategy handles scenarios with multiple strategies correctly.
   */
  @Test
  public void testChooseMove_MultipleStrategiesInChain() {
    // Create multiple strategies with different behaviors
    mockTracker.setPlayerHand(PlayerColors.RED, List.of(testCard))
            .setReturnAllLegalMoves(false);

    // Third strategy will have legal moves
    mockTracker.setLegalMoveCoordinates(List.of(new int[]{2, 2, 0}));

    ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(
            strategyFactory.createFillFirstStrategy()
                    .addMaximizeRowScore()
                    .addControlBoard()
    );

    Optional<PawnsBoardMove> move = chainedStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
  }

  /**
   * Tests that ChainedStrategy handles game not started scenario.
   */
  @Test
  public void testChooseMove_GameNotStarted() {
    mockTracker.setGameStarted(false);

    ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(
            strategyFactory.createFillFirstStrategy()
    );

    Optional<PawnsBoardMove> move = chainedStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }
}