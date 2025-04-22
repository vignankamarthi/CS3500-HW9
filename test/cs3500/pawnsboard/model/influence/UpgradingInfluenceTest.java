package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for the UpgradingInfluence class which implements Influence interface.
 * Tests the application of upgrading influence to different cell states and utility methods.
 */
public class UpgradingInfluenceTest {

  private UpgradingInfluence upgradingInfluence;
  private PawnsBoardAugmentedCell<Card> testCell;
  private Card mockCard;

  /**
   * Sets up test fixtures before each test.
   */
  @Before
  public void setUp() {
    upgradingInfluence = new UpgradingInfluence();
    testCell = new PawnsBoardAugmentedCell<>();

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
  }

  /**
   * Tests that applying upgrading influence to an empty cell increases its value modifier.
   */
  @Test
  public void testApplyInfluenceToEmptyCell() throws Exception {
    // Initial state - empty cell with no value modifier
    assertEquals(CellContent.EMPTY, testCell.getContent());
    assertEquals(0, testCell.getValueModifier());

    // Apply upgrading influence
    boolean result = upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);

    // Verify results
    assertTrue(result);
    assertEquals(1, testCell.getValueModifier());
  }

  /**
   * Tests that applying upgrading influence to a cell with pawns increases its value modifier.
   */
  @Test
  public void testApplyInfluenceToPawnCell() throws Exception {
    // Set up cell with pawns
    testCell.addPawn(PlayerColors.RED);
    assertEquals(CellContent.PAWNS, testCell.getContent());
    assertEquals(0, testCell.getValueModifier());

    // Apply upgrading influence
    boolean result = upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);

    // Verify results
    assertTrue(result);
    assertEquals(1, testCell.getValueModifier());
  }

  /**
   * Tests that applying upgrading influence to a cell with a card increases its value modifier.
   */
  @Test
  public void testApplyInfluenceToCardCell() throws Exception {
    // Set up cell with a card
    testCell.setCard(mockCard, PlayerColors.RED);
    assertEquals(CellContent.CARD, testCell.getContent());
    assertEquals(0, testCell.getValueModifier());

    // Apply upgrading influence
    boolean result = upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);

    // Verify results
    assertTrue(result);
    assertEquals(1, testCell.getValueModifier());

    // Verify effective card value has increased
    assertEquals(3, testCell.getEffectiveCardValue());  // Original value 2 + modifier 1
  }

  /**
   * Tests that applying upgrading influence multiple times accumulates the value modifier.
   */
  @Test
  public void testApplyInfluenceMultipleTimes() throws Exception {
    // Initial state
    assertEquals(0, testCell.getValueModifier());

    // Apply upgrading influence multiple times
    upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);
    upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);
    upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);

    // Verify accumulated value modifier
    assertEquals(3, testCell.getValueModifier());
  }

  /**
   * Tests that the isRegular method returns false.
   */
  @Test
  public void testIsRegular() {
    assertFalse(upgradingInfluence.isRegular());
  }

  /**
   * Tests that the isUpgrading method returns true.
   */
  @Test
  public void testIsUpgrading() {
    assertTrue(upgradingInfluence.isUpgrading());
  }

  /**
   * Tests that the isDevaluing method returns false.
   */
  @Test
  public void testIsDevaluing() {
    assertFalse(upgradingInfluence.isDevaluing());
  }

  /**
   * Tests that the toChar method returns 'U'.
   */
  @Test
  public void testToChar() {
    assertEquals('U', upgradingInfluence.toChar());
  }

  /**
   * Tests that applying upgrading influence to a cell with a negative value modifier
   * counteracts the negative value.
   */
  @Test
  public void testApplyInfluenceToNegativeValueModifierCell() throws Exception {
    // Set up cell with negative value modifier
    testCell.devalue(2);  // Create a -2 value modifier
    assertEquals(-2, testCell.getValueModifier());

    // Apply upgrading influence
    boolean result = upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);

    // Verify results
    assertTrue(result);
    assertEquals(-1, testCell.getValueModifier());  // From -2 to -1
  }

  /**
   * Tests that applying upgrading influence works regardless of the player color.
   */
  @Test
  public void testApplyInfluenceWithDifferentPlayers() throws Exception {
    // Initial state
    PawnsBoardAugmentedCell<Card> cell1 = new PawnsBoardAugmentedCell<>();
    PawnsBoardAugmentedCell<Card> cell2 = new PawnsBoardAugmentedCell<>();

    // Test with RED player
    boolean resultRed = upgradingInfluence.applyInfluence(cell1, PlayerColors.RED);
    assertTrue(resultRed);
    assertEquals(1, cell1.getValueModifier());

    // Test with BLUE player
    boolean resultBlue = upgradingInfluence.applyInfluence(cell2, PlayerColors.BLUE);
    assertTrue(resultBlue);
    assertEquals(1, cell2.getValueModifier());
  }

  /**
   * Tests that applying upgrading influence preserves the upgrade for future cards.
   */
  @Test
  public void testUpgradePreservedForFutureCards() throws Exception {
    // Apply upgrading influence to empty cell
    upgradingInfluence.applyInfluence(testCell, PlayerColors.RED);
    assertEquals(1, testCell.getValueModifier());

    // Place a card in the cell
    testCell.setCard(mockCard, PlayerColors.RED);

    // Verify that the card has the upgraded value
    assertEquals(3, testCell.getEffectiveCardValue());  // Original value 2 + modifier 1
  }
}