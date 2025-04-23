package cs3500.pawnsboard.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;
import cs3500.pawnsboard.view.guicomponents.AugmentedGameBoardPanel;
import cs3500.pawnsboard.view.guicomponents.AugmentedCardHandPanel;

/**
 * An augmented graphical implementation of the Pawns Board game view using Java Swing.
 * This class extends the functionality of the base graphical view to support
 * value modifiers and different influence types.
 */
public class PawnsBoardAugmentedGraphicalView extends JFrame implements PawnsBoardAugmentedGUIView {

  private final AugmentedReadOnlyPawnsBoard<?, ?> augmentedModel;
  private final ColorSchemeManager colorSchemeManager;
  private final AugmentedGameBoardPanel boardPanel;
  private final AugmentedCardHandPanel handPanel;
  private final JLabel statusLabel;
  private final JComboBox<String> colorSchemeComboBox;

  private final List<CardSelectionListener> cardListeners;
  private final List<CellSelectionListener> cellListeners;
  private final List<KeyboardActionListener> keyListeners;

  // Track which player this view represents
  private PlayerColors viewPlayer;
  
  // Flag for showing influence types
  private boolean showInfluenceTypes;
  
  // Available color schemes
  private String[] availableSchemes;

  /**
   * Constructs an augmented graphical view for the Pawns Board game.
   *
   * @param model the augmented read-only game model to display
   * @throws IllegalArgumentException if model is not an AugmentedReadOnlyPawnsBoard
   */
  public PawnsBoardAugmentedGraphicalView(ReadOnlyPawnsBoard<?, ?> model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (!(model instanceof AugmentedReadOnlyPawnsBoard)) {
      throw new IllegalArgumentException("Model must be an AugmentedReadOnlyPawnsBoard");
    }

    this.augmentedModel = (AugmentedReadOnlyPawnsBoard<?, ?>) model;
    this.cardListeners = new ArrayList<>();
    this.cellListeners = new ArrayList<>();
    this.keyListeners = new ArrayList<>();
    this.showInfluenceTypes = true; // Enable by default
    
    // Create a view-specific color scheme manager
    this.colorSchemeManager = new ColorSchemeManager();
    this.availableSchemes = colorSchemeManager.getAvailableSchemeNames();

    // Create panels with this view's color scheme manager
    this.boardPanel = new AugmentedGameBoardPanel(model, colorSchemeManager);
    this.handPanel = new AugmentedCardHandPanel(model, colorSchemeManager);
    JPanel infoPanel = new JPanel();

    // Create dropdown for color schemes
    this.colorSchemeComboBox = new JComboBox<>(availableSchemes);
    colorSchemeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedScheme = (String) colorSchemeComboBox.getSelectedItem();
        if (selectedScheme != null) {
          setColorScheme(selectedScheme);
        }
      }
    });

    // Initial setup
    this.statusLabel = new JLabel("Current Player: ", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

    setupLayout();
    setupKeyboardListeners();
    updateStatusLabel();

    // Set default properties
    setTitle("Augmented Pawns Board Game");
    setSize(1200, 900); // Increased window size for larger board
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  /**
   * Sets the color scheme by name.
   *
   * @param schemeName the name of the color scheme to use
   * @throws IllegalArgumentException if scheme name is null, empty, or not in available schemes
   */
  public void setColorScheme(String schemeName) {
    if (schemeName == null || schemeName.isEmpty() || !schemeExists(schemeName)) {
      throw new IllegalArgumentException("Scheme name cannot be null or empty and must be one of: " 
              + String.join(", ", availableSchemes));
    }
    
    // Set the new scheme
    colorSchemeManager.setColorScheme(schemeName);
    
    // Update the combo box selection if it doesn't match
    if (!schemeName.equals(colorSchemeComboBox.getSelectedItem())) {
      colorSchemeComboBox.setSelectedItem(schemeName);
    }
    
    // Refresh the view
    refresh();
  }
  
  /**
   * Checks if a scheme name exists in the available schemes.
   *
   * @param schemeName the name to check
   * @return true if the scheme exists, false otherwise
   */
  private boolean schemeExists(String schemeName) {
    for (String scheme : availableSchemes) {
      if (scheme.equals(schemeName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets up the layout of the frame.
   * This method arranges the panels in the frame using BorderLayout.
   */
  private void setupLayout() {
    // Main layout
    setLayout(new BorderLayout());

    // Create a panel for the info and toggle button
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(Color.LIGHT_GRAY);
    topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Add status label to center of top panel
    topPanel.add(statusLabel, BorderLayout.CENTER);
    
    // Create a panel for the toggle button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.LIGHT_GRAY);
    buttonPanel.add(colorSchemeComboBox);
    
    // Add button panel to right side of top panel
    topPanel.add(buttonPanel, BorderLayout.EAST);
    
    // Add panels to the frame
    add(topPanel, BorderLayout.NORTH);
    add(boardPanel.getPanel(), BorderLayout.CENTER);
    add(handPanel.getPanel(), BorderLayout.SOUTH);

    // Set panel sizes
    topPanel.setPreferredSize(new Dimension(getWidth(), 50));
    handPanel.getPanel().setPreferredSize(new Dimension(getWidth(), 300));
  }

  /**
   * Sets up keyboard listeners for confirm and pass actions.
   * This method binds specific keys to the actions.
   */
  private void setupKeyboardListeners() {
    // Bind Enter key to confirm action
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ENTER"), "confirm");
    getRootPane().getActionMap().put("confirm", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        notifyKeyListenersConfirm();
      }
    });

    // Bind P key to pass action
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("P"), "pass");
    getRootPane().getActionMap().put("pass", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        notifyKeyListenersPass();
      }
    });
  }

  /**
   * Notifies all registered keyboard listeners of a confirm action.
   */
  private void notifyKeyListenersConfirm() {
    for (KeyboardActionListener listener : keyListeners) {
      listener.onConfirmAction();
    }
  }

  /**
   * Notifies all registered keyboard listeners of a pass action.
   */
  private void notifyKeyListenersPass() {
    for (KeyboardActionListener listener : keyListeners) {
      listener.onPassAction();
    }
  }

  // PawnsBoardView implementations

  /**
   * Returns a string representation of the graphical view.
   * Note: For a complete representation of the game state, use renderGameState() instead.
   *
   * @return a simple identification string for this view
   */
  @Override
  public String toString() {
    return "Augmented Graphical View - use renderGameState() for textual representation";
  }

  /**
   * Renders a comprehensive view of the game state including current player,
   * board state, and game results if the game is over.
   * In the graphical view, this method is not supported as the rendering
   * is handled by the GUI components.
   *
   * @return a text representation of the game state
   * @throws UnsupportedOperationException always, as this method is not supported in graphical view
   */
  @Override
  public String renderGameState() {
    throw new UnsupportedOperationException(
            "Text-based game state rendering not supported in graphical view");
  }

  /**
   * Renders the game state with a custom message header.
   * In the graphical view, this method is not supported as the rendering
   * is handled by the GUI components.
   *
   * @param headerMessage the message to display as a header
   * @return a text representation with the header and game state
   * @throws UnsupportedOperationException always, as this method is not supported in graphical view
   */
  @Override
  public String renderGameState(String headerMessage) {
    throw new UnsupportedOperationException(
            "Text-based game state rendering not supported in graphical view");
  }

  /**
   * Sets the visibility of the view.
   * Shows or hides the graphical window based on the provided parameter.
   *
   * @param visible true to make the view visible, false to hide it
   */
  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
  }

  /**
   * Updates the status label with the current player information.
   */
  private void updateStatusLabel() {
    try {
      PlayerColors currentPlayer = augmentedModel.getCurrentPlayer();
      statusLabel.setText("Current Player: " + currentPlayer);

      // Set color based on current player
      if (currentPlayer == PlayerColors.RED) {
        statusLabel.setForeground(Color.RED);
      } else {
        statusLabel.setForeground(Color.BLUE);
      }
    } catch (IllegalStateException e) {
      // Game might not be started yet
      statusLabel.setText("Game not started");
      statusLabel.setForeground(Color.BLACK);
    }
  }

  /**
   * Sets the position of the view window on the screen.
   *
   * @param x the x-coordinate of the window's top-left corner
   * @param y the y-coordinate of the window's top-left corner
   */
  @Override
  public void setPosition(int x, int y) {
    setLocation(x, y);
  }

  /**
   * Manually sets the current player for the view.
   * This is used to simulate player turn changes for testing.
   *
   * @param player the player to set as current
   */
  @Override
  public void simulatePlayerChange(PlayerColors player) {
    // Update the status label with the new player
    statusLabel.setText("Current Player: " + player);

    // Set color based on player
    if (player == PlayerColors.RED) {
      statusLabel.setForeground(Color.RED);
    } else {
      statusLabel.setForeground(Color.BLUE);
    }

    // Don't update the hand panel - we want to keep showing this view's player's hand
    // Repaint
    repaint();
  }

  /**
   * Sets the player this view is associated with.
   * This determines which player's hand will be displayed.
   *
   * @param player the player color this view represents
   */
  public void setViewPlayer(PlayerColors player) {
    this.viewPlayer = player;

    // Update the title to reflect which player this view is for
    setTitle("Augmented Pawns Board - " + player + " Player");

    // Update the hand to display the correct player's hand
    handPanel.renderHand(player);
  }

  /**
   * Gets the player this view is associated with.
   *
   * @return the player color this view represents
   */
  public PlayerColors getViewPlayer() {
    return viewPlayer;
  }

  /**
   * Refreshes the view to reflect the current state of the model.
   * Updates the board panel, status information, and ensures the correct player's hand is shown.
   */
  @Override
  public void refresh() {
    // Update the board
    boardPanel.renderBoard();

    try {
      // Get current player for status updates
      PlayerColors currentPlayer = augmentedModel.getCurrentPlayer();

      // Always show this view's player's hand, regardless of whose turn it is
      if (viewPlayer != null) {
        handPanel.renderHand(viewPlayer);
      }

      // Update status label
      updateStatusLabel();
    } catch (IllegalStateException e) {
      // Game might not be started yet
    }

    repaint();
  }

  /**
   * Clears any selections or highlights in the view.
   * Resets highlighting in both the board and hand panels.
   */
  @Override
  public void clearSelections() {
    boardPanel.clearCellHighlights();
    handPanel.clearCardHighlights();
    repaint();
  }

  // PawnsBoardGUIView implementation

  /**
   * Highlights a card in the current player's hand.
   * The hand panel will visually indicate which card is selected.
   *
   * @param cardIndex the index of the card to highlight (0-based)
   */
  @Override
  public void highlightCard(int cardIndex) {
    handPanel.highlightCard(cardIndex);
    repaint();
  }

  /**
   * Highlights a specific cell on the game board.
   * The board panel will visually indicate which cell is selected.
   *
   * @param row the row index of the cell to highlight
   * @param col the column index of the cell to highlight
   */
  @Override
  public void highlightCell(int row, int col) {
    boardPanel.highlightCell(row, col);
    repaint();
  }

  /**
   * Registers a listener for card selection events.
   * The listener will be notified when a user selects a card in their hand.
   *
   * @param listener the card selection listener to register
   */
  @Override
  public void addCardSelectionListener(CardSelectionListener listener) {
    if (listener != null) {
      cardListeners.add(listener);
      handPanel.addCardSelectionListener(listener);
    }
  }

  /**
   * Registers a listener for cell selection events.
   * The listener will be notified when a user selects a cell on the game board.
   *
   * @param listener the cell selection listener to register
   */
  @Override
  public void addCellSelectionListener(CellSelectionListener listener) {
    if (listener != null) {
      cellListeners.add(listener);
      boardPanel.addCellSelectionListener(listener);
    }
  }

  /**
   * Registers a listener for keyboard action events.
   * The listener will be notified when a user performs a keyboard action
   * like confirming a move or passing.
   *
   * @param listener the keyboard action listener to register
   */
  @Override
  public void addKeyboardActionListener(KeyboardActionListener listener) {
    if (listener != null) {
      keyListeners.add(listener);
    }
  }

  /**
   * Sets the title of the view window.
   *
   * @param title the title to set
   */
  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  /**
   * Sets the size of the view window.
   *
   * @param width the width of the window in pixels
   * @param height the height of the window in pixels
   */
  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);
  }

  // PawnsBoardAugmentedGUIView implementation

  /**
   * Gets the augmented model this view is using.
   *
   * @return the augmented model
   */
  @Override
  public AugmentedReadOnlyPawnsBoard<?, ?> getAugmentedModel() {
    return augmentedModel;
  }

  /**
   * Updates the display of a cell's value modifier.
   * Refreshes the board panel to show the updated modifier.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @param valueModifier the new value modifier
   */
  @Override
  public void updateCellValueModifier(int row, int col, int valueModifier) {
    // Since value modifiers are read directly from the model during rendering,
    // we just need to refresh the board panel
    boardPanel.renderBoard();
  }

  /**
   * Toggles the display of different influence types in the view.
   * When enabled, different colors will be used for different influence types.
   *
   * @param showInfluenceTypes true to show different influence types, false to use a single color
   */
  @Override
  public void displayInfluenceTypes(boolean showInfluenceTypes) {
    this.showInfluenceTypes = showInfluenceTypes;
    // Refresh both panels to apply the change
    boardPanel.renderBoard();
    handPanel.renderHand(viewPlayer);
  }

  /**
   * Gets the augmented board panel used by this view.
   *
   * @return the augmented board panel
   */
  @Override
  public AugmentedGameBoardPanel getAugmentedBoardPanel() {
    return boardPanel;
  }

  /**
   * Gets the augmented hand panel used by this view.
   *
   * @return the augmented hand panel
   */
  @Override
  public AugmentedCardHandPanel getAugmentedHandPanel() {
    return handPanel;
  }
}
