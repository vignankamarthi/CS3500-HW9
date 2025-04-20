package cs3500.pawnsboard.view;

/**
 * Interface for views of the Pawns Board game.
 * Defines methods to render the game state visually.
 */
public interface PawnsBoardView {
  
  /**
   * Renders the current state of the game board.
   *
   * @return a string representation of the board
   */
  String toString();
  
  /**
   * Renders a comprehensive view of the game state including current player,
   * board state, and game results if the game is over.
   *
   * @return a complete representation of the game state
   */
  String renderGameState();
  
  /**
   * Renders the game state with a custom message header.
   * Useful for indicating specific events like game start or player actions.
   *
   * @param headerMessage the message to display as a header
   * @return a string with the header and game state
   */
  String renderGameState(String headerMessage);
  
  /**
   * Sets the visibility of the view.
   * Implementations may use this to show or hide the view as needed.
   *
   * @param visible true to make the view visible, false to hide it
   */
  void setVisible(boolean visible);
  
  /**
   * Refreshes the view to reflect the current state of the model.
   * This should be called whenever the model changes.
   */
  void refresh();
  
  /**
   * Clears any selections or highlights in the view.
   * This is useful when canceling a selection or after a move is completed.
   */
  void clearSelections();
}
