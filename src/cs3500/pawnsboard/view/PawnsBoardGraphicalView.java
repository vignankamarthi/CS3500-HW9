package cs3500.pawnsboard.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JButton;
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
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;
import cs3500.pawnsboard.view.guicomponents.CardHandPanel;
import cs3500.pawnsboard.view.guicomponents.DrawingUtils;
import cs3500.pawnsboard.view.guicomponents.GameBoardPanel;

/**
 * A graphical implementation of the Pawns Board game view using Java Swing.
 * This class creates and manages a window that displays the game board,
 * player cards, and game state.
 */
public class PawnsBoardGraphicalView extends JFrame implements PawnsBoardGUIView {

  private final ReadOnlyPawnsBoard<?, ?> model;
  private final GameBoardPanel boardPanel;
  private final CardHandPanel handPanel;
  private final JPanel infoPanel;
  private final JLabel statusLabel;
  private final JButton contrastToggleButton;

  private final List<CardSelectionListener> cardListeners;
  private final List<CellSelectionListener> cellListeners;
  private final List<KeyboardActionListener> keyListeners;

  // Track which player this view represents
  private PlayerColors viewPlayer;
  
  // Current index in available schemes
  private int currentSchemeIndex;
  private String[] availableSchemes;

  /**
   * Constructs a graphical view for the Pawns Board game.
   *
   * @param model the read-only game model to display
   */
  public PawnsBoardGraphicalView(ReadOnlyPawnsBoard<?, ?> model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;
    this.cardListeners = new ArrayList<>();
    this.cellListeners = new ArrayList<>();
    this.keyListeners = new ArrayList<>();
    
    // Get available color schemes
    ColorSchemeManager manager = DrawingUtils.getColorSchemeManager();
    this.availableSchemes = manager.getAvailableSchemeNames();
    this.currentSchemeIndex = 0; // Start with the first scheme (normal)

    // Create panels
    this.boardPanel = new GameBoardPanel(model);
    this.handPanel = new CardHandPanel(model);
    this.infoPanel = new JPanel();

    // Create toggle button for color schemes
    this.contrastToggleButton = new JButton(getNextSchemeName());
    contrastToggleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        toggleColorScheme();
      }
    });

    // Initial setup
    this.statusLabel = new JLabel("Current Player: ", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

    setupLayout();
    setupKeyboardListeners();
    updateStatusLabel();

    // Set default properties
    setTitle("Pawns Board Game");
    setSize(1200, 900); // Further increased window size for larger board
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  /**
   * Gets the name of the next color scheme in the cycle.
   *
   * @return the name of the next color scheme
   */
  private String getNextSchemeName() {
    // Calculate the next index (cycling through available schemes)
    int nextIndex = (currentSchemeIndex + 1) % availableSchemes.length;
    // Return the scheme name at that index
    return "Switch to " + availableSchemes[nextIndex] + " mode";
  }

  /**
   * Toggles between available color schemes.
   */
  public void toggleColorScheme() {
    // Cycle to the next scheme
    currentSchemeIndex = (currentSchemeIndex + 1) % availableSchemes.length;
    
    // Set the new scheme
    String newScheme = availableSchemes[currentSchemeIndex];
    DrawingUtils.getColorSchemeManager().setColorScheme(newScheme);
    
    // Update button text to show the next scheme
    contrastToggleButton.setText("Switch to " + 
            availableSchemes[(currentSchemeIndex + 1) % availableSchemes.length] + " mode");
    
    // Refresh the view
    refresh();
  }

  /**
   * Gets the current color scheme name.
   *
   * @return the current color scheme name
   */
  public String getCurrentColorScheme() {
    return DrawingUtils.getColorSchemeManager().getCurrentSchemeName();
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
    buttonPanel.add(contrastToggleButton);
    
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
    
    // Bind H key to toggle color scheme
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("H"), "toggleScheme");
    getRootPane().getActionMap().put("toggleScheme", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        toggleColorScheme();
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
    // Delegate to textual representation for now
    return "Graphical View - use renderGameState() for textual representation";
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
      PlayerColors currentPlayer = model.getCurrentPlayer();
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
   * This is used by the stub controller to simulate player turn changes for testing purposes.
   *
   * @param player the player to set as current
   */
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
    setTitle("Pawns Board - " + player + " Player");

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
      PlayerColors currentPlayer = model.getCurrentPlayer();

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
}
