package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for the RegularInfluence class which implements Influence interface.
 * Tests the application of regular influence to different cell states and utility methods.
 */
public class RegularInfluenceTest {

  private RegularInfluence regularInfluence;
  private PawnsBoardAugmentedCell<Card> emptyCell;
  private PawnsBoardAugmentedCell<Card> redPawnCell;
  private PawnsBoardAugmentedCell<Card> bluePawnCell;
  private PawnsBoardAugmentedCell<Card> maxPawnCell;
  private PawnsBoardAugmentedCell<Card> cardCell;
  private Card mockCard;

  /**
   * Sets up test fixtures before each test.
   */
  @Before
  public void setUp() {
    regularInfluence = new RegularInfluence();

    // Create cells with different states
    emptyCell = new PawnsBoardAugmentedCell<>();

    redPawnCell = new PawnsBoardAugmentedCell<>();
    try {
      redPawnCell.addPawn(PlayerColors.RED);
    } catch (Exception e) {
      fail("Exception should not be thrown during setup");
    }

    bluePawnCell = new PawnsBoardAugmentedCell<>();
    try {
      bluePawnCell.addPawn(PlayerColors.BLUE);
    } catch (Exception e) {
      fail("Exception should not be thrown during setup");
    }

    maxPawnCell = new PawnsBoardAugmentedCell<>();
    try {
      maxPawnCell.addPawn(PlayerColors.RED);
      maxPawnCell.addPawn(PlayerColors.RED);
      maxPawnCell.addPawn(PlayerColors.RED);
    } catch (Exception e) {
      fail("Exception should not be thrown during setup");
    }

    // Create a mock card for testing
    mockCard = new Card() {
      @Override
      public String getName() {
        return "MockCard";
      }

      @Override
      public int getCost() {
        return 1;
      }

      @Override
      public int getValue() {
        return 2;
      }

      @Override
      public boolean[][] getInfluenceGrid() {
        return new boolean[5][5];
      }

      @Override
      public char[][] getInfluenceGridAsChars() {
        return new char[5][5];
      }
    };

    cardCell = new PawnsBoardAugmentedCell<>();
    cardCell.setCard(mockCard, PlayerColors.RED);
  }

  /**
   * Tests that applying influence to an empty cell adds a pawn of the current player.
   */
  @Test
  public void testApplyInfluenceToEmptyCell() throws Exception {
    // Before influence
    assertEquals(CellContent.EMPTY, emptyCell.getContent());
    assertNull(emptyCell.getOwner());
    assertEquals(0, emptyCell.getPawnCount());

    // Apply influence
    boolean result = regularInfluence.applyInfluence(emptyCell, PlayerColors.RED);

    // After influence
    assertTrue(result);
    assertEquals(CellContent.PAWNS, emptyCell.getContent());
    assertEquals(PlayerColors.RED, emptyCell.getOwner());
    assertEquals(1, emptyCell.getPawnCount());
  }

  /**
   * Tests that applying influence to a cell with pawns owned by the current player
   * increases the pawn count.
   */
  @Test
  public void testApplyInfluenceToOwnedPawnCell() throws Exception {
    // Before influence
    assertEquals(CellContent.PAWNS, redPawnCell.getContent());
    assertEquals(PlayerColors.RED, redPawnCell.getOwner());
    assertEquals(1, redPawnCell.getPawnCount());

    // Apply influence
    boolean result = regularInfluence.applyInfluence(redPawnCell, PlayerColors.RED);

    // After influence
    assertTrue(result);
    assertEquals(CellContent.PAWNS, redPawnCell.getContent());
    assertEquals(PlayerColors.RED, redPawnCell.getOwner());
    assertEquals(2, redPawnCell.getPawnCount());
  }

  /**
   * Tests that applying influence to a cell with pawns owned by the opponent
   * changes ownership to the current player.
   */
  @Test
  public void testApplyInfluenceToOpponentPawnCell() throws Exception {
    // Before influence
    assertEquals(CellContent.PAWNS, bluePawnCell.getContent());
    assertEquals(PlayerColors.BLUE, bluePawnCell.getOwner());
    assertEquals(1, bluePawnCell.getPawnCount());

    // Apply influence
    boolean result = regularInfluence.applyInfluence(bluePawnCell, PlayerColors.RED);

    // After influence
    assertTrue(result);
    assertEquals(CellContent.PAWNS, bluePawnCell.getContent());
    assertEquals(PlayerColors.RED, bluePawnCell.getOwner());
    assertEquals(1, bluePawnCell.getPawnCount());
  }

