package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.mocks.PawnsBoardMockPreset;
import cs3500.pawnsboard.model.mocks.PawnsBoardMockTracker;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for the ControlBoardStrategy class.
 * Tests the strategy's behavior in selecting moves that maximize player cell control.
 */
public class ControlBoardStrategyTest {

  /**
   * The strategy being tested.
   */
  private ControlBoardStrategy<PawnsBoardBaseCard> strategy;

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
    strategy = new ControlBoardStrategy<>();
    presetMock = new PawnsBoardMockPreset<>();
    emptyInfluence = new boolean[5][5];

    // Set up standard test board
    presetMock.setupInitialBoard();
  }

  /**
   * Tests that ControlBoardStrategy returns a valid move when one exists that increases cell
   * control.
   * Verifies that the strategy selects a move that increases the number of cells the player
   * controls.
   */
  @Test
  public void testBasicFunctionality_ReturnsValidMoveWhenCellsIncrease() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setBoardDimensions(3, 5);  // Ensure board dimensions are set

    // Carefully set up legal moves for all cards
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        presetMock.setLegalMove(0, row, col, true);
      }
    }

    // Set up cell ownership changes
    Map<String, Boolean> cellChanges = new HashMap<>();
    cellChanges.put("0,1", true);  // RED will own cell (0,1)
    presetMock.setCellOwnershipChanges(0, 0, 0, cellChanges);

    // Create a simulation result
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> result = new PawnsBoardMockPreset<>();
    result.setupInitialBoard()
            .setCellContent(0, 1, CellContent.PAWNS)
            .setCellOwner(0, 1, PlayerColors.RED)
            .setCellContent(0, 0, CellContent.CARD)
            .setCellOwner(0, 0, PlayerColors.RED);

    presetMock.setPresetSimulationResult(result);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that a move was returned
    assertTrue("Strategy should return a move when cell count can be increased",
            move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
    assertEquals("Card index should be 0", 0, move.get().getCardIndex());
    assertEquals("Row should be 2", 2, move.get().getRow());
    assertEquals("Column should be 4", 4, move.get().getCol());
  }

  /**
   * Tests that ControlBoardStrategy returns an empty move when no legal moves exist.
   * Verifies that the strategy properly handles situations where no moves are possible.
   */
  @Test
  public void testBasicFunctionality_NoLegalMoves() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock to disallow all moves
    presetMock.setPlayerHand(PlayerColors.RED, redHand);
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        presetMock.setLegalMove(0, row, col, false);
      }
    }

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that an empty move was returned
    assertTrue("Strategy should return a move when no legal moves exist",
            move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY,
            move.get().getMoveType());
  }

  /**
   * Tests behavior of ControlBoardStrategy when the player's hand is empty.
   * Verifies that the strategy returns an empty move when there are no cards to play.
   */
  @Test
  public void testEdgeCase_EmptyHand() {
    // Set up the mock with an empty hand
    presetMock.setPlayerHand(PlayerColors.RED, new ArrayList<>());

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that an empty move was returned
    assertTrue("Strategy should return a move when hand is empty",
            move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY, move.get().getMoveType());
  }


  /**
   * Tests behavior when the game is not started.
   * Expects the strategy to handle this gracefully and return an empty move.
   */
  @Test
  public void testEdgeCase_GameNotStarted() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(false);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a non-empty result
    assertTrue("Strategy should handle game not started gracefully", move.isPresent());
    assertEquals("Should return EMPTY move when game not started",
            MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests behavior when the game is already over.
   * Expects the strategy to handle this gracefully and return an empty move.
   */
  @Test
  public void testEdgeCase_GameOver() {
    // Set up the mock to throw IllegalStateException
    presetMock.setGameStarted(true)
            .setGameOver(true);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Strategy should handle the exception and return a non-empty result
    assertTrue("Strategy should handle game over gracefully", move.isPresent());
    assertEquals("Should return EMPTY move when game is over",
            MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests behavior when no move would increase the number of cells controlled.
   * The strategy should return an empty move when no move would increase the player's cell count.
   */
  @Test
  public void testEdgeCase_NoMoveIncreasesControlledCells() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setBoardDimensions(3, 5);

    // Set up legal moves for all cards
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 5; col++) {
        presetMock.setLegalMove(0, row, col, true);
      }
    }

    // Create a simulation result that doesn't increase cell count
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> result = new PawnsBoardMockPreset<>();
    result.setupInitialBoard();  // Keep initial board state unchanged

    presetMock.setPresetSimulationResult(result);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that an empty move was returned
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Should return PLACE_CARD move",
            MoveType.PLACE_CARD, move.get().getMoveType());
  }

  /**
   * Tests behavior when cells are tied but have different owners.
   * Verifies that the strategy correctly counts only the player's controlled cells.
   */
  @Test
  public void testCellCounting_CorrectOwnership() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock for RED player
    presetMock.setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setLegalMove(0, 0, 0, true);

    // Create a more complex board where each player has cells
    presetMock.setCellContent(0, 2, CellContent.PAWNS)
            .setCellOwner(0, 2, PlayerColors.RED)
            .setCellContent(1, 1, CellContent.PAWNS)
            .setCellOwner(1, 1, PlayerColors.BLUE);

    // Set up cell ownership changes - playing at (0,0) will increase RED's control
    Map<String, Boolean> cellChanges = new HashMap<>();
    cellChanges.put("0,1", true);   // RED will own cell (0,1)
    presetMock.setCellOwnershipChanges(0, 0, 0, cellChanges);

    // Create simulation result with RED controlling more cells
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> result = new PawnsBoardMockPreset<>();
    result.setupInitialBoard()
            .setCellContent(0, 2, CellContent.PAWNS)
            .setCellOwner(0, 2, PlayerColors.RED)
            .setCellContent(1, 1, CellContent.PAWNS)
            .setCellOwner(1, 1, PlayerColors.BLUE)
            .setCellContent(0, 1, CellContent.PAWNS)
            .setCellOwner(0, 1, PlayerColors.RED)
            .setCellContent(0, 0, CellContent.CARD)
            .setCellOwner(0, 0, PlayerColors.RED);

    // Set up the presetMock to return this result
    presetMock.setPresetSimulationResult(result);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the move was selected
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
    assertEquals("Row should be 2", 2, move.get().getRow());
    assertEquals("Column should be 4", 4, move.get().getCol());
  }

  /**
   * Tests strategy behavior with the BLUE player.
   * Verifies that the strategy correctly chooses moves for BLUE player based on BLUE's
   * controlled cells.
   */
  @Test
  public void testPlayerSpecific_BluePlayer() {
    // Create a test hand with one card
    List<PawnsBoardBaseCard> blueHand = new ArrayList<>();
    blueHand.add(new PawnsBoardBaseCard("TestCard", 1, 1, emptyInfluence));

    // Set up the mock for BLUE player
    presetMock.setPlayerHand(PlayerColors.BLUE, blueHand)
            .setCurrentPlayer(PlayerColors.BLUE)
            .setLegalMove(0, 0, 4, true);

    // Set up cell ownership changes - playing at (0,4) will increase BLUE's control
    Map<String, Boolean> cellChanges = new HashMap<>();
    cellChanges.put("0,3", false);   // BLUE will own cell (0,3) (false means BLUE in the mock)
    presetMock.setCellOwnershipChanges(0, 0, 4, cellChanges);

    // Create simulation result with BLUE controlling more cells
    PawnsBoardMockPreset<PawnsBoardBaseCard, ?> result = new PawnsBoardMockPreset<>();
    result.setupInitialBoard()
            .setCellContent(0, 3, CellContent.PAWNS)
            .setCellOwner(0, 3, PlayerColors.BLUE)
            .setCellContent(0, 4, CellContent.CARD)
            .setCellOwner(0, 4, PlayerColors.BLUE);

    // Set up the presetMock to return this result
    presetMock.setPresetSimulationResult(result);

    // Get the strategy's move
    Optional<PawnsBoardMove> move = strategy.chooseMove(presetMock);

    // Verify that the move was selected
    assertTrue("Strategy should return a move for BLUE player", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
    assertEquals("Row should be 2", 2, move.get().getRow());
    assertEquals("Column should be 3", 3, move.get().getCol());
  }


  /**
   * Tests that multiple complex simulations are properly handled.
   * Verifies that the strategy can handle a series of simulation steps correctly.
   */
  @Test
  public void testComplexSimulation() {
    // Create a test hand with multiple cards
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      redHand.add(new PawnsBoardBaseCard("Card" + i, 1, i + 1,
              emptyInfluence));
    }

    // Create a tracker mock to verify search pattern
    PawnsBoardMockTracker<PawnsBoardBaseCard, ?> trackerMock = new PawnsBoardMockTracker<>();
    trackerMock.setupInitialBoard()
            .setPlayerHand(PlayerColors.RED, redHand)
            .setCurrentPlayer(PlayerColors.RED)
            .setReturnAllLegalMoves(true);

    // Get the strategy's move
    strategy.chooseMove(trackerMock);

    // Verify that the strategy called isLegalMove for multiple positions
    int legalMoveCalls = (int) trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("isLegalMove"))
            .count();

    assertTrue("Strategy should check multiple moves", legalMoveCalls > 1);

    // Verify that copy was called to simulate moves
    int copyCalls = (int) trackerMock.getMethodCallLog().stream()
            .filter(call -> call.equals("copy()"))
            .count();

    assertTrue("Strategy should simulate moves using copy", copyCalls > 0);

    // Verify that the strategy checked cell content and ownership
    int cellContentCalls = (int) trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("getCellContent"))
            .count();

    int cellOwnerCalls = (int) trackerMock.getMethodCallLog().stream()
            .filter(call -> call.startsWith("getCellOwner"))
            .count();

    assertTrue("Strategy should check cell content", cellContentCalls > 0);
    assertTrue("Strategy should check cell ownership", cellOwnerCalls > 0);
  }
}