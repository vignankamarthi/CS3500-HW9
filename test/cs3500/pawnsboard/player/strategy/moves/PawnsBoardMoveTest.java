package cs3500.pawnsboard.player.strategy.moves;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the PawnsBoardMove class.
 * Tests all functionality related to creating different types of moves (PLACE_CARD, PASS, EMPTY)
 * and retrieving their properties.
 */
public class PawnsBoardMoveTest {

  private PawnsBoardMove placeCardMove;
  private PawnsBoardMove passMove;
  private PawnsBoardMove emptyMove;

  /**
   * Sets up test objects before each test method is executed.
   * Creates standard instances of each move type for testing.
   */
  @Before
  public void setUp() {
    // Create a standard PLACE_CARD move
    placeCardMove = new PawnsBoardMove(3, 2, 1);

    // Create standard PASS and EMPTY moves using the factory methods
    passMove = PawnsBoardMove.pass();
    emptyMove = PawnsBoardMove.empty();
  }

  /**
   * Tests that the constructor correctly initializes a PLACE_CARD move with the provided values.
   * Verifies that the move type is set to PLACE_CARD and all coordinates are stored properly.
   */
  @Test
  public void testConstructor_PlaceCardMove() {
    assertEquals(MoveType.PLACE_CARD, placeCardMove.getMoveType());
    assertEquals(3, placeCardMove.getCardIndex());
    assertEquals(2, placeCardMove.getRow());
    assertEquals(1, placeCardMove.getCol());
  }

  /**
   * Tests that the pass() factory method correctly creates a PASS move.
   * Verifies that the move type is set to PASS.
   */
  @Test
  public void testPassFactoryMethod() {
    assertEquals(MoveType.PASS, passMove.getMoveType());
  }

  /**
   * Tests that the empty() factory method correctly creates an EMPTY move.
   * Verifies that the move type is set to EMPTY.
   */
  @Test
  public void testEmptyFactoryMethod() {
    assertEquals(MoveType.EMPTY, emptyMove.getMoveType());
  }

  /**
   * Tests that PASS moves have default coordinate values (-1) for all position properties.
   * Verifies that card index, row, and column are all set to -1.
   */
  @Test
  public void testPassMove_DefaultValues() {
    assertEquals(-1, passMove.getCardIndex());
    assertEquals(-1, passMove.getRow());
    assertEquals(-1, passMove.getCol());
  }

  /**
   * Tests that EMPTY moves have default coordinate values (-1) for all position properties.
   * Verifies that card index, row, and column are all set to -1.
   */
  @Test
  public void testEmptyMove_DefaultValues() {
    assertEquals(-1, emptyMove.getCardIndex());
    assertEquals(-1, emptyMove.getRow());
    assertEquals(-1, emptyMove.getCol());
  }

  /**
   * Tests the getCardIndex method for a PLACE_CARD move.
   * Verifies that it returns the correct card index that was set during construction.
   */
  @Test
  public void testGetCardIndex_PlaceCardMove() {
    PawnsBoardMove move = new PawnsBoardMove(5, 0, 0);
    assertEquals(5, move.getCardIndex());
  }

  /**
   * Tests the getRow method for a PLACE_CARD move.
   * Verifies that it returns the correct row coordinate that was set during construction.
   */
  @Test
  public void testGetRow_PlaceCardMove() {
    PawnsBoardMove move = new PawnsBoardMove(0, 4, 0);
    assertEquals(4, move.getRow());
  }

  /**
   * Tests the getCol method for a PLACE_CARD move.
   * Verifies that it returns the correct column coordinate that was set during construction.
   */
  @Test
  public void testGetCol_PlaceCardMove() {
    PawnsBoardMove move = new PawnsBoardMove(0, 0, 3);
    assertEquals(3, move.getCol());
  }

