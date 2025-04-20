package cs3500.pawnsboard.model.cards.deckbuilder;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;

import java.util.List;

/**
 * Interface for building card decks from configuration files.
 * Provides methods to create and validate decks for different players.
 */
public interface DeckBuilder<C extends Card> {

  /**
   * Creates a single deck from a configuration file.
   *
   * @param filePath path to the {@link Card} configuration file
   * @return a list of cards representing a deck
   * @throws InvalidDeckConfigurationException if the deck configuration is invalid
   */
  List<C> createDeck(String filePath) throws InvalidDeckConfigurationException;

  /**
   * Creates a single deck with optional shuffling.
   *
   * @param filePath path to the card configuration file
   * @param shuffle whether to shuffle the deck
   * @return a list of cards representing a deck
   * @throws InvalidDeckConfigurationException if the deck configuration is invalid
   */
  List<C> createDeck(String filePath, boolean shuffle)
          throws InvalidDeckConfigurationException;


  /**
   * Validates that a deck follows the {@link PawnsBoard} rules.
   *
   * @param deck the deck to validate
   * @throws InvalidDeckConfigurationException if the deck doesn't follow game rules
   */
  void validateDeck(List<C> deck) throws InvalidDeckConfigurationException;
}