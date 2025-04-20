package cs3500.pawnsboard.model.mocks;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for the PawnsBoardMockPreset class.
 * Tests the creation, configuration, and behavior of the mock implementation used for testing
 * strategies with predetermined game states, legal moves, and model status listener functionality.
 */
public class PawnsBoardMockPresetTest {

  private PawnsBoardMockPreset<PawnsBoardBaseCard, ?> mockModel;
  private boolean[][] emptyInfluence;
  private PawnsBoardBaseCard testCard;

  /**
   * Sets up a fresh mock model and test card for each test.
   */
  @Before
  public void setUp() {
    mockModel = new PawnsBoardMockPreset<>();
    emptyInfluence = new boolean[5][5];
    testCard = new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence);
  }

  /**
   * Tests that a new mock model is properly initialized with default empty state.
   * Verifies that the game is not started, not over, and collections are initialized as empty.
   */
  @Test
  public void testInitialState() {
    assertFalse("Game should not be over initially", mockModel.isGameOver());

    // Should throw exception when not started
    try {
      mockModel.getCurrentPlayer();
      fail("Should throw IllegalStateException for game not started");
    } catch (IllegalStateException e) {
      assertEquals("Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests the setupInitialBoard method.
   * Verifies that it correctly initializes a standard 3x5 game board with:
   * - Game started but not over
   * - RED as the current player
   * - RED pawns in the first column
   * - BLUE pawns in the last column
   * - Row scores and total score initialized to 0
   */
  @Test
  public void testSetupInitialBoard() {
    mockModel.setupInitialBoard();

    assertTrue("Game should be started after setup", mockModel.getGameStarted());
    assertFalse("Game should not be over after setup", mockModel.isGameOver());
    assertEquals("RED should be the current player", PlayerColors.RED,
            mockModel.getCurrentPlayer());

    // Check dimensions
    int[] dimensions = mockModel.getBoardDimensions();
    assertEquals("Board should have 3 rows", 3, dimensions[0]);
    assertEquals("Board should have 5 columns", 5, dimensions[1]);

    // Check RED pawns in first column
    for (int r = 0; r < 3; r++) {
      assertEquals("First column should contain pawns", CellContent.PAWNS,
              mockModel.getCellContent(r, 0));
      assertEquals("First column pawns should be RED", PlayerColors.RED,
              mockModel.getCellOwner(r, 0));
      assertEquals("First column should have 1 pawn", 1,
              mockModel.getPawnCount(r, 0));
    }

    // Check BLUE pawns in last column
    for (int r = 0; r < 3; r++) {
      assertEquals("Last column should contain pawns", CellContent.PAWNS,
              mockModel.getCellContent(r, 4));
      assertEquals("Last column pawns should be BLUE", PlayerColors.BLUE,
              mockModel.getCellOwner(r, 4));
      assertEquals("Last column should have 1 pawn", 1,
              mockModel.getPawnCount(r, 4));
    }

    // Check row scores
    for (int r = 0; r < 3; r++) {
      assertArrayEquals("Row scores should be [0,0]", new int[]{0, 0},
              mockModel.getRowScores(r));
    }

    // Check total score
    assertArrayEquals("Total score should be [0,0]", new int[]{0, 0},
            mockModel.getTotalScore());
  }

  /**
   * Tests the setLegalMove method.
   * Verifies that legal and illegal moves are correctly tracked and returned by isLegalMove.
   */
  @Test
  public void testSetLegalMove() {
    mockModel.setupInitialBoard();

    // Set some moves as legal and others as illegal
    mockModel.setLegalMove(0, 0, 0, true);
    // Make (0,0) legal for card 0
    mockModel.setLegalMove(0, 0, 1, false);
    // Make (0,1) illegal for card 0

    // Check legal move
    assertTrue("Move (0,0,0) should be legal",
            mockModel.isLegalMove(0, 0, 0));

    // Check illegal move
    assertFalse("Move (0,0,1) should be illegal",
            mockModel.isLegalMove(0, 0, 1));

    // Check a move that wasn't explicitly set (should default to checking card index only)
    // Assuming player hand has cards
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    assertTrue("Move not explicitly set should check card index validity",
            mockModel.isLegalMove(0, 1, 1));
    assertFalse("Invalid card index should be illegal",
            mockModel.isLegalMove(1, 1, 1));
  }

  /**
   * Tests the setMoveRowScoreChanges method.
   * Verifies that row scores are correctly updated when a move is made.
   */
  @Test
  public void testSetMoveRowScoreChanges() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Set row score changes for a move
    mockModel.setMoveRowScoreChanges(0, 0, 0, 2,
            0); // +2 for RED, +0 for BLUE in row 0

    // Make the move
    try {
      mockModel.placeCard(0, 0, 0);

      // Check if row scores were updated
      assertArrayEquals("Row 0 scores should be [2,0]", new int[]{2, 0},
              mockModel.getRowScores(0));

      // Other rows should remain unchanged
      assertArrayEquals("Row 1 scores should be [0,0]", new int[]{0, 0},
              mockModel.getRowScores(1));
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests the setMoveTotalScoreChanges method.
   * Verifies that total scores are correctly updated when a move is made.
   */
  @Test
  public void testSetMoveTotalScoreChanges() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Set total score changes for a move
    mockModel.setMoveTotalScoreChanges(0, 0, 0, 3,
            1); // +3 for RED, +1 for BLUE total

    // Make the move
    try {
      mockModel.placeCard(0, 0, 0);

      // Check if total scores were updated
      assertArrayEquals("Total scores should be [3,1]", new int[]{3, 1},
              mockModel.getTotalScore());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests the setCellOwnershipChanges method.
   * Verifies that cell ownership is correctly updated when a move is made.
   */
  @Test
  public void testSetCellOwnershipChanges() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Create map of cell ownership changes
    Map<String, Boolean> changes = new HashMap<>();
    changes.put("0,1", true);  // RED will own cell (0,1)
    changes.put("1,0", false); // BLUE will own cell (1,0)

    // Set cell ownership changes for a move
    mockModel.setCellOwnershipChanges(0, 0, 0, changes);

    // Make the move
    try {
      mockModel.placeCard(0, 0, 0);

      // Check if cell ownership was updated
      assertEquals("Cell (0,1) should be owned by RED",
              PlayerColors.RED, mockModel.getCellOwner(0, 1));
      assertEquals("Cell (1,0) should be owned by BLUE",
              PlayerColors.BLUE, mockModel.getCellOwner(1, 0));
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests the setPresetSimulationResult method.
   * Verifies that copy() returns the preset simulation result when set.
   */
  @Test
  public void testSetPresetSimulationResult() {
    mockModel.setupInitialBoard();

    // Create a different mock to use as simulation result
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> simulationResult = new PawnsBoardMockPreset<>();
    simulationResult.setupInitialBoard();
    simulationResult.setCurrentPlayer(PlayerColors.BLUE); // Differs from original

    // Set preset simulation result
    mockModel.setPresetSimulationResult(simulationResult);

    // Call copy and check if it returns the preset
    PawnsBoard<PawnsBoardBaseCard, ?> copy = mockModel.copy();

    assertEquals("Copy should return preset with BLUE as current player",
            PlayerColors.BLUE, copy.getCurrentPlayer());
  }

  /**
   * Tests the placeCard method.
   * Verifies that a card is correctly placed and game state updated accordingly.
   */
  @Test
  public void testPlaceCard() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Place a card
    try {
      mockModel.placeCard(0, 0, 0);

      // Verify card placement
      assertEquals("Cell (0,0) should contain a card",
              CellContent.CARD, mockModel.getCellContent(0, 0));
      assertEquals("Cell (0,0) should be owned by RED",
              PlayerColors.RED, mockModel.getCellOwner(0, 0));
      assertEquals("Cell (0,0) should have the test card",
              testCard, mockModel.getCardAtCell(0, 0));

      // Verify turn switched
      assertEquals("Current player should be BLUE",
              PlayerColors.BLUE, mockModel.getCurrentPlayer());

      // Verify card removed from hand
      assertEquals("RED hand should be empty",
              0, mockModel.getPlayerHand(PlayerColors.RED).size());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that placeCard throws an exception for illegal moves when configured to do so.
   */
  @Test
  public void testPlaceCard_IllegalMove() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Set move as illegal
    mockModel.setLegalMove(0, 0, 0, false);

    // Attempt to place a card
    try {
      mockModel.placeCard(0, 0, 0);
      fail("Should throw IllegalAccessException for illegal move");
    } catch (IllegalAccessException e) {
      assertEquals("Move is not legal", e.getMessage());
    } catch (Exception e) {
      fail("Wrong exception type thrown: " + e.getClass().getName());
    }
  }

  /**
   * Tests the passTurn method.
   * Verifies that passing correctly switches the current player.
   */
  @Test
  public void testPassTurn() {
    mockModel.setupInitialBoard();

    try {
      mockModel.passTurn();

      // Verify turn switched
      assertEquals("Current player should be BLUE",
              PlayerColors.BLUE, mockModel.getCurrentPlayer());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests the copy method.
   * Verifies that a full copy of the mock is created with the same state.
   */
  @Test
  public void testCopy() {
    mockModel.setupInitialBoard();

    // Make some changes to the state
    mockModel.setCellContent(1, 1, CellContent.PAWNS);
    mockModel.setCellOwner(1, 1, PlayerColors.RED);
    mockModel.setPawnCount(1, 1, 2);

    // Create a copy
    PawnsBoard<PawnsBoardBaseCard, ?> copy = mockModel.copy();

    // Verify copy has the same state
    assertEquals("Copy should have same content at (1,1)",
            CellContent.PAWNS, copy.getCellContent(1, 1));
    assertEquals("Copy should have same owner at (1,1)",
            PlayerColors.RED, copy.getCellOwner(1, 1));
    assertEquals("Copy should have same pawn count at (1,1)",
            2, copy.getPawnCount(1, 1));
  }

  /**
   * Tests the setGameStarted method.
   * Verifies that the game started state is correctly set.
   */
  @Test
  public void testSetGameStarted() {
    mockModel.setGameStarted(true);

    try {
      // Should succeed now that game is started
      mockModel.getBoardDimensions();
    } catch (IllegalStateException e) {
      fail("Exception should not be thrown after setting game started");
    }
  }

  /**
   * Tests the setGameOver method.
   * Verifies that the game over state is correctly set.
   */
  @Test
  public void testSetGameOver() {
    mockModel.setGameStarted(true)
            .setGameOver(true);

    assertTrue("Game should be over", mockModel.isGameOver());
  }

  /**
   * Tests the setCurrentPlayer method.
   * Verifies that the current player is correctly set.
   */
  @Test
  public void testSetCurrentPlayer() {
    mockModel.setGameStarted(true)
            .setCurrentPlayer(PlayerColors.BLUE);

    assertEquals("Current player should be BLUE",
            PlayerColors.BLUE, mockModel.getCurrentPlayer());
  }

  /**
   * Tests the setBoardDimensions method.
   * Verifies that the board dimensions are correctly set.
   */
  @Test
  public void testSetBoardDimensions() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(4, 7);

    int[] dimensions = mockModel.getBoardDimensions();
    assertEquals("Board should have 4 rows", 4, dimensions[0]);
    assertEquals("Board should have 7 columns", 7, dimensions[1]);
  }

  /**
   * Tests the setCellContent method.
   * Verifies that cell content can be set for a specific cell and retrieved correctly.
   */
  @Test
  public void testSetCellContent() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCellContent(1, 2, CellContent.PAWNS);

    assertEquals("Cell (1,2) should have PAWNS content",
            CellContent.PAWNS, mockModel.getCellContent(1, 2));
  }

  /**
   * Tests the setCellOwner method.
   * Verifies that cell ownership can be set for a specific cell and retrieved correctly.
   */
  @Test
  public void testSetCellOwner() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCellContent(1, 2, CellContent.PAWNS)
            .setCellOwner(1, 2, PlayerColors.BLUE);

    assertEquals("Cell (1,2) should be owned by BLUE",
            PlayerColors.BLUE, mockModel.getCellOwner(1, 2));
  }

  /**
   * Tests the setPawnCount method.
   * Verifies that pawn count can be set for a specific cell and retrieved correctly.
   */
  @Test
  public void testSetPawnCount() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCellContent(1, 2, CellContent.PAWNS)
            .setPawnCount(1, 2, 3);

    assertEquals("Cell (1,2) should have 3 pawns",
            3, mockModel.getPawnCount(1, 2));
  }

  /**
   * Tests the setCardAtCell method.
   * Verifies that a card can be set for a specific cell and retrieved correctly.
   */
  @Test
  public void testSetCardAtCell() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCellContent(1, 2, CellContent.CARD)
            .setCardAtCell(1, 2, testCard);

    assertEquals("Cell (1,2) should have test card",
            testCard, mockModel.getCardAtCell(1, 2));
  }

  /**
   * Tests the setRowScores method.
   * Verifies that row scores can be set and retrieved correctly.
   */
  @Test
  public void testSetRowScores() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setRowScores(0, 3, 2)
            .setRowScores(1, 5, 1)
            .setRowScores(2, 0, 4);

    assertArrayEquals("Row 0 scores should be [3,2]", new int[]{3, 2},
            mockModel.getRowScores(0));
    assertArrayEquals("Row 1 scores should be [5,1]", new int[]{5, 1},
            mockModel.getRowScores(1));
    assertArrayEquals("Row 2 scores should be [0,4]", new int[]{0, 4},
            mockModel.getRowScores(2));
  }

  /**
   * Tests the setTotalScore method.
   * Verifies that total scores can be set and retrieved correctly.
   */
  @Test
  public void testSetTotalScore() {
    mockModel.setGameStarted(true)
            .setTotalScore(8, 6);

    assertArrayEquals("Total scores should be [8,6]", new int[]{8, 6},
            mockModel.getTotalScore());
  }

  /**
   * Tests the setWinner method.
   * Verifies that the winner can be set and retrieved correctly.
   */
  @Test
  public void testSetWinner() {
    mockModel.setGameStarted(true)
            .setGameOver(true)
            .setWinner(PlayerColors.RED);

    assertEquals("Winner should be RED", PlayerColors.RED, mockModel.getWinner());
  }

  /**
   * Tests the setPlayerHand method.
   * Verifies that player hands can be set and retrieved correctly.
   */
  @Test
  public void testSetPlayerHand() {
    // Create hands for both players
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);

    List<PawnsBoardBaseCard> blueHand = new ArrayList<>();
    PawnsBoardBaseCard blueCard = new PawnsBoardBaseCard("BlueCard", 1, 3,
            emptyInfluence);
    blueHand.add(blueCard);

    mockModel.setGameStarted(true)
            .setPlayerHand(PlayerColors.RED, redHand)
            .setPlayerHand(PlayerColors.BLUE, blueHand);

    // Verify hands
    List<PawnsBoardBaseCard> retrievedRedHand = mockModel.getPlayerHand(PlayerColors.RED);
    List<PawnsBoardBaseCard> retrievedBlueHand = mockModel.getPlayerHand(PlayerColors.BLUE);

    assertEquals("RED hand should have 1 card", 1, retrievedRedHand.size());
    assertEquals("BLUE hand should have 1 card", 1, retrievedBlueHand.size());
    assertEquals("RED hand should contain test card", testCard, retrievedRedHand.get(0));
    assertEquals("BLUE hand should contain blue card", blueCard,
            retrievedBlueHand.get(0));
  }

  /**
   * Tests the setRemainingDeckSize method.
   * Verifies that remaining deck sizes can be set and retrieved correctly.
   */
  @Test
  public void testSetRemainingDeckSize() {
    mockModel.setGameStarted(true)
            .setRemainingDeckSize(PlayerColors.RED, 10)
            .setRemainingDeckSize(PlayerColors.BLUE, 8);

    assertEquals("RED deck should have 10 cards remaining",
            10, mockModel.getRemainingDeckSize(PlayerColors.RED));
    assertEquals("BLUE deck should have 8 cards remaining",
            8, mockModel.getRemainingDeckSize(PlayerColors.BLUE));
  }

  /**
   * Tests that getCellContent throws an exception for invalid coordinates.
   */
  @Test
  public void testGetCellContent_InvalidCoordinates() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.getCellContent(5, 5);
      fail("Should throw IllegalArgumentException for invalid coordinates");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid coordinates",
              e.getMessage().contains("Invalid coordinates"));
    }
  }

  /**
   * Tests that getCellOwner throws an exception for invalid coordinates.
   */
  @Test
  public void testGetCellOwner_InvalidCoordinates() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.getCellOwner(5, 5);
      fail("Should throw IllegalArgumentException for invalid coordinates");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid coordinates",
              e.getMessage().contains("Invalid coordinates"));
    }
  }

  /**
   * Tests that getPawnCount throws an exception for invalid coordinates.
   */
  @Test
  public void testGetPawnCount_InvalidCoordinates() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.getPawnCount(5, 5);
      fail("Should throw IllegalArgumentException for invalid coordinates");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid coordinates",
              e.getMessage().contains("Invalid coordinates"));
    }
  }

  /**
   * Tests that getCardAtCell throws an exception for invalid coordinates.
   */
  @Test
  public void testGetCardAtCell_InvalidCoordinates() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.getCardAtCell(5, 5);
      fail("Should throw IllegalArgumentException for invalid coordinates");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid coordinates",
              e.getMessage().contains("Invalid coordinates"));
    }
  }

  /**
   * Tests that getRowScores throws an exception for invalid row index.
   */
  @Test
  public void testGetRowScores_InvalidRow() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.getRowScores(5);
      fail("Should throw IllegalArgumentException for invalid row");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention row index",
              e.getMessage().contains("Row index out of bounds"));
    }
  }

  /**
   * Tests that getWinner throws an exception when the game is not over.
   */
  @Test
  public void testGetWinner_GameNotOver() {
    mockModel.setGameStarted(true)
            .setGameOver(false);

    try {
      mockModel.getWinner();
      fail("Should throw IllegalStateException when game is not over");
    } catch (IllegalStateException e) {
      assertEquals("Game is not over yet", e.getMessage());
    }
  }

  /**
   * Tests that getPlayerHand throws an exception for null player.
   */
  @Test
  public void testGetPlayerHand_NullPlayer() {
    mockModel.setGameStarted(true);

    try {
      mockModel.getPlayerHand(null);
      fail("Should throw IllegalArgumentException for null player");
    } catch (IllegalArgumentException e) {
      assertEquals("PlayerColors cannot be null", e.getMessage());
    }
  }

  /**
   * Tests that getRemainingDeckSize throws an exception for null player.
   */
  @Test
  public void testGetRemainingDeckSize_NullPlayer() {
    mockModel.setGameStarted(true);

    try {
      mockModel.getRemainingDeckSize(null);
      fail("Should throw IllegalArgumentException for null player");
    } catch (IllegalArgumentException e) {
      assertEquals("PlayerColors cannot be null", e.getMessage());
    }
  }

  /**
   * Tests that placeCard throws an exception when the game is not started.
   */
  @Test
  public void testPlaceCard_GameNotStarted() {
    mockModel.setGameStarted(false);

    try {
      mockModel.placeCard(0, 0, 0);
      fail("Should throw IllegalStateException when game is not started");
    } catch (IllegalStateException e) {
      assertEquals("Game has not been started", e.getMessage());
    } catch (Exception e) {
      fail("Wrong exception type thrown: " + e.getClass().getName());
    }
  }

  /**
   * Tests that placeCard throws an exception when the game is over.
   */
  @Test
  public void testPlaceCard_GameOver() {
    mockModel.setGameStarted(true)
            .setGameOver(true);

    try {
      mockModel.placeCard(0, 0, 0);
      fail("Should throw IllegalStateException when game is over");
    } catch (IllegalStateException e) {
      assertEquals("Game is already over", e.getMessage());
    } catch (Exception e) {
      fail("Wrong exception type thrown: " + e.getClass().getName());
    }
  }

  /**
   * Tests that passTurn throws an exception when the game is not started.
   */
  @Test
  public void testPassTurn_GameNotStarted() {
    mockModel.setGameStarted(false);

    try {
      mockModel.passTurn();
      fail("Should throw IllegalStateException when game is not started");
    } catch (IllegalStateException e) {
      assertEquals("Game has not been started", e.getMessage());
    } catch (Exception e) {
      fail("Wrong exception type thrown: " + e.getClass().getName());
    }
  }

  /**
   * Tests that passTurn throws an exception when the game is over.
   */
  @Test
  public void testPassTurn_GameOver() {
    mockModel.setGameStarted(true)
            .setGameOver(true);

    try {
      mockModel.passTurn();
      fail("Should throw IllegalStateException when game is over");
    } catch (IllegalStateException e) {
      assertEquals("Game is already over", e.getMessage());
    } catch (Exception e) {
      fail("Wrong exception type thrown: " + e.getClass().getName());
    }
  }

  /**
   * Tests that copy throws an exception when the game is not started.
   */
  @Test
  public void testCopy_GameNotStarted() {
    mockModel.setGameStarted(false);

    try {
      mockModel.copy();
      fail("Should throw IllegalStateException when game is not started");
    } catch (IllegalStateException e) {
      assertEquals("Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests the method chaining functionality.
   * Verifies that multiple setter methods can be chained together.
   */
  @Test
  public void testMethodChaining() {
    // Test a long method chain
    mockModel.setGameStarted(true)
            .setBoardDimensions(4, 5)
            .setCurrentPlayer(PlayerColors.BLUE)
            .setCellContent(1, 2, CellContent.PAWNS)
            .setCellOwner(1, 2, PlayerColors.BLUE)
            .setPawnCount(1, 2, 2)
            .setRowScores(1, 3, 4)
            .setTotalScore(5, 7);

    // Verify all settings took effect
    assertEquals(PlayerColors.BLUE, mockModel.getCurrentPlayer());
    assertEquals(CellContent.PAWNS, mockModel.getCellContent(1, 2));
    assertEquals(PlayerColors.BLUE, mockModel.getCellOwner(1, 2));
    assertEquals(2, mockModel.getPawnCount(1, 2));
    assertArrayEquals(new int[]{3, 4}, mockModel.getRowScores(1));
    assertArrayEquals(new int[]{5, 7}, mockModel.getTotalScore());
  }

  /**
   * Tests the behavior of the mock when combining row scores and cell ownership changes.
   * Verifies that complex interactions between different configuration settings work correctly.
   */
  @Test
  public void testComplexInteraction_ScoresAndOwnership() {
    mockModel.setupInitialBoard();

    // Create test hand
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, hand);

    // Set up complex interactions for a move
    mockModel.setMoveRowScoreChanges(0, 0, 0, 2,
                    0) // Update row 0 scores
            .setMoveTotalScoreChanges(0, 0, 0, 2,
                    0); // Update total score

    // Create cell ownership changes
    Map<String, Boolean> changes = new HashMap<>();
    changes.put("0,1", true);  // RED will own cell (0,1)
    changes.put("1,0", false); // BLUE will own cell (1,0)
    mockModel.setCellOwnershipChanges(0, 0, 0, changes);

    // Make the move
    try {
      mockModel.placeCard(0, 0, 0);

      // Check everything was updated correctly
      assertArrayEquals("Row 0 scores should be [2,0]", new int[]{2, 0},
              mockModel.getRowScores(0));
      assertArrayEquals("Total scores should be [2,0]", new int[]{2, 0},
              mockModel.getTotalScore());
      assertEquals("Cell (0,1) should be owned by RED",
              PlayerColors.RED, mockModel.getCellOwner(0, 1));
      assertEquals("Cell (1,0) should be owned by BLUE",
              PlayerColors.BLUE, mockModel.getCellOwner(1, 0));
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that the getPlayerHand method returns a defensive copy.
   * Verifies that modifying the returned list doesn't affect the mock's state.
   */
  @Test
  public void testGetPlayerHand_DefensiveCopy() {
    // Create a hand
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);

    mockModel.setGameStarted(true)
            .setPlayerHand(PlayerColors.RED, redHand);

    // Get the hand and modify it
    List<PawnsBoardBaseCard> retrievedHand = mockModel.getPlayerHand(PlayerColors.RED);
    retrievedHand.clear();

    // Verify the original hand wasn't affected
    assertEquals("Mock's hand should still have 1 card",
            1, mockModel.getPlayerHand(PlayerColors.RED).size());
  }

  /**
   * Tests the isLegalMove method when no moves are explicitly set.
   * Verifies that the default behavior is to check card index validity.
   */
  @Test
  public void testIsLegalMove_DefaultBehavior() {
    mockModel.setupInitialBoard();

    // Create a hand
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);
    mockModel.setPlayerHand(PlayerColors.RED, redHand);

    // Check valid card index
    assertTrue("Valid card index should be legal by default",
            mockModel.isLegalMove(0, 0, 0));

    // Check invalid card index
    assertFalse("Invalid card index should be illegal",
            mockModel.isLegalMove(1, 0, 0));
  }

  /**
   * Tests that isLegalMove throws an exception when the game is not started.
   */
  @Test
  public void testIsLegalMove_GameNotStarted() {
    mockModel.setGameStarted(false);

    try {
      mockModel.isLegalMove(0, 0, 0);
      fail("Should throw IllegalStateException when game is not started");
    } catch (IllegalStateException e) {
      assertEquals("Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests that isLegalMove throws an exception when the game is over.
   */
  @Test
  public void testIsLegalMove_GameOver() {
    mockModel.setGameStarted(true)
            .setGameOver(true);

    try {
      mockModel.isLegalMove(0, 0, 0);
      fail("Should throw IllegalStateException when game is over");
    } catch (IllegalStateException e) {
      assertEquals("Game is already over", e.getMessage());
    }
  }

  /**
   * Tests that isLegalMove throws an exception for invalid coordinates.
   */
  @Test
  public void testIsLegalMove_InvalidCoordinates() {
    mockModel.setGameStarted(true)
            .setBoardDimensions(3, 5);

    try {
      mockModel.isLegalMove(0, 5, 5);
      fail("Should throw IllegalArgumentException for invalid coordinates");
    } catch (IllegalArgumentException e) {
      assertTrue("Exception message should mention invalid coordinates",
              e.getMessage().contains("Invalid coordinates"));
    }
  }

  /**
   * Tests the startGame method.
   * Verifies that this stub method doesn't throw exceptions.
   */
  @Test
  public void testStartGame() {
    // This is a stub method, so it should do nothing
    try {
      mockModel.startGame(3, 5, "redpath",
              "bluepath", 5);
    } catch (Exception e) {
      fail("Exception should not be thrown from stub method: " + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // Tests for ModelStatusListener functionality
  // -----------------------------------------------------------------------

}