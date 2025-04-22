package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for BlankInfluence.
 * This tests the functionality of the blank influence type, which has no effect on cells.
 */
public class BlankInfluenceTest {

  private BlankInfluence blankInfluence;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> emptyCell;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> pawnCell;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> cardCell;

  /**
   * Set up test fixtures before each test.
   */
  @Before
  public void setUp() {
    blankInfluence = new BlankInfluence();

    // Create cells in different states for testing
    emptyCell = new PawnsBoardAugmentedCell<>();

    pawnCell = new PawnsBoardAugmentedCell<>();
    try {
      pawnCell.addPawn(PlayerColors.RED);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set up test fixture", e);
    }

    cardCell = new PawnsBoardAugmentedCell<>();
    // Create a simple card for testing
    boolean[][] influenceGrid = new boolean[5][5];
    PawnsBoardBaseCard card = new PawnsBoardBaseCard("TestCard", 1, 3, 
            influenceGrid);
    cardCell.setCard(card, PlayerColors.BLUE);
  }

  /**
   * Test that applying blank influence to an empty cell has no effect and returns false.
   */
  @Test
  public void testApplyInfluenceOnEmptyCell() throws Exception {
    // Capture initial state
    CellContent initialContent = emptyCell.getContent();
    PlayerColors initialOwner = emptyCell.getOwner();
    int initialPawnCount = emptyCell.getPawnCount();
    int initialValueModifier = emptyCell.getValueModifier();

    // Apply influence
    boolean result = blankInfluence.applyInfluence(emptyCell, PlayerColors.RED);

    // Verify result and that cell didn't change
    assertFalse("Applying blank influence should return false", result);
    assertEquals("Cell content should not change", 
            initialContent, emptyCell.getContent());
    assertEquals("Cell owner should not change", initialOwner, emptyCell.getOwner());
    assertEquals("Pawn count should not change", initialPawnCount, 
            emptyCell.getPawnCount());
    assertEquals("Value modifier should not change", initialValueModifier, 
            emptyCell.getValueModifier());
  }

  /**
   * Test that applying blank influence to a cell with pawns has no effect and returns false.
   */
  @Test
  public void testApplyInfluenceOnPawnCell() throws Exception {
    // Capture initial state
    CellContent initialContent = pawnCell.getContent();
    PlayerColors initialOwner = pawnCell.getOwner();
    int initialPawnCount = pawnCell.getPawnCount();
    int initialValueModifier = pawnCell.getValueModifier();

    // Apply influence
    boolean result = blankInfluence.applyInfluence(pawnCell, PlayerColors.BLUE);

    // Verify result and that cell didn't change
    assertFalse("Applying blank influence should return false", result);
    assertEquals("Cell content should not change", initialContent, pawnCell.getContent());
    assertEquals("Cell owner should not change", initialOwner, pawnCell.getOwner());
    assertEquals("Pawn count should not change", initialPawnCount, 
            pawnCell.getPawnCount());
    assertEquals("Value modifier should not change", initialValueModifier, 
            pawnCell.getValueModifier());
  }

  /**
   * Test that applying blank influence to a cell with a card has no effect and returns false.
   */
  @Test
  public void testApplyInfluenceOnCardCell() throws Exception {
    // Capture initial state
    CellContent initialContent = cardCell.getContent();
    PlayerColors initialOwner = cardCell.getOwner();
    PawnsBoardBaseCard initialCard = cardCell.getCard();
    int initialValueModifier = cardCell.getValueModifier();

    // Apply influence
    boolean result = blankInfluence.applyInfluence(cardCell, PlayerColors.RED);

    // Verify result and that cell didn't change
    assertFalse("Applying blank influence should return false", result);
    assertEquals("Cell content should not change", initialContent, cardCell.getContent());
    assertEquals("Cell owner should not change", initialOwner, cardCell.getOwner());
    assertEquals("Card should not change", initialCard, cardCell.getCard());
    assertEquals("Value modifier should not change", initialValueModifier, 
            cardCell.getValueModifier());
  }

  /**
   * Test that applying blank influence with null player doesn't throw an exception.
   */
  @Test
  public void testApplyInfluenceWithNullPlayer() throws Exception {
    // Since blank influence doesn't use the player parameter, this should not throw
    boolean result = blankInfluence.applyInfluence(emptyCell, null);

    // Verify result is still false
    assertFalse("Applying blank influence with null player should return false", result);
  }

  /**
   * Test that isRegular returns false for blank influence.
   */
  @Test
  public void testIsRegular() {
    assertFalse("BlankInfluence should not be a regular influence", 
            blankInfluence.isRegular());
  }

  /**
   * Test that isUpgrading returns false for blank influence.
   */
  @Test
  public void testIsUpgrading() {
    assertFalse("BlankInfluence should not be an upgrading influence", 
            blankInfluence.isUpgrading());
  }

  /**
   * Test that isDevaluing returns false for blank influence.
   */
  @Test
  public void testIsDevaluing() {
    assertFalse("BlankInfluence should not be a devaluing influence", 
            blankInfluence.isDevaluing());
  }

  /**
   * Test that toChar returns 'X' for blank influence.
   */
  @Test
  public void testToChar() {
    assertEquals("BlankInfluence should be represented by 'X'", 'X', 
            blankInfluence.toChar());
  }
}