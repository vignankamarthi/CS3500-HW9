package cs3500.pawnsboard.model.cell;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * An augmented cell implementation that supports value modification for cards.
 * This cell extends PawnsBoardBaseCell and adds the ability to track value modifiers
 * for implementing upgrading and devaluing influences.
 *
 * @param <C> the type of Card that can be placed in this cell
 */
//TODO: Test this class
public class PawnsBoardAugmentedCell<C extends Card> extends PawnsBoardBaseCell<C> {
  
  private int valueModifier; // Positive for upgrades, negative for devaluations
  
  /**
   * Creates an empty augmented cell with no value modifier.
   */
  public PawnsBoardAugmentedCell() {
    super();
    this.valueModifier = 0;
  }
  
  /**
   * Increases the value modifier of this cell (upgrading).
   * This will make any card placed in this cell worth more points.
   *
   * @param amount the amount to increase the value modifier by
   * @return the new value modifier
   */
  public int upgrade(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Upgrade amount cannot be negative");
    }
    valueModifier += amount;
    return valueModifier;
  }
  
  /**
   * Decreases the value modifier of this cell (devaluing).
   * This will make any card placed in this cell worth fewer points.
   * A card's value will never go below 1, regardless of the devaluation.
   *
   * @param amount the amount to decrease the value modifier by
   * @return the new value modifier
   */
  public int devalue(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Devalue amount cannot be negative");
    }
    valueModifier -= amount;
    return valueModifier;
  }
  
  /**
   * Gets the current value modifier of this cell.
   *
   * @return the value modifier (positive for upgrades, negative for devaluations)
   */
  public int getValueModifier() {
    return valueModifier;
  }
  
  /**
   * Resets the value modifier to zero.
   */
  public void resetValueModifier() {
    valueModifier = 0;
  }
  
  /**
   * Places a card in this cell, replacing any pawns.
   * Resets any existing value modifier when a new card is placed.
   *
   * @param card the card to place
   * @param playerColors the player who owns the card
   * @throws IllegalArgumentException if card is null
   */
  @Override
  public void setCard(C card, PlayerColors playerColors) {
    resetValueModifier(); // Reset modifier when a new card is placed
    super.setCard(card, playerColors);
  }
  
  /**
   * Gets the effective value of the card in this cell, including any value modifiers.
   * If there is no card in the cell, returns 0.
   * The effective value will never be less than 1, regardless of devaluations.
   *
   * @return the effective value of the card, or 0 if there is no card
   */
  public int getEffectiveCardValue() {
    C card = super.getCard();
    if (card == null) {
      return 0;
    }
    return Math.max(1, card.getValue() + valueModifier);
  }
}