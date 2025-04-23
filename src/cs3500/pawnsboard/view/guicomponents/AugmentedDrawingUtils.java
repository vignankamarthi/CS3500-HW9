package cs3500.pawnsboard.view.guicomponents;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.geom.Path2D;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.view.colorscheme.ColorScheme;

/**
 * Utility class containing methods for drawing augmented game elements.
 * This class extends the functionality of DrawingUtils to support value modifiers
 * and special influence types (upgrading and devaluing).
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class. </p>
 */
public class AugmentedDrawingUtils {
  
  private AugmentedDrawingUtils() {
    // Utility class, not meant to be instantiated rather utilized
  }
  
  /**
   * Draws a cell background with value modifier indicator.
   * The value modifier is displayed in the top right corner of the cell.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param isHighlighted whether the cell is highlighted
   * @param colorScheme the color scheme to use for drawing
   * @param content the content type of the cell (EMPTY, PAWNS, CARD)
   * @param owner the owner of the cell, or null if the cell is empty
   * @param valueModifier the value modifier to display
   */
  public static void drawCellWithValueModifier(Graphics2D g2d, Rectangle bounds, 
                                              boolean isHighlighted, 
                                              ColorScheme colorScheme, 
                                              CellContent content, 
                                              PlayerColors owner,
                                              int valueModifier) {
    // First draw the regular cell background
    DrawingUtils.drawCellBackground(g2d, bounds, isHighlighted, colorScheme, content, owner);
    
    // Show value modifiers (positive and negative) on empty and pawn cells
    if (valueModifier != 0) {
      // Save original font and color
      Font originalFont = g2d.getFont();
      Color originalColor = g2d.getColor();
      
      // Set modifier color based on positive/negative
      if (valueModifier > 0) {
        g2d.setColor(colorScheme.getUpgradingInfluenceColor());
      } else {
        g2d.setColor(colorScheme.getDevaluingInfluenceColor());
      }
      
      // Create modifier text with appropriate size (16pt font is a good size for top corner)
      g2d.setFont(new Font("Arial", Font.BOLD, 16));
      String modifierText = (valueModifier > 0 ? "+" : "") + valueModifier;
      
      // Draw in top right corner with small margin
      int x = bounds.x + bounds.width - 5 - g2d.getFontMetrics().stringWidth(modifierText);
      int y = bounds.y + 15; // Top margin of 15px
      
      // Draw the modifier text
      g2d.drawString(modifierText, x, y);
      
      // Restore original font and color
      g2d.setFont(originalFont);
      g2d.setColor(originalColor);
    }
  }
  
