package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

/**
 * The main controller interface for the Pawns Board game.
 * This interface defines the contract for a controller that mediates
 * between the game model and view, handling user interactions and
 * updating the model accordingly.
 * 
 * <p>The controller is responsible for translating user actions in the view
 * into operations on the model, and ensuring the view reflects the current 
 * state of the model.</p>
 */
public interface PawnsBoardController {
  
  /**
   * Initializes the controller with references to the model and view.
   * This method should set up all necessary event listeners and prepare
   * the controller to handle user interactions.
   *
   * @param model the read-only game model
   * @param view the graphical user interface view
   */
  void initialize(ReadOnlyPawnsBoard<?, ?> model, PawnsBoardGUIView view);
  
  /**
   * Handles a card selection event.
   * This is typically called when a user selects a card in their hand.
   *
   * @param cardIndex the index of the selected card
   */
  void handleCardSelection(int cardIndex);
  
  /**
   * Handles a cell selection event.
   * This is typically called when a user selects a cell on the game board.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  void handleCellSelection(int row, int col);
  
  /**
   * Handles a confirm action event.
   * This is typically called when a user presses a key to confirm a move.
   */
  void handleConfirmAction();
  
  /**
   * Handles a pass action event.
   * This is typically called when a user presses a key to pass their turn.
   */
  void handlePassAction();
}
