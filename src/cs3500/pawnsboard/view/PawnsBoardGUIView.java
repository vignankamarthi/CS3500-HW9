package cs3500.pawnsboard.view;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Interface for a graphical view of the Pawns Board game using Java Swing.
 * This interface extends the base {@link PawnsBoardView} with methods specific to
 * a graphical user interface, including component highlighting and event listener registration.
 * 
 * <p>Implementations of this interface should manage the visualization of the game board,
 * player cards, and game state while allowing user interaction through mouse
 * and keyboard input.</p>
 */
public interface PawnsBoardGUIView extends PawnsBoardView {
  
  /**
   * Highlights a card in the current player's hand.
   * The view should visually indicate which card is selected.
   *
   * @param cardIndex the index of the card to highlight (0-based)
   */
  void highlightCard(int cardIndex);
  
  /**
   * Highlights a specific cell on the game board.
   * The view should visually indicate which cell is selected.
   * The cell is a 0 based index with the origin (0, 0) at the top-left.
   *
   * @param row the row index of the cell to highlight
   * @param col the column index of the cell to highlight
   */
  void highlightCell(int row, int col);
  
  /**
   * Registers a listener for card selection events.
   * The listener will be notified when a user selects a card in their hand.
   *
   * @param listener the card selection listener to register
   */
  void addCardSelectionListener(CardSelectionListener listener);
  
  /**
   * Registers a listener for cell selection events.
   * The listener will be notified when a user selects a cell on the game board.
   *
   * @param listener the cell selection listener to register
   */
  void addCellSelectionListener(CellSelectionListener listener);
  
  /**
   * Registers a listener for keyboard action events.
   * The listener will be notified when a user performs a keyboard action
   * like confirming a move or passing.
   *
   * @param listener the keyboard action listener to register
   */
  void addKeyboardActionListener(KeyboardActionListener listener);
  
  /**
   * Sets the title of the view window.
   *
   * @param title the title to set
   */
  void setTitle(String title);
  
  /**
   * Sets the size of the view window.
   *
   * @param width the width of the window in pixels
   * @param height the height of the window in pixels
   */
  void setSize(int width, int height);
  
  /**
   * Manually sets the current player for the view.
   * This is used for simulating player turn changes.
   * 
   * @param player the player to set as current
   */
  void simulatePlayerChange(PlayerColors player);
  
  /**
   * Sets the position of the view window on the screen.
   *
   * @param x the x-coordinate of the window's top-left corner
   * @param y the y-coordinate of the window's top-left corner
   */
  void setPosition(int x, int y);
}