package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for DevaluingInfluence.
 * This tests the functionality of the devaluing influence type,
 * which decreases the value of cells with cards.
 */
public class DevaluingInfluenceTest {

  private DevaluingInfluence devaluingInfluence;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> emptyCell;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> pawnCell;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> cardCell;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> lowValueCardCell;

  /**
   * Set up test fixtures before each test.
   */
  @Before
  public void setUp() {
    devaluingInfluence = new DevaluingInfluence();

    // Create cells in different states for testing
    emptyCell = new PawnsBoardAugmentedCell<>();

    pawnCell = new PawnsBoardAugmentedCell<>();
    try {
      pawnCell.addPawn(PlayerColors.RED);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set up test fixture", e);
    }

    // Create a normal card for testing
    boolean[][] influenceGrid = new boolean[5][5];
    PawnsBoardBaseCard card = new PawnsBoardBaseCard("TestCard", 2, 3, 
            influenceGrid);
    cardCell = new PawnsBoardAugmentedCell<>();
    cardCell.setCard(card, PlayerColors.BLUE);

    // Create a card with low value for testing card removal
    PawnsBoardBaseCard lowValueCard = new PawnsBoardBaseCard("LowValueCard", 2, 
            1, influenceGrid);
    lowValueCardCell = new PawnsBoardAugmentedCell<>();
    lowValueCardCell.setCard(lowValueCard, PlayerColors.RED);
  }

  /**
   * Test that applying devaluing influence to an empty cell decreases its value modifier.
   */
  @Test
  public void testApplyInfluenceOnEmptyCell() throws Exception {
    // Capture initial state
    int initialValueModifier = emptyCell.getValueModifier();

    // Apply influence
    boolean result = devaluingInfluence.applyInfluence(emptyCell, PlayerColors.RED);

    // Verify result and that value modifier decreased
    assertTrue("Applying devaluing influence should return true", result);
    assertEquals("Value modifier should decrease by 1",
            initialValueModifier - 1, emptyCell.getValueModifier());

    // Verify cell content didn't change (remains empty)
    assertEquals("Cell content should remain empty",
            CellContent.EMPTY, emptyCell.getContent());
  }

  /**
   * Test that applying devaluing influence to a cell with pawns decreases its value modifier.
   */
  @Test
  public void testApplyInfluenceOnPawnCell() throws Exception {
    // Capture initial state
    int initialValueModifier = pawnCell.getValueModifier();
    int initialPawnCount = pawnCell.getPawnCount();
    PlayerColors initialOwner = pawnCell.getOwner();

    // Apply influence
    boolean result = devaluingInfluence.applyInfluence(pawnCell, PlayerColors.BLUE);

    // Verify result and that value modifier decreased
    assertTrue("Applying devaluing influence should return true", result);
    assertEquals("Value modifier should decrease by 1",
            initialValueModifier - 1, pawnCell.getValueModifier());

    // Verify cell content didn't change
    assertEquals("Cell content should remain PAWNS",
            CellContent.PAWNS, pawnCell.getContent());
    assertEquals("Pawn count should not change",
            initialPawnCount, pawnCell.getPawnCount());
    assertEquals("Cell owner should not change",
            initialOwner, pawnCell.getOwner());
  }

  /**
   * Test that applying devaluing influence to a cell with a card decreases its value modifier
   * but doesn't remove the card if its effective value remains positive.
   */
  @Test
  public void testApplyInfluenceOnCardCell() throws Exception {
    // Capture initial state
    int initialValueModifier = cardCell.getValueModifier();
    PawnsBoardBaseCard initialCard = cardCell.getCard();

    // Apply influence
    boolean result = devaluingInfluence.applyInfluence(cardCell, PlayerColors.RED);

    // Verify result and that value modifier decreased
    assertTrue("Applying devaluing influence should return true", result);
    assertEquals("Value modifier should decrease by 1",
            initialValueModifier - 1, cardCell.getValueModifier());

    // Verify card wasn't removed
    assertEquals("Cell content should remain CARD",
            CellContent.CARD, cardCell.getContent());
    assertEquals("Card should not change",
            initialCard, cardCell.getCard());

    // Verify effective card value decreased
    assertEquals("Effective card value should decrease by 1",
            initialCard.getValue() - 1, cardCell.getEffectiveCardValue());
  }

