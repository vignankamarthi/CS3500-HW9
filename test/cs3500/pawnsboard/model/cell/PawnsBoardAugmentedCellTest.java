package cs3500.pawnsboard.model.cell;

import org.junit.Before;
import org.junit.Test;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test suite for PawnsBoardAugmentedCell class.
 * Tests all functionality, exception handling, and edge cases.
 */
public class PawnsBoardAugmentedCellTest {

  private PawnsBoardAugmentedCell<TestCard> cell;
  private TestCard testCard1;
  private TestCard testCard2;

  /**
   * Sets up test fixtures before each test.
   */
  @Before
  public void setUp() {
    cell = new PawnsBoardAugmentedCell<>();
    testCard1 = new TestCard("TestCard1", 2, 3);
    testCard2 = new TestCard("TestCard2", 1, 5);
  }

  // ------------------ Constructor Test ------------------

  /**
   * Tests that the constructor initializes the cell with correct default values.
   */
  @Test
  public void testConstructorInitializesWithCorrectDefaults() {
    assertEquals(CellContent.EMPTY, cell.getContent());
    assertNull(cell.getOwner());
    assertEquals(0, cell.getPawnCount());
    assertNull(cell.getCard());
    assertEquals(0, cell.getValueModifier());
  }

  // ------------------ Basic Cell Functionality Tests ------------------