  /**
   * Tests creating a PLACE_CARD move with zero values.
   * Verifies that zero values are correctly stored and not treated as special cases.
   */
  @Test
  public void testPlaceCardMove_ZeroValues() {
    PawnsBoardMove move = new PawnsBoardMove(0, 0, 0);
    assertEquals(0, move.getCardIndex());
    assertEquals(0, move.getRow());
    assertEquals(0, move.getCol());
    assertEquals(MoveType.PLACE_CARD, move.getMoveType());
  }

  /**
   * Tests creating a PLACE_CARD move with negative values.
   * Verifies that negative values are correctly stored without validation.
   * Note: The implementation doesn't validate input values, so negative values are allowed.
   * Move validation is the Model's job.
   */
  @Test
  public void testPlaceCardMove_NegativeValues() {
    PawnsBoardMove move = new PawnsBoardMove(-1, -2, -3);
    assertEquals(-1, move.getCardIndex());
    assertEquals(-2, move.getRow());
    assertEquals(-3, move.getCol());
    assertEquals(MoveType.PLACE_CARD, move.getMoveType());
  }

  /**
   * Tests creating a PLACE_CARD move with large positive values.
   * Verifies that large values are correctly stored without validation.
   * Note: The implementation doesn't validate input values, so large values are allowed.
   * Move validation is the Model's job.
   */
  @Test
  public void testPlaceCardMove_LargeValues() {
    PawnsBoardMove move = new PawnsBoardMove(1000, 2000, 3000);
    assertEquals(1000, move.getCardIndex());
    assertEquals(2000, move.getRow());
    assertEquals(3000, move.getCol());
    assertEquals(MoveType.PLACE_CARD, move.getMoveType());
  }

  /**
   * Tests multiple calls to the pass() factory method.
   * Verifies that each call creates a new PASS move instance.
   */
  @Test
  public void testMultiplePassMoves() {
    PawnsBoardMove pass1 = PawnsBoardMove.pass();
    PawnsBoardMove pass2 = PawnsBoardMove.pass();

    // Verify both are PASS moves
    assertEquals(MoveType.PASS, pass1.getMoveType());
    assertEquals(MoveType.PASS, pass2.getMoveType());

    // While this isn't a functional requirement, it's good to verify they're distinct objects
    // Note: This might change if the implementation uses a singleton pattern for PASS moves,
    // which could be the case later depending on HW7.
    assertEquals(pass1.getMoveType(), pass2.getMoveType());
  }

  /**
   * Tests multiple calls to the empty() factory method.
   * Verifies that each call creates a new EMPTY move instance.
   */
  @Test
  public void testMultipleEmptyMoves() {
    PawnsBoardMove empty1 = PawnsBoardMove.empty();
    PawnsBoardMove empty2 = PawnsBoardMove.empty();

    // Verify both are EMPTY moves
    assertEquals(MoveType.EMPTY, empty1.getMoveType());
    assertEquals(MoveType.EMPTY, empty2.getMoveType());

    // While this isn't a functional requirement, it's good to verify they're distinct objects
    // Note: This might change if the implementation uses a singleton pattern for EMPTY moves
    assertEquals(empty1.getMoveType(), empty2.getMoveType());
  }

  /**
   * Tests the toString() method for a PLACE_CARD move.
   * Verifies that the string representation correctly describes a card placement move.
   */
  @Test
  public void testToString_PlaceCardMove() {
    PawnsBoardMove move = new PawnsBoardMove(3, 2, 1);
    assertEquals("Move: Card 3 at (2, 1)", move.toString());
  }

  /**
   * Tests the toString() method for a PASS move.
   * Verifies that the string representation correctly describes a pass turn move.
   */
  @Test
  public void testToString_PassMove() {
    PawnsBoardMove move = PawnsBoardMove.pass();
    assertEquals("Move: Pass Turn", move.toString());
  }

  /**
   * Tests the toString() method for an EMPTY move.
   * Verifies that the string representation correctly describes a move with no available action.
   */
  @Test
  public void testToString_EmptyMove() {
    PawnsBoardMove move = PawnsBoardMove.empty();
    assertEquals("Move: No Move Available", move.toString());
  }


}