package cs3500.pawnsboard.view;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of PawnsBoardGUIView for testing.
 * This class captures method calls and state for verification.
 * It provides methods to check which operations were performed on the view
 * and what listeners were registered.
 */
public class PawnsBoardGUIViewMock implements PawnsBoardGUIView {
  private boolean visible = false;
  private int highlightedCardIndex = -1;
  private int highlightedRow = -1;
  private int highlightedCol = -1;
  private PlayerColors simulatedPlayer = null;
  private boolean refreshed = false;
  private boolean selectionsCleared = false;
  private String title = "";
  private int positionX = 0;
  private int positionY = 0;
  private final ColorSchemeManager colorSchemeManager;

  private final List<CardSelectionListener> cardListeners = new ArrayList<>();
  private final List<CellSelectionListener> cellListeners = new ArrayList<>();
  private final List<KeyboardActionListener> keyListeners = new ArrayList<>();

  /**
   * Constructs a new PawnsBoardGUIViewMock.
   */
  public PawnsBoardGUIViewMock() {
    // Create a view-specific color scheme manager
    this.colorSchemeManager = new ColorSchemeManager();
  }

  /**
   * Gets the color scheme manager for this view.
   *
   * @return the color scheme manager
   */
  public ColorSchemeManager getColorSchemeManager() {
    return colorSchemeManager;
  }

  /**
   * Highlights a card in the current player's hand.
   * Records the index of the highlighted card for testing verification.
   *
   * @param cardIndex the index of the card to highlight (0-based)
   */
  @Override
  public void highlightCard(int cardIndex) {
    this.highlightedCardIndex = cardIndex;
  }

  /**
   * Highlights a specific cell on the game board.
   * Records the row and column of the highlighted cell for testing verification.
   *
   * @param row the row index of the cell to highlight
   * @param col the column index of the cell to highlight
   */
  @Override
  public void highlightCell(int row, int col) {
    this.highlightedRow = row;
    this.highlightedCol = col;
  }

  /**
   * Registers a listener for card selection events.
   * Adds the listener to the list of card selection listeners for testing verification.
   *
   * @param listener the card selection listener to register
   */
  @Override
  public void addCardSelectionListener(CardSelectionListener listener) {
    cardListeners.add(listener);
  }

  /**
   * Registers a listener for cell selection events.
   * Adds the listener to the list of cell selection listeners for testing verification.
   *
   * @param listener the cell selection listener to register
   */
  @Override
  public void addCellSelectionListener(CellSelectionListener listener) {
    cellListeners.add(listener);
  }

  /**
   * Registers a listener for keyboard action events.
   * Adds the listener to the list of keyboard action listeners for testing verification.
   *
   * @param listener the keyboard action listener to register
   */
  @Override
  public void addKeyboardActionListener(KeyboardActionListener listener) {
    keyListeners.add(listener);
  }

  /**
   * Sets the title of the view window.
   * This implementation is a no-op for testing.
   *
   * @param title the title to set
   */
  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Sets the size of the view window.
   * This implementation is a no-op for testing.
   *
   * @param width  the width of the window in pixels
   * @param height the height of the window in pixels
   */
  @Override
  public void setSize(int width, int height) {
    // Not needed for testing
  }

  /**
   * Sets the position of the view window on the screen.
   *
   * @param x the x-coordinate of the window's top-left corner
   * @param y the y-coordinate of the window's top-left corner
   */
  @Override
  public void setPosition(int x, int y) {
    this.positionX = x;
    this.positionY = y;
  }

  /**
   * Simulates a player change in the view.
   * Records the new player for testing verification.
   *
   * @param player the player to set as current
   */
  @Override
  public void simulatePlayerChange(PlayerColors player) {
    this.simulatedPlayer = player;
  }

  /**
   * Returns a string representation of the mock view.
   *
   * @return a string representation
   */
  @Override
  public String toString() {
    return "Mock View";
  }

  /**
   * Renders the current state of the game.
   * Returns a fixed string for testing.
   *
   * @return a mock game state string
   */
  @Override
  public String renderGameState() {
    return "Mock Game State";
  }

  /**
   * Renders the game state with a custom issuage header.
   * Returns a fixed string with the provided header for testing.
   *
   * @param headerMessage the message to display as a header
   * @return a mock game state string with header
   */
  @Override
  public String renderGameState(String headerMessage) {
    return "--- " + headerMessage + " ---\nMock Game State";
  }

