package cs3500.pawnsboard.model.cards;


import org.junit.Before;
import org.junit.Test;

import cs3500.pawnsboard.model.influence.BlankInfluence;
import cs3500.pawnsboard.model.influence.DevaluingInfluence;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import cs3500.pawnsboard.model.influence.RegularInfluence;
import cs3500.pawnsboard.model.influence.UpgradingInfluence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the PawnsBoardAugmentedCard class.
 * Tests all functionality, exception handling, and edge cases.
 */
public class PawnsBoardAugmentedCardTest {

  private Influence[][] validInfluenceGrid;
  private InfluenceManager influenceManager;
  private char[][] validCharGrid;

  /**
   * Sets up test fixtures before each test.
   */
  @Before
  public void setUp() {
    // Create a valid influence grid for testing
    influenceManager = new InfluenceManager();
    validInfluenceGrid = new Influence[5][5];

    // Initialize with blank influences
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        validInfluenceGrid[i][j] = new BlankInfluence();
      }
    }

    // Add some different influence types
    validInfluenceGrid[0][0] = new RegularInfluence();
    validInfluenceGrid[0][1] = new UpgradingInfluence();
    validInfluenceGrid[0][2] = new DevaluingInfluence();

    // Create a valid character grid for testing
    validCharGrid = new char[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        validCharGrid[i][j] = 'X';
      }
    }
    validCharGrid[0][0] = 'I';
    validCharGrid[0][1] = 'U';
    validCharGrid[0][2] = 'D';
    validCharGrid[2][2] = 'C';
  }

  // ------------------ Constructor Tests ------------------

  /**
   * Tests that the constructor successfully creates a card with valid inputs.
   */
  @Test
  public void testConstructorWithValidInputs() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard(
            "TestCard", 2, 3, validInfluenceGrid);
    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(3, card.getValue());
  }

  /**
   * Tests that the constructor throws an exception when the name is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullName() {
    new PawnsBoardAugmentedCard(null, 2, 3, validInfluenceGrid);
  }

  /**
   * Tests that the constructor throws an exception when the name is empty.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithEmptyName() {
    new PawnsBoardAugmentedCard("", 2, 3, validInfluenceGrid);
  }

  /**
   * Tests that the constructor throws an exception when the cost is less than 1.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidCostLow() {
    new PawnsBoardAugmentedCard("TestCard", 0, 3, validInfluenceGrid);
  }

  /**
   * Tests that the constructor throws an exception when the cost is greater than 3.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidCostHigh() {
    new PawnsBoardAugmentedCard("TestCard", 4, 3, validInfluenceGrid);
  }

  /**
   * Tests that the constructor throws an exception when the value is less than 1.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidValue() {
    new PawnsBoardAugmentedCard("TestCard", 2, 0, validInfluenceGrid);
  }

  /**
   * Tests that the constructor throws an exception when the influence grid is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullInfluenceGrid() {
    new PawnsBoardAugmentedCard("TestCard", 2, 3, null);
  }

  /**
   * Tests that the constructor throws an exception when the influence grid doesn't have 5 rows.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidInfluenceGridRows() {
    Influence[][] invalidGrid = new Influence[4][5];
    new PawnsBoardAugmentedCard("TestCard", 2, 3, invalidGrid);
  }

  /**
   * Tests that the constructor throws an exception when a row in the influence grid doesn't 
   * have 5 columns.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidInfluenceGridColumns() {
    Influence[][] invalidGrid = new Influence[5][];
    for (int i = 0; i < 4; i++) {
      invalidGrid[i] = new Influence[5];
    }
    invalidGrid[4] = new Influence[4]; // Last row has only 4 columns

    new PawnsBoardAugmentedCard("TestCard", 2, 3, invalidGrid);
  }

  /**
   * Tests that the constructor handles null values in the influence grid by replacing them 
   * with blank influences.
   */
  @Test
  public void testConstructorHandlesNullValuesInGrid() {
    Influence[][] gridWithNulls = new Influence[5][5];
    // Leave all values as null

    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, gridWithNulls);
    Influence[][] retrievedGrid = card.getAugmentedInfluenceGrid();

    // Check that all null values were replaced with BlankInfluence
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        assertNotNull(retrievedGrid[i][j]);
        assertTrue(retrievedGrid[i][j] instanceof BlankInfluence);
      }
    }
  }

  // ------------------ Getter Method Tests ------------------

  /**
   * Tests the getName method.
   */
  @Test
  public void testGetName() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertEquals("TestCard", card.getName());
  }

  /**
   * Tests the getCost method.
   */
  @Test
  public void testGetCost() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertEquals(2, card.getCost());
  }

  /**
   * Tests the getValue method.
   */
  @Test
  public void testGetValue() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertEquals(3, card.getValue());
  }

  /**
   * Tests that getInfluenceGrid throws UnsupportedOperationException.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetInfluenceGridThrowsException() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    card.getInfluenceGrid();
  }

  /**
   * Tests that getAugmentedInfluenceGrid returns a defensive copy.
   */
  @Test
  public void testGetAugmentedInfluenceGridReturnsCopy() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    Influence[][] retrievedGrid = card.getAugmentedInfluenceGrid();

    // Verify content is the same
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        assertEquals(validInfluenceGrid[i][j].toChar(), retrievedGrid[i][j].toChar());
      }
    }

    // Verify that modifying the returned grid doesn't affect the original
    retrievedGrid[1][1] = new RegularInfluence();
    Influence[][] retrievedGrid2 = card.getAugmentedInfluenceGrid();
    assertNotEquals(retrievedGrid[1][1].toChar(), retrievedGrid2[1][1].toChar());
  }

  /**
   * Tests the getInfluenceGridAsChars method.
   */
  @Test
  public void testGetInfluenceGridAsChars() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    char[][] charGrid = card.getInfluenceGridAsChars();

    // Check center position is C
    assertEquals('C', charGrid[2][2]);

    // Check other positions match the influence types
    assertEquals('I', charGrid[0][0]); // Regular influence
    assertEquals('U', charGrid[0][1]); // Upgrading influence
    assertEquals('D', charGrid[0][2]); // Devaluing influence
    assertEquals('X', charGrid[1][1]); // Blank influence
  }

  // ------------------ fromCharGrid Static Method Tests ------------------

  /**
   * Tests the fromCharGrid static factory method with valid inputs.
   */
  @Test
  public void testFromCharGridWithValidInputs() {
    PawnsBoardAugmentedCard card = PawnsBoardAugmentedCard.fromCharGrid(
            "TestCard", 2, 3, validCharGrid, influenceManager);

    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(3, card.getValue());

    char[][] retrievedCharGrid = card.getInfluenceGridAsChars();
    // Check a few key positions
    assertEquals('I', retrievedCharGrid[0][0]);
    assertEquals('U', retrievedCharGrid[0][1]);
    assertEquals('D', retrievedCharGrid[0][2]);
    assertEquals('C', retrievedCharGrid[2][2]);
  }

  /**
   * Tests that fromCharGrid throws an exception when the character grid is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFromCharGridWithNullCharGrid() {
    PawnsBoardAugmentedCard.fromCharGrid("TestCard", 2, 3, 
            null, influenceManager);
  }

  /**
   * Tests that fromCharGrid throws an exception when the character grid doesn't have 5 rows.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFromCharGridWithInvalidRowCount() {
    char[][] invalidGrid = new char[4][5];
    PawnsBoardAugmentedCard.fromCharGrid("TestCard", 
            2, 3, invalidGrid, influenceManager);
  }

  /**
   * Tests that fromCharGrid throws an exception when a row in the character grid doesn't have 5 
   * columns.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFromCharGridWithInvalidColumnCount() {
    char[][] invalidGrid = new char[5][];
    for (int i = 0; i < 4; i++) {
      invalidGrid[i] = new char[5];
    }
    invalidGrid[4] = new char[4]; // Last row has only 4 columns

    PawnsBoardAugmentedCard.fromCharGrid("TestCard", 2, 3, invalidGrid,
            influenceManager);
  }

  /**
   * Tests that fromCharGrid throws an exception when the influence manager is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFromCharGridWithNullManager() {
    PawnsBoardAugmentedCard.fromCharGrid("TestCard", 2, 3, validCharGrid,
            null);
  }

  // ------------------ equals and hashCode Tests ------------------

  /**
   * Tests that equal cards have the same hashCode.
   */
  @Test
  public void testEqualsAndHashCodeConsistency() {
    PawnsBoardAugmentedCard card1 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    PawnsBoardAugmentedCard card2 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);

    assertEquals(card1, card2);
    assertEquals(card1.hashCode(), card2.hashCode());
  }

  /**
   * Tests that cards with different names are not equal.
   */
  @Test
  public void testEqualsDifferentName() {
    PawnsBoardAugmentedCard card1 = new PawnsBoardAugmentedCard("TestCard1", 2, 
            3, validInfluenceGrid);
    PawnsBoardAugmentedCard card2 = new PawnsBoardAugmentedCard("TestCard2", 2, 
            3, validInfluenceGrid);

    assertNotEquals(card1, card2);
  }

  /**
   * Tests that cards with different costs are not equal.
   */
  @Test
  public void testEqualsDifferentCost() {
    PawnsBoardAugmentedCard card1 = new PawnsBoardAugmentedCard("TestCard", 1, 
            3, validInfluenceGrid);
    PawnsBoardAugmentedCard card2 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);

    assertNotEquals(card1, card2);
  }

  /**
   * Tests that cards with different values are not equal.
   */
  @Test
  public void testEqualsDifferentValue() {
    PawnsBoardAugmentedCard card1 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    PawnsBoardAugmentedCard card2 = new PawnsBoardAugmentedCard("TestCard", 2, 
            4, validInfluenceGrid);

    assertNotEquals(card1, card2);
  }

  /**
   * Tests that cards with different influence grids are not equal.
   */
  @Test
  public void testEqualsDifferentInfluenceGrid() {
    Influence[][] differentGrid = new Influence[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        differentGrid[i][j] = new BlankInfluence();
      }
    }
    // Make a different influence grid
    differentGrid[1][1] = new RegularInfluence();

    PawnsBoardAugmentedCard card1 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    PawnsBoardAugmentedCard card2 = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, differentGrid);

    assertNotEquals(card1, card2);
  }

  /**
   * Tests that a card is not equal to null.
   */
  @Test
  public void testEqualsWithNull() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertNotEquals(card, null);
  }

  /**
   * Tests that a card is not equal to an object of a different class.
   */
  @Test
  public void testEqualsWithDifferentClass() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertNotEquals(card, "Not a card");
  }

  /**
   * Tests that a card is equal to itself.
   */
  @Test
  public void testEqualsWithSameObject() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    assertEquals(card, card);
  }

  // ------------------ toString Test ------------------

  /**
   * Tests the toString method.
   */
  @Test
  public void testToString() {
    PawnsBoardAugmentedCard card = new PawnsBoardAugmentedCard("TestCard", 2, 
            3, validInfluenceGrid);
    String cardString = card.toString();

    // Check that the string contains the card's key information
    assertTrue(cardString.contains("TestCard"));
    assertTrue(cardString.contains("Cost: 2"));
    assertTrue(cardString.contains("Value: 3"));

    // Check that the influence grid is represented in the string
    assertTrue(cardString.contains("I"));
    assertTrue(cardString.contains("U"));
    assertTrue(cardString.contains("D"));
    assertTrue(cardString.contains("C"));
  }
}