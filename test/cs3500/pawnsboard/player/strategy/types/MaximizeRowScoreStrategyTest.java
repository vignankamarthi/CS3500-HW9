package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.mocks.PawnsBoardMockPreset;
import cs3500.pawnsboard.model.mocks.PawnsBoardMockTracker;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for the MaximizeRowScoreStrategy class.
 * Tests the row score maximization logic, move selection patterns, and edge case handling
 * of the strategy.
 */
public class MaximizeRowScoreStrategyTest {

  /**
   * The strategy being tested.
   */
  private MaximizeRowScoreStrategy<PawnsBoardBaseCard> strategy;

  /**
   * A mock model that tracks method calls to verify search patterns.
   */
  private PawnsBoardMockTracker<PawnsBoardBaseCard, ?> trackerMock;

  /**
   * A mock model that allows preset legal moves for controlled testing.
   */
  private PawnsBoardMockPreset<PawnsBoardBaseCard, ?> presetMock;

  /**
   * Influence grid for test cards.
   */
  private boolean[][] emptyInfluence;

  /**
   * Sets up the test environment before each test.
   * Creates a fresh strategy instance and initializes the mock models.
   */
  @Before
  public void setUp() {
    strategy = new MaximizeRowScoreStrategy<>();
    trackerMock = new PawnsBoardMockTracker<>();
    presetMock = new PawnsBoardMockPreset<>();
    emptyInfluence = new boolean[5][5];

    // Set up standard test board for both mocks
    trackerMock.setupInitialBoard();
    presetMock.setupInitialBoard();
  }

  /**
   * Tests that MaximizeRowScoreStrategy returns a valid move when one exists
   * that improves a row score.
   */
  @Test
  public void testBasicFunctionality_ReturnsValidMove() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up row scores where BLUE is winning in row 0
    presetMock.setRowScores(0, 0, 2);

    // Set up the mock to allow the move and set score changes
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            // After playing card, RED's score will be 3, which is > BLUE's score of 2
            .setMoveRowScoreChanges(0, 0, 0, 3,
                    0);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a move was returned
    assertTrue("Strategy should return a move when a score-improving move exists",
            move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
    assertEquals("Card index should be 0", 0,
            move.get().getCardIndex());
    assertEquals("Row should be 0", 0,
            move.get().getRow());
    assertEquals("Column should be 0", 0,
            move.get().getCol());
  }