  /**
   * Tests that getContent returns the correct content type.
   */
  @Test
  public void testGetContent() {
    assertEquals(CellContent.EMPTY, cell.getContent());

    // Add a pawn
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(CellContent.PAWNS, cell.getContent());

      // Set a card
      cell.setCard(testCard1, PlayerColors.RED);
      assertEquals(CellContent.CARD, cell.getContent());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that getOwner returns the correct owner.
   */
  @Test
  public void testGetOwner() {
    assertNull(cell.getOwner());

    // Add a pawn
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(PlayerColors.RED, cell.getOwner());

      // Change ownership
      cell.changeOwnership(PlayerColors.BLUE);
      assertEquals(PlayerColors.BLUE, cell.getOwner());

      // Set a card
      cell.setCard(testCard1, PlayerColors.RED);
      assertEquals(PlayerColors.RED, cell.getOwner());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that getPawnCount returns the correct pawn count.
   */
  @Test
  public void testGetPawnCount() {
    assertEquals(0, cell.getPawnCount());

    // Add pawns
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(1, cell.getPawnCount());

      cell.addPawn(PlayerColors.RED);
      assertEquals(2, cell.getPawnCount());

      // Set a card (should reset pawn count)
      cell.setCard(testCard1, PlayerColors.RED);
      assertEquals(0, cell.getPawnCount());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that getCard returns the correct card.
   */
  @Test
  public void testGetCard() {
    assertNull(cell.getCard());

    // Set a card
    cell.setCard(testCard1, PlayerColors.RED);
    assertEquals(testCard1, cell.getCard());

    // Change to a different card
    cell.setCard(testCard2, PlayerColors.BLUE);
    assertEquals(testCard2, cell.getCard());
  }

  // ------------------ addPawn Tests ------------------

  /**
   * Tests adding a pawn to an empty cell.
   */
  @Test
  public void testAddPawnToEmptyCell() {
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(CellContent.PAWNS, cell.getContent());
      assertEquals(PlayerColors.RED, cell.getOwner());
      assertEquals(1, cell.getPawnCount());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests adding multiple pawns to a cell.
   */
  @Test
  public void testAddMultiplePawns() {
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(1, cell.getPawnCount());

      cell.addPawn(PlayerColors.RED);
      assertEquals(2, cell.getPawnCount());

      cell.addPawn(PlayerColors.RED);
      assertEquals(3, cell.getPawnCount());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that adding a pawn of a different owner throws IllegalOwnerException.
   */
  @Test
  public void testAddPawnDifferentOwner() {
    try {
      cell.addPawn(PlayerColors.RED);

      try {
        cell.addPawn(PlayerColors.BLUE);
        fail("Should throw IllegalOwnerException");
      } catch (Exception e) {
        assertTrue(e instanceof IllegalOwnerException);
      }
    } catch (Exception e) {
      fail("First addPawn should not throw exception: " + e.getMessage());
    }
  }

  /**
   * Tests that adding a pawn with null player colors throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddPawnNullPlayerColors() throws Exception {
    cell.addPawn(null);
  }

  /**
   * Tests that adding a pawn to a cell with maximum pawns throws IllegalStateException.
   */
  @Test
  public void testAddPawnToFullCell() {
    try {
      // Add maximum number of pawns
      cell.addPawn(PlayerColors.RED);
      cell.addPawn(PlayerColors.RED);
      cell.addPawn(PlayerColors.RED);

      // Try to add one more
      try {
        cell.addPawn(PlayerColors.RED);
        fail("Should throw IllegalStateException");
      } catch (Exception e) {
        assertTrue(e instanceof IllegalStateException);
      }
    } catch (Exception e) {
      fail("Initial pawn additions should not throw exception: " + e.getMessage());
    }
  }

  /**
   * Tests that adding a pawn to a cell with a card throws IllegalStateException.
   */
  @Test
  public void testAddPawnToCellWithCard() {
    cell.setCard(testCard1, PlayerColors.RED);

    try {
      cell.addPawn(PlayerColors.RED);
      fail("Should throw IllegalStateException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
    }
  }

  // ------------------ changeOwnership Tests ------------------

  /**
   * Tests changing ownership of pawns.
   */
  @Test
  public void testChangeOwnership() {
    try {
      cell.addPawn(PlayerColors.RED);
      assertEquals(PlayerColors.RED, cell.getOwner());

      cell.changeOwnership(PlayerColors.BLUE);
      assertEquals(PlayerColors.BLUE, cell.getOwner());
      assertEquals(1, cell.getPawnCount()); // Pawn count shouldn't change
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that changing ownership of an empty cell throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testChangeOwnershipOfEmptyCell() {
    cell.changeOwnership(PlayerColors.RED);
  }

  /**
   * Tests that changing ownership of a cell with a card throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testChangeOwnershipOfCellWithCard() {
    cell.setCard(testCard1, PlayerColors.RED);
    cell.changeOwnership(PlayerColors.BLUE);
  }

  // ------------------ setCard Tests ------------------

  /**
   * Tests setting a card in an empty cell.
   */
  @Test
  public void testSetCardInEmptyCell() {
    cell.setCard(testCard1, PlayerColors.RED);
    assertEquals(CellContent.CARD, cell.getContent());
    assertEquals(PlayerColors.RED, cell.getOwner());
    assertEquals(testCard1, cell.getCard());
    assertEquals(0, cell.getPawnCount());
  }

  /**
   * Tests setting a card in a cell with pawns.
   */
  @Test
  public void testSetCardInCellWithPawns() {
    try {
      cell.addPawn(PlayerColors.RED);
      cell.addPawn(PlayerColors.RED);

      cell.setCard(testCard1, PlayerColors.BLUE);
      assertEquals(CellContent.CARD, cell.getContent());
      assertEquals(PlayerColors.BLUE, cell.getOwner()); // Owner changes
      assertEquals(testCard1, cell.getCard());
      assertEquals(0, cell.getPawnCount()); // Pawns are removed
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests that setting a null card throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetNullCard() {
    cell.setCard(null, PlayerColors.RED);
  }

  /**
   * Tests that setting a card preserves the value modifier.
   */
  @Test
  public void testSetCardPreservesValueModifier() {
    // Set a value modifier
    cell.upgrade(2);
    assertEquals(2, cell.getValueModifier());

    // Set a card
    cell.setCard(testCard1, PlayerColors.RED);
    assertEquals(2, cell.getValueModifier()); // Modifier is preserved
    assertEquals(5, cell.getEffectiveCardValue()); // 3 (base value) + 2 (modifier)
  }

  // ------------------ Value Modifier Tests ------------------

  /**
   * Tests upgrading a cell.
   */
  @Test
  public void testUpgrade() {
    assertEquals(0, cell.getValueModifier());

    cell.upgrade(2);
    assertEquals(2, cell.getValueModifier());

    cell.upgrade(3);
    assertEquals(5, cell.getValueModifier());
  }

  /**
   * Tests that upgrading with a negative amount throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testUpgradeNegativeAmount() {
    cell.upgrade(-1);
  }

  /**
   * Tests devaluing a cell.
   */
  @Test
  public void testDevalue() {
    assertEquals(0, cell.getValueModifier());

    cell.devalue(2);
    assertEquals(-2, cell.getValueModifier());

    cell.devalue(3);
    assertEquals(-5, cell.getValueModifier());
  }

  /**
   * Tests that devaluing with a negative amount throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDevalueNegativeAmount() {
    cell.devalue(-1);
  }

  /**
   * Tests getting the effective card value.
   */
  @Test
  public void testGetEffectiveCardValue() {
    // Empty cell has no effective value
    assertEquals(0, cell.getEffectiveCardValue());

    // Cell with a card but no modifier
    cell.setCard(testCard1, PlayerColors.RED);
    assertEquals(3, cell.getEffectiveCardValue()); // Base value

    // Cell with a card and positive modifier
    cell.upgrade(2);
    assertEquals(5, cell.getEffectiveCardValue()); // 3 + 2

    // Cell with a card and negative modifier
    cell.resetValueModifier();
    cell.devalue(1);
    assertEquals(2, cell.getEffectiveCardValue()); // 3 - 1

    // Cell with a card and large negative modifier
    cell.resetValueModifier();
    cell.devalue(4);
    assertEquals(0, cell.getEffectiveCardValue()); // Max of 0, min effective value is 0
  }

  /**
   * Tests resetting the value modifier.
   */
  @Test
  public void testResetValueModifier() {
    cell.upgrade(3);
    assertEquals(3, cell.getValueModifier());

    cell.resetValueModifier();
    assertEquals(0, cell.getValueModifier());

    cell.devalue(2);
    assertEquals(-2, cell.getValueModifier());

    cell.resetValueModifier();
    assertEquals(0, cell.getValueModifier());
  }

  // ------------------ Card Devaluation Tests ------------------

  /**
   * Tests that a card is removed when its effective value becomes zero.
   */
  @Test
  public void testCardRemovedWhenEffectiveValueZero() {
    cell.setCard(testCard1, PlayerColors.RED); // Value 3
    assertEquals(CellContent.CARD, cell.getContent());

    cell.devalue(3); // Reduce to 0
    assertEquals(CellContent.PAWNS, cell.getContent()); // Card is replaced with pawns
    assertEquals(2, cell.getPawnCount()); // Card cost was 2
    assertEquals(PlayerColors.RED, cell.getOwner()); // Owner remains the same
    assertEquals(0, cell.getValueModifier()); // Value modifier is reset
  }

  /**
   * Tests that a card is removed when its effective value becomes negative.
   */
  @Test
  public void testCardRemovedWhenEffectiveValueNegative() {
    cell.setCard(testCard1, PlayerColors.RED); // Value 3
    assertEquals(CellContent.CARD, cell.getContent());

    cell.devalue(4); // Reduce to -1
    assertEquals(CellContent.PAWNS, cell.getContent()); // Card is replaced with pawns
    assertEquals(2, cell.getPawnCount()); // Card cost was 2
    assertEquals(PlayerColors.RED, cell.getOwner()); // Owner remains the same
    assertEquals(0, cell.getValueModifier()); // Value modifier is reset
  }

  /**
   * Tests that pawns are capped at 3 when a card is removed due to devaluation.
   */
  @Test
  public void testPawnsCapAt3WhenCardRemoved() {
    // Create a test card with cost 5 (more than max pawns)
    TestCard highCostCard = new TestCard("HighCost", 5, 3);

    cell.setCard(highCostCard, PlayerColors.RED);
    cell.devalue(3); // Reduce to 0

    assertEquals(CellContent.PAWNS, cell.getContent());
    assertEquals(3, cell.getPawnCount()); // Capped at 3 even though cost was 5
  }

  /**
   * Tests explicitly calling restorePawnsAfterCardRemoval.
   */
  @Test
  public void testRestorePawnsAfterCardRemoval() {
    cell.setCard(testCard1, PlayerColors.RED);

    // Directly call restorePawnsAfterCardRemoval
    cell.restorePawnsAfterCardRemoval(2, PlayerColors.BLUE);

    assertEquals(CellContent.PAWNS, cell.getContent());
    assertEquals(2, cell.getPawnCount());
    assertEquals(PlayerColors.BLUE, cell.getOwner());
    assertNull(cell.getCard());
    assertEquals(0, cell.getValueModifier());
  }

  // ------------------ Edge Case Tests ------------------

  /**
   * Tests multiple operations in sequence to ensure proper state transitions.
   */
  @Test
  public void testComplexSequence() {
    try {
      // 1. Add pawns
      cell.addPawn(PlayerColors.RED);
      cell.addPawn(PlayerColors.RED);

      // 2. Upgrade the cell
      cell.upgrade(2);

      // 3. Place a card
      cell.setCard(testCard1, PlayerColors.BLUE);

      // 4. Verify state
      assertEquals(CellContent.CARD, cell.getContent());
      assertEquals(PlayerColors.BLUE, cell.getOwner());
      assertEquals(testCard1, cell.getCard());
      assertEquals(2, cell.getValueModifier());
      assertEquals(5, cell.getEffectiveCardValue()); // 3 + 2

      // 5. Devalue to remove card
      cell.devalue(5); // Now -3

      // 6. Verify card was removed
      assertEquals(CellContent.PAWNS, cell.getContent());
      assertEquals(PlayerColors.BLUE, cell.getOwner());
      assertEquals(2, cell.getPawnCount());
      assertEquals(0, cell.getValueModifier());

      // 7. Add more pawns
      cell.addPawn(PlayerColors.BLUE);

      // 8. Verify final state
      assertEquals(3, cell.getPawnCount());
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  /**
   * Tests upgrading then devaluing to verify the combined effect.
   */
  @Test
  public void testUpgradeThenDevalue() {
    cell.upgrade(5);
    assertEquals(5, cell.getValueModifier());

    cell.devalue(3);
    assertEquals(2, cell.getValueModifier());

    cell.devalue(4);
    assertEquals(-2, cell.getValueModifier());
  }

  /**
   * Simple test card implementation for testing.
   */
  private static class TestCard implements Card {
    private final String name;
    private final int cost;
    private final int value;

    public TestCard(String name, int cost, int value) {
      this.name = name;
      this.cost = cost;
      this.value = value;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public int getCost() {
      return cost;
    }

    @Override
    public int getValue() {
      return value;
    }

    @Override
    public boolean[][] getInfluenceGrid() {
      return new boolean[5][5];
    }

    @Override
    public char[][] getInfluenceGridAsChars() {
      char[][] grid = new char[5][5];
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          grid[i][j] = 'X';
        }
      }
      grid[2][2] = 'C';
      return grid;
    }
  }
}