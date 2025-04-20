package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.controller.listeners.ModelStatusListener;
import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.Player;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

/**
 * Abstract base controller for the Pawns Board game.
 * Provides common functionality for both human and AI player controllers.
 *
 * @param <C> the type of Card used in the game
 * @param <E> the type of Cell used in the game's board
 */
public abstract class AbstractPawnsBoardController<C extends Card, E extends PawnsBoardCell<C>>
        implements PawnsBoardController, ModelStatusListener {

  protected final PawnsBoard<C, E> model;
  protected final Player<C> player;
  protected final PawnsBoardGUIView view;
  protected boolean isMyTurn;

  /**
   * Creates a new controller for the specified player with the given model and view.
   *
   * @param model  the game model to control
   * @param player the player this controller is responsible for
   * @param view   the view displaying the game for this player
   * @throws IllegalArgumentException if any parameter is null
   */
  public AbstractPawnsBoardController(PawnsBoard<C, E> model, Player<C> player,
                                      PawnsBoardGUIView view) {
    if (model == null || player == null || view == null) {
      throw new IllegalArgumentException("Model, player, or view cannot be null");
    }

    this.model = model;
    this.player = player;
    this.view = view;
  }

  /**
   * Initializes the controller by registering listeners and configuring the view.
   *
   * @param model the read-only game model
   * @param view  the graphical user interface view
   */
  @Override
  public void initialize(ReadOnlyPawnsBoard<?, ?> model, PawnsBoardGUIView view) {
    // Register as a listener for model events
    this.model.addModelStatusListener(this);

    // Set the view title to show which player this controller is for
    updateViewTitle();

    // Check if it's this player's turn
    try {
      isMyTurn = player.isMyTurn(this.model);
      onTurnStatusChanged();
    } catch (IllegalStateException e) {
      // Game may not be started yet
    }

    // Initial refresh of the view
    view.refresh();
  }

  /**
   * Updates the view title based on player and turn status.
   */
  protected void updateViewTitle() {
    String title = "Pawns Board - " + player.getPlayerColor() + " Player";
    if (isMyTurn) {
      title += " - YOUR TURN";
    } else {
      title += " - Waiting for opponent";
    }
    view.setTitle(title);
  }

  /**
   * Called when the turn status changes.
   * Subclasses will override this to provide type-specific behavior.
   */
  protected abstract void onTurnStatusChanged();

  /**
   * Displays a game over message with final scores.
   *
   * @param winner      the winning player, or null for a tie
   * @param finalScores the final scores array
   */
  protected abstract void showGameOverMessage(PlayerColors winner, int[] finalScores);

  /**
   * Displays an error message to the user.
   *
   * @param message the error message text
   */
  protected abstract void showErrorMessage(String message);

  /**
   * Called by the model when the turn changes to a new player.
   *
   * @param newCurrentPlayer the player who now has the turn
   */
  @Override
  public void onTurnChange(PlayerColors newCurrentPlayer) {
    // Check if it's now this player's turn
    boolean wasMyTurn = isMyTurn;  // Store previous state
    isMyTurn = newCurrentPlayer == player.getPlayerColor();

    // Clear selections when it's no longer player's turn
    if (wasMyTurn && !isMyTurn) {
      view.clearSelections();
    }

    updateViewTitle();
    view.refresh();

    // Call turn status changed handler for subclasses
    onTurnStatusChanged();
  }

  /**
   * Called by the model when the game ends.
   *
   * @param winner      the winning player, or null if the game ended in a tie
   * @param finalScores the final scores for RED (index 0) and BLUE (index 1)
   */
  @Override
  public void onGameOver(PlayerColors winner, int[] finalScores) {
    // Notify the player
    player.notifyGameEnd(model, winner == player.getPlayerColor());

    // Update the view
    showGameOverMessage(winner, finalScores);

    // Disable further interactions
    isMyTurn = false;

    view.refresh();
  }

  /**
   * Called by the model when an invalid move is attempted.
   *
   * @param errorMessage a description of why the move was invalid
   */
  @Override
  public void onInvalidMove(String errorMessage) {
    // Only show error message if it's this player's turn
    if (isMyTurn) {
      // For human players, show the error message in a dialog
      // For AI players, the error is already logged in the console
      if (player.isHuman()) {
        showErrorMessage("Invalid move: " + errorMessage);
      }
    }
  }
}