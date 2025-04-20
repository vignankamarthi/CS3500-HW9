package cs3500.pawnsboard.controller.listeners;

/**
 * An event listener for keyboard actions in the Pawns Board game.
 * This interface is part of the observer pattern implementation where
 * the view notifies listeners when keyboard actions like confirming a move
 * or passing are performed.
 */
public interface KeyboardActionListener {
  
  /**
   * Called when the user performs an action to confirm a move.
   * This could be triggered by pressing a specific key like Enter or Space.
   */
  void onConfirmAction();
  
  /**
   * Called when the user performs an action to pass their turn.
   * This could be triggered by pressing a specific key like 'P'.
   */
  void onPassAction();
}
