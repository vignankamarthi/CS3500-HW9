package cs3500.pawnsboard.view.guicomponents;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorScheme;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * A panel that displays the Pawns Board game board.
 * This panel is responsible for rendering the game grid with cells,
 * pawns, cards, and row scores.
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class GameBoardPanel implements BoardPanel {
  
  private final ReadOnlyPawnsBoard<?, ?> model;
  private final ColorSchemeManager colorSchemeManager;
  private final JPanel panel;
  private final List<CellSelectionListener> listeners;
  
  private int highlightedRow = -1;
  private int highlightedCol = -1;
  private int cellSize = 100; // Increased default cell size
  
  /**
   * Constructs a board panel for the Pawns Board game.
   *
   * @param model the read-only game model to display
   * @param colorSchemeManager the color scheme manager to use
   */
  public GameBoardPanel(ReadOnlyPawnsBoard<?, ?> model, ColorSchemeManager colorSchemeManager) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (colorSchemeManager == null) {
      throw new IllegalArgumentException("ColorSchemeManager cannot be null");
    }
    
    this.model = model;
    this.colorSchemeManager = colorSchemeManager;
    this.listeners = new ArrayList<>();
    
    // Create the actual Swing panel
    panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard((Graphics2D) g);
      }
    };
    
    // Set panel properties - use the background color from the color scheme
    updatePanelProperties();
    
    // Add mouse listener for cell selection
    setupMouseListener();
  }
  
  /**
   * Updates the panel properties based on the current color scheme.
   */
  private void updatePanelProperties() {
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    panel.setBackground(scheme.getBackgroundColor());
    panel.setPreferredSize(new Dimension(600, 450)); // Increased board size
  }
  
  /**
   * Sets up the mouse listener for cell selection.
   */
  private void setupMouseListener() {
    panel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        try {
          int[] dims = model.getBoardDimensions();
          
          // Calculate cell size based on panel dimensions
          int width = panel.getWidth();
          int height = panel.getHeight();
          int rows = dims[0];
          int cols = dims[1];
          
          // Use exact measurements for better precision
          double cellWidth = (width * 0.8) / (cols + 2); // Add 2 for row score columns
          double cellHeight = (height * 0.8) / rows;
          cellSize = (int)Math.min(cellWidth, cellHeight);
          
          // Calculate board offset to center it
          int boardWidth = cellSize * (cols + 2);
          int boardHeight = cellSize * rows;
          int xOffset = (width - boardWidth) / 2;
          int yOffset = (height - boardHeight) / 2;
          
          // First determine if we're in the board area at all
          boolean inBoardArea = e.getX() >= xOffset && e.getX() < xOffset + boardWidth &&
                               e.getY() >= yOffset && e.getY() < yOffset + boardHeight;
          
          if (!inBoardArea) {
            return; // Click is outside the board area completely
          }
          
          // Calculate cell coordinates from mouse position
          int col = (e.getX() - xOffset) / cellSize - 1; // Subtract 1 for row score
          int row = (e.getY() - yOffset) / cellSize;
          
          // Skip row score cells (leftmost and rightmost columns)
          if (col < 0 || col >= cols) {
            return; // Clicked on a row score cell
          }
          
          // Calculate the exact cell boundaries with 2 pixel buffer for better precision
          int cellX = xOffset + (col + 1) * cellSize;
          int cellY = yOffset + row * cellSize;
          int buffer = 2; // Small buffer to ensure clicks near the edge work properly
          
          // Check if the click is within the cell boundaries (with buffer)
          boolean isWithinCell = e.getX() >= cellX + buffer && e.getX() <= cellX + cellSize - buffer
                  && e.getY() >= cellY + buffer && e.getY() <= cellY + cellSize - buffer;
          
          // Check if within valid range and within cell boundaries
          if (isWithinCell && row >= 0 && row < rows && col >= 0 && col < cols) {
            // Toggle highlight if clicking on the same cell
            if (row == highlightedRow && col == highlightedCol) {
              clearCellHighlights();
            } else {
              highlightCell(row, col);
              notifyListeners(row, col);
            }
          }
        } catch (IllegalStateException ex) {
          throw new IllegalStateException(ex);
        }
      }
    });
  }
  
  /**
   * Draws the game board.
   *
   * @param g2d the graphics context
   */
  private void drawBoard(Graphics2D g2d) {
    try {
      int[] dims = model.getBoardDimensions();
      int rows = dims[0];
      int cols = dims[1];
      
      // Calculate cell size based on panel dimensions
      int width = panel.getWidth();
      int height = panel.getHeight();
      
      // Use exact measurements for better precision - same as in mouse listener
      double cellWidth = (width * 0.8) / (cols + 2);
      double cellHeight = (height * 0.8) / rows;
      cellSize = (int)Math.min(cellWidth, cellHeight);
      
      // Calculate board offset to center it
      int boardWidth = cellSize * (cols + 2); // Add 2 for row scores
      int boardHeight = cellSize * rows;
      int xOffset = (width - boardWidth) / 2;
      int yOffset = (height - boardHeight) / 2;
      
      // Draw cells and contents
      for (int r = 0; r < rows; r++) {
        // Draw row scores
        drawRowScore(g2d, r, xOffset, yOffset);
        
        for (int c = 0; c < cols; c++) {
          // Draw cell
          drawCell(g2d, r, c, xOffset, yOffset);
        }
      }
    } catch (IllegalStateException e) {
      // Game may not be started yet
      drawUnstartedGameMessage(g2d);
    }
  }
  
  /**
   * Draws a single cell on the board.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param xOffset the x offset for the board
   * @param yOffset the y offset for the board
   */
  private void drawCell(Graphics2D g2d, int row, int col, int xOffset, int yOffset) {
    // Get the current color scheme
    ColorScheme scheme = colorSchemeManager.getColorScheme();
    
    // Calculate cell position
    int x = xOffset + (col + 1) * cellSize; // Add 1 for row score
    int y = yOffset + row * cellSize;
    
    // Create bounds for utility method
    java.awt.Rectangle bounds = new java.awt.Rectangle(x, y, cellSize, cellSize);
    
    try {
      // Get cell content and owner information
      CellContent content = model.getCellContent(row, col);
      PlayerColors owner = model.getCellOwner(row, col);
      
      // Use DrawingUtils to draw cell background with content and owner information
      DrawingUtils.drawCellBackground(g2d, bounds, row == highlightedRow && col == highlightedCol, 
                                    scheme, content, owner);
      
      // Draw additional cell content based on type
      switch (content) {
        case EMPTY:
          // Nothing to draw for empty cells
          break;
        case PAWNS:
          drawPawns(g2d, row, col, x, y);
          break;
        case CARD:
          drawCard(g2d, row, col, x, y);
          break;
        default:
          break;
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * Draws pawns in a cell.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param x the x coordinate of the cell
   * @param y the y coordinate of the cell
   */
  private void drawPawns(Graphics2D g2d, int row, int col, int x, int y) {
    try {
      PlayerColors owner = model.getCellOwner(row, col);
      int count = model.getPawnCount(row, col);
      
      // Create bounds for utility method
      java.awt.Rectangle bounds = new java.awt.Rectangle(x, y, cellSize, cellSize);
      
      // Use DrawingUtils to draw pawns
      DrawingUtils.drawPawns(g2d, bounds, count, owner, colorSchemeManager.getColorScheme());
    } catch (IllegalArgumentException | IllegalStateException e) {
      // Cell may be invalid or game not started
    }
  }
  
  /**
   * Draws a card in a cell.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param col the column index
   * @param x the x coordinate of the cell
   * @param y the y coordinate of the cell
   */
  private void drawCard(Graphics2D g2d, int row, int col, int x, int y) {
    try {
      PlayerColors owner = model.getCellOwner(row, col);
      
      // Get the actual card and its value
      Card card = model.getCardAtCell(row, col);
      int value = card != null ? card.getValue() : 0;
      
      // Create bounds for utility method
      java.awt.Rectangle bounds = new java.awt.Rectangle(x, y, cellSize, cellSize);
      
      // Use DrawingUtils to draw cell card
      DrawingUtils.drawCellCard(g2d, bounds, value, owner, colorSchemeManager.getColorScheme());
    } catch (IllegalArgumentException | IllegalStateException e) {
      // Cell may be invalid or game not started
    }
  }
  
  /**
   * Draws row scores at the beginning and end of each row.
   *
   * @param g2d the graphics context
   * @param row the row index
   * @param xOffset the x offset for the board
   * @param yOffset the y offset for the board
   */
  private void drawRowScore(Graphics2D g2d, int row, int xOffset, int yOffset) {
    try {
      int[] rowScores = model.getRowScores(row);
      int redScore = rowScores[0];
      int blueScore = rowScores[1];
      int cols = model.getBoardDimensions()[1];
      
      // Calculate positions
      int redX = xOffset;
      int blueX = xOffset + (cols + 1) * cellSize;
      int y = yOffset + row * cellSize;
      
      // Create bounds for utility methods
      java.awt.Rectangle redBounds = new java.awt.Rectangle(redX, y, cellSize, cellSize);
      java.awt.Rectangle blueBounds = new java.awt.Rectangle(blueX, y, cellSize, cellSize);
      
      // Use DrawingUtils to draw scores
      DrawingUtils.drawScore(g2d, redBounds, redScore, PlayerColors.RED, 
              colorSchemeManager.getColorScheme());
      DrawingUtils.drawScore(g2d, blueBounds, blueScore, PlayerColors.BLUE, 
              colorSchemeManager.getColorScheme());
    } catch (IllegalArgumentException | IllegalStateException e) {
      // Row may be invalid or game not started
    }
  }
  
  /**
   * Draws a message when the game has not been started.
   *
   * @param g2d the graphics context
   */
  private void drawUnstartedGameMessage(Graphics2D g2d) {
    // Set background to match the color scheme
    g2d.setColor(colorSchemeManager.getColorScheme().getBackgroundColor());
    g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
    
    // Draw the message
    g2d.setColor(Color.WHITE);
    String message = "Game has not been started";
    int width = g2d.getFontMetrics().stringWidth(message);
    g2d.drawString(message, 
            (panel.getWidth() - width) / 2, 
            panel.getHeight() / 2);
  }
  
  /**
   * Notifies all registered listeners of a cell selection.
   *
   * @param row the row index
   * @param col the column index
   */
  private void notifyListeners(int row, int col) {
    for (CellSelectionListener listener : listeners) {
      listener.onCellSelected(row, col);
    }
  }
  
  // BoardPanel implementation
  
  @Override
  public void renderBoard() {
    // Update the panel properties to reflect any color scheme changes
    updatePanelProperties();
    
    if (panel != null) {
      panel.repaint();
    }
  }
  
  @Override
  public void highlightCell(int row, int col) {
    this.highlightedRow = row;
    this.highlightedCol = col;
    renderBoard();
  }
  
  @Override
  public void clearCellHighlights() {
    this.highlightedRow = -1;
    this.highlightedCol = -1;
    renderBoard();
  }
  
  @Override
  public void addCellSelectionListener(CellSelectionListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }
  
  @Override
  public JPanel getPanel() {
    return panel;
  }
}
