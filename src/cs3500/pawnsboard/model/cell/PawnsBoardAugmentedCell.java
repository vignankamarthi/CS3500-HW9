package cs3500.pawnsboard.model.cell;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;

/**
 * An augmented cell implementation that supports value modification for cards.
 * This cell implements PawnsBoardCell and adds the ability to track value modifiers
 * for implementing upgrading and devaluing influences.
 *
 * @param <C> the type of Card that can be placed in this cell
 */
public class PawnsBoardAugmentedCell<C extends Card> implements PawnsBoardCell<C> {
  
  private CellContent content;
  private PlayerColors owner;
  private int pawnCount;
  private C card;
  private int valueModifier; // Positive for upgrades, negative for devaluations
  
  /**
   * Creates an empty augmented cell with no value modifier.
   */
  public PawnsBoardAugmentedCell() {
    this.content = CellContent.EMPTY;
    this.owner = null;
    this.pawnCount = 0;
    this.card = null;
    this.valueModifier = 0;
  }
  
  /**
   * Gets the content type of this cell.
   *
   * @return the cell content type
   */
  @Override
  public CellContent getContent() {
    return content;
  }

  /**
   * Gets the owner of this cell's contents.
   *
   * @return the player who owns the contents, or null if the cell is empty
   */
  @Override
  public PlayerColors getOwner() {
    return owner;
  }

  /**
   * Gets the number of pawns in this cell.
   *
   * @return the pawn count, or 0 if the cell is empty or contains a card
   */
  @Override
  public int getPawnCount() {
    return (content == CellContent.PAWNS) ? pawnCount : 0;
  }

  /**
   * Gets the card in this cell.
   *
   * @return the card, or null if the cell is empty or contains pawns
   */
  @Override
  public C getCard() {
    return (content == CellContent.CARD) ? card : null;
  }
  
  /**
   * Adds a pawn to this cell. If the cell is empty, it becomes a pawn cell.
   * The maximum number of pawns in a cell is 3 in this implementation.
   *
   * @param playerColors the playerColors who owns the pawn
   * @throws IllegalStateException if trying to add a pawn to a cell with a card
   * @throws IllegalStateException if the cell already has the maximum number of pawns
   * @throws Exception actually throws an {@link IllegalOwnerException} when trying to add a pawn of
   *                   a different owner
   * @throws IllegalArgumentException if playerColors is null
   */
  @Override
  public void addPawn(PlayerColors playerColors) throws Exception {
    if (playerColors == null) {
      throw new IllegalArgumentException("Player colors cannot be null");
    }

    if (content == CellContent.CARD) {
      throw new IllegalStateException("Cannot add pawn to a cell containing a card");
    }

    if (content == CellContent.EMPTY) {
      content = CellContent.PAWNS;
      owner = playerColors;
      pawnCount = 1;
    } else {
      // Cell already has pawns
      if (pawnCount >= 3) {
        throw new IllegalStateException("Cell already has maximum number of pawns");
      }

      if (owner != playerColors) {
        throw new IllegalOwnerException("Cannot add pawn of different owner");
      }

      pawnCount++;
    }
  }

  /**
   * Changes the ownership of pawns in this cell.
   * The pawn count remains the same, but the owner changes.
   *
   * @param newOwner the new owner of the pawns
   * @throws IllegalStateException if trying to change ownership of non-pawn content
   */
  @Override
  public void changeOwnership(PlayerColors newOwner) {
    if (content != CellContent.PAWNS) {
      throw new IllegalStateException("Can only change ownership of pawns");
    }

    owner = newOwner;
  }

  /**
   * Places a card in this cell, replacing any pawns.
   * The cell's content becomes a card, and pawns are removed.
   * Value modifiers are preserved for the new card.
   *
   * @param card the card to place
   * @param playerColors the playerColors who owns the card
   * @throws IllegalArgumentException if card is null
   */
  @Override
  public void setCard(C card, PlayerColors playerColors) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }

    this.content = CellContent.CARD;
    this.owner = playerColors;
    this.card = card;
    this.pawnCount = 0;
    // Note: We do NOT reset valueModifier when placing a card
    // This allows upgrading/devaluing influences to affect future cards
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
    
    // Check if there's a card and it needs to be removed due to devaluation
    checkAndHandleDevaluation();
    
    return valueModifier;
  }
  
  /**
   * Decreases the value modifier of this cell (devaluing).
   * This will make any card placed in this cell worth fewer points.
   *
   * @param amount the amount to decrease the value modifier by
   * @return the new value modifier
   */
  public int devalue(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Devalue amount cannot be negative");
    }
    
    // Decrease the value modifier
    valueModifier -= amount;
    
    // Only check for card removal if there's actually a card present
    // This prevents resetting value modifiers for cells without cards
    if (content == CellContent.CARD && card != null) {
      checkAndHandleDevaluation();
    }
    
    return valueModifier;
  }
  
  /**
   * Checks if the card in this cell needs to be removed due to devaluation.
   * If the effective value of the card is 0 or less, the card is removed
   * and replaced with pawns equal to its cost.
   */
  private void checkAndHandleDevaluation() {
    // Only check for removal if there's a card present
    if (content == CellContent.CARD && card != null) {
      // Card should be removed if its effective value is 0 or less
      // Effective value is original value + value modifier
      int originalValue = card.getValue();
      int effectiveValue = originalValue + valueModifier;
      
      // If effective value is 0 or negative, remove the card
      if (effectiveValue <= 0) {
        // Store card cost and owner before removing it
        int cardCost = card.getCost();
        PlayerColors cardOwner = owner;

        // Remove the card and restore pawns
        restorePawnsAfterCardRemoval(cardCost, cardOwner);
      }
    }
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
   * Sets the value modifier directly without triggering card removal checks.
   * This is used specifically for copying the board state.
   *
   * @param modifier the value modifier to set
   */
  public void setValueModifierDirectly(int modifier) {
    this.valueModifier = modifier;
  }
  
  /**
   * Gets the effective value of the card in this cell, including any value modifiers.
   * If there is no card in the cell, returns 0.
   * The effective value will never be less than 0 for scoring purposes.
   *
   * @return the effective value of the card, or 0 if there is no card
   */
  public int getEffectiveCardValue() {
    if (content != CellContent.CARD || card == null) {
      return 0;
    }
    
    // Calculate the effective value including modifier
    int effectiveValue = card.getValue() + valueModifier;
    
    // For scoring purposes, a card cannot contribute negative points
    return Math.max(0, effectiveValue);
  }
  
  /**
   * Restores pawns after a card is removed due to devaluation.
   * The number of pawns restored is equal to the card's cost, up to a maximum of 3.
   *
   * @param cardCost the cost of the card that was removed
   * @param cardOwner the owner of the card that was removed
   */
  public void restorePawnsAfterCardRemoval(int cardCost, PlayerColors cardOwner) {
    // Change cell content from CARD to PAWNS
    content = CellContent.PAWNS;
    
    // Set pawn count to card cost (max 3)
    pawnCount = Math.min(cardCost, 3);
    
    // Keep the same owner
    owner = cardOwner;
    
    // Remove the card reference
    card = null;
    
    // Reset value modifier to zero when a card is removed due to devaluation
    // According to the rules: "Any increases or decreases from other influences exerted on that cell are removed."
    // "In other words, the cell now adds +0 to a future card's value until another upgrading or devaluing influence affects it."
    valueModifier = 0;
  }
}