  /**
   * Test that applying devaluing influence to a cell with a low-value card removes the card
   * when its effective value becomes 0 or less.
   */
  @Test
  public void testApplyInfluenceRemovesCardWhenValueBecomesZero() throws Exception {
    // Capture initial state
    PawnsBoardBaseCard initialCard = lowValueCardCell.getCard();
    PlayerColors initialOwner = lowValueCardCell.getOwner();
    int initialCardCost = initialCard.getValue(); // Store card cost

    // Ensure initial state is correct
    assertEquals("Initial card value should be 1",
            1, initialCard.getValue());

    // Apply influence
    boolean result = devaluingInfluence.applyInfluence(lowValueCardCell, PlayerColors.BLUE);

    // Verify result
    assertTrue("Applying devaluing influence should return true", result);

    // Verify card was removed and replaced with pawns
    assertEquals("Cell content should change to PAWNS",
            CellContent.PAWNS, lowValueCardCell.getContent());
    assertNull("Card should be removed",
            lowValueCardCell.getCard());

    // Verify pawns were added with the same owner
    assertEquals("Cell should retain the same owner",
            initialOwner, lowValueCardCell.getOwner());

    // Verify pawn count equals card cost (max 3)
    int expectedPawnCount = Math.min(initialCard.getCost(), 3);
    assertEquals("Pawn count should equal card cost (max 3)",
            expectedPawnCount, lowValueCardCell.getPawnCount());

    // Verify value modifier was reset
    assertEquals("Value modifier should be reset to 0",
            0, lowValueCardCell.getValueModifier());
  }

  /**
   * Test that applying multiple devaluing influences has a cumulative effect.
   */
  @Test
  public void testMultipleDevaluingInfluences() throws Exception {
    // Apply devaluing influence multiple times
    devaluingInfluence.applyInfluence(cardCell, PlayerColors.RED);
    devaluingInfluence.applyInfluence(cardCell, PlayerColors.RED);

    // Verify cumulative effect on value modifier
    assertEquals("Value modifier should decrease by 2",
            -2, cardCell.getValueModifier());

    // Verify effective card value
    PawnsBoardBaseCard card = cardCell.getCard();
    assertEquals("Effective card value should be original value minus 2",
            card.getValue() - 2, cardCell.getEffectiveCardValue());
  }

  /**
   * Test that isRegular returns false for devaluing influence.
   */
  @Test
  public void testIsRegular() {
    assertFalse("DevaluingInfluence should not be a regular influence",
            devaluingInfluence.isRegular());
  }

  /**
   * Test that isUpgrading returns false for devaluing influence.
   */
  @Test
  public void testIsUpgrading() {
    assertFalse("DevaluingInfluence should not be an upgrading influence",
            devaluingInfluence.isUpgrading());
  }

  /**
   * Test that isDevaluing returns true for devaluing influence.
   */
  @Test
  public void testIsDevaluing() {
    assertTrue("DevaluingInfluence should be a devaluing influence",
            devaluingInfluence.isDevaluing());
  }

  /**
   * Test that toChar returns 'D' for devaluing influence.
   */
  @Test
  public void testToChar() {
    assertEquals("DevaluingInfluence should be represented by 'D'",
            'D', devaluingInfluence.toChar());
  }

  /**
   * Test devaluing a cell that already has a negative value modifier.
   */
  @Test
  public void testDevaluingCellWithNegativeValueModifier() throws Exception {
    // First apply devaluing influence to make value modifier negative
    devaluingInfluence.applyInfluence(emptyCell, PlayerColors.RED);
    int valueModifierAfterFirstDevalue = emptyCell.getValueModifier();

    // Apply devaluing influence again
    devaluingInfluence.applyInfluence(emptyCell, PlayerColors.RED);

    // Verify value modifier decreased further
    assertEquals("Value modifier should decrease by 1 more",
            valueModifierAfterFirstDevalue - 1, emptyCell.getValueModifier());
  }

  /**
   * Test that null player parameter doesn't affect the influence application.
   */
  @Test
  public void testApplyInfluenceWithNullPlayer() throws Exception {
    // Apply influence with null player
    boolean result = devaluingInfluence.applyInfluence(emptyCell, null);

    // Verify result and that value modifier decreased
    assertTrue("Applying devaluing influence with null player should return true", result);
    assertEquals("Value modifier should decrease by 1",
            -1, emptyCell.getValueModifier());
  }
}