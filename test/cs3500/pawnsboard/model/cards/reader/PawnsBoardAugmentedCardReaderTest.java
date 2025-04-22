package cs3500.pawnsboard.model.cards.reader;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.factory.CardFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Test suite for PawnsBoardAugmentedCardReader. Tests reading card data from
 * configuration files with all supported influence types.
 */
public class PawnsBoardAugmentedCardReaderTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private CardFactory<PawnsBoardAugmentedCard> mockFactory;
  private PawnsBoardAugmentedCardReader reader;
  
  @Before
  public void setUp() {
    // Create a mock factory that returns dummy cards for testing
    mockFactory = new CardFactory<>() {
      @Override
      public PawnsBoardAugmentedCard createPawnsBoardCard(String name, int cost, int value,
                                                          char[][] influenceGrid) {
        // Return a simple mock card that records the parameters it was created with
        return new MockPawnsBoardAugmentedCard(name, cost, value, influenceGrid);
      }
    };

    reader = new PawnsBoardAugmentedCardReader(mockFactory);
  }

  /**
   * Tests constructor with valid card factory.
   */
  @Test
  public void testConstructorWithValidFactory() {
    // Constructor should not throw exceptions with valid factory
    PawnsBoardAugmentedCardReader testReader = new PawnsBoardAugmentedCardReader(mockFactory);
    assertNotNull(testReader);
  }

  /**
   * Tests constructor throws exception when null card factory is provided.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullFactory() {
    new PawnsBoardAugmentedCardReader(null);
  }

  /**
   * Tests reading a valid card definition file.
   */
  @Test
  public void testReadCardsWithValidFile() throws IOException {
    // Create a temporary file with valid card definitions
    File validFile = tempFolder.newFile("validCards.config");
    try (FileWriter writer = new FileWriter(validFile)) {
      // Write a valid card definition
      writer.write("TestCard 2 5\n"); // Header line
      writer.write("XXXXX\n");       // 5 rows of 5 columns
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");       // Card position in center
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");

      // Add another card with mixed influence types
      writer.write("MixedCard 1 3\n");
      writer.write("IUDXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    List<PawnsBoardAugmentedCard> cards = reader.readCards(validFile.getPath());

    // Verify both cards were read correctly
    assertEquals(2, cards.size());

    // Check first card
    PawnsBoardAugmentedCard card1 = cards.get(0);
    assertEquals("TestCard", card1.getName());
    assertEquals(2, card1.getCost());
    assertEquals(5, card1.getValue());

    // Check second card
    PawnsBoardAugmentedCard card2 = cards.get(1);
    assertEquals("MixedCard", card2.getName());
    assertEquals(1, card2.getCost());
    assertEquals(3, card2.getValue());

    // Verify influence grid of second card
    char[][] gridChars = card2.getInfluenceGridAsChars();
    assertEquals('I', gridChars[0][0]);
    assertEquals('U', gridChars[0][1]);
    assertEquals('D', gridChars[0][2]);
  }

  /**
   * Tests exception thrown when file doesn't exist.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testReadCardsWithNonExistentFile() {
    reader.readCards("nonexistent.file");
  }
  
  /**
   * Tests exception thrown when header format is invalid.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidHeaderFormat() throws IOException {
    File invalidFile = tempFolder.newFile("invalidHeader.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Missing value in header
      writer.write("InvalidCard 2\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when cost is not a number.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCostNotANumber() throws IOException {
    File invalidFile = tempFolder.newFile("invalidCost.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Cost is not a number
      writer.write("TestCard NotANumber 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when cost is less than 1.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCostTooLow() throws IOException {
    File invalidFile = tempFolder.newFile("invalidCostLow.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Cost is below minimum
      writer.write("TestCard 0 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when cost is greater than 3.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCostTooHigh() throws IOException {
    File invalidFile = tempFolder.newFile("invalidCostHigh.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Cost is above maximum
      writer.write("TestCard 4 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when value is not a number.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueNotANumber() throws IOException {
    File invalidFile = tempFolder.newFile("invalidValue.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Value is not a number
      writer.write("TestCard 2 NotANumber\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when value is not positive.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueNotPositive() throws IOException {
    File invalidFile = tempFolder.newFile("invalidValueZero.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      // Value is not positive
      writer.write("TestCard 2 0\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when grid has an invalid character.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGridCharacter() throws IOException {
    File invalidFile = tempFolder.newFile("invalidGridChar.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXC?X\n"); // Invalid character '?'
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when grid is missing a card position.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMissingCardPosition() throws IOException {
    File invalidFile = tempFolder.newFile("missingCardPos.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n"); // No 'C' position
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when grid has multiple card positions.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMultipleCardPositions() throws IOException {
    File invalidFile = tempFolder.newFile("multipleCardPos.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n"); // One card position
      writer.write("XCCXX\n"); // Additional card positions
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when card position is not in center.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCardPositionNotInCenter() throws IOException {
    File invalidFile = tempFolder.newFile("offsetCardPos.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("CXXXX\n"); // Card position not at (2,2)
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when grid line is too short.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGridLineTooShort() throws IOException {
    File invalidFile = tempFolder.newFile("shortGridLine.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXC\n"); // Line too short
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests exception thrown when file ends unexpectedly while reading grid.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testUnexpectedEndOfFile() throws IOException {
    File invalidFile = tempFolder.newFile("incompleteGrid.config");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("TestCard 2 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      // Missing last two lines of grid
    }

    reader.readCards(invalidFile.getPath());
  }

  /**
   * Tests reading a card with all supported influence types.
   */
  @Test
  public void testReadCardWithAllInfluenceTypes() throws IOException {
    File validFile = tempFolder.newFile("allInfluenceTypes.config");
    try (FileWriter writer = new FileWriter(validFile)) {
      writer.write("AllTypes 3 10\n");
      writer.write("IUDXX\n"); // Include all influence types
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
    }

    List<PawnsBoardAugmentedCard> cards = reader.readCards(validFile.getPath());
    assertEquals(1, cards.size());

    PawnsBoardAugmentedCard card = cards.get(0);
    assertEquals("AllTypes", card.getName());
    assertEquals(3, card.getCost());
    assertEquals(10, card.getValue());

    // Verify influence grid
    char[][] gridChars = card.getInfluenceGridAsChars();
    assertEquals('I', gridChars[0][0]);
    assertEquals('U', gridChars[0][1]);
    assertEquals('D', gridChars[0][2]);
  }

  /**
   * Rudimentary Mock implementation of PawnsBoardAugmentedCard for testing.
   * Records the parameters it was created with and returns them in appropriate getter methods.
   */
  private static class MockPawnsBoardAugmentedCard extends PawnsBoardAugmentedCard {
    private final String name;
    private final int cost;
    private final int value;
    private final char[][] influenceChars;

    public MockPawnsBoardAugmentedCard(String name, int cost, int value, char[][] influenceGrid) {
      super(name, cost, value, new MockInfluence[5][5]); // Call super with dummy values
      this.name = name;
      this.cost = cost;
      this.value = value;
      this.influenceChars = influenceGrid;
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
    public char[][] getInfluenceGridAsChars() {
      return influenceChars;
    }
  }

  /**
   * Mock influence implementation for testing.
   */
  private static class MockInfluence implements cs3500.pawnsboard.model.influence.Influence {
    @Override
    public boolean applyInfluence(cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell<?> cell,
                                  cs3500.pawnsboard.model.enumerations.PlayerColors currentPlayer) {
      return false;
    }

    @Override
    public boolean isRegular() {
      return false;
    }

    @Override
    public boolean isUpgrading() {
      return false;
    }

    @Override
    public boolean isDevaluing() {
      return false;
    }

    @Override
    public char toChar() {
      return 'X';
    }
  }
}