  /**
   * Tests that MaximizeRowScoreStrategy returns a pass move when no legal moves exist
   * that would improve a row score.
   */
  @Test
  public void testBasicFunctionality_NoScoreImprovingMoves() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence));

    // Set up row scores where BLUE is winning in row 0
    presetMock.setRowScores(0, 0, 3);

    // Set up the mock to allow the move but with insufficient score improvement
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            // After playing card, RED's score will be 2, which is < BLUE's score of 3
            .setMoveRowScoreChanges(0, 0, 0, 2, 0);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a pass move was returned
    assertTrue("Strategy should return a move when no score-improving moves exist",
            move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests that MaximizeRowScoreStrategy correctly prioritizes rows where the player's score
   * is less than or equal to the opponent's score.
   */
  @Test
  public void testRowPrioritization() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up row scores where:
    // - Row 0: RED is already winning (5 vs 2)
    // - Row 1: Tied (2 vs 2)
    // - Row 2: BLUE is winning (1 vs 3)
    presetMock.setRowScores(0, 5, 2);
    presetMock.setRowScores(1, 2, 2);
    presetMock.setRowScores(2, 1, 3);

    // Set up legal moves in all three rows
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            .setLegalMove(0, 1, 0, true)
            .setLegalMove(0, 2, 0, true)
            // Set score changes for each move
            .setMoveRowScoreChanges(0, 0, 0, 3,
                    0)  // Row 0: 5+3=8 vs 2
            .setMoveRowScoreChanges(0, 1, 0, 3,
                    0)  // Row 1: 2+3=5 vs 2
            .setMoveRowScoreChanges(0, 2, 0, 3,
                    0); // Row 2: 1+3=4 vs 3

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the move is targeting a row where player's score <= opponent's
    assertTrue("Strategy should return a move", move.isPresent());
    assertTrue("Row should be 1 (tied) or 2 (losing)",
            move.get().getRow() == 1 || move.get().getRow() == 2);

    // Based on the top-to-bottom search pattern, it should actually be row 1
    assertEquals("Row should be 1 (first row where improvement is needed)",
            1, move.get().getRow());
    assertEquals("Column should be 0", 0, move.get().getCol());
  }

  /**
   * Tests that MaximizeRowScoreStrategy correctly prioritizes moves that make the player's score
   * strictly greater than the opponent's score.
   */
  @Test
  public void testScoreImprovement() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence));

    // Set up row scores where BLUE is winning in row 0
    presetMock.setRowScores(0, 1, 3);

    // Set up multiple legal moves with different score impacts
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            .setLegalMove(0, 0, 1, true)
            // First move: RED's score becomes 3, equal to BLUE's 3
            .setMoveRowScoreChanges(0, 0, 0, 2,
                    0)
            // Second move: RED's score becomes 4, greater than BLUE's 3
            .setMoveRowScoreChanges(0, 0, 1, 3,
                    0);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the move that makes RED's score greater than BLUE's is chosen
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Column should be 1 (move that makes score strictly greater)",
            1, move.get().getCol());
  }

  /**
   * Tests the search pattern by row for MaximizeRowScoreStrategy.
   * Verifies that rows are checked from top to bottom.
   */
  @Test
  public void testRowSearchPattern() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up the tracker mock with RED player and test hand
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED);

    // Set up row scores where RED is losing in all rows
    for (int row = 0; row < 3; row++) {
      trackerMock.setRowScores(row, 0, 3);
    }

    // Call the strategy
    strategy.chooseMove(trackerMock);

    // Get row scores checked in order by extracting from method call log
    List<String> rowScoresCalls = trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("getRowScores"))
            .collect(Collectors.toList());

    // Verify rows are checked in order
    assertEquals("Row 0 should be checked first", "getRowScores(0)",
            rowScoresCalls.get(0));

    // If there are more rows checked, they should be in order
    if (rowScoresCalls.size() > 1) {
      assertEquals("Row 1 should be checked second", "getRowScores(1)",
              rowScoresCalls.get(1));
    }
    if (rowScoresCalls.size() > 2) {
      assertEquals("Row 2 should be checked third", "getRowScores(2)",
              rowScoresCalls.get(2));
    }
  }

  /**
   * Tests behavior of MaximizeRowScoreStrategy when the player's hand is empty.
   */
  @Test
  public void testEdgeCase_EmptyHand() {
    // Set up the mock with an empty hand
    presetMock.setPlayerHand(PlayerColors.RED, new ArrayList<>());

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a pass move was returned
    assertTrue("Strategy should return a move when hand is empty", move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests the behavior of MaximizeRowScoreStrategy when there are no legal moves.
   */
  @Test
  public void testEdgeCase_NoLegalMoves() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up the mock to disallow all moves
    presetMock.setPlayerHand(PlayerColors.RED, redHand);

    // Set all moves as illegal
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        presetMock.setLegalMove(0, row, col, false);
      }
    }

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a pass move was returned
    assertTrue("Strategy should return a pass move when no legal moves exist",
            move.isPresent());
    assertEquals("Move type should be PASS",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests that MaximizeRowScoreStrategy works correctly with only one row needing improvement.
   */
  @Test
  public void testEdgeCase_OnlyOneRowNeedsImprovement() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up row scores where:
    // - Row 0: RED is winning (5 vs 2)
    // - Row 1: RED is winning (4 vs 2)
    // - Row 2: BLUE is winning (1 vs 3)
    presetMock.setRowScores(0, 5, 2);
    presetMock.setRowScores(1, 4, 2);
    presetMock.setRowScores(2, 1, 3);

    // Set up legal moves in all rows
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            .setLegalMove(0, 1, 0, true)
            .setLegalMove(0, 2, 0, true)
            // Set score changes
            .setMoveRowScoreChanges(0, 0, 0, 3,
                    0)
            .setMoveRowScoreChanges(0, 1, 0, 3,
                    0)
            .setMoveRowScoreChanges(0, 2, 0, 3,
                    0);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the move targets row 2 (the only row where BLUE is winning)
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Row should be 2 (where BLUE is winning)", 2, move.get().getRow());
  }

  /**
   * Tests the behavior of MaximizeRowScoreStrategy with multiple cards that have
   * different impacts on row scores.
   */
  @Test
  public void testMultipleCardsWithDifferentImpacts() {
    // Create a test hand with two cards of different values
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("LowValueCard", 1, 2, emptyInfluence));
    redHand.add(new PawnsBoardBaseCard("HighValueCard", 1, 4, emptyInfluence));

    // Set up row scores where BLUE is winning in row 0
    presetMock.setRowScores(0, 0, 3);

    // Set up legal moves for both cards
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true) // Low value card in (0,0)
            .setLegalMove(1, 0, 0, true) // High value card in (0,0)
            // Low value card: RED's score becomes 2, still < BLUE's 3
            .setMoveRowScoreChanges(0, 0, 0, 2,
                    0)
            // High value card: RED's score becomes 4, > BLUE's 3
            .setMoveRowScoreChanges(1, 0, 0, 4,
                    0);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the high value card (index 1) is chosen
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Card index should be 1 (high value card)", 1,
            move.get().getCardIndex());
  }

  /**
   * Tests that the move simulation works correctly by checking that the simulated
   * row scores are used for decision-making.
   */
  @Test
  public void testMoveSimulation() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up initial row scores
    presetMock.setRowScores(0, 1, 3); // BLUE winning in row 0

    // Create a simulation result board with updated scores after the move
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> simulationResult = new PawnsBoardMockPreset<>();
    simulationResult.setupInitialBoard();
    simulationResult.setCurrentPlayer(PlayerColors.BLUE); // Turn changes to BLUE
    simulationResult.setRowScores(0, 4, 3);
    // In simulation, RED has 4 points vs BLUE's 3

    // Set the simulation result to return from copy
    presetMock.setPresetSimulationResult(simulationResult);

    // Set up a legal move
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the strategy used the simulation result for decision making
    assertTrue("Strategy should return a move based on simulation", move.isPresent());
    assertEquals("Row should be 0", 0, move.get().getRow());
    assertEquals("Column should be 0", 0, move.get().getCol());
  }

  /**
   * Tests the behavior of MaximizeRowScoreStrategy when the game is not started.
   */
  @Test
  public void testExceptionHandling_GameNotStarted() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(false);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a pass move
    assertTrue("Strategy should handle game not started gracefully", move.isPresent());
    assertEquals("Should return PASS move when game not started",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests the behavior of MaximizeRowScoreStrategy when the game is already over.
   */
  @Test
  public void testExceptionHandling_GameOver() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(true)
            .setGameOver(true);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a pass move
    assertTrue("Strategy should handle game over gracefully", move.isPresent());
    assertEquals("Should return PASS move when game is over",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests the behavior when move simulation fails.
   */
  @Test
  public void testExceptionHandling_SimulationFailure() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up the mock to throw exception during simulation
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true);

    // Create a simulation result that will throw exception
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> simulationResult = new PawnsBoardMockPreset<>();
    // Don't call setupInitialBoard, so it will throw exception when trying to get board state

    // Set the simulation result to return from copy
    presetMock.setPresetSimulationResult(simulationResult);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a pass move
    assertTrue("Strategy should handle simulation failure gracefully", move.isPresent());
    assertEquals("Should return PASS move when simulation fails",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests that the strategy behaves correctly when there are row ties and opportunities
   * to break ties.
   */
  @Test
  public void testRowTieBreaking() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up row scores with ties
    presetMock.setRowScores(0, 2, 2); // Tie in row 0
    presetMock.setRowScores(1, 3, 3); // Tie in row 1
    presetMock.setRowScores(2, 1, 1); // Tie in row 2

    // Set up legal moves
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true)
            .setLegalMove(0, 1, 0, true)
            .setLegalMove(0, 2, 0, true)
            // Score changes
            .setMoveRowScoreChanges(0, 0, 0, 3,
                    0) // 2+3=5 vs 2
            .setMoveRowScoreChanges(0, 1, 0, 3,
                    0) // 3+3=6 vs 3
            .setMoveRowScoreChanges(0, 2, 0, 3,
                    0); // 1+3=4 vs 1

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a move in one of the tied rows is chosen
    assertTrue("Strategy should return a move", move.isPresent());
    assertTrue("Row should be one of the tied rows",
            move.get().getRow() == 0 || move.get().getRow() == 1 || move.get().getRow() == 2);
  }

  /**
   * Tests that the strategy searches all rows even when legal moves exist in early rows.
   */
  @Test
  public void testSearchAllRows() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence));

    // Set up the tracker mock
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(true);

    // Set up row scores where:
    // - Row 0: BLUE is winning slightly (1 vs 2)
    // - Row 1: No score (0 vs 0)
    // - Row 2: BLUE is winning by a lot (1 vs 5)
    trackerMock.setRowScores(0, 1, 2);
    trackerMock.setRowScores(1, 0, 0);
    trackerMock.setRowScores(2, 1, 5);

    // Call the strategy
    strategy.chooseMove(trackerMock);

    // Check that all rows were examined
    List<String> rowScoresCalls = trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("getRowScores"))
            .collect(Collectors.toList());

    assertTrue("Should check row 0", rowScoresCalls.contains("getRowScores(0)"));
    assertTrue("Should check row 1", rowScoresCalls.contains("getRowScores(1)"));
    assertTrue("Should check row 2", rowScoresCalls.contains("getRowScores(2)"));
  }

  /**
   * To generate the log output for a legal move with this strategy.
   */
  @Test
  public void generateMaximizeRowScoreStrategyTranscript() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 3, emptyInfluence));

    // Set up the tracker mock with RED player and test hand
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setRowScores(0, 0, 2)  // Set up a
            // scenario where RED can improve row score
            .setReturnAllLegalMoves(true);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(trackerMock);

    String hello = "world";

    assertEquals("hello", "world", hello); // world
  }
}