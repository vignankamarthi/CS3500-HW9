package cs3500.pawnsboard.view.guicomponents;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Color;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorScheme;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * An augmented version of the card hand panel that supports displaying special influence types
 * like upgrading and devaluing influences. This panel extends the base CardHandPanel to add
 * visualization for the augmented card features.
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class AugmentedCardHandPanel extends CardHandPanel {
  
  /**
   * Constructs an augmented hand panel for the Pawns Board game.
   *
   * @param model the read-only game model to display
   * @param colorSchemeManager the color scheme manager to use
   */
  public AugmentedCardHandPanel(ReadOnlyPawnsBoard<?, ?> model, ColorSchemeManager colorSchemeManager) {
    super(model, colorSchemeManager);
  }
  
  /**
   * Draws a single card in the hand with special influence types.
   * Overrides the parent class method to add support for special influence types.
   *
   * @param g2d the graphics context
   * @param index the index of the card in the hand
   * @param card the card to draw
   */
  // Override with no annotation since parent method is likely package-private
  protected void drawCard(Graphics2D g2d, int index, Card card) {
    // Calculate card position
    int x = 10 + index * (getCardWidth() + 10);
    int y = 30;
    
    // Create bounds for the card
    Rectangle bounds = new Rectangle(x, y, getCardWidth(), getCardHeight());
    
    // Get the color scheme
    ColorScheme colorScheme = getColorSchemeManager().getColorScheme();
    
    // Check if this is an augmented card
    if (card instanceof PawnsBoardAugmentedCard) {
      // Use AugmentedDrawingUtils to draw the augmented card
      AugmentedDrawingUtils.drawAugmentedCard(g2d, bounds, card, getCurrentPlayer(), 
          isCardHighlighted(index), colorScheme);
      
      // Draw an indicator that this is an augmented card (optional)
      drawAugmentedIndicator(g2d, x, y);
    } else {
      // For regular cards, use the standard DrawingUtils
      DrawingUtils.drawCard(g2d, bounds, card, getCurrentPlayer(), index == getHighlightedCard());
    }
  }
  
  /**
   * Draws a small indicator that the card is an augmented card.
   * This adds a colored corner or badge to make augmented cards easily identifiable.
   *
   * @param g2d the graphics context
   * @param x the x coordinate of the card
   * @param y the y coordinate of the card
   */
  private void drawAugmentedIndicator(Graphics2D g2d, int x, int y) {
    // Save the original color
    Color originalColor = g2d.getColor();
    Font originalFont = g2d.getFont();
    
    // Create a small colorful indicator in the top-left corner
    ColorScheme colorScheme = getColorSchemeManager().getColorScheme();
    
    // Triangle in top-left corner
    int[] xPoints = {x, x + 20, x};
    int[] yPoints = {y, y, y + 20};
    g2d.setColor(colorScheme.getUpgradingInfluenceColor());
    g2d.fillPolygon(xPoints, yPoints, 3);
    
    // Add a small border
    g2d.setColor(Color.BLACK);
    g2d.drawPolygon(xPoints, yPoints, 3);
    
    // Add 'A' for Augmented
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 10));
    g2d.drawString("A", x + 5, y + 10);
    
    // Restore the original color and font
    g2d.setColor(originalColor);
    g2d.setFont(originalFont);
  }
  
  /**
   * Draws the influence grid for a card with special influence types.
   * Overrides the parent class method to use AugmentedDrawingUtils.
   *
   * @param g2d the graphics context
   * @param x the x coordinate to start drawing
   * @param y the y coordinate to start drawing
   * @param card the card containing the influence grid
   */
  // Override with no annotation since parent method is likely package-private
  protected void drawInfluenceGrid(Graphics2D g2d, int x, int y, Card card) {
    // Check if this is an augmented card
    if (card instanceof PawnsBoardAugmentedCard) {
      // Use AugmentedDrawingUtils for the grid with special influence types
      AugmentedDrawingUtils.drawInfluenceGridWithSpecialTypes(g2d, x, y, card, getGridSize(), 
          getCurrentPlayer(), getColorSchemeManager().getColorScheme());
    } else {
      // For regular cards, use the standard DrawingUtils
      DrawingUtils.drawInfluenceGrid(g2d, x, y, card, getGridSize(), getCurrentPlayer());
    }
  }
  
  /**
   * Gets the card width.
   *
   * @return the card width
   */
  protected int getCardWidth() {
    return 140; // Must match the value in the parent class
  }
  
  /**
   * Gets the card height.
   *
   * @return the card height
   */
  protected int getCardHeight() {
    return 230; // Must match the value in the parent class
  }
  
  /**
   * Gets the grid size.
   *
   * @return the grid size
   */
  protected int getGridSize() {
    return 100; // Must match the value in the parent class
  }
  
  // Track the highlightedCardIndex locally
  private int highlightedCardIndex = -1;
  private PlayerColors currentPlayer;
  private ColorSchemeManager colorSchemeManager;
  
  /**
   * Checks if a card is highlighted.
   *
   * @param index the card index to check
   * @return true if the card is highlighted
   */
  private boolean isCardHighlighted(int index) {
    return index == highlightedCardIndex;
  }
  
  /**
   * Gets the color scheme manager.
   * This overrides the parent's method to access the color scheme.
   *
   * @return the color scheme manager
   */
  public ColorSchemeManager getColorSchemeManager() {
    // Access the parent's colorSchemeManager if possible, or use our local copy
    // This assumes the parent has a public getColorSchemeManager method
    try {
      return super.getColorSchemeManager();
    } catch (Exception e) {
      // If we can't access parent's, use our own
      return this.colorSchemeManager;
    }
  }
  
  @Override
  public void renderHand(PlayerColors player) {
    // Store the player locally as well
    this.currentPlayer = player;
    super.renderHand(player);
  }
  
  @Override
  public void highlightCard(int cardIndex) {
    // Store the highlighted card index locally
    this.highlightedCardIndex = cardIndex;
    super.highlightCard(cardIndex);
  }
  
  @Override
  public void clearCardHighlights() {
    // Reset our local highlighted card index
    this.highlightedCardIndex = -1;
    super.clearCardHighlights();
  }
}