  /**
   * Tests that applying influence to a cell with the maximum number of pawns
   * owned by the current player has no effect and returns false.
   */
  @Test
  public void testApplyInfluenceToMaxPawnCell() throws Exception {
    // Before influence
    assertEquals(CellContent.PAWNS, maxPawnCell.getContent());
    assertEquals(PlayerColors.RED, maxPawnCell.getOwner());
    assertEquals(3, maxPawnCell.getPawnCount());

    // Apply influence
    boolean result = regularInfluence.applyInfluence(maxPawnCell, PlayerColors.RED);

    // After influence - should be unchanged
    assertFalse(result);
    assertEquals(CellContent.PAWNS, maxPawnCell.getContent());
    assertEquals(PlayerColors.RED, maxPawnCell.getOwner());
    assertEquals(3, maxPawnCell.getPawnCount());
  }

  /**
   * Tests that applying influence to a cell with a card has no effect and returns false.
   */
  @Test
  public void testApplyInfluenceToCellWithCard() throws Exception {
    // Before influence
    assertEquals(CellContent.CARD, cardCell.getContent());
    assertEquals(PlayerColors.RED, cardCell.getOwner());
    assertEquals(mockCard, cardCell.getCard());

    // Apply influence
    boolean result = regularInfluence.applyInfluence(cardCell, PlayerColors.BLUE);

    // After influence - should be unchanged
    assertFalse(result);
    assertEquals(CellContent.CARD, cardCell.getContent());
    assertEquals(PlayerColors.RED, cardCell.getOwner());
    assertEquals(mockCard, cardCell.getCard());
  }

  /**
   * Tests that applying influence with a null player throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testApplyInfluenceWithNullPlayer() throws Exception {
    regularInfluence.applyInfluence(emptyCell, null);
  }

  /**
   * Tests that the isRegular method returns true.
   */
  @Test
  public void testIsRegular() {
    assertTrue(regularInfluence.isRegular());
  }

  /**
   * Tests that the isUpgrading method returns false.
   */
  @Test
  public void testIsUpgrading() {
    assertFalse(regularInfluence.isUpgrading());
  }

  /**
   * Tests that the isDevaluing method returns false.
   */
  @Test
  public void testIsDevaluing() {
    assertFalse(regularInfluence.isDevaluing());
  }

  /**
   * Tests that the toChar method returns 'I'.
   */
  @Test
  public void testToChar() {
    assertEquals('I', regularInfluence.toChar());
  }

  /**
   * Tests that applying influence to a cell with null owner
   * but with PAWNS content type returns false.
   */
  @Test
  public void testApplyInfluenceToCellWithNullOwner() throws Exception {
    // Create a cell with PAWNS content but null owner
    // This is an edge case that shouldn't happen in normal operation
    // but we should test it for robustness
    PawnsBoardAugmentedCell<Card> nullOwnerCell = new PawnsBoardAugmentedCell<Card>() {
      @Override
      public CellContent getContent() {
        return CellContent.PAWNS;
      }

      @Override
      public PlayerColors getOwner() {
        return null;
      }

      @Override
      public int getPawnCount() {
        return 1;
      }
    };

    // Apply influence
    boolean result = regularInfluence.applyInfluence(nullOwnerCell, PlayerColors.RED);

    // Should return false as the owner is null
    assertFalse(result);
  }

  /**
   * Tests that attempting to apply influence with an invalid cell content type
   * throws NullPointerException.
   */
  @Test(expected = NullPointerException.class)
  public void testApplyInfluenceWithInvalidCellContent() throws Exception {
    // Create a cell with invalid content type
    PawnsBoardAugmentedCell<Card> invalidCell = new PawnsBoardAugmentedCell<Card>() {
      @Override
      public CellContent getContent() {
        return null; // Invalid content type
      }
    };

    regularInfluence.applyInfluence(invalidCell, PlayerColors.RED);
  }
}