package cs3500.pawnsboard.model.mocks;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for the PawnsBoardMockTracker class.
 * Tests the tracking functionality, method implementations, and other behaviors
 * of the mock that are essential for tracking strategy interactions.
 */
public class PawnsBoardMockTrackerTest {

  private PawnsBoardMockTracker<PawnsBoardBaseCard, ?> mockTracker;
  private PawnsBoardBaseCard testCard;

  /**
   * Sets up a fresh mock tracker and test card for each test.
   */
  @Before
  public void setUp() {
    mockTracker = new PawnsBoardMockTracker<>();
    boolean[][] emptyInfluence = new boolean[5][5];
    testCard = new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence);
  }

  // BASIC FUNCTIONALITY TESTS

  /**
   * Tests that a new mock tracker is properly initialized with empty tracking data.
   */
  @Test
  public void testInitialState() {
    assertTrue("Method call log should be empty initially",
            mockTracker.getMethodCallLog().isEmpty());
    assertTrue("Cells checked list should be empty initially",
            mockTracker.getCellsCheckedInOrder().isEmpty());
    assertTrue("Card indices list should be empty initially",
            mockTracker.getCardIndicesCheckedInOrder().isEmpty());
    assertEquals("Method call counts should be zero initially", 0,
            mockTracker.getMethodCallCount("anyMethod"));
  }

  /**
   * Tests that method calls are correctly logged and counted.
   */
  @Test
  public void testMethodCallTracking() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    mockTracker.getCurrentPlayer();

    List<String> callLog = mockTracker.getMethodCallLog();
    assertFalse("Method call log should not be empty", callLog.isEmpty());
    assertTrue("Call log should contain getCurrentPlayer",
            callLog.contains("getCurrentPlayer()"));
    assertEquals("getCurrentPlayer should be called once", 1,
            mockTracker.getMethodCallCount("getCurrentPlayer"));

    mockTracker.getCurrentPlayer();
    assertEquals("getCurrentPlayer should be called twice", 2,
            mockTracker.getMethodCallCount("getCurrentPlayer"));
  }

  /**
   * Tests that cells checked are correctly tracked in order.
   */
  @Test
  public void testCellCheckTracking() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    mockTracker.getCellContent(0, 1);
    mockTracker.getCellContent(2, 3);

    List<int[]> cellsChecked = mockTracker.getCellsCheckedInOrder();
    assertEquals("Should track 2 cell checks", 2, cellsChecked.size());

    int[] firstCell = cellsChecked.get(0);
    assertEquals("First cell row should be 0", 0, firstCell[0]);
    assertEquals("First cell column should be 1", 1, firstCell[1]);

    int[] secondCell = cellsChecked.get(1);
    assertEquals("Second cell row should be 2", 2, secondCell[0]);
    assertEquals("Second cell column should be 3", 3, secondCell[1]);
  }

  /**
   * Tests that card indices are correctly tracked in order.
   */
  @Test
  public void testCardIndexTracking() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setGameOver(false);

    mockTracker.isLegalMove(0, 1, 1);
    mockTracker.isLegalMove(2, 0, 0);

    List<Integer> indices = mockTracker.getCardIndicesCheckedInOrder();
    assertEquals("Should track 2 card index checks", 2, indices.size());
    assertEquals("First card index should be 0", Integer.valueOf(0), indices.get(0));
    assertEquals("Second card index should be 2", Integer.valueOf(2), indices.get(1));
  }

  /**
   * Tests that tracking data can be cleared.
   */
  @Test
  public void testClearTracking() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    mockTracker.getCurrentPlayer();
    mockTracker.getCellContent(0, 1);

    mockTracker.clearTracking();

    assertTrue("Method call log should be empty after clearing",
            mockTracker.getMethodCallLog().isEmpty());
    assertTrue("Cells checked list should be empty after clearing",
            mockTracker.getCellsCheckedInOrder().isEmpty());
    assertTrue("Card indices list should be empty after clearing",
            mockTracker.getCardIndicesCheckedInOrder().isEmpty());
    assertEquals("Method call counts should be zero after clearing",
            0, mockTracker.getMethodCallCount("getCurrentPlayer"));
  }

  // CONFIGURATION TESTS

  /**
   * Tests setting legal move coordinates.
   */
  @Test
  public void testSetLegalMoveCoordinates() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setGameOver(false)
            .setReturnAllLegalMoves(false);

    List<int[]> legalMoves = new ArrayList<>();
    legalMoves.add(new int[]{0, 0, 0});
    legalMoves.add(new int[]{1, 2, 1});
    mockTracker.setLegalMoveCoordinates(legalMoves);

    assertTrue("Should recognize first legal move",
            mockTracker.isLegalMove(0, 0, 0));
    assertTrue("Should recognize second legal move",
            mockTracker.isLegalMove(1, 1, 2));
    assertFalse("Should reject illegal move",
            mockTracker.isLegalMove(2, 2, 0));
  }

  /**
   * Tests setting returnAllLegalMoves flag.
   */
  @Test
  public void testSetReturnAllLegalMoves() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setGameOver(false)
            .setReturnAllLegalMoves(true);

    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, hand);
    mockTracker.setCurrentPlayer(PlayerColors.RED);

    assertTrue("Should accept any move with valid card index",
            mockTracker.isLegalMove(0, 1, 1));
    assertFalse("Should reject move with invalid card index",
            mockTracker.isLegalMove(1, 1, 1));

    mockTracker.setReturnAllLegalMoves(false);
    assertFalse("Should reject all moves when returnAllLegalMoves is false",
            mockTracker.isLegalMove(0, 1, 1));
  }

  /**
   * Tests setup of a standard initial board.
   */
  @Test
  public void testSetupInitialBoard() {
    mockTracker.setupInitialBoard();

    assertTrue("Game should be started", mockTracker.getGameStarted());
    assertFalse("Game should not be over", mockTracker.isGameOver());
    assertEquals("Current player should be RED", PlayerColors.RED,
            mockTracker.getCurrentPlayer());

    int[] dimensions = mockTracker.getBoardDimensions();
    assertEquals("Board should have 3 rows", 3, dimensions[0]);
    assertEquals("Board should have 5 columns", 5, dimensions[1]);

    for (int r = 0; r < 3; r++) {
      assertEquals("Cell in first column should have pawns", CellContent.PAWNS,
              mockTracker.getCellContent(r, 0));
      assertEquals("Cell in first column should be owned by RED", PlayerColors.RED,
              mockTracker.getCellOwner(r, 0));
      assertEquals("Cell in first column should have 1 pawn", 1,
              mockTracker.getPawnCount(r, 0));
    }

    for (int r = 0; r < 3; r++) {
      assertEquals("Cell in last column should have pawns", CellContent.PAWNS,
              mockTracker.getCellContent(r, 4));
      assertEquals("Cell in last column should be owned by BLUE", PlayerColors.BLUE,
              mockTracker.getCellOwner(r, 4));
      assertEquals("Cell in last column should have 1 pawn", 1,
              mockTracker.getPawnCount(r, 4));
    }

    for (int r = 0; r < 3; r++) {
      assertArrayEquals("Row scores should be initialized to 0", new int[]{0, 0},
              mockTracker.getRowScores(r));
    }

    assertArrayEquals("Total score should be initialized to 0", new int[]{0, 0},
            mockTracker.getTotalScore());
  }

  // MOCK IMPLEMENTATION TESTS

  /**
   * Tests basic getter methods of the PawnsBoard interface.
   */
  @Test
  public void testBasicGetterMethods() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCurrentPlayer(PlayerColors.RED)
            .setTotalScore(5, 3)
            .setRemainingDeckSize(PlayerColors.RED, 10)
            .setRemainingDeckSize(PlayerColors.BLUE, 12);

    assertEquals("getCurrentPlayer should return RED",
            PlayerColors.RED, mockTracker.getCurrentPlayer());
    assertArrayEquals("getBoardDimensions should return correct dimensions",
            new int[]{3, 5}, mockTracker.getBoardDimensions());
    assertArrayEquals("getTotalScore should return correct scores", new int[]{5, 3},
            mockTracker.getTotalScore());
    assertEquals("getRemainingDeckSize should return correct RED deck size",
            10, mockTracker.getRemainingDeckSize(PlayerColors.RED));
    assertEquals("getRemainingDeckSize should return correct BLUE deck size",
            12, mockTracker.getRemainingDeckSize(PlayerColors.BLUE));
  }

  /**
   * Tests cell-related getter methods of the PawnsBoard interface.
   */
  @Test
  public void testCellGetterMethods() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCellContent(1, 2, CellContent.PAWNS)
            .setCellOwner(1, 2, PlayerColors.BLUE)
            .setPawnCount(1, 2, 2)
            .setCardAtCell(0, 0, testCard);

    assertEquals("getCellContent should return correct content",
            CellContent.PAWNS, mockTracker.getCellContent(1, 2));
    assertEquals("getCellOwner should return correct owner",
            PlayerColors.BLUE, mockTracker.getCellOwner(1, 2));
    assertEquals("getPawnCount should return correct count",
            2, mockTracker.getPawnCount(1, 2));
    assertEquals("getCardAtCell should return correct card",
            testCard, mockTracker.getCardAtCell(0, 0));
  }

  /**
   * Tests isLegalMove method implementation.
   */
  @Test
  public void testIsLegalMove() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setGameOver(false);

    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, hand);
    mockTracker.setCurrentPlayer(PlayerColors.RED);

    mockTracker.setReturnAllLegalMoves(true);
    assertTrue("Move with valid card index should be legal",
            mockTracker.isLegalMove(0, 1, 1));
    assertFalse("Move with invalid card index should be illegal",
            mockTracker.isLegalMove(1, 1, 1));

    mockTracker.setReturnAllLegalMoves(false);
    List<int[]> legalMoves = Arrays.asList(new int[]{1, 1, 0});
    mockTracker.setLegalMoveCoordinates(legalMoves);
    assertTrue("Specified legal move should be recognized",
            mockTracker.isLegalMove(0, 1, 1));
    assertFalse("Unspecified move should be illegal",
            mockTracker.isLegalMove(0, 0, 0));

    List<Integer> cardIndices = mockTracker.getCardIndicesCheckedInOrder();
    assertTrue("Card index should be tracked", cardIndices.contains(0));

    List<int[]> cellsChecked = mockTracker.getCellsCheckedInOrder();
    boolean found = false;
    for (int[] cell : cellsChecked) {
      if (cell[0] == 1 && cell[1] == 1) {
        found = true;
        break;
      }
    }
    assertTrue("Cell coordinates should be tracked", found);
  }

  @Test
  public void testCopy() {
    mockTracker.setGameStarted(true)
            .setBoardDimensions(3, 5)
            .setCurrentPlayer(PlayerColors.RED)
            .setCellContent(1, 2, CellContent.PAWNS)
            .setCellOwner(1, 2, PlayerColors.BLUE)
            .setPawnCount(1, 2, 2);

    mockTracker.getCurrentPlayer();
    mockTracker.getCellContent(1, 2);

    assertFalse("Original should have method call log",
            mockTracker.getMethodCallLog().isEmpty());

    PawnsBoard<PawnsBoardBaseCard, ?> copyBoard = mockTracker.copy();
    assertTrue("Copy should be a PawnsBoardMockTracker",

            copyBoard instanceof PawnsBoardMockTracker);

    PawnsBoardMockTracker<PawnsBoardBaseCard, ?> copy =
            (PawnsBoardMockTracker<PawnsBoardBaseCard, ?>) copyBoard;

    copy.clearTracking();

    boolean copyGameOver = copy.isGameOver();
    PlayerColors copyCurrentPlayer = copy.getCurrentPlayer();
    CellContent copyCellContent = copy.getCellContent(1, 2);
    PlayerColors copyCellOwner = copy.getCellOwner(1, 2);
    int copyPawnCount = copy.getPawnCount(1, 2);

    assertFalse("Copy should not be game over", copyGameOver);
    assertEquals("Copy should have same current player",
            PlayerColors.RED, copyCurrentPlayer);
    assertEquals("Copy should have same cell content",
            CellContent.PAWNS, copyCellContent);
    assertEquals("Copy should have same cell owner",
            PlayerColors.BLUE, copyCellOwner);
    assertEquals("Copy should have same pawn count",
            2, copyPawnCount);

    copy.clearTracking();

    assertTrue("Copy should have empty method call log",
            copy.getMethodCallLog().isEmpty());
    assertTrue("Copy should have empty cells checked list",
            copy.getCellsCheckedInOrder().isEmpty());
    assertTrue("Copy should have empty card indices list",
            copy.getCardIndicesCheckedInOrder().isEmpty());
  }

  // DEFENSIVE COPY TESTS

  /**
   * Tests that getMethodCallLog returns a defensive copy.
   */
  @Test
  public void testGetMethodCallLogDefensiveCopy() {
    mockTracker.setGameStarted(true);
    mockTracker.getCurrentPlayer();

    List<String> log = mockTracker.getMethodCallLog();
    int originalSize = log.size();
    log.clear();

    assertEquals("Original log should not be affected by modifications to returned log",
            originalSize, mockTracker.getMethodCallLog().size());
  }

  /**
   * Tests that getCellsCheckedInOrder returns a defensive copy.
   */
  @Test
  public void testGetCellsCheckedInOrderDefensiveCopy() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    mockTracker.getCellContent(0, 1);

    List<int[]> cells = mockTracker.getCellsCheckedInOrder();
    int originalSize = cells.size();
    cells.clear();

    assertEquals("Original list should not be affected by modifications to returned list",
            originalSize, mockTracker.getCellsCheckedInOrder().size());
  }

  /**
   * Tests that getCardIndicesCheckedInOrder returns a defensive copy.
   */
  @Test
  public void testGetCardIndicesCheckedInOrderDefensiveCopy() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5).setGameOver(false);
    mockTracker.isLegalMove(0, 0, 0);

    List<Integer> indices = mockTracker.getCardIndicesCheckedInOrder();
    int originalSize = indices.size();
    indices.clear();

    assertEquals("Original list should not be affected by modifications to returned list",
            originalSize, mockTracker.getCardIndicesCheckedInOrder().size());
  }

  /**
   * Tests that getPlayerHand returns a defensive copy.
   */
  @Test
  public void testGetPlayerHandDefensiveCopy() {
    mockTracker.setGameStarted(true);
    List<PawnsBoardBaseCard> hand = new ArrayList<>();
    hand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, hand);

    List<PawnsBoardBaseCard> returnedHand = mockTracker.getPlayerHand(PlayerColors.RED);
    int originalSize = returnedHand.size();
    returnedHand.clear();

    assertEquals("Original hand should not be affected by modifications to returned hand",
            originalSize, mockTracker.getPlayerHand(PlayerColors.RED).size());
  }

  // EXCEPTION TESTS

  /**
   * Tests that getCurrentPlayer throws exception when game is not started.
   */
  @Test
  public void testGetCurrentPlayer_GameNotStarted() {
    try {
      mockTracker.getCurrentPlayer();
      fail("Expected IllegalStateException to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Expected exception message for game not started",
              "Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests that getBoardDimensions throws exception when game is not started.
   */
  @Test
  public void testGetBoardDimensions_GameNotStarted() {
    try {
      mockTracker.getBoardDimensions();
      fail("Expected IllegalStateException to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Expected exception message for game not started",
              "Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests that getCellContent throws exception for invalid coordinates.
   */
  @Test
  public void testGetCellContent_InvalidCoordinates() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    try {
      mockTracker.getCellContent(5, 5);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Expected exception message for invalid coordinates",
              "Invalid coordinates: (5, 5)", e.getMessage());
    }
  }

  /**
   * Tests that getWinner throws exception when game is not over.
   */
  @Test
  public void testGetWinner_GameNotOver() {
    mockTracker.setGameStarted(true).setGameOver(false);
    try {
      mockTracker.getWinner();
      fail("Expected IllegalStateException to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Expected exception message for game not over",
              "Game is not over yet", e.getMessage());
    }
  }

  /**
   * Tests that getPlayerHand throws exception for null player.
   */
  @Test
  public void testGetPlayerHand_NullPlayer() {
    mockTracker.setGameStarted(true);
    try {
      mockTracker.getPlayerHand(null);
      fail("Expected NullPointerException to be thrown");
    } catch (NullPointerException e) {
      assertNull("Player is null", e.getMessage());
    }
  }

  /**
   * Tests that isLegalMove throws exception when game is over.
   */
  @Test
  public void testIsLegalMove_GameOver() {
    mockTracker.setGameStarted(true).setGameOver(true);
    try {
      mockTracker.isLegalMove(0, 0, 0);
      fail("Expected IllegalStateException to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Expected exception message for game over",
              "Game is already over", e.getMessage());
    }
  }

  /**
   * Tests that copy throws exception when game is not started.
   */
  @Test
  public void testCopy_GameNotStarted() {
    try {
      mockTracker.copy();
      fail("Expected IllegalStateException to be thrown");
    } catch (IllegalStateException e) {
      assertEquals("Expected exception message for game not started",
              "Game has not been started", e.getMessage());
    }
  }

  /**
   * Tests that getRowScores throws exception for invalid row.
   */
  @Test
  public void testGetRowScores_InvalidRow() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);
    try {
      mockTracker.getRowScores(5);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      assertEquals("Expected exception message for invalid row",
              "Row index out of bounds: 5", e.getMessage());
    }
  }

  /**
   * Tests tracking of complex method call patterns.
   */
  @Test
  public void testComplexMethodCallTracking() {
    mockTracker.setGameStarted(true).setBoardDimensions(3, 5);

    mockTracker.getCurrentPlayer();
    mockTracker.getCellContent(0, 0);
    mockTracker.getCellOwner(0, 0);
    mockTracker.getCellContent(1, 1);
    mockTracker.getCurrentPlayer();

    List<String> log = mockTracker.getMethodCallLog();
    assertEquals("Log should contain 5 method calls", 5, log.size());

    assertEquals("First call should be getCurrentPlayer",
            "getCurrentPlayer()", log.get(0));
    assertEquals("Second call should be getCellContent",
            "getCellContent(0,0)", log.get(1));
    assertEquals("Fifth call should be getCurrentPlayer",
            "getCurrentPlayer()", log.get(4));

    assertEquals("getCurrentPlayer should be called twice", 2,
            mockTracker.getMethodCallCount("getCurrentPlayer"));
    assertEquals("getCellContent should be called twice", 2,
            mockTracker.getMethodCallCount("getCellContent"));
    assertEquals("getCellOwner should be called once", 1,
            mockTracker.getMethodCallCount("getCellOwner"));
  }


}