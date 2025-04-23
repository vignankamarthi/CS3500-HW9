package cs3500.pawnsboard.view.guicomponents;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorScheme;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * An augmented version of the game board panel that supports displaying value modifiers
 * and special influence types. This panel extends the base GameBoardPanel to add
 * visualization for the augmented game features.
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class AugmentedGameBoardPanel extends GameBoardPanel {
  
  private final AugmentedReadOnlyPawnsBoard<?, ?> augmentedModel;
  
  // Track necessary values locally to avoid access issues
  private int highlightedRow = -1;
  private int highlightedCol = -1;
  private int cellSize = 100; // Default cell size
  
  /**
   * Constructs an augmented board panel for the Pawns Board game.
   *
   * @param model the read-only augmented game model to display
   * @param colorSchemeManager the color scheme manager to use
   * @throws IllegalArgumentException if model is not an AugmentedReadOnlyPawnsBoard
   */
  public AugmentedGameBoardPanel(ReadOnlyPawnsBoard<?, ?> model, ColorSchemeManager colorSchemeManager) {
    super(model, colorSchemeManager);
    
    // Validate that the model is an AugmentedReadOnlyPawnsBoard
    if (!(model instanceof AugmentedReadOnlyPawnsBoard)) {
      throw new IllegalArgumentException("Model must be an AugmentedReadOnlyPawnsBoard");
    }
    
    this.augmentedModel = (AugmentedReadOnlyPawnsBoard<?, ?>) model;
  }
  
  /**
   * Draws a single cell on the board with value modifiers.
   * Overrides the parent class method to add support for value modifiers.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param xOffset the x offset for the board
   * @param yOffset the y offset for the board
   */
  // Override with no annotation since parent method is likely protected
  protected void drawCell(Graphics2D g2d, int row, int col, int xOffset, int yOffset) {
    // Get the current color scheme
    ColorScheme scheme = getColorSchemeManager().getColorScheme();
    
    // Use our local cellSize
    int x = xOffset + (col + 1) * cellSize; // Add 1 for row score
    int y = yOffset + row * cellSize;
    
    // Create bounds for utility method
    Rectangle bounds = new Rectangle(x, y, cellSize, cellSize);
    
    try {
      // Get cell content and owner information
      CellContent content = augmentedModel.getCellContent(row, col);
      PlayerColors owner = augmentedModel.getCellOwner(row, col);
      
      // Get value modifier for this cell
      int valueModifier = augmentedModel.getCellValueModifier(row, col);
      
      // Use AugmentedDrawingUtils to draw cell background with value modifier
      boolean isHighlighted = (row == highlightedRow && col == highlightedCol);
      AugmentedDrawingUtils.drawCellWithValueModifier(g2d, bounds, isHighlighted, 
          scheme, content, owner, valueModifier);
      
      // Draw additional cell content based on type
      switch (content) {
        case EMPTY:
          // Nothing to draw for empty cells
          break;
        case PAWNS:
          drawPawns(g2d, row, col, x, y, valueModifier);
          break;
        case CARD:
          drawCard(g2d, row, col, x, y, valueModifier);
          break;
        default:
          break;
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * Draws pawns in a cell with value modifier.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param x the x coordinate of the cell
   * @param y the y coordinate of the cell
   * @param valueModifier the value modifier for this cell
   */
  private void drawPawns(Graphics2D g2d, int row, int col, int x, int y, int valueModifier) {
    try {
      PlayerColors owner = augmentedModel.getCellOwner(row, col);
      int count = augmentedModel.getPawnCount(row, col);
      
      // Create bounds for utility method
      Rectangle bounds = new Rectangle(x, y, cellSize, cellSize);
      
      // Use AugmentedDrawingUtils to draw pawns with value modifier
      AugmentedDrawingUtils.drawPawnsWithValueModifier(g2d, bounds, count, owner, 
          getColorSchemeManager().getColorScheme(), valueModifier);
    } catch (IllegalArgumentException | IllegalStateException e) {
      // Cell may be invalid or game not started
    }
  }
  
  /**
   * Draws a card in a cell with value modifier.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param x the x coordinate of the cell
   * @param y the y coordinate of the cell
   * @param valueModifier the value modifier for this cell
   */
  private void drawCard(Graphics2D g2d, int row, int col, int x, int y, int valueModifier) {
    try {
      PlayerColors owner = augmentedModel.getCellOwner(row, col);
      
      // Get the actual card and its original value
      Card card = augmentedModel.getCardAtCell(row, col);
      int originalValue = card != null ? card.getValue() : 0;
      
      // Create bounds for utility method
      Rectangle bounds = new Rectangle(x, y, cellSize, cellSize);
      
      // Use AugmentedDrawingUtils to draw cell card with value modifier
      AugmentedDrawingUtils.drawCellCardWithModifier(g2d, bounds, originalValue, valueModifier,
          owner, getColorSchemeManager().getColorScheme());
    } catch (IllegalArgumentException | IllegalStateException e) {
      // Cell may be invalid or game not started
    }
  }
  
  @Override
  public void highlightCell(int row, int col) {
    // Store highlighted cell coordinates locally
    this.highlightedRow = row;
    this.highlightedCol = col;
    // Call parent method
    super.highlightCell(row, col);
  }
  
  @Override
  public void clearCellHighlights() {
    // Reset our local highlighted cell coordinates
    this.highlightedRow = -1;
    this.highlightedCol = -1;
    // Call parent method
    super.clearCellHighlights();
  }
  
  /**
   * Used internally to determine if we're using normal or high contrast mode.
   * This method is required because we might not be able to access the parent's method.
   *
   * @return the current color scheme manager
   */
  public ColorSchemeManager getColorSchemeManager() {
    return super.getColorSchemeManager(); // May need to be handled differently
  }
}
