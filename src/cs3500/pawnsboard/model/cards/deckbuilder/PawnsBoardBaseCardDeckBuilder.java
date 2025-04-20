package cs3500.pawnsboard.model.cards.deckbuilder;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cards.factory.CardFactory;
import cs3500.pawnsboard.model.cards.factory.PawnsBoardBaseCardFactory;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.cards.reader.CardReader;
import cs3500.pawnsboard.model.cards.reader.PawnsBoardBaseCardReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Implementation of {@link DeckBuilder} for {@link PawnsBoardBaseCard}.
 * Provides methods to create, validate, and mirror decks.
 */
public class PawnsBoardBaseCardDeckBuilder implements DeckBuilder<PawnsBoardBaseCard> {

  private final CardReader<PawnsBoardBaseCard> cardReader;

  /**
   * Constructs a PawnsBoardBaseCardDeckBuilder with default factory and reader.
   */
  public PawnsBoardBaseCardDeckBuilder() {
    CardFactory<PawnsBoardBaseCard> cardFactory = new PawnsBoardBaseCardFactory();
    this.cardReader = new PawnsBoardBaseCardReader(cardFactory);
  }

  /**
   * Creates a single deck from a configuration file.
   *
   * @param filePath path to the {@link Card} configuration file
   * @return a list of cards representing a deck
   * @throws InvalidDeckConfigurationException if the deck configuration is invalid
   */
  @Override
  public List<PawnsBoardBaseCard> createDeck(String filePath)
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
  public List<PawnsBoardBaseCard> createDeck(String filePath, boolean shuffle)
          throws InvalidDeckConfigurationException {
    // Read cards from file
    List<PawnsBoardBaseCard> cards = new ArrayList<>(cardReader.readCards(filePath));

    // Validate deck
    validateDeck(cards);

    // Shuffle the deck if requested
    if (shuffle) {
      Collections.shuffle(cards);
    }

    return cards;
  }


  /**
   * Validates that a deck follows the {@link cs3500.pawnsboard.model.PawnsBoard} rules.
   *
   * @param deck the deck to validate
   * @throws InvalidDeckConfigurationException if the deck doesn't follow game rules
   */
  @Override
  public void validateDeck(List<PawnsBoardBaseCard> deck)
          throws InvalidDeckConfigurationException {
    // Count occurrences of each card name
    Map<String, Integer> cardCounts = new HashMap<>();

    for (PawnsBoardBaseCard card : deck) {
      String name = card.getName();
      cardCounts.put(name, cardCounts.getOrDefault(name, 0) + 1);

      if (cardCounts.get(name) > 2) {
        throw new InvalidDeckConfigurationException(
                "Deck contains more than two copies of card: " + name);
      }
    }
  }

}