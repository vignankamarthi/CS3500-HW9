package cs3500.pawnsboard.view.guicomponents;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.geom.Path2D;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorScheme;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * Utility class containing static methods for drawing game elements.
 * This class provides consistent drawing methods for cards, cells, pawns,
 * and other visual elements of the Pawns Board game.
 *
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class DrawingUtils {
  
  // Static ColorSchemeManager to manage color schemes
  // We use a static manager to ensure consistent color schemes across the application
  private static final ColorSchemeManager colorSchemeManager = new ColorSchemeManager();
  
  private DrawingUtils() {
    // Utility class, not meant to be instantiated rather utilized
  }
  
  /**
   * Gets the current color scheme manager.
   * This allows access to the color scheme manager for changing or getting the current scheme.
   *
   * @return the color scheme manager
   */
  public static ColorSchemeManager getColorSchemeManager() {
    return colorSchemeManager;
  }
  
  /**
   * Draws a card outline.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the card
   * @param isHighlighted whether the card is highlighted
   * @param player the player who owns the card
   */
  public static void drawCardOutline(Graphics2D g2d, Rectangle bounds, 
                                     boolean isHighlighted, PlayerColors player) {
    // Create a path for the card with rounded corners
    Path2D.Double path = new Path2D.Double();
    double arcSize = 8.0;
    
    // Top left corner
    path.moveTo(bounds.x + arcSize, bounds.y);
    // Top edge and top right corner
    path.lineTo(bounds.x + bounds.width - arcSize, bounds.y);
    path.quadTo(bounds.x + bounds.width, bounds.y,
            bounds.x + bounds.width, bounds.y + arcSize);
    // Right edge and bottom right corner
    path.lineTo(bounds.x + bounds.width, bounds.y + bounds.height - arcSize);
    path.quadTo(bounds.x + bounds.width, bounds.y + bounds.height,
            bounds.x + bounds.width - arcSize, bounds.y + bounds.height);
    // Bottom edge and bottom left corner
    path.lineTo(bounds.x + arcSize, bounds.y + bounds.height);
    path.quadTo(bounds.x, bounds.y + bounds.height,
            bounds.x, bounds.y + bounds.height - arcSize);
    // Left edge and top left corner
    path.lineTo(bounds.x, bounds.y + arcSize);
    path.quadTo(bounds.x, bounds.y,
            bounds.x + arcSize, bounds.y);
    path.closePath();
    
    // Draw card background based on player
    if (player == PlayerColors.RED) {
      g2d.setColor(new Color(255, 200, 200)); // Light red
    } else {
      g2d.setColor(new Color(200, 200, 255)); // Light blue
    }
    g2d.fill(path);
    
    // Draw highlight if needed (after filling background)
    if (isHighlighted) {
      // Draw a thicker highlight border
      g2d.setColor(Color.CYAN);
      g2d.setStroke(new java.awt.BasicStroke(5.0f));
      g2d.draw(path);
      // Also draw a glowing effect around the card
      g2d.setColor(new Color(0, 255, 255, 100)); // Semi-transparent cyan
      g2d.setStroke(new java.awt.BasicStroke(8.0f));
      g2d.draw(path);
    } else {
      // Draw normal card border
      g2d.setColor(Color.BLACK);
      g2d.setStroke(new java.awt.BasicStroke(1.0f));
      g2d.draw(path);
    }
  }
  
  /**
   * Draws a card with its content.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the card
   * @param card the card to draw
   * @param player the player who owns the card
   * @param isHighlighted whether the card is highlighted
   */
  public static void drawCard(Graphics2D g2d, Rectangle bounds, Card card, 
                             PlayerColors player, boolean isHighlighted) {
    // Draw the card outline
    drawCardOutline(g2d, bounds, isHighlighted, player);
    
    // Draw card details
    g2d.setColor(Color.BLACK);
    
    // Restore normal stroke for text
    g2d.setStroke(new java.awt.BasicStroke(1.0f));
    
    // Draw card name with larger font
    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    g2d.drawString(card.getName(), bounds.x + 10, bounds.y + 20);
    
    // Draw card cost and value with larger font
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString("Cost: " + card.getCost(), bounds.x + 10, bounds.y + 40);
    g2d.drawString("Value: " + card.getValue(), bounds.x + 10, bounds.y + 60);
    
    // Draw influence grid
    int gridSize = Math.min(bounds.width - 20, bounds.height - 80);
    drawInfluenceGrid(g2d, 
                      bounds.x + (bounds.width - gridSize) / 2, 
                      bounds.y + 80, 
                      card, 
                      gridSize, 
                      player);
  }
  
  /**
   * Draws an influence grid for a card.
   *
   * @param g2d the graphics context
   * @param x the x coordinate to start drawing
   * @param y the y coordinate to start drawing
   * @param card the card containing the influence grid
   * @param size the overall size of the grid
   * @param player the player who owns the card
   */
  public static void drawInfluenceGrid(Graphics2D g2d, int x, int y, Card card, 
                                     int size, PlayerColors player) {
    char[][] grid = card.getInfluenceGridAsChars();
    int cellSize = size / 5;
    
    // Mirror for BLUE player
    if (player == PlayerColors.BLUE) {
      grid = mirrorGrid(grid);
    }
    
    // Draw grid background
    g2d.setColor(new Color(40, 40, 40));
    g2d.fillRect(x, y, size, size);
    
    // Draw each cell
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        // Determine cell color
        Color cellColor;
        boolean hasInfluence = false;
        
        switch (grid[r][c]) {
          case 'I': // Influence
            cellColor = new Color(0, 255, 255); // Brighter cyan
            hasInfluence = true;
            break;
          case 'C': // Center
            cellColor = new Color(255, 255, 0); // Brighter yellow
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
   * Draws a board cell with its content.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param isHighlighted whether the cell is highlighted
   */
  public static void drawCellBackground(Graphics2D g2d, Rectangle bounds, boolean isHighlighted) {
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    
    // Set color based on highlight state
    if (isHighlighted) {
      g2d.setColor(scheme.getHighlightedCell());
    } else {
      g2d.setColor(scheme.getCellBackground());
    }
    
    // Draw cell background
    g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    
    // Draw cell border using the cell border color from the scheme
    g2d.setColor(scheme.getCellBorderColor());
    g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }
  
  /**
   * Draws pawns in a cell.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param count the number of pawns
   * @param player the owner of the pawns
   */
  public static void drawPawns(Graphics2D g2d, Rectangle bounds, int count, PlayerColors player) {
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    
    // Set color based on player
    if (player == PlayerColors.RED) {
      g2d.setColor(scheme.getRedPawnColor());
    } else {
      g2d.setColor(scheme.getBluePawnColor());
    }
    
    // Calculate pawn size and position
    int diameter = bounds.width / 2;
    int circleX = bounds.x + (bounds.width - diameter) / 2;
    int circleY = bounds.y + (bounds.height - diameter) / 2;
    
    // Draw a circle for the pawn
    g2d.fillOval(circleX, circleY, diameter, diameter);
    
    // Draw the count
    g2d.setColor(scheme.getPawnTextColor());
    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    String countStr = String.valueOf(count);
    int textWidth = g2d.getFontMetrics().stringWidth(countStr);
    g2d.drawString(countStr, 
                  circleX + (diameter - textWidth) / 2, 
                  circleY + diameter / 2 + 5);
  }
  
  /**
   * Draws a card indicator in a cell (for cells containing cards).
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the cell
   * @param value the value of the card
   * @param player the owner of the card
   */
  public static void drawCellCard(Graphics2D g2d, Rectangle bounds, int value,
                                  PlayerColors player) {
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    
    // Set color based on player, using colors from the color scheme
    if (player == PlayerColors.RED) {
      g2d.setColor(scheme.getRedPawnColor());
    } else {
      g2d.setColor(scheme.getBluePawnColor());
    }
    
    // Draw card background with insets
    int inset = 2;
    g2d.fillRect(bounds.x + inset, bounds.y + inset, 
                 bounds.width - 2 * inset, bounds.height - 2 * inset);
    
    // Draw card border
    g2d.setColor(scheme.getCellBorderColor());
    g2d.drawRect(bounds.x + inset, bounds.y + inset, 
                 bounds.width - 2 * inset, bounds.height - 2 * inset);
    
    // Draw value with player prefix
    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    String text = String.valueOf(value);
    int textWidth = g2d.getFontMetrics().stringWidth(text);
    
    // Set text color to ensure readability
    if (player == PlayerColors.RED) {
      g2d.setColor(scheme.getRedScoreTextColor() == Color.WHITE ? Color.BLACK : Color.WHITE);
    } else {
      g2d.setColor(scheme.getBlueScoreTextColor() == Color.WHITE ? Color.BLACK : Color.WHITE);
    }
    
    g2d.drawString(text, 
                  bounds.x + (bounds.width - textWidth) / 2, 
                  bounds.y + bounds.height / 2 + 6);
  }
  
  /**
   * Draws a score indicator.
   *
   * @param g2d the graphics context
   * @param bounds the bounds of the score cell
   * @param score the score value
   * @param player the player whose score is being displayed
   */
  public static void drawScore(Graphics2D g2d, Rectangle bounds, int score, PlayerColors player) {
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    
    // Draw score background
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    
    // Draw score border
    g2d.setColor(scheme.getCellBorderColor());
    g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    
    // Draw score value
    if (player == PlayerColors.RED) {
      g2d.setColor(scheme.getRedScoreTextColor());
    } else {
      g2d.setColor(scheme.getBlueScoreTextColor());
    }
    
    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    String text = String.valueOf(score);
    int textWidth = g2d.getFontMetrics().stringWidth(text);
    g2d.drawString(text, 
                  bounds.x + (bounds.width - textWidth) / 2, 
                  bounds.y + bounds.height / 2 + 6);
  }
  
  /**
   * Mirrors a grid horizontally for the BLUE player.
   *
   * @param grid the original grid
   * @return the mirrored grid
   */
  public static char[][] mirrorGrid(char[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    char[][] mirrored = new char[rows][cols];
    
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        mirrored[r][c] = grid[r][cols - c - 1];
      }
    }
    
    return mirrored;
  }
}
