package cs3500.pawnsboard.model.cards.factory;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the PawnsBoardAugmentedCardFactory class.
 * Tests constructor validation and card creation functionality.
 */
public class PawnsBoardAugmentedCardFactoryTest {

  private InfluenceManager influenceManager;
  private PawnsBoardAugmentedCardFactory factory;
  
  @Before
  public void setUp() {
    influenceManager = new InfluenceManager();
    factory = new PawnsBoardAugmentedCardFactory(influenceManager);
  }

  /**
   * Tests constructor with valid influence manager.
   */
  @Test
  public void testConstructorWithValidInfluenceManager() {
    // Constructor should not throw exceptions with valid manager
    PawnsBoardAugmentedCardFactory testFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);
    assertNotNull(testFactory);
  }

  /**
   * Tests constructor throws exception when null influence manager is provided.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullInfluenceManager() {
    new PawnsBoardAugmentedCardFactory(null);
  }

  /**
   * Tests creating a card with valid parameters and single influence type.
   */
  @Test
  public void testCreatePawnsBoardCardWithValidParameters() {
    // Create a standard influence grid with only regular influence ('I')
    char[][] influenceGrid = createTestGrid('I');

    // Place card in center
    influenceGrid[2][2] = 'C';

    // Create card
    PawnsBoardAugmentedCard card = factory.createPawnsBoardCard("TestCard",
            2, 3, influenceGrid);

    // Verify card properties
    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(3, card.getValue());

    // Verify influence grid through char representation
    char[][] resultGrid = card.getInfluenceGridAsChars();
    assertEquals('C', resultGrid[2][2]);
    assertEquals('I', resultGrid[0][0]);
  }

  /**
   * Tests creating a card with minimum valid cost value.
   */
  @Test
  public void testCreatePawnsBoardCardWithMinCost() {
    char[][] influenceGrid = createTestGrid('X');
    influenceGrid[2][2] = 'C';

    PawnsBoardAugmentedCard card = factory.createPawnsBoardCard("MinCost", 
            1, 5, influenceGrid);
    assertEquals(1, card.getCost());
  }

  /**
   * Tests creating a card with maximum valid cost value.
   */
  @Test
  public void testCreatePawnsBoardCardWithMaxCost() {
    char[][] influenceGrid = createTestGrid('X');
    influenceGrid[2][2] = 'C';

    PawnsBoardAugmentedCard card = factory.createPawnsBoardCard("MaxCost", 
            3, 5, influenceGrid);
    assertEquals(3, card.getCost());
  }

  /**
   * Tests creating a card with mixed influence types in the grid.
   */
  @Test
  public void testCreatePawnsBoardCardWithMixedInfluenceTypes() {
    char[][] influenceGrid = createTestGrid('X');
    influenceGrid[2][2] = 'C'; // Card position

    // Add different influence types
    influenceGrid[0][0] = 'I'; // Regular influence
    influenceGrid[0][1] = 'U'; // Upgrading influence
    influenceGrid[0][2] = 'D'; // Devaluing influence

    PawnsBoardAugmentedCard card = factory.createPawnsBoardCard("MixedInfluence", 
            2, 3, influenceGrid);

    // Verify the influence types through char representation
    char[][] resultGrid = card.getInfluenceGridAsChars();
    assertEquals('I', resultGrid[0][0]);
    assertEquals('U', resultGrid[0][1]);
    assertEquals('D', resultGrid[0][2]);
  }

  /**
   * Tests that the factory correctly passes character grid to PawnsBoardAugmentedCard.
   */
  @Test
  public void testFactoryPassesGridToCard() {
    // Create a grid with unique pattern to verify it's passed correctly
    char[][] influenceGrid = createTestGrid('X');
    influenceGrid[2][2] = 'C';
    influenceGrid[0][0] = 'I';
    influenceGrid[1][1] = 'U';
    influenceGrid[0][4] = 'D';
    influenceGrid[4][0] = 'D';

    PawnsBoardAugmentedCard card = factory.createPawnsBoardCard("Pattern", 
            2, 3, influenceGrid);

    // Verify the unique pattern is preserved
    char[][] resultGrid = card.getInfluenceGridAsChars();
    assertEquals('I', resultGrid[0][0]);
    assertEquals('U', resultGrid[1][1]);
    assertEquals('D', resultGrid[0][4]);
    assertEquals('D', resultGrid[4][0]);
  }

  /**
   * Helper method to create a test grid filled with the specified character.
   *
   * @param fillChar the character to fill the grid with
   * @return a 5x5 grid filled with the specified character
   */
  private char[][] createTestGrid(char fillChar) {
    char[][] grid = new char[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        grid[row][col] = fillChar;
      }
    }
    return grid;
  }
}