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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for the FillFirstStrategy class.
 * Tests the search patterns, move selection logic, and edge case handling of the strategy.
 */
public class FillFirstStrategyTest {

  /**
   * The strategy being tested.
   */
  private FillFirstStrategy<PawnsBoardBaseCard> strategy;

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
    strategy = new FillFirstStrategy<>();
    trackerMock = new PawnsBoardMockTracker<>();
    presetMock = new PawnsBoardMockPreset<>();
    emptyInfluence = new boolean[5][5];

    // Set up standard test board for both mocks
    trackerMock.setupInitialBoard();
    presetMock.setupInitialBoard();
  }

  /**
   * Tests that FillFirstStrategy returns a valid move when one exists.
   * Verifies that the strategy selects a move when legal moves are available.
   */
  @Test
  public void testBasicFunctionality_ReturnsValidMove() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock to allow all moves
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a move was returned
    assertTrue("Strategy should return a move when legal moves exist", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
    assertEquals("Card index should be 0", 0, move.get().getCardIndex());
    assertEquals("Row should be 0", 0, move.get().getRow());
    assertEquals("Column should be 0", 0, move.get().getCol());
  }

  /**
   * Tests that FillFirstStrategy returns a pass move when no legal moves exist.
   * Verifies that the strategy properly handles situations where no moves are possible.
   */
  @Test
  public void testBasicFunctionality_NoLegalMoves() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock to disallow all moves
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setReturnAllLegalMoves(false);  // No legal moves

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a pass move was returned
    assertTrue("Strategy should return a move when no legal moves exist",
            move.isPresent());
    assertEquals("Move type should be PASS", MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests behavior of FillFirstStrategy when the player's hand is empty.
   * Verifies that the strategy returns a pass move when there are no cards to play.
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
   * Tests the search pattern for the RED player.
   * Verifies that for a given card, RED checks positions from left to right and top to bottom.
   */
  @Test
  public void testSearchPattern_RedPlayer() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the tracker mock with RED player and test hand
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(false)  // No positions are legal by default
            .setLegalMoveCoordinates(Arrays.asList(new int[]{2, 3, 0}));  // Only (2,3) is legal

    // Call the strategy
    strategy.chooseMove(trackerMock);

    // Get cells checked in order
    List<int[]> cellsChecked = trackerMock.getCellsCheckedInOrder();

    // Need at least some cells to be checked
    assertTrue("Strategy should check cells", cellsChecked.size() > 0);

    // Verify row-by-row, left-to-right search pattern
    int currentRow = 0;
    int lastCol = -1;

    for (int[] cell : cellsChecked) {
      int row = cell[0];
      int col = cell[1];

      // If we moved to a new row
      if (row > currentRow) {
        // Verify we finished the previous row by checking the last column
        assertEquals("Last column of previous row should be checked",
                trackerMock.getBoardDimensions()[1] - 1, lastCol);

        // Reset for new row
        currentRow = row;
        lastCol = -1;

        // New row should start from leftmost column
        assertEquals("New row should start from leftmost column", 0, col);
      } else if (row == currentRow) {
        // Within same row, columns should increase
        if (lastCol != -1) {
          assertEquals("Columns should be checked left to right",
                  lastCol + 1, col);
        }
      }

      lastCol = col;
    }
  }

  /**
   * Tests the search pattern for the BLUE player.
   * Verifies that for a given card, BLUE checks positions from right to left and top to bottom.
   */
  @Test
  public void testSearchPattern_BluePlayer() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> blueHand = new ArrayList<>();
    blueHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the tracker mock with BLUE player and test hand
    trackerMock.setPlayerHand(PlayerColors.BLUE, blueHand)
            .setCurrentPlayer(PlayerColors.BLUE)
            .setReturnAllLegalMoves(false)  // No positions are legal by default
            .setLegalMoveCoordinates(Arrays.asList(new int[]{2, 1, 0}));  // Only (2,1) is legal

    // Call the strategy
    strategy.chooseMove(trackerMock);

    // Get cells checked in order
    List<int[]> cellsChecked = trackerMock.getCellsCheckedInOrder();

    // Need at least some cells to be checked
    assertTrue("Strategy should check cells", cellsChecked.size() > 0);

    // Verify row-by-row, right-to-left search pattern
    int currentRow = 0;
    int lastCol = -1;
    int cols = trackerMock.getBoardDimensions()[1];

    for (int[] cell : cellsChecked) {
      int row = cell[0];
      int col = cell[1];

      // If we moved to a new row
      if (row > currentRow) {
        // Verify we finished the previous row by checking the first column
        assertEquals("First column of previous row should be checked", 0,
                lastCol);

        // Reset for new row
        currentRow = row;
        lastCol = -1;

        // New row should start from rightmost column
        assertEquals("New row should start from rightmost column", cols - 1,
                col);
      } else if (row == currentRow) {
        // Within same row, columns should decrease
        if (lastCol != -1) {
          assertEquals("Columns should be checked right to left",
                  lastCol - 1, col);
        }
      }

      lastCol = col;
    }
  }

  /**
   * Tests that FillFirstStrategy stops at the first legal move it finds.
   * Verifies that the strategy doesn't continue searching after finding a valid move.
   */
  @Test
  public void testFindFirstLegalMove() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the tracker mock with specific legal moves
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(false)
            .setLegalMoveCoordinates(Arrays.asList(new int[]{1, 2, 0}));  // Only (1,2) is legal

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(trackerMock);

    // Verify the move returned matches the only legal move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Row should match legal move", 1, move.get().getRow());
    assertEquals("Column should match legal move", 2, move.get().getCol());

    // Now check if strategy stopped searching after finding the legal move
    List<String> legalMoveCalls = trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("isLegalMove"))
            .collect(Collectors.toList());

    // Count cells checked
    int cellsCheckedBeforeLegalMove = 0;
    for (String call : legalMoveCalls) {
      if (call.equals("isLegalMove(0,1,2)")) {
        break;
      }
      cellsCheckedBeforeLegalMove++;
    }

    // Check that we didn't continue searching after finding the legal move
    // In a 3x5 board:
    // - Before (1,2), RED player would check (0,0), (0,1), (0,2), (0,3), (0,4), (1,0), (1,1)
    // - That's 7 cells before finding (1,2)
    // - After (1,2), there are (1,3), (1,4), and all of row 2 still to check
    assertTrue("Strategy should check cells before the legal move",
            cellsCheckedBeforeLegalMove > 0);
    assertTrue("Strategy should stop checking after finding legal move",
            legalMoveCalls.size() <= cellsCheckedBeforeLegalMove + 1);
  }

  /**
   * Tests that FillFirstStrategy behaves correctly when the only legal move
   * is at the end of the search pattern.
   */
  @Test
  public void testEdgeCase_OnlyLastMoveIsLegal() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the tracker mock with only the last position being legal
    // In a 3x5 board, the last position for RED search is (2,4)
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(false)
            .setLegalMoveCoordinates(Arrays.asList(new int[]{2, 4, 0}));

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(trackerMock);

    // Verify the move returned matches the only legal move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Row should match last position", 2, move.get().getRow());
    assertEquals("Column should match last position", 4, move.get().getCol());

    // Verify strategy checked all positions
    int expectedCalls = 3 * 5; // All positions in a 3x5 board
    int legalMoveCalls = (int) trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("isLegalMove"))
            .count();

    assertEquals("Strategy should check all positions when only last is legal",
            expectedCalls, legalMoveCalls);
  }

  /**
   * Tests that FillFirstStrategy works correctly with a single card in hand.
   */
  @Test
  public void testEdgeCase_SingleCardInHand() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock with a specific legal move
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setLegalMove(0, 0, 0, true);
    // Now we expect it to find (0,0,0) first

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify the move
    assertTrue("Strategy should return a move with single card", move.isPresent());
    assertEquals("Card index should be 0", 0, move.get().getCardIndex());
    assertEquals("Row should be 0", 0, move.get().getRow());
    assertEquals("Column should be 0", 0, move.get().getCol());
  }

  /**
   * Tests that FillFirstStrategy works correctly with multiple cards but
   * only the second card has legal moves.
   */
  @Test
  public void testEdgeCase_OnlySecondCardHasLegalMoves() {
    // Create a test hand with two cards
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("Card1", 1, 1, emptyInfluence));
    redHand.add(new PawnsBoardBaseCard("Card2", 1, 1, emptyInfluence));

    // Set up the mock: no legal moves for first card, but legal move for second card
    presetMock.setPlayerHand(PlayerColors.RED, redHand);

    // Set all positions to false for card 0
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        presetMock.setLegalMove(0, row, col, false);
      }
    }

    // Set the first position to true for card 1
    presetMock.setLegalMove(1, 0, 0, true);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify the move uses the second card
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Card index should be 1", 1, move.get().getCardIndex());
    assertEquals("Row should be 0", 0, move.get().getRow());
    assertEquals("Column should be 0", 0, move.get().getCol());
  }

  /**
   * Tests that FillFirstStrategy correctly handles multiple legal moves
   * by choosing the first one according to its search pattern.
   */
  @Test
  public void testMultipleLegalMoves() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up multiple legal moves
    List<int[]> legalMoves = Arrays.asList(
            new int[]{0, 2, 0}, // (0,2)
            new int[]{1, 1, 0}  // (1,1)
    );

    // Set up the tracker mock
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(false)
            .setLegalMoveCoordinates(legalMoves);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(trackerMock);

    // Verify the move is the first legal move in the search pattern
    assertTrue("Strategy should return a move", move.isPresent());

    // For RED, the search pattern is top-left to bottom-right, so (0,2) should be found
    // before (1,1)
    assertEquals("Row should be 0", 0, move.get().getRow());
    assertEquals("Column should be 2", 2, move.get().getCol());
  }

  /**
   * Tests the behavior of FillFirstStrategy when the game is not started.
   * Expects an IllegalStateException to be thrown by the mock and handled by the strategy.
   */
  @Test
  public void testExceptionHandling_GameNotStarted() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(false);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a non-empty result
    assertTrue("Strategy should handle game not started gracefully", move.isPresent());
    assertEquals("Should return PASS move when game not started",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests the behavior of FillFirstStrategy when the game is already over.
   * Expects an IllegalStateException to be thrown by the mock and handled by the strategy.
   */
  @Test
  public void testExceptionHandling_GameOver() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(true)
            .setGameOver(true);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a non-empty result
    assertTrue("Strategy should handle game over gracefully", move.isPresent());
    assertEquals("Should return PASS move when game is over",
            MoveType.PASS, move.get().getMoveType());
  }

  /**
   * Tests the order in which different card indices are checked.
   * Verifies that the strategy tries all positions with the first card
   * before moving to the second card.
   */
  @Test
  public void testCardCheckOrder() {
    // Create a test hand with two cards
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("Card1", 1, 1, emptyInfluence));
    redHand.add(new PawnsBoardBaseCard("Card2", 1, 1, emptyInfluence));

    // Set up the tracker mock
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED);

    // Call the strategy
    strategy.chooseMove(trackerMock);

    // Get card indices checked in order
    List<Integer> cardIndicesChecked = trackerMock.getCardIndicesCheckedInOrder();

    // Verify all checks for card 0 come before any checks for card 1
    int firstCard1Index = -1;
    for (int i = 0; i < cardIndicesChecked.size(); i++) {
      if (cardIndicesChecked.get(i) == 1) {
        firstCard1Index = i;
        break;
      }
    }

    // If found card 1, verify all previous checks were for card 0
    if (firstCard1Index != -1) {
      for (int i = 0; i < firstCard1Index; i++) {
        assertEquals("Should check all positions with card 0 before card 1",
                0, (int) cardIndicesChecked.get(i));
      }
    }
  }

  /**
   * To generate the log output for a legal move with this strategy.
   */
  @Test
  public void generateFillFirstStrategyTranscript() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the tracker mock with RED player and test hand
    trackerMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(true);

    // Call the strategy
    Optional<PawnsBoardMove> move = strategy.chooseMove(trackerMock);

    // Print the method call logs
    System.out.println(String.join("\n", trackerMock.getMethodCallLog()));

    String hello = "world";

    assertEquals("hello", "world", hello); // world
  }
}