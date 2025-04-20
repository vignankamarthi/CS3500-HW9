package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.player.HumanPlayer;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

import javax.swing.JOptionPane;

/**
 * Controller implementation for human players in the Pawns Board game.
 * Handles user interface interactions, including card and cell selection,
 * move confirmation, and passing.
 *
 * @param <C> the type of Card used in the game
 * @param <E> the type of Cell used in the game's board
 */
public class HumanPawnsBoardController<C extends Card, E extends PawnsBoardCell<C>>
        extends AbstractPawnsBoardController<C, E>
        implements CardSelectionListener, CellSelectionListener, KeyboardActionListener {

  // Selection state
  private int selectedCardIndex = -1;
  private int selectedRow = -1;
  private int selectedCol = -1;

  /**
   * Creates a new controller for a human player.
   *
   * @param model  the game model
   * @param player the human player
   * @param view   the GUI view
   */
  public HumanPawnsBoardController(PawnsBoard<C, E> model, HumanPlayer player,
                                   PawnsBoardGUIView view) {
    super(model, player, view);
  }

  /**
   * Initializes the controller by registering listeners and configuring the view.
   *
   * @param model the read-only game model
   * @param view  the graphical user interface view
   */
  @Override
  public void initialize(ReadOnlyPawnsBoard<?, ?> model, PawnsBoardGUIView view) {
    // Register as a listener for view events
    view.addCardSelectionListener(this);
    view.addCellSelectionListener(this);
    view.addKeyboardActionListener(this);

    // Call parent initialization
    super.initialize(model, view);
  }

  /**
   * Handles changes in turn status.
   * For human players, this means clearing selections when it's not their turn.
   */
  @Override
  protected void onTurnStatusChanged() {
    if (!isMyTurn) {
      // Clear selections when it's not this player's turn
      clearSelections();
    }
    view.refresh();
  }

  /**
   * Clears all current selections in both the controller and view.
   */
  private void clearSelections() {
    selectedCardIndex = -1;
    selectedRow = -1;
    selectedCol = -1;
    view.clearSelections();
  }

  /**
   * Handles a card selection event.
   * This is typically called when a user selects a card in their hand.
   *
   * @param cardIndex the index of the selected card
   */
  @Override
  public void handleCardSelection(int cardIndex) {
    if (!isMyTurn) {
      // Ignore selections when it's not this player's turn
      return;
    }

    // If the same card is selected again, deselect it
    if (cardIndex == selectedCardIndex) {
      selectedCardIndex = -1;
      // Just clear the card highlight, not all selections
      view.highlightCard(-1);
    } else {
      // Update card selection
      selectedCardIndex = cardIndex;
      view.highlightCard(cardIndex);
    }
  }

  /**
   * Handles a cell selection event.
   * This is typically called when a user selects a cell on the game board.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  @Override
  public void handleCellSelection(int row, int col) {
    if (!isMyTurn) {
      // Ignore selections when it's not this player's turn
      return;
    }

    // If the same cell is selected again, deselect it
    if (row == selectedRow && col == selectedCol) {
      selectedRow = -1;
      selectedCol = -1;
      // Just clear the cell highlight, not all selections
      view.highlightCell(-1, -1);
    } else {
      // Update cell selection
      selectedRow = row;
      selectedCol = col;
      view.highlightCell(row, col);
    }
  }

  /**
   * Handles a confirm action event.
   * This is called when a user presses a key to confirm a move.
   */
  @Override
  public void handleConfirmAction() {
    if (!isMyTurn) {
      // Ignore confirmation when it's not this player's turn
      return;
    }

    // Check if both a card and cell have been selected
    if (selectedCardIndex != -1 && selectedRow != -1 && selectedCol != -1) {
      try {
        // Forward the move to the player
        player.placeCard(model, selectedCardIndex, selectedRow, selectedCol);

        // Clear selections after a successful move
        clearSelections();
      } catch (IllegalStateException | IllegalAccessException |
               IllegalOwnerException | IllegalCardException e) {
        // Don't show error message here - we'll rely on the onInvalidMove method
        // to avoid duplicate error messages
      }
    } else {
      // Construct more specific error message
      StringBuilder errorMsg = new StringBuilder("Please select ");
      if (selectedCardIndex == -1) {
        errorMsg.append("a card");
        if (selectedRow == -1 || selectedCol == -1) {
          errorMsg.append(" and a cell");
        }
      } else if (selectedRow == -1 || selectedCol == -1) {
        errorMsg.append("a cell");
      }
      errorMsg.append(" before confirming your move.");
      showErrorMessage(errorMsg.toString());
    }
  }

  /**
   * Handles a pass action event.
   * This is called when a user presses a key to pass their turn.
   */
  @Override
  public void handlePassAction() {
    if (!isMyTurn) {
      // Ignore pass action when it's not this player's turn
      return;
    }

    try {
      // Forward the pass action to the model
      player.passTurn(model);

      // Clear selections after passing
      clearSelections();
    } catch (IllegalStateException | IllegalOwnerException e) {
      // Display error message to the user
      showErrorMessage("Cannot pass turn: " + e.getMessage());
    }
  }

  /**
   * Called by the view when a card is selected from a player's hand.
   *
   * @param cardIndex the index of the selected card in the player's hand (0-based)
   * @param player    the player whose hand contains the selected card
   */
  @Override
  public void onCardSelected(int cardIndex, PlayerColors player) {
    // Only handle selections for this controller's player
    if (player == this.player.getPlayerColor()) {
      handleCardSelection(cardIndex);
    }
  }

  /**
   * Called by the view when a cell is selected on the game board.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  @Override
  public void onCellSelected(int row, int col) {
    handleCellSelection(row, col);
  }

  /**
   * Called by the view when the user performs an action to confirm a move.
   */
  @Override
  public void onConfirmAction() {
    handleConfirmAction();
  }

  /**
   * Called by the view when the user performs an action to pass their turn.
   */
  @Override
  public void onPassAction() {
    handlePassAction();
  }

  /**
   * Shows a game over message dialog.
   *
   * @param winner      the winning player, or null for a tie
   * @param finalScores the final scores array
   */
  @Override
  protected void showGameOverMessage(PlayerColors winner, int[] finalScores) {
    StringBuilder message = new StringBuilder("Game Over!\n");

    if (winner == null) {
      message.append("The game ended in a tie!\n");
    } else {
      message.append(winner).append(" player wins!\n");
    }

    message.append("Final score - RED: ").append(finalScores[0])
            .append(", BLUE: ").append(finalScores[1]);

    JOptionPane.showMessageDialog(null, message.toString(),
            "Game Information", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows an error message dialog to the user.
   *
   * @param message the error message to display
   */
  @Override
  protected void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}