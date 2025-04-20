package cs3500.pawnsboard.controller.listeners;

import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * An event listener for card selection events in the Pawns Board game.
 * This interface is part of the observer pattern implementation where
 * the view notifies listeners when a card is selected in a player's hand.
 */
public interface CardSelectionListener {
  
  /**
   * Called when a card is selected from a player's hand.
   *
   * @param cardIndex the index of the selected card in the player's hand (0-based)
   * @param player the player whose hand contains the selected card
   */
  void onCardSelected(int cardIndex, PlayerColors player);
}
