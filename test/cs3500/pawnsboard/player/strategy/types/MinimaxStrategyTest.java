package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.mocks.PawnsBoardMockTracker;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the MinimaxStrategy class.
 * Tests the strategy's decision-making process, move selection, and edge cases.
 */
public class MinimaxStrategyTest {

  private MinimaxStrategy<PawnsBoardBaseCard> minimaxStrategy;
  private PawnsBoardMockTracker<PawnsBoardBaseCard, ?> mockTracker;
  private boolean[][] emptyInfluence;
  private PawnsBoardBaseCard testCard;

  @Before
  public void setUp() {
    // Create a simple Fill First Strategy as the opponent strategy
    PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy =
            new FillFirstStrategy<>();

    // Initialize Minimax Strategy with opponent strategy
    minimaxStrategy = new MinimaxStrategy<>(opponentStrategy);

    // Create mock tracker and setup initial board
    mockTracker = new PawnsBoardMockTracker<>();
    mockTracker.setupInitialBoard();

    // Create an empty influence grid for test cards
    emptyInfluence = new boolean[5][5];
    testCard = new PawnsBoardBaseCard("TestCard", 1, 2, emptyInfluence);
  }

  /**
   * Tests basic functionality when a legal move exists.
   * Verifies that the strategy returns a valid move.
   */
  @Test
  public void testBasicFunctionality_ReturnsValidMove() {
    // Prepare a hand with a test card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, redHand);

    // Set up a legal move
    mockTracker.setReturnAllLegalMoves(true);

    // Choose move
    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    // Verify move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
  }

  /**
   * Tests that the strategy returns a pass move when no legal moves exist.
   */
  @Test
  public void testNoLegalMoves() {
    // Prepare a hand with a test card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, redHand);

    // Set all moves as illegal
    mockTracker.setReturnAllLegalMoves(false);

    // Choose move
    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    // Verify move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests strategy behavior when the game is not started.
   */
  @Test
  public void testGameNotStarted() {
    mockTracker.setGameStarted(false);

    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests strategy behavior when the game is already over.
   */
  @Test
  public void testGameOver() {
    mockTracker.setGameStarted(true)
            .setGameOver(true);

    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests that the strategy considers moves that improve row scores.
   */
  @Test
  public void testScoreImprovingMove() {
    // Prepare a hand with a test card
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(testCard);
    mockTracker.setPlayerHand(PlayerColors.RED, redHand);

    // Setup a scenario with legal moves
    mockTracker.setReturnAllLegalMoves(true);

    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    // Verify move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
  }

  /**
   * Tests strategy with multiple legal moves.
   */
  @Test
  public void testMultipleLegalMoves() {
    // Prepare a hand with multiple test cards
    List<PawnsBoardBaseCard> redHand = new ArrayList<>();
    redHand.add(new PawnsBoardBaseCard("Card1", 1, 1, emptyInfluence));
    redHand.add(new PawnsBoardBaseCard("Card2", 1, 2, emptyInfluence));
    mockTracker.setPlayerHand(PlayerColors.RED, redHand);

    // Setup multiple legal moves
    mockTracker.setReturnAllLegalMoves(true);

    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    // Verify move
    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be PLACE_CARD", MoveType.PLACE_CARD,
            move.get().getMoveType());
  }

  /**
   * Tests strategy with an empty hand.
   */
  @Test
  public void testEmptyHand() {
    // Set an empty hand
    mockTracker.setPlayerHand(PlayerColors.RED, new ArrayList<>());

    Optional<PawnsBoardMove> move = minimaxStrategy.chooseMove(mockTracker);

    assertTrue("Strategy should return a move", move.isPresent());
    assertEquals("Move type should be EMPTY", MoveType.EMPTY, move.get().getMoveType());
  }

  /**
   * Tests constructor to ensure it throws an exception for null opponent strategy.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullStrategy() {
    new MinimaxStrategy<>(null);
  }
}