  /**
   * Draws an influence grid for a card with special influence types.
   * Different colors are used for different influence types:
   * - Regular (I): Cyan
   * - Upgrading (U): Green (from color scheme)
   * - Devaluing (D): Purple/Red (from color scheme)
   * - Center (C): Yellow
   *
   * @param g2d the graphics context
   * @param x the x coordinate to start drawing
   * @param y the y coordinate to start drawing
   * @param card the card containing the influence grid
   * @param size the overall size of the grid
   * @param player the player who owns the card
   * @param colorScheme the color scheme to use for drawing
   */
  public static void drawInfluenceGridWithSpecialTypes(Graphics2D g2d, int x, int y, 
                                                      Card card, int size, 
                                                      PlayerColors player,
                                                      ColorScheme colorScheme) {
    // For non-augmented cards, fall back to the regular drawing method
    if (!(card instanceof PawnsBoardAugmentedCard)) {
      DrawingUtils.drawInfluenceGrid(g2d, x, y, card, size, player);
      return;
    }
    
    PawnsBoardAugmentedCard augmentedCard = (PawnsBoardAugmentedCard) card;
    char[][] grid = augmentedCard.getInfluenceGridAsChars();
    int cellSize = size / 5;
    
    // Mirror for BLUE player
    if (player == PlayerColors.BLUE) {
      grid = DrawingUtils.mirrorGrid(grid);
    }
    
    // Draw grid background
    g2d.setColor(new Color(40, 40, 40)); // Dark background
    g2d.fillRect(x, y, size, size);
    
    // Draw each cell
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        // Determine cell color based on influence type
        Color cellColor;
        boolean hasInfluence = false;
        
        switch (grid[r][c]) {
          case 'I': // Regular influence
            cellColor = new Color(0, 255, 255); // Cyan
            hasInfluence = true;
            break;
          case 'U': // Upgrading influence
            cellColor = colorScheme.getUpgradingInfluenceColor();
            hasInfluence = true;
            break;
          case 'D': // Devaluing influence
            cellColor = colorScheme.getDevaluingInfluenceColor();
            hasInfluence = true;
            break;
          case 'C': // Center
            cellColor = new Color(255, 255, 0); // Yellow
            hasInfluence = true;
            break;
          default: // No influence (X)
            cellColor = new Color(60, 60, 60); // Slightly lighter than background
            break;
        }
        
        // Draw cell with slight gap
        int gap = 1;
        g2d.setColor(cellColor);
        g2d.fillRect(x + c * cellSize + gap, y + r * cellSize + gap, 
                   cellSize - 2 * gap, cellSize - 2 * gap);
        
        // Add a glow effect for influenced cells
        if (hasInfluence) {
          g2d.setColor(new Color(cellColor.getRed(), cellColor.getGreen(), 
                               cellColor.getBlue(), 70)); // Semi-transparent
          g2d.fillRect(x + c * cellSize - 1, y + r * cellSize - 1, 
                     cellSize + 2, cellSize + 2);
        }
      }
    }
    
    // Draw outer grid border
    g2d.setColor(Color.WHITE);
    g2d.drawRect(x, y, size, size);
  }
  
  /**
   * Draws pawns in a cell with a value modifier.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param count the number of pawns
   * @param player the owner of the pawns
   * @param colorScheme the color scheme to use for drawing
   * @param valueModifier the value modifier to display
   */
  public static void drawPawnsWithValueModifier(Graphics2D g2d, Rectangle bounds, 
                                              int count, PlayerColors player, 
                                              ColorScheme colorScheme, 
                                              int valueModifier) {
    // First draw the regular pawns
    DrawingUtils.drawPawns(g2d, bounds, count, player, colorScheme);
    
    // Show value modifiers (positive and negative) on pawn cells
    if (valueModifier != 0) {
      // Save original font and color
      Font originalFont = g2d.getFont();
      Color originalColor = g2d.getColor();
      
      // Set modifier color based on positive/negative
      if (valueModifier > 0) {
        g2d.setColor(colorScheme.getUpgradingInfluenceColor());
      } else {
        g2d.setColor(colorScheme.getDevaluingInfluenceColor());
      }
      
      // Create modifier text with appropriate size
      g2d.setFont(new Font("Arial", Font.BOLD, 16));
      String modifierText = (valueModifier > 0 ? "+" : "") + valueModifier;
      
      // Draw in top right corner with small margin
      int x = bounds.x + bounds.width - 5 - g2d.getFontMetrics().stringWidth(modifierText);
      int y = bounds.y + 15; // Top margin of 15px
      
      // Draw the modifier text
      g2d.drawString(modifierText, x, y);
      
      // Restore original font and color
      g2d.setFont(originalFont);
      g2d.setColor(originalColor);
    }
  }
  
  /**
   * Draws a card with its content, displaying special influence types.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the card
   * @param card the card to draw
   * @param player the player who owns the card
   * @param isHighlighted whether the card is highlighted
   * @param colorScheme the color scheme to use for drawing
   */
  public static void drawAugmentedCard(Graphics2D g2d, Rectangle bounds, 
                                      Card card, PlayerColors player, 
                                      boolean isHighlighted,
                                      ColorScheme colorScheme) {
    // Draw the card outline
    DrawingUtils.drawCardOutline(g2d, bounds, isHighlighted, player);
    
    // Draw card details
    g2d.setColor(Color.BLACK);
    
    // Restore normal stroke for text
    g2d.setStroke(new java.awt.BasicStroke(1.0f));
    
    // Draw card name with larger font
    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    g2d.drawString(card.getName(), bounds.x + 10, bounds.y + 20);
    
    // Draw card cost and value with larger font
    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.drawString("Cost: " + card.getCost(), bounds.x + 10, bounds.y + 40);
    g2d.drawString("Value: " + card.getValue(), bounds.x + 10, bounds.y + 60);
    
    // For augmented cards, indicate the card type
    if (card instanceof PawnsBoardAugmentedCard) {
      g2d.setFont(new Font("Arial", Font.ITALIC, 10));
      g2d.drawString("(Augmented)", bounds.x + 10, bounds.y + 80);
    }
    
    // Draw influence grid with special types
    int gridSize = Math.min(bounds.width - 20, bounds.height - 100);
    drawInfluenceGridWithSpecialTypes(g2d, 
                                    bounds.x + (bounds.width - gridSize) / 2, 
                                    bounds.y + 100, 
                                    card, 
                                    gridSize, 
                                    player,
                                    colorScheme);
  }
  
  /**
   * Draws a cell card with value modifier.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param originalValue the original value of the card
   * @param valueModifier the value modifier
   * @param player the owner of the card
   * @param colorScheme the color scheme to use for drawing
   */
  public static void drawCellCardWithModifier(Graphics2D g2d, Rectangle bounds, 
                                            int originalValue, int valueModifier,
                                            PlayerColors player, 
                                            ColorScheme colorScheme) {
    // Calculate effective value (never less than 0 for display)
    int effectiveValue = Math.max(0, originalValue + valueModifier);
    
    // Draw the standard card cell (using effective value)
    DrawingUtils.drawCellCard(g2d, bounds, effectiveValue, player, colorScheme);
    
    // Only show negative value modifiers on cards
    if (valueModifier < 0) {
      // Save original font and color
      Font originalFont = g2d.getFont();
      Color originalColor = g2d.getColor();
      
      // Set modifier color based on positive/negative
      if (valueModifier > 0) {
        g2d.setColor(colorScheme.getUpgradingInfluenceColor());
      } else {
        g2d.setColor(colorScheme.getDevaluingInfluenceColor());
      }
      
      // Create modifier text with appropriate size
      g2d.setFont(new Font("Arial", Font.BOLD, 16));
      String modifierText = (valueModifier > 0 ? "+" : "") + valueModifier;
      
      // Draw in top right corner with small margin
      int x = bounds.x + bounds.width - 5 - g2d.getFontMetrics().stringWidth(modifierText);
      int y = bounds.y + 15; // Top margin of 15px
      
      // Draw the modifier text
      g2d.drawString(modifierText, x, y);
      
      // Restore original font and color
      g2d.setFont(originalFont);
      g2d.setColor(originalColor);
    }
  }
}
