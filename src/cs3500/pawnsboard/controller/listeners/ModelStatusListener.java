package cs3500.pawnsboard.controller.listeners;

import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * An event listener for model status events in the Pawns Board game.
 * This interface is part of the observer pattern implementation where
 * the model notifies listeners about game state changes like turn changes,
 * game endings, and invalid moves.
 */
public interface ModelStatusListener {
  
  /**
   * Called when the turn changes to a new player.
   *
   * @param newCurrentPlayer the player who now has the turn
   */
  void onTurnChange(PlayerColors newCurrentPlayer);
  
  /**
   * Called when the game ends.
   *
   * @param winner the winning player, or null if the game ended in a tie
   * @param finalScores the final scores for RED (index 0) and BLUE (index 1)
   */
  void onGameOver(PlayerColors winner, int[] finalScores);
  
  /**
   * Called when a player attempts an invalid move.
   *
   * @param errorMessage a description of why the move was invalid
   */
  void onInvalidMove(String errorMessage);
}
