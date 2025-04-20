package cs3500.pawnsboard.view.guicomponents;

import javax.swing.JPanel;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;

/**
 * Interface for a panel that displays the Pawns Board game board.
 * This panel is responsible for rendering the game grid with cells,
 * pawns, cards, and row scores.
 */
public interface BoardPanel {
  
  /**
   * Renders the current state of the game board.
   * This should update the visual representation based on the model.
   */
  void renderBoard();
  
  /**
   * Highlights a specific cell on the game board.
   * The panel should visually indicate which cell is selected.
   *
   * @param row the row index of the cell to highlight
   * @param col the column index of the cell to highlight
   */
  void highlightCell(int row, int col);
  
  /**
   * Clears any cell highlights on the board.
   * This should reset all cells to their normal appearance.
   */
  void clearCellHighlights();
  
  /**
   * Registers a listener for cell selection events.
   * The listener will be notified when a cell is selected on the board.
   *
   * @param listener the cell selection listener to register
   */
  void addCellSelectionListener(CellSelectionListener listener);
  
  /**
   * Gets the underlying Swing panel.
   * This is necessary for adding the panel to a container.
   *
   * @return the JPanel representing this board panel
   */
  JPanel getPanel();
}
