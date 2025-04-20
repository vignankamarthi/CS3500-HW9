package cs3500.pawnsboard.controller.listeners;

import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Mock implementation of ModelStatusListener for testing.
 * Records events and provides methods to check received notifications.
 */
public class ModelListenerMock implements ModelStatusListener {
  private boolean turnChangeReceived;
  private boolean gameOverReceived;
  private boolean invalidMoveReceived;
  private PlayerColors lastTurnPlayer;
  private PlayerColors lastWinner;
  private int[] lastFinalScores;
  private String lastErrorMessage;

  /**
   * Callback method invoked when the game turn changes to a new player.
   * Updates the internal tracking variables to indicate a turn change has occurred
   * and records the player who is now current.
   *
   * @param newCurrentPlayer the {@link PlayerColors} representing the player
   *                         whose turn has just become active
   */
  @Override
  public void onTurnChange(PlayerColors newCurrentPlayer) {
    this.turnChangeReceived = true;
    this.lastTurnPlayer = newCurrentPlayer;
  }

  /**
   * Callback method invoked when the game reaches its conclusion.
   * Updates the internal tracking variables to indicate the game has ended,
   * records the winning player, and stores the final scores.
   *
   * @param winner      the {@link PlayerColors} representing the winning player,
   *                    or null if the game ended in a tie
   * @param finalScores an array containing the final scores for both players,
   *                    where index 0 represents RED's score and index 1 represents BLUE's score
   */
  @Override
  public void onGameOver(PlayerColors winner, int[] finalScores) {
    this.gameOverReceived = true;
    this.lastWinner = winner;
    this.lastFinalScores = finalScores;
  }

  /**
   * Callback method invoked when an invalid move is attempted during the game.
   * Updates the internal tracking variables to indicate an invalid move occurred
   * and stores the specific error message describing the reason for invalidity.
   *
   * @param errorMessage a {@link String} describing why the move was considered invalid
   */
  @Override
  public void onInvalidMove(String errorMessage) {
    this.invalidMoveReceived = true;
    this.lastErrorMessage = errorMessage;
  }

  /**
   * Reset all tracking variables.
   */
  public void reset() {
    turnChangeReceived = false;
    gameOverReceived = false;
    invalidMoveReceived = false;
    lastTurnPlayer = null;
    lastWinner = null;
    lastFinalScores = null;
    lastErrorMessage = null;
  }

  /**
   * Checks if a turn change notification was received.
   *
   * @return true if a turn change was received
   */
  public boolean wasTurnChangeReceived() {
    return turnChangeReceived;
  }


  /**
   * Checks if a game over notification was received.
   *
   * @return true if a game over notification was received
   */
  public boolean wasGameOverReceived() {
    return gameOverReceived;
  }

  /**
   * Checks if an invalid move notification was received.
   *
   * @return true if an invalid move notification was received
   */
  public boolean wasInvalidMoveReceived() {
    return invalidMoveReceived;
  }

  /**
   * Gets the last player received in a turn change notification.
   *
   * @return the player whose turn it is now
   */
  public PlayerColors getLastTurnPlayer() {
    return lastTurnPlayer;
  }

  /**
   * Gets the winner from the last game over notification.
   *
   * @return the winning player, or null if the game was a tie
   */
  public PlayerColors getLastWinner() {
    return lastWinner;
  }

  /**
   * Gets the final scores from the last game over notification.
   *
   * @return the final scores array, where index 0 is RED's score and index 1 is BLUE's
   */
  public int[] getLastFinalScores() {
    return lastFinalScores;
  }

  /**
   * Gets the error message from the last invalid move notification.
   *
   * @return the error message
   */
  public String getLastErrorMessage() {
    return lastErrorMessage;
  }

  /**
   * Gets the turn change received variable.
   *
   * @return the turn change boolean flag
   */
  public boolean getTurnChangeReceived() {
    return turnChangeReceived;
  }
}