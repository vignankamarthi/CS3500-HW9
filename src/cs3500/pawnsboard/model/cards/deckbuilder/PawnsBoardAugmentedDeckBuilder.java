package cs3500.pawnsboard.model.cards.deckbuilder;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.factory.CardFactory;
import cs3500.pawnsboard.model.cards.factory.PawnsBoardAugmentedCardFactory;
import cs3500.pawnsboard.model.cards.reader.CardReader;
import cs3500.pawnsboard.model.cards.reader.PawnsBoardAugmentedCardReader;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.InfluenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link DeckBuilder} for {@link PawnsBoardAugmentedCard}.
 * Provides methods to create, validate, and process decks of augmented cards
 * that support different influence types.
 */
public class PawnsBoardAugmentedDeckBuilder implements DeckBuilder<PawnsBoardAugmentedCard> {

  private final CardReader<PawnsBoardAugmentedCard> cardReader;
  
  /**
   * Constructs a PawnsBoardAugmentedDeckBuilder with a default influence manager.
   */
  public PawnsBoardAugmentedDeckBuilder() {
    InfluenceManager influenceManager = new InfluenceManager();
    CardFactory<PawnsBoardAugmentedCard> cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);
    this.cardReader = new PawnsBoardAugmentedCardReader(cardFactory);
  }
  
  /**
   * Constructs a PawnsBoardAugmentedDeckBuilder with the specified influence manager.
   *
   * @param influenceManager the influence manager to use for creating cards
   * @throws IllegalArgumentException if influenceManager is null
   */
  public PawnsBoardAugmentedDeckBuilder(InfluenceManager influenceManager) {
    if (influenceManager == null) {
      throw new IllegalArgumentException("Influence manager cannot be null");
    }
    CardFactory<PawnsBoardAugmentedCard> cardFactory = 
            new PawnsBoardAugmentedCardFactory(influenceManager);
    this.cardReader = new PawnsBoardAugmentedCardReader(cardFactory);
  }
  
  /**
   * Constructs a PawnsBoardAugmentedDeckBuilder with the specified card reader.
   *
   * @param cardReader the card reader to use for reading cards from files
   * @throws IllegalArgumentException if cardReader is null
   */
  public PawnsBoardAugmentedDeckBuilder(CardReader<PawnsBoardAugmentedCard> cardReader) {
    if (cardReader == null) {
      throw new IllegalArgumentException("Card reader cannot be null");
    }
    this.cardReader = cardReader;
  }

  /**
   * Creates a single deck from a configuration file.
   *
   * @param filePath path to the {@link PawnsBoardAugmentedCard} configuration file
   * @return a list of cards representing a deck
   * @throws InvalidDeckConfigurationException if the deck configuration is invalid
   */
  @Override
  public List<PawnsBoardAugmentedCard> createDeck(String filePath)
          throws InvalidDeckConfigurationException {
    return createDeck(filePath, true);
  }

  /**
   * Creates a single deck with optional shuffling.
   *
   * @param filePath path to the card configuration file
   * @param shuffle  whether to shuffle the deck
   * @return a list of cards representing a deck
   * @throws InvalidDeckConfigurationException if the deck configuration is invalid
   */
  @Override
  public List<PawnsBoardAugmentedCard> createDeck(String filePath, boolean shuffle)
          throws InvalidDeckConfigurationException {
    try {
      // Read cards from file
      List<PawnsBoardAugmentedCard> cards = new ArrayList<>(cardReader.readCards(filePath));

      // Validate deck
      validateDeck(cards);

      // Shuffle the deck if requested
      if (shuffle) {
        Collections.shuffle(cards);
      }

      return cards;
    } catch (IllegalArgumentException e) {
      throw new InvalidDeckConfigurationException("Error creating deck: " + e.getMessage());
    }
  }

  /**
   * Validates that a deck follows the rules.
   * For augmented cards, this checks that no card appears more than twice
   * and that there's a good distribution of influence types.
   *
   * @param deck the deck to validate
   * @throws InvalidDeckConfigurationException if the deck doesn't follow game rules
   */
  @Override
  public void validateDeck(List<PawnsBoardAugmentedCard> deck)
          throws InvalidDeckConfigurationException {
    // Count occurrences of each card name
    Map<String, Integer> cardCounts = new HashMap<>();

    for (PawnsBoardAugmentedCard card : deck) {
      String name = card.getName();
      cardCounts.put(name, cardCounts.getOrDefault(name, 0) + 1);

      if (cardCounts.get(name) > 2) {
        throw new InvalidDeckConfigurationException(
                "Deck contains more than two copies of card: " + name);
      }
    }

  }
}