  /**
   * Sets the visibility of the view.
   * Records the visibility state for testing verification.
   *
   * @param visible true to make the view visible, false to hide it
   */
  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Refreshes the view to reflect the current state of the model.
   * Records that a refresh was requested for testing verification.
   */
  @Override
  public void refresh() {
    this.refreshed = true;
  }

  /**
   * Clears any selections or highlights in the view.
   * Resets highlighted card and cell indices and records that
   * selections were cleared for testing verification.
   */
  @Override
  public void clearSelections() {
    this.highlightedCardIndex = -1;
    this.highlightedRow = -1;
    this.highlightedCol = -1;
    this.selectionsCleared = true;
  }

  /**
   * Checks if a card selection listener has been registered.
   *
   * @param listener the listener to check for
   * @return true if the listener is registered, false otherwise
   */
  public boolean hasCardSelectionListener(CardSelectionListener listener) {
    return cardListeners.contains(listener);
  }

  /**
   * Checks if a cell selection listener has been registered.
   *
   * @param listener the listener to check for
   * @return true if the listener is registered, false otherwise
   */
  public boolean hasCellSelectionListener(CellSelectionListener listener) {
    return cellListeners.contains(listener);
  }

  /**
   * Checks if a keyboard action listener has been registered.
   *
   * @param listener the listener to check for
   * @return true if the listener is registered, false otherwise
   */
  public boolean hasKeyboardActionListener(KeyboardActionListener listener) {
    return keyListeners.contains(listener);
  }

  /**
   * Gets the index of the highlighted card.
   *
   * @return the index of the highlighted card, or -1 if no card is highlighted
   */
  public int getHighlightedCardIndex() {
    return highlightedCardIndex;
  }

  /**
   * Gets the row index of the highlighted cell.
   *
   * @return the row index of the highlighted cell, or -1 if no cell is highlighted
   */
  public int getHighlightedRow() {
    return highlightedRow;
  }

  /**
   * Gets the column index of the highlighted cell.
   *
   * @return the column index of the highlighted cell, or -1 if no cell is highlighted
   */
  public int getHighlightedCol() {
    return highlightedCol;
  }

  /**
   * Gets the player that was set by simulatePlayerChange.
   *
   * @return the simulated player, or null if no player was simulated
   */
  public PlayerColors getSimulatedPlayer() {
    return simulatedPlayer;
  }

  /**
   * Gets the current title of the view.
   *
   * @return the current title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the X position of the view.
   *
   * @return the X coordinate
   */
  public int getPositionX() {
    return positionX;
  }

  /**
   * Gets the Y position of the view.
   *
   * @return the Y coordinate
   */
  public int getPositionY() {
    return positionY;
  }

  /**
   * Checks if the refresh method was called.
   *
   * @return true if refresh was called, false otherwise
   */
  public boolean wasRefreshed() {
    return refreshed;
  }

  /**
   * Checks if the clearSelections method was called.
   *
   * @return true if clearSelections was called, false otherwise
   */
  public boolean wereSelectionsCleared() {
    return selectionsCleared;
  }

  /**
   * Checks if the view is currently set to visible.
   *
   * @return true if the view is visible, false otherwise
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Checks if high contrast mode is enabled.
   *
   * @return true if high contrast mode is enabled, false otherwise
   */
  public boolean isHighContrastMode() {
    return "high_contrast".equals(colorSchemeManager.getCurrentSchemeName());
  }

  /**
   * Sets the color scheme mode.
   *
   * @param schemeName the name of the scheme to set
   */
  public void setColorScheme(String schemeName) {
    colorSchemeManager.setColorScheme(schemeName);
  }

  /**
   * Toggles the color scheme.
   */
  public void toggleColorScheme() {
    String currentScheme = colorSchemeManager.getCurrentSchemeName();
    String[] availableSchemes = colorSchemeManager.getAvailableSchemeNames();
    
    // Find current scheme index
    int currentIndex = -1;
    for (int i = 0; i < availableSchemes.length; i++) {
      if (availableSchemes[i].equals(currentScheme)) {
        currentIndex = i;
        break;
      }
    }
    
    // Move to next scheme (circular)
    int nextIndex = (currentIndex + 1) % availableSchemes.length;
    colorSchemeManager.setColorScheme(availableSchemes[nextIndex]);
  }

  /**
   * Gets the current color scheme name.
   *
   * @return the current color scheme name
   */
  public String getCurrentColorScheme() {
    return colorSchemeManager.getCurrentSchemeName();
  }
}
