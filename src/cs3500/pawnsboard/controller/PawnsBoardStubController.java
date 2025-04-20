package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

/**
 * A stub implementation of the {@link PawnsBoardController} interface.
 * This class serves as a placeholder controller that logs user interactions
 * to the console without actually modifying the game state.
 *
 * <p>Useful for testing the view and user interaction flow without
 * needing a fully functional controller implementation.</p>
 */
public class PawnsBoardStubController implements
        PawnsBoardController,
        CardSelectionListener,
        CellSelectionListener,
        KeyboardActionListener {

  private ReadOnlyPawnsBoard<?, ?> model;
  private PawnsBoardGUIView view;
  private int selectedCardIndex = -1;
  private int selectedRow = -1;
  private int selectedCol = -1;
  private PlayerColors currentPlayer;

  /**
   * Initializes the stub controller with references to the model and view.
   * Registers itself as a listener for the various event types.
   *
   * @param model the read-only game model
   * @param view  the graphical user interface view
   */
  @Override
  public void initialize(ReadOnlyPawnsBoard<?, ?> model, PawnsBoardGUIView view) {
    this.model = model;
    this.view = view;

    try {
      this.currentPlayer = model.getCurrentPlayer();

      // Show instructions in the console
      displayInstructions();
    } catch (IllegalStateException e) {
      // Game might not be started yet
    }

    // Register as listener for various "subjects" or events
    view.addCardSelectionListener(this);
    view.addCellSelectionListener(this);
    view.addKeyboardActionListener(this);
  }


  //-------------------------------------------------------------------------------------------
  // Listening methods that the controller subscribes to from the listening interfaces
  //-------------------------------------------------------------------------------------------

  /**
   * Handles a card selection event by printing information to the console.
   *
   * @param cardIndex the index of the selected card
   * @param player    the player whose hand contains the selected card
   */
  @Override
  public void onCardSelected(int cardIndex, PlayerColors player) {

    System.out.println("Card " + cardIndex + " selected from " + player + "'s hand");

    // Store the selected card index
    selectedCardIndex = cardIndex;

    // Highlight the selected card in the view
    view.highlightCard(cardIndex);
  }

  /**
   * Handles a cell selection event by printing the coordinates to the console.
   * The cell is a 0 based index with the origin (0, 0) at the top-left.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  @Override
  public void onCellSelected(int row, int col) {
    System.out.println("Cell selected at coordinates: (" + row + ", " + col + ")");

    // Store the selected cell coordinates
    selectedRow = row;
    selectedCol = col;

    // Highlight the selected cell in the view
    view.highlightCell(row, col);
  }

  /**
   * Handles a confirm action event by printing a message to the console.
   * For the stub controller, this simulates placing a card or making a move.
   */
  @Override
  public void onConfirmAction() {
    try {
      // Check if both a card and cell are selected
      if (selectedCardIndex >= 0 && selectedRow >= 0 && selectedCol >= 0) {
        System.out.println("Confirm action requested: Placing card " + selectedCardIndex
                + " at (" + selectedRow + ", " + selectedCol + ")");

        // For the stub, simulate a player change
        PlayerColors previousPlayer = currentPlayer;
        currentPlayer = (currentPlayer == PlayerColors.RED) ?
                PlayerColors.BLUE : PlayerColors.RED;
        System.out.println("Turn switched from " + previousPlayer + " to " + currentPlayer);

        // Update view with the new player
        view.simulatePlayerChange(currentPlayer);

        // Clear selections and refresh the view
        selectedCardIndex = -1;
        selectedRow = -1;
        selectedCol = -1;
        view.clearSelections();
        view.refresh();
      } else {
        System.out.println("Cannot confirm action: Please select both a card and a cell");
      }
    } catch (Exception e) {
      System.out.println("Error processing confirm action: " + e.getMessage());
    }
  }

  /**
   * Handles a pass action event by printing a message to the console.
   * For the stub controller, this simulates passing a turn.
   */
  @Override
  public void onPassAction() {
    System.out.println("Pass action requested");

    try {
      // For the stub, simulate a player change
      PlayerColors previousPlayer = currentPlayer;
      currentPlayer = (currentPlayer == PlayerColors.RED) ?
              PlayerColors.BLUE : PlayerColors.RED;
      System.out.println("Turn passed from " + previousPlayer + " to " + currentPlayer);

      // Update view with the new player
      view.simulatePlayerChange(currentPlayer);

      // Clear selections and refresh the view
      selectedCardIndex = -1;
      selectedRow = -1;
      selectedCol = -1;
      view.clearSelections();
      view.refresh();
    } catch (Exception e) {
      System.out.println("Error processing pass action: " + e.getMessage());
    }
  }


  //-------------------------------------------------------------------------------------------
  // Methods for interpreting user action and updating the model (only stubs in this implementation)
  //-------------------------------------------------------------------------------------------

  /**
   * Implementation of handleCardSelection that just forwards to the listener method.
   *
   * @param cardIndex the index of the selected card
   */
  @Override
  public void handleCardSelection(int cardIndex) {
    try {
      onCardSelected(cardIndex, model.getCurrentPlayer());
    } catch (IllegalStateException e) {
      System.out.println("Error handling card selection: " + e.getMessage());
    }
  }

  /**
   * Implementation of handleCellSelection that just forwards to the listener method.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   */
  @Override
  public void handleCellSelection(int row, int col) {
    onCellSelected(row, col);
  }

  /**
   * Implementation of handleConfirmAction that just forwards to the listener method.
   */
  @Override
  public void handleConfirmAction() {
    onConfirmAction();
  }

  /**
   * Implementation of handlePassAction that just forwards to the listener method.
   */
  @Override
  public void handlePassAction() {
    onPassAction();
  }

  /**
   * Displays instructions for the game in the console.
   */
  private void displayInstructions() {
    System.out.println("\n---- Pawns Board Game Instructions ----");
    System.out.println("Controls:");
    System.out.println("  - Click on a card in your hand to select it");
    System.out.println("  - Click on a cell on the board to select where to place the card");
    System.out.println("  - Press ENTER to confirm your move");
    System.out.println("  - Press 'P' to pass your turn");
    System.out.println("  - Click on a selected card or cell again to deselect it");
    System.out.println("--------------------------------------------------\n");
  }
}
