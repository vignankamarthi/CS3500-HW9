package cs3500.pawnsboard.controller.listeners;

/**
 * An event listener for cell selection events in the Pawns Board game.
 * This interface is part of the observer pattern implementation where
 * the view notifies listeners when a cell is selected on the game board.
 */
public interface CellSelectionListener {
  
  /**
   * Called when a cell is selected on the game board.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  void onCellSelected(int row, int col);
}
