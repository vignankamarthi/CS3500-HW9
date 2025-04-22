package cs3500.pawnsboard.model.cards.factory;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.model.influence.InfluenceManager;

/**
 * Factory implementation for creating {@link PawnsBoardAugmentedCard} objects.
 * This class is responsible for creating augmented cards with different influence types.
 */
public class PawnsBoardAugmentedCardFactory implements CardFactory<PawnsBoardAugmentedCard> {
  
  private final InfluenceManager influenceManager;
  
  /**
   * Constructs a PawnsBoardAugmentedCardFactory with the specified influence manager.
   *
   * @param influenceManager the influence manager to use for creating influence grids
   * @throws IllegalArgumentException if influenceManager is null
   */
  public PawnsBoardAugmentedCardFactory(InfluenceManager influenceManager) {
    if (influenceManager == null) {
      throw new IllegalArgumentException("Influence manager cannot be null");
    }
    this.influenceManager = influenceManager;
  }
  
  /**
   * Creates an augmented card with the specified parameters.
   * Supports mixed influence types including regular, upgrading, and devaluing.
   *
   * @param name         the name of the card
   * @param cost         the cost of the card (1-3)
   * @param value        the value score of the card
   * @param influenceGrid the 5x5 influence grid for the card as chars
   * @return a new PawnsBoardAugmentedCard instance
   */
  @Override
  public PawnsBoardAugmentedCard createPawnsBoardBaseCard(String name, int cost, int value,
                                                      char[][] influenceGrid) {
    return PawnsBoardAugmentedCard.fromCharGrid(name, cost, value, influenceGrid, influenceManager);
  }
}