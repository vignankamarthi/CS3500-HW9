package cs3500.pawnsboard.view.guicomponents;

import javax.swing.JPanel;
import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Interface for a panel that displays a player's hand of cards.
 * This panel is responsible for rendering cards with their names,
 * costs, values, and influence grids.
 */
public interface HandPanel {
  
  /**
   * Renders the hand of the specified player.
   * This should update the visual representation based on the model.
   *
   * @param player the player whose hand to render
   */
  void renderHand(PlayerColors player);
  
  /**
   * Highlights a specific card in the player's hand.
   * The panel should visually indicate which card is selected.
   *
   * @param cardIndex the index of the card to highlight (0-based)
   */
  void highlightCard(int cardIndex);
  
  /**
   * Clears any card highlights in the hand.
   * This should reset all cards to their normal appearance.
   */
  void clearCardHighlights();
  
  /**
   * Registers a listener for card selection events.
   * The listener will be notified when a card is selected in the hand.
   *
   * @param listener the card selection listener to register
   */
  void addCardSelectionListener(CardSelectionListener listener);
  
  /**
   * Gets the underlying Swing panel.
   * This is necessary for adding the panel to a container.
   *
   * @return the JPanel representing this hand panel
   */
  JPanel getPanel();
}
