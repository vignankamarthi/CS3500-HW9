package cs3500.pawnsboard.player.strategy;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.mocks.PawnsBoardMockForControllerTest;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;
import cs3500.pawnsboard.player.strategy.types.ControlBoardStrategy;
import cs3500.pawnsboard.player.strategy.types.FillFirstStrategy;
import cs3500.pawnsboard.player.strategy.types.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.player.strategy.types.MinimaxStrategy;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for the StrategyFactory class.
 * Tests the creation, chaining, and management of game strategies.
 */
public class StrategyFactoryTest {

  private StrategyFactory<PawnsBoardBaseCard> factory;
  private PawnsBoard<PawnsBoardBaseCard, ?> mockModel;

  /**
   * Sets up a fresh factory and mock model before each test.
   */
  @Before
  public void setUp() {
    factory = new StrategyFactory<>();
    mockModel = new PawnsBoardMockForControllerTest<>();
    ((PawnsBoardMockForControllerTest<PawnsBoardBaseCard, ?>) mockModel).setupInitialBoard();
  }

  /**
   * Tests that a new factory is properly initialized with an empty strategy list
   * and a default fallback strategy.
   */
  @Test
  public void testInitialState() {
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();

    // Verify strategies list starts empty
    assertNotNull("Strategy list should not be null", strategies);
    assertTrue("Strategy list should start empty", strategies.isEmpty());

    // Verify default fallback strategy is MaximizeRowScoreStrategy
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> fallback = factory.getFallbackStrategy();
    assertNotNull("Default fallback strategy should not be null", fallback);
    assertTrue("Default fallback strategy should be MaximizeRowScoreStrategy",
            fallback instanceof MaximizeRowScoreStrategy);
  }

  /**
   * Tests the createMaximizeRowScoreStrategy method.
   * Verifies that it adds a strategy to the list and returns the factory for chaining.
   */
  @Test
  public void testCreateMaximizeRowScoreStrategy() {
    StrategyFactory<PawnsBoardBaseCard> result = factory.createMaximizeRowScoreStrategy();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify a strategy was added
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have one strategy", 1, strategies.size());
    assertTrue("Added strategy should be MaximizeRowScoreStrategy",
            strategies.get(0) instanceof MaximizeRowScoreStrategy);
  }

  /**
   * Tests the createFillFirstStrategy method.
   * Verifies that it adds a strategy to the list and returns the factory for chaining.
   */
  @Test
  public void testCreateFillFirstStrategy() {
    StrategyFactory<PawnsBoardBaseCard> result = factory.createFillFirstStrategy();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify a strategy was added
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have one strategy", 1, strategies.size());
    assertTrue("Added strategy should be FillFirstStrategy",
            strategies.get(0) instanceof FillFirstStrategy);
  }

  /**
   * Tests the createControlBoardStrategy method.
   * Verifies that it adds a strategy to the list and returns the factory for chaining.
   */
  @Test
  public void testCreateControlBoardStrategy() {
    StrategyFactory<PawnsBoardBaseCard> result = factory.createControlBoardStrategy();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify a strategy was added
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have one strategy", 1, strategies.size());
    assertTrue("Added strategy should be ControlBoardStrategy",
            strategies.get(0) instanceof ControlBoardStrategy);
  }

  /**
   * Tests the createMinimaxStrategy method.
   * Verifies that it adds a strategy to the list and returns the factory for chaining.
   */
  @Test
  public void testCreateMinimaxStrategy() {
    // Create a strategy to pass as the opponent strategy
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy
            = new FillFirstStrategy<>();

    StrategyFactory<PawnsBoardBaseCard> result = factory.createMinimaxStrategy(opponentStrategy);

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify a strategy was added
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have one strategy", 1, strategies.size());
    assertTrue("Added strategy should be MinimaxStrategy",
            strategies.get(0) instanceof MinimaxStrategy);
  }

  /**
   * Tests that createMinimaxStrategy with null opponent strategy throws exception.
   */
  @Test
  public void testCreateMinimaxStrategy_NullOpponentStrategy() {
    try {
      factory.createMinimaxStrategy(null);
      fail("Should throw IllegalArgumentException for null opponent strategy");
    } catch (IllegalArgumentException e) {
      // Expected exception
      assertTrue("Exception message should mention opponent strategy",
              e.getMessage().contains("opponent")
                      || e.getMessage().contains("strategy"));
    }
  }

  /**
   * Tests the addMaximizeRowScore method.
   * Verifies that it adds a strategy to an existing chain.
   */
  @Test
  public void testAddMaximizeRowScore() {
    // First create a strategy to start the chain
    factory.createFillFirstStrategy();

    // Then add a MaximizeRowScoreStrategy
    StrategyFactory<PawnsBoardBaseCard> result = factory.addMaximizeRowScore();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify the strategy was added after the first one
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have two strategies", 2, strategies.size());
    assertTrue("First strategy should be FillFirstStrategy",
            strategies.get(0) instanceof FillFirstStrategy);
    assertTrue("Second strategy should be MaximizeRowScoreStrategy",
            strategies.get(1) instanceof MaximizeRowScoreStrategy);
  }

  /**
   * Tests that addMaximizeRowScore without starting a chain throws exception.
   */
  @Test
  public void testAddMaximizeRowScore_NoChainStarted() {
    try {
      factory.addMaximizeRowScore();
      fail("Should throw IllegalStateException when no chain started");
    } catch (IllegalStateException e) {
      // Expected exception
      assertTrue("Exception message should mention chain",
              e.getMessage().contains("chain"));
    }
  }

  /**
   * Tests the addFillFirst method.
   * Verifies that it adds a strategy to an existing chain.
   */
  @Test
  public void testAddFillFirst() {
    // First create a strategy to start the chain
    factory.createMaximizeRowScoreStrategy();

    // Then add a FillFirstStrategy
    StrategyFactory<PawnsBoardBaseCard> result = factory.addFillFirst();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify the strategy was added after the first one
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have two strategies", 2, strategies.size());
    assertTrue("First strategy should be MaximizeRowScoreStrategy",
            strategies.get(0) instanceof MaximizeRowScoreStrategy);
    assertTrue("Second strategy should be FillFirstStrategy",
            strategies.get(1) instanceof FillFirstStrategy);
  }

  /**
   * Tests that addFillFirst without starting a chain throws exception.
   */
  @Test
  public void testAddFillFirst_NoChainStarted() {
    try {
      factory.addFillFirst();
      fail("Should throw IllegalStateException when no chain started");
    } catch (IllegalStateException e) {
      // Expected exception
      assertTrue("Exception message should mention chain",
              e.getMessage().contains("chain"));
    }
  }

  /**
   * Tests the addControlBoard method.
   * Verifies that it adds a strategy to an existing chain.
   */
  @Test
  public void testAddControlBoard() {
    // First create a strategy to start the chain
    factory.createFillFirstStrategy();

    // Then add a ControlBoardStrategy
    StrategyFactory<PawnsBoardBaseCard> result = factory.addControlBoard();

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify the strategy was added after the first one
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have two strategies", 2, strategies.size());
    assertTrue("First strategy should be FillFirstStrategy",
            strategies.get(0) instanceof FillFirstStrategy);
    assertTrue("Second strategy should be ControlBoardStrategy",
            strategies.get(1) instanceof ControlBoardStrategy);
  }

  /**
   * Tests that addControlBoard without starting a chain throws exception.
   */
  @Test
  public void testAddControlBoard_NoChainStarted() {
    try {
      factory.addControlBoard();
      fail("Should throw IllegalStateException when no chain started");
    } catch (IllegalStateException e) {
      // Expected exception
      assertTrue("Exception message should mention chain",
              e.getMessage().contains("chain"));
    }
  }

  /**
   * Tests the addMinimax method.
   * Verifies that it adds a strategy to an existing chain.
   */
  @Test
  public void testAddMinimax() {
    // Create a strategy for the opponent
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy
            = new FillFirstStrategy<>();

    // First create a strategy to start the chain
    factory.createMaximizeRowScoreStrategy();

    // Then add a MinimaxStrategy
    StrategyFactory<PawnsBoardBaseCard> result = factory.addMinimax(opponentStrategy);

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify the strategy was added after the first one
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have two strategies", 2, strategies.size());
    assertTrue("First strategy should be MaximizeRowScoreStrategy",
            strategies.get(0) instanceof MaximizeRowScoreStrategy);
    assertTrue("Second strategy should be MinimaxStrategy",
            strategies.get(1) instanceof MinimaxStrategy);
  }

  /**
   * Tests that addMinimax without starting a chain throws exception.
   */
  @Test
  public void testAddMinimax_NoChainStarted() {
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy
            = new FillFirstStrategy<>();

    try {
      factory.addMinimax(opponentStrategy);
      fail("Should throw IllegalStateException when no chain started");
    } catch (IllegalStateException e) {
      // Expected exception
      assertTrue("Exception message should mention chain",
              e.getMessage().contains("chain"));
    }
  }

  /**
   * Tests that addMinimax with null opponent strategy throws exception.
   */
  @Test
  public void testAddMinimax_NullOpponentStrategy() {
    // First create a strategy to start the chain
    factory.createFillFirstStrategy();

    try {
      factory.addMinimax(null);
      fail("Should throw IllegalArgumentException for null opponent strategy");
    } catch (IllegalArgumentException e) {
      // Expected exception
      assertTrue("Exception message should mention opponent strategy",
              e.getMessage().contains("opponent")
                      || e.getMessage().contains("strategy"));
    }
  }

  /**
   * Tests the setFallbackStrategy method.
   * Verifies that it changes the fallback strategy.
   */
  @Test
  public void testSetFallbackStrategy() {
    // Create a new fallback strategy
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> newFallback
            = new FillFirstStrategy<>();

    // Set the fallback strategy
    StrategyFactory<PawnsBoardBaseCard> result = factory.setFallbackStrategy(newFallback);

    // Verify return value is the factory itself (for chaining)
    assertEquals("Method should return the factory itself", factory, result);

    // Verify the fallback strategy was changed
    assertEquals("Fallback strategy should be changed",
            newFallback, factory.getFallbackStrategy());
  }

  /**
   * Tests that setFallbackStrategy with null strategy throws exception.
   */
  @Test
  public void testSetFallbackStrategy_NullStrategy() {
    try {
      factory.setFallbackStrategy(null);
      fail("Should throw IllegalArgumentException for null fallback strategy");
    } catch (IllegalArgumentException e) {
      // Expected exception
      assertTrue("Exception message should mention fallback strategy",
              e.getMessage().contains("fallback")
                      || e.getMessage().contains("strategy"));
    }
  }

  /**
   * Tests that calling a create method after adding strategies throws exception.
   */
  @Test
  public void testCreateAfterAdd() {
    // First create a strategy to start the chain
    factory.createFillFirstStrategy();

    // Then add another strategy
    factory.addMaximizeRowScore();

    // Now try to call a create method
    try {
      factory.createControlBoardStrategy();
      fail("Should throw IllegalStateException when create is called after add");
    } catch (IllegalStateException e) {
      // Expected exception
      assertTrue("Exception message should mention create after strategies have been added",
              e.getMessage().contains("createStrategy")
                      && e.getMessage().contains("add"));
    }
  }

  /**
   * Tests that the getStrategies method returns a defensive copy.
   * Verifies that modifying the returned list doesn't affect the factory.
   */
  @Test
  public void testGetStrategiesDefensiveCopy() {
    // Add a strategy
    factory.createFillFirstStrategy();

    // Get the strategies
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();

    // Try to modify the list
    strategies.clear();

    // Verify the factory's list wasn't affected
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategiesAfter
            = factory.getStrategies();
    assertEquals("Factory's strategy list should be unchanged",
            1, strategiesAfter.size());
  }

  /**
   * Tests that chooseMove method throws UnsupportedOperationException.
   */
  @Test
  public void testChooseMove_ThrowsException() {
    try {
      factory.chooseMove(mockModel);
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // Expected exception
      assertTrue("Exception message should mention using ChainedStrategy instead",
              e.getMessage().contains("ChainedStrategy"));
    }
  }

  /**
   * Tests method chaining functionality for create and add methods.
   */
  @Test
  public void testMethodChaining() {
    // Create a strategy for the opponent
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy
            = new FillFirstStrategy<>();

    // Chain multiple methods together
    factory.createFillFirstStrategy()
            .addMaximizeRowScore()
            .addControlBoard()
            .addMinimax(opponentStrategy)
            .setFallbackStrategy(new ControlBoardStrategy<>());

    // Verify all strategies were added in the correct order
    List<PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove>> strategies
            = factory.getStrategies();
    assertEquals("Should have four strategies", 4, strategies.size());
    assertTrue("First strategy should be FillFirstStrategy",
            strategies.get(0) instanceof FillFirstStrategy);
    assertTrue("Second strategy should be MaximizeRowScoreStrategy",
            strategies.get(1) instanceof MaximizeRowScoreStrategy);
    assertTrue("Third strategy should be ControlBoardStrategy",
            strategies.get(2) instanceof ControlBoardStrategy);
    assertTrue("Fourth strategy should be MinimaxStrategy",
            strategies.get(3) instanceof MinimaxStrategy);

    // Verify fallback strategy was set
    assertTrue("Fallback strategy should be ControlBoardStrategy",
            factory.getFallbackStrategy() instanceof ControlBoardStrategy);
  }
}