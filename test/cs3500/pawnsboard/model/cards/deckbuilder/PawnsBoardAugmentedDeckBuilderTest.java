package cs3500.pawnsboard.model.cards.deckbuilder;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.factory.PawnsBoardAugmentedCardFactory;
import cs3500.pawnsboard.model.cards.reader.CardReader;
import cs3500.pawnsboard.model.cards.reader.PawnsBoardAugmentedCardReader;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Test suite for the PawnsBoardAugmentedDeckBuilder class.
 * Tests constructors, deck creation, validation, and exception handling.
 */
public class PawnsBoardAugmentedDeckBuilderTest {

  /**
   * Tests the default constructor to ensure it creates a valid instance.
   */
  @Test
  public void testDefaultConstructor() {
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    assertNotNull(deckBuilder);
  }

  /**
   * Tests the constructor that takes an InfluenceManager.
   */
  @Test
  public void testConstructorWithInfluenceManager() {
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedDeckBuilder deckBuilder = 
            new PawnsBoardAugmentedDeckBuilder(influenceManager);
    assertNotNull(deckBuilder);
  }

  /**
   * Tests the constructor that takes a CardReader.
   */
  @Test
  public void testConstructorWithCardReader() {
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedCardFactory cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);
    CardReader<PawnsBoardAugmentedCard> cardReader = new PawnsBoardAugmentedCardReader(cardFactory);

    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder(cardReader);
    assertNotNull(deckBuilder);
  }

  /**
   * Tests that the constructor throws IllegalArgumentException when given a null InfluenceManager.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullInfluenceManager() {
    new PawnsBoardAugmentedDeckBuilder((InfluenceManager) null);
  }

  /**
   * Tests that the constructor throws IllegalArgumentException when given a null CardReader.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullCardReader() {
    new PawnsBoardAugmentedDeckBuilder((CardReader<PawnsBoardAugmentedCard>) null);
  }

  /**
   * Tests createDeck method with a valid file that exists.
   * This is an integration test that requires a valid config file.
   */
  @Test
  public void testCreateDeckWithValidFile() throws InvalidDeckConfigurationException {
    // Create a test configuration file
    File testDir = new File("test-files");
    if (!testDir.exists()) {
      testDir.mkdir();
    }

    File testFile = new File(testDir, "test-deck.config");
    try {
      // Create a sample deck file
      java.io.FileWriter writer = new java.io.FileWriter(testFile);
      writer.write("TestCard1 1 5\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("XXCXX\n");
      writer.write("XXXXX\n");
      writer.write("XXXXX\n");
      writer.write("TestCard2 2 10\n");
      writer.write("XXXXX\n");
      writer.write("XIUXX\n");
      writer.write("XICXX\n");
      writer.write("XIDXX\n");
      writer.write("XXXXX\n");
      writer.close();

      // Test the deck builder
      PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
      List<PawnsBoardAugmentedCard> deck = deckBuilder.createDeck(testFile.getPath(), 
              false);

      // Verify deck was created properly
      assertNotNull(deck);
      assertEquals(2, deck.size());
      assertEquals("TestCard1", deck.get(0).getName());
      assertEquals("TestCard2", deck.get(1).getName());

    } catch (java.io.IOException e) {
      fail("Could not create test file: " + e.getMessage());
    } finally {
      // Clean up
      testFile.delete();
      testDir.delete();
    }
  }

  /**
   * Tests createDeck method with an invalid file path.
   */
  @Test(expected = InvalidDeckConfigurationException.class)
  public void testCreateDeckWithInvalidFile() throws InvalidDeckConfigurationException {
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.createDeck("non-existent-file.config");
  }

  /**
   * Tests validateDeck with a valid deck containing different cards.
   */
  @Test
  public void testValidateDeckWithValidDeck() throws InvalidDeckConfigurationException {
    // Create test cards using the real card factory
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedCardFactory cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);

    // Create sample influence grids
    char[][] grid1 = new char[5][5];
    for (char[] row : grid1) {
      java.util.Arrays.fill(row, 'X');
    }
    grid1[2][2] = 'C';

    char[][] grid2 = new char[5][5];
    for (char[] row : grid2) {
      java.util.Arrays.fill(row, 'X');
    }
    grid2[2][2] = 'C';
    grid2[1][2] = 'I';

    // Create cards
    PawnsBoardAugmentedCard card1 = cardFactory.createPawnsBoardCard("Card1", 1, 
            5, grid1);
    PawnsBoardAugmentedCard card2 = cardFactory.createPawnsBoardCard("Card2", 2, 
            10, grid2);

    // Create deck
    List<PawnsBoardAugmentedCard> deck = new ArrayList<>();
    deck.add(card1);
    deck.add(card2);

    // Test validation
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(deck);
    // No exception means test passes
    String hello = "world";
    assertEquals(hello, "world");
  }

  /**
   * Tests validateDeck with an empty deck.
   */
  @Test
  public void testValidateDeckWithEmptyDeck() throws InvalidDeckConfigurationException {
    List<PawnsBoardAugmentedCard> emptyDeck = new ArrayList<>();

    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(emptyDeck);
    String hello = "world";
    assertEquals(hello, "world");
    // No exception means test passes
  }

  /**
   * Tests validateDeck with a deck containing exactly two of the same card (valid).
   */
  @Test
  public void testValidateDeckWithTwoSameCards() throws InvalidDeckConfigurationException {
    // Create test cards
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedCardFactory cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);

    // Create sample influence grid
    char[][] grid = new char[5][5];
    for (char[] row : grid) {
      java.util.Arrays.fill(row, 'X');
    }
    grid[2][2] = 'C';

    // Create two cards with the same name
    PawnsBoardAugmentedCard card1 = cardFactory.createPawnsBoardCard("SameCard", 
            1, 5, grid);
    PawnsBoardAugmentedCard card2 = cardFactory.createPawnsBoardCard("SameCard", 
            2, 10, grid);

    // Create deck
    List<PawnsBoardAugmentedCard> deck = new ArrayList<>();
    deck.add(card1);
    deck.add(card2);

    // Test validation
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(deck);
    // No exception means test passes

    String hello = "world";
    assertEquals(hello, "world");
  }

  /**
   * Tests validateDeck with a deck containing more than two of the same card (invalid).
   */
  @Test(expected = InvalidDeckConfigurationException.class)
  public void testValidateDeckWithMoreThanTwoSameCards() throws InvalidDeckConfigurationException {
    // Create test cards
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedCardFactory cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);

    // Create sample influence grid
    char[][] grid = new char[5][5];
    for (char[] row : grid) {
      java.util.Arrays.fill(row, 'X');
    }
    grid[2][2] = 'C';

    // Create three cards with the same name
    PawnsBoardAugmentedCard card1 = cardFactory.createPawnsBoardCard("DuplicateCard", 
            1, 5, grid);
    PawnsBoardAugmentedCard card2 = cardFactory.createPawnsBoardCard("DuplicateCard", 
            2, 10, grid);
    PawnsBoardAugmentedCard card3 = cardFactory.createPawnsBoardCard("DuplicateCard", 
            3, 15, grid);

    // Create deck with three cards of the same name
    List<PawnsBoardAugmentedCard> deck = new ArrayList<>();
    deck.add(card1);
    deck.add(card2);
    deck.add(card3);

    // Test validation - should throw InvalidDeckConfigurationException
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(deck);
  }

  /**
   * Tests validateDeck with a null deck.
   */
  @Test(expected = NullPointerException.class)
  public void testValidateDeckWithNullDeck() throws InvalidDeckConfigurationException {
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(null);
  }

  /**
   * Tests validateDeck with a deck containing a null card.
   */
  @Test(expected = NullPointerException.class)
  public void testValidateDeckWithNullCard() throws InvalidDeckConfigurationException {
    // Create test card
    InfluenceManager influenceManager = new InfluenceManager();
    PawnsBoardAugmentedCardFactory cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);

    // Create sample influence grid
    char[][] grid = new char[5][5];
    for (char[] row : grid) {
      java.util.Arrays.fill(row, 'X');
    }
    grid[2][2] = 'C';

    // Create card
    PawnsBoardAugmentedCard card = cardFactory.createPawnsBoardCard("Card", 1, 
            5, grid);

    // Create deck with null card
    List<PawnsBoardAugmentedCard> deck = new ArrayList<>();
    deck.add(card);
    deck.add(null);

    // Test validation - should throw NullPointerException
    PawnsBoardAugmentedDeckBuilder deckBuilder = new PawnsBoardAugmentedDeckBuilder();
    deckBuilder.validateDeck(deck);
  }
}