package cs3500.pawnsboard.controller.listeners;

import cs3500.pawnsboard.model.enumerations.PlayerColors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for the ModelListenerMock class.
 * Tests the recording of model status events and accessor methods.
 */
public class ModelListenerMockTest {

  private ModelListenerMock listener;

  /**
   * Sets up a fresh mock listener for each test.
   */
  @Before
  public void setUp() {
    listener = new ModelListenerMock();
  }

  /**
   * Tests the initial state of the listener.
   * Verifies that all tracking flags are false and data fields are null.
   */
  @Test
  public void testInitialState() {
    assertFalse("Turn change flag should be false initially",
            listener.wasTurnChangeReceived());
    assertFalse("Game over flag should be false initially", 
            listener.wasGameOverReceived());
    assertFalse("Invalid move flag should be false initially",
            listener.wasInvalidMoveReceived());
    assertNull("Last turn player should be null initially", 
            listener.getLastTurnPlayer());
    assertNull("Last winner should be null initially", 
            listener.getLastWinner());
    assertNull("Last final scores should be null initially", 
            listener.getLastFinalScores());
    assertNull("Last error message should be null initially", 
            listener.getLastErrorMessage());
  }

  /**
   * Tests the onTurnChange method.
   * Verifies that the method correctly updates tracking variables.
   */
  @Test
  public void testOnTurnChange() {
    // Call the method
    listener.onTurnChange(PlayerColors.RED);

    // Verify the tracking variables
    assertTrue("Turn change flag should be set", 
            listener.wasTurnChangeReceived());
    assertEquals("Last turn player should be RED", PlayerColors.RED,
            listener.getLastTurnPlayer());

    // Verify other variables remain unaffected
    assertFalse("Game over flag should not be set", 
            listener.wasGameOverReceived());
    assertFalse("Invalid move flag should not be set", 
            listener.wasInvalidMoveReceived());
  }

  /**
   * Tests the onGameOver method.
   * Verifies that the method correctly updates tracking variables.
   */
  @Test
  public void testOnGameOver() {
    // Create test data
    int[] finalScores = new int[]{5, 3};

    // Call the method
    listener.onGameOver(PlayerColors.RED, finalScores);

    // Verify the tracking variables
    assertTrue("Game over flag should be set", 
            listener.wasGameOverReceived());
    assertEquals("Last winner should be RED", PlayerColors.RED, 
            listener.getLastWinner());
    assertArrayEquals("Last final scores should match",
            finalScores, listener.getLastFinalScores());

    // Verify other variables remain unaffected
    assertFalse("Turn change flag should not be set", 
            listener.wasTurnChangeReceived());
    assertFalse("Invalid move flag should not be set", 
            listener.wasInvalidMoveReceived());
  }

  /**
   * Tests the onGameOver method with a tie game.
   * Verifies that null winner is correctly handled.
   */
  @Test
  public void testOnGameOverTie() {
    // Create test data
    int[] finalScores = new int[]{4, 4};

    // Call the method with null winner (tie game)
    listener.onGameOver(null, finalScores);

    // Verify the tracking variables
    assertTrue("Game over flag should be set", 
            listener.wasGameOverReceived());
    assertNull("Last winner should be null for tie game", 
            listener.getLastWinner());
    assertArrayEquals("Last final scores should match",
            finalScores, listener.getLastFinalScores());
  }

  /**
   * Tests the onInvalidMove method.
   * Verifies that the method correctly updates tracking variables.
   */
  @Test
  public void testOnInvalidMove() {
    // Create test data
    String errorMessage = "Illegal move: not enough pawns";

    // Call the method
    listener.onInvalidMove(errorMessage);

    // Verify the tracking variables
    assertTrue("Invalid move flag should be set", 
            listener.wasInvalidMoveReceived());
    assertEquals("Last error message should match",
            errorMessage, listener.getLastErrorMessage());

    // Verify other variables remain unaffected
    assertFalse("Turn change flag should not be set", 
            listener.wasTurnChangeReceived());
    assertFalse("Game over flag should not be set", 
            listener.wasGameOverReceived());
  }

  /**
   * Tests the reset method.
   * Verifies that all tracking variables are reset to their initial state.
   */
  @Test
  public void testReset() {
    // Set up some state
    listener.onTurnChange(PlayerColors.RED);
    listener.onGameOver(PlayerColors.BLUE, new int[]{3, 5});
    listener.onInvalidMove("Test error");

    // Verify state is set
    assertTrue(listener.wasTurnChangeReceived());
    assertTrue(listener.wasGameOverReceived());
    assertTrue(listener.wasInvalidMoveReceived());

    // Reset state
    listener.reset();

    // Verify all tracking variables are reset
    assertFalse("Turn change flag should be reset", 
            listener.wasTurnChangeReceived());
    assertFalse("Game over flag should be reset", 
            listener.wasGameOverReceived());
    assertFalse("Invalid move flag should be reset", 
            listener.wasInvalidMoveReceived());
    assertNull("Last turn player should be reset", 
            listener.getLastTurnPlayer());
    assertNull("Last winner should be reset", 
            listener.getLastWinner());
    assertNull("Last final scores should be reset", 
            listener.getLastFinalScores());
    assertNull("Last error message should be reset", 
            listener.getLastErrorMessage());
  }

  /**
   * Tests the getter methods for positive cases.
   * Verifies that getters correctly return state information.
   */
  @Test
  public void testGetters() {
    // Set up state
    PlayerColors player = PlayerColors.RED;
    PlayerColors winner = PlayerColors.BLUE;
    int[] scores = new int[]{2, 6};
    String errorMessage = "Test error message";

    listener.onTurnChange(player);
    listener.onGameOver(winner, scores);
    listener.onInvalidMove(errorMessage);

    // Test getters
    assertTrue("wasTurnChangeReceived should return true",
            listener.wasTurnChangeReceived());
    assertTrue("wasGameOverReceived should return true",
            listener.wasGameOverReceived());
    assertTrue("wasInvalidMoveReceived should return true",
            listener.wasInvalidMoveReceived());
    assertEquals("getLastTurnPlayer should return RED",
            player, listener.getLastTurnPlayer());
    assertEquals("getLastWinner should return BLUE", winner, 
            listener.getLastWinner());
    assertArrayEquals("getLastFinalScores should return correct scores",
            scores, listener.getLastFinalScores());
    assertEquals("getLastErrorMessage should return correct message",
            errorMessage, listener.getLastErrorMessage());
  }

  /**
   * Tests the getter methods for negative cases.
   * Verifies that getters correctly return default values when no events have been received.
   */
  @Test
  public void testGettersWithNoEvents() {
    // Test getters with no events received
    assertFalse("wasTurnChangeReceived should return false",
            listener.wasTurnChangeReceived());
    assertFalse("wasGameOverReceived should return false",
            listener.wasGameOverReceived());
    assertFalse("wasInvalidMoveReceived should return false",
            listener.wasInvalidMoveReceived());
    assertNull("getLastTurnPlayer should return null",
            listener.getLastTurnPlayer());
    assertNull("getLastWinner should return null",
            listener.getLastWinner());
    assertNull("getLastFinalScores should return null",
            listener.getLastFinalScores());
    assertNull("getLastErrorMessage should return null",
            listener.getLastErrorMessage());
  }

  /**
   * Tests that multiple event notifications work correctly.
   * Verifies that the listener correctly handles receiving multiple events of different types.
   */
  @Test
  public void testMultipleEvents() {
    // First event
    listener.onTurnChange(PlayerColors.RED);
    assertTrue(listener.wasTurnChangeReceived());
    assertEquals(PlayerColors.RED, listener.getLastTurnPlayer());

    // Second event
    listener.onInvalidMove("First error");
    assertTrue(listener.wasInvalidMoveReceived());
    assertEquals("First error", listener.getLastErrorMessage());

    // Third event - same type as first
    listener.onTurnChange(PlayerColors.BLUE);
    assertTrue(listener.wasTurnChangeReceived());
    assertEquals("Last turn player should be updated", PlayerColors.BLUE,
            listener.getLastTurnPlayer());

    // Fourth event - same type as second
    listener.onInvalidMove("Second error");
    assertTrue(listener.wasInvalidMoveReceived());
    assertEquals("Last error message should be updated", "Second error",
            listener.getLastErrorMessage());

    // Final event
    listener.onGameOver(PlayerColors.RED, new int[]{5, 3});
    assertTrue(listener.wasGameOverReceived());
    assertEquals(PlayerColors.RED, listener.getLastWinner());
    assertArrayEquals(new int[]{5, 3}, listener.getLastFinalScores());
  }

  /**
   * Tests multiple resets of the listener.
   * Verifies that the reset method can be called multiple times without issues.
   */
  @Test
  public void testMultipleResets() {
    // Set some state and reset
    listener.onTurnChange(PlayerColors.RED);
    listener.reset();
    assertFalse(listener.wasTurnChangeReceived());

    // Set state again and reset again
    listener.onGameOver(PlayerColors.BLUE, new int[]{3, 4});
    listener.reset();
    assertFalse(listener.wasGameOverReceived());

    // Third cycle
    listener.onInvalidMove("Error");
    listener.reset();
    assertFalse(listener.wasInvalidMoveReceived());
  }
}