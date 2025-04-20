package cs3500.pawnsboard.controller;


import javax.swing.SwingUtilities;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.AIPlayer;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

/**
 * Controller implementation for AI players in the Pawns Board game.
 * Automatically selects and executes moves when it's the AI's turn,
 * using the AI's strategy.
 *
 * @param <C> the type of Card used in the game
 * @param <E> the type of Cell used in the game's board
 */
public class AIPawnsBoardController<C extends Card, E extends PawnsBoardCell<C>>
        extends AbstractPawnsBoardController<C, E> {

  /**
   * Creates a new controller for an AI player.
   *
   * @param model  the game model
   * @param player the AI player (must be an AIPlayer)
   * @param view   the GUI view
   * @throws IllegalArgumentException if player is not an AIPlayer
   */
  public AIPawnsBoardController(PawnsBoard<C, E> model, AIPlayer<C> player,
                                PawnsBoardGUIView view) {
    super(model, player, view);
  }

  /**
   * Handles card selection, which is automated for AI players.
   * This method is not used directly since AI moves are handled automatically.
   *
   * @param cardIndex the index of the selected card
   * @throws UnsupportedOperationException always, as AI players don't support manual card selection
   */
  @Override
  public void handleCardSelection(int cardIndex) {
    throw new UnsupportedOperationException("AI players don't support manual card selection");
  }

  /**
   * Handles cell selection, which is automated for AI players.
   * This method is not used directly since AI moves are handled automatically.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   * @throws UnsupportedOperationException always, as AI players don't support manual cell selection
   */
  @Override
  public void handleCellSelection(int row, int col) {
    throw new UnsupportedOperationException("AI players don't support manual cell selection");
  }

  /**
   * Handles a confirmation action, which is automated for AI players.
   * This method is not used directly since AI moves are handled automatically.
   *
   * @throws UnsupportedOperationException always, as AI players don't support manual move
   *                                       confirmation
   */
  @Override
  public void handleConfirmAction() {
    throw new UnsupportedOperationException("AI players don't support manual move confirmation");
  }

  /**
   * Handles a pass action, which is automated for AI players.
   * This method is not used directly since AI moves are handled automatically.
   *
   * @throws UnsupportedOperationException always, as AI players don't support manual passing
   */
  @Override
  public void handlePassAction() {
    throw new UnsupportedOperationException("AI players don't support manual passing");
  }

  /**
   * Initializes the controller by registering listeners and configuring the view.
   * For AI players, it also makes a move immediately if it's their turn.
   *
   * @param model the read-only game model
   * @param view  the graphical user interface view
   */
  @Override
  public void initialize(ReadOnlyPawnsBoard<?, ?> model, PawnsBoardGUIView view) {
    // Call parent initialization
    super.initialize(model, view);

    // If it's the AI's turn at initialization, make a move after a short delay
    if (isMyTurn) {
      scheduleAIMove(1000); // Delay for initial move
    }
  }

  /**
   * Called when the turn status changes.
   * For AI players, this automatically makes a move when it's their turn.
   */
  @Override
  protected void onTurnStatusChanged() {
    if (isMyTurn) {
      // Schedule the AI move with a delay to allow UI to update
      scheduleAIMove(1500); // 1.5-second delay for subsequent moves
    }

    // Force a refresh to ensure UI is up to date
    view.refresh();
  }

  /**
   * Schedules an AI move after a specified delay.
   *
   * @param delayMillis the delay in milliseconds
   */
  private void scheduleAIMove(int delayMillis) {
    // Add a small delay before AI makes its move
    // This makes the game more user-friendly by allowing the human player
    // to see what's happening
    new Thread(() -> {
      try {
        Thread.sleep(delayMillis);
        // Verify it's still AI's turn before making a move
        if (model.getCurrentPlayer() == player.getPlayerColor()) {
          SwingUtilities.invokeLater(() -> {
            makeAIMove();
          });
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        // Log and continue
      }
    }).start();
  }

  /**
   * Makes an AI move using the AI player's strategy.
   */
  protected void makeAIMove() {
    if (!isMyTurn) {
      return;
    }

    try {
      // Have the AI player take its turn
      player.takeTurn(model);

      // Force a view refresh after making a move
      view.refresh();
    } catch (Exception e) {
      // If there's an error, try to pass
      try {
        // If move fails, try to pass instead
        player.passTurn(model);

        // Force a view refresh after passing
        view.refresh();
      } catch (Exception ex) {
        // Log the error but continue
        System.err.println("AI player " + player.getPlayerColor() + " failed to pass: "
                + ex.getMessage());
      }
    }
  }

  /**
   * Shows a game over message.
   * For AI players, this is simplified and only logged to the console during normal play.
   *
   * @param winner      the winning player, or null for a tie
   * @param finalScores the final scores array
   */
  @Override
  protected void showGameOverMessage(PlayerColors winner, int[] finalScores) {
    // For AI, we don't need to display a dialog, but we'll update the view title
    String result;
    if (winner == null) {
      result = "Game ended in a tie!";
    } else if (winner == player.getPlayerColor()) {
      result = "AI won!";
    } else {
      result = "AI lost!";
    }

    // Set the title for the view
    view.setTitle("Pawns Board - " + player.getPlayerColor() + " Player (AI) - " + result);
    view.refresh();
  }

  /**
   * Shows an error message.
   * For AI players, errors are only handled internally.
   *
   * @param message the error message
   */
  @Override
  protected void showErrorMessage(String message) {
    // For AI testing, we just consume errors silently
  }
}