package cs3500.pawnsboard.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;
import cs3500.pawnsboard.view.guicomponents.AugmentedGameBoardPanel;
import cs3500.pawnsboard.view.guicomponents.AugmentedCardHandPanel;
import cs3500.pawnsboard.view.guicomponents.BoardPanel;
import cs3500.pawnsboard.view.guicomponents.HandPanel;

/**
 * A graphical implementation of the Pawns Board game view using Java Swing.
 * This implementation supports the augmented game features, including value modifiers
 * and special influence types.
 */
public class PawnsBoardAugmentedGUIView extends PawnsBoardGraphicalView {

  private final AugmentedReadOnlyPawnsBoard<?, ?> augmentedModel;
  private final BoardPanel augmentedBoardPanel;
  private final HandPanel augmentedHandPanel;
  private final JLabel valueModifierInfoLabel;
  
  /**
   * Constructs an augmented graphical view for the Pawns Board game.
   *
   * @param model the augmented read-only game model to display
   * @throws IllegalArgumentException if model is not an AugmentedReadOnlyPawnsBoard
   */
  public PawnsBoardAugmentedGUIView(ReadOnlyPawnsBoard<?, ?> model) {
    super(model); // Initialize the parent
    
    // Validate that the model is an AugmentedReadOnlyPawnsBoard
    if (!(model instanceof AugmentedReadOnlyPawnsBoard)) {
      throw new IllegalArgumentException("Model must be an AugmentedReadOnlyPawnsBoard");
    }
    
    this.augmentedModel = (AugmentedReadOnlyPawnsBoard<?, ?>) model;
    
    // Create color scheme manager
    ColorSchemeManager colorSchemeManager = getColorSchemeManager();
    
    // Replace the default panels with augmented ones
    this.augmentedBoardPanel = new AugmentedGameBoardPanel(model, colorSchemeManager);
    this.augmentedHandPanel = new AugmentedCardHandPanel(model, colorSchemeManager);
    
    // Create an information label for value modifiers
    this.valueModifierInfoLabel = new JLabel(
            "Value Modifiers: Green (+) increases card value, Purple (-) decreases card value", 
            SwingConstants.CENTER);
    valueModifierInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    
    // Update the layout to use the augmented panels
    replaceComponents();
  }
  
  /**
   * Replaces the default components with augmented ones.
   */
  private void replaceComponents() {
    // Remove all components
    getContentPane().removeAll();
    
    // Main layout
    setLayout(new BorderLayout());
    
    // Create top panel with status and color scheme controls
    JPanel topPanel = createTopPanel();
    
    // Add panels to the frame
    add(topPanel, BorderLayout.NORTH);
    add(augmentedBoardPanel.getPanel(), BorderLayout.CENTER);
    add(augmentedHandPanel.getPanel(), BorderLayout.SOUTH);
    
    // Add value modifier info at the bottom
    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    infoPanel.add(valueModifierInfoLabel);
    
    // Add info panel above the hand panel
    add(infoPanel, BorderLayout.SOUTH);
    
    // Set panel sizes
    topPanel.setPreferredSize(new Dimension(getWidth(), 50));
    augmentedHandPanel.getPanel().setPreferredSize(new Dimension(getWidth(), 300));
    
    // Transfer any registered listeners from parent to augmented components
    transferListeners();
    
    // Force layout update
    revalidate();
    repaint();
  }
  
  /**
   * Creates the top panel with status and controls.
   *
   * @return the configured top panel
   */
  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(Color.LIGHT_GRAY);
    
    // Re-create status label
    JLabel statusLabel = new JLabel("Augmented Game - Current Player: ", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
    topPanel.add(statusLabel, BorderLayout.CENTER);
    
    // Re-create color scheme combobox
    String[] schemes = getColorSchemeManager().getAvailableSchemeNames();
    JComboBox<String> colorSchemeComboBox = new JComboBox<>(schemes);
    colorSchemeComboBox.setSelectedItem(getColorSchemeManager().getCurrentSchemeName());
    
    // Add listener to change the color scheme
    colorSchemeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedScheme = (String) colorSchemeComboBox.getSelectedItem();
        if (selectedScheme != null) {
          setColorScheme(selectedScheme);
        }
      }
    });
    
    // Add color scheme control to top panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.LIGHT_GRAY);
    buttonPanel.add(colorSchemeComboBox);
    topPanel.add(buttonPanel, BorderLayout.EAST);
    
    // Add augmented game indicator
    JLabel augmentedLabel = new JLabel("Augmented Game", SwingConstants.LEFT);
    augmentedLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
    augmentedLabel.setForeground(new Color(128, 0, 128)); // Purple color
    topPanel.add(augmentedLabel, BorderLayout.WEST);
    
    return topPanel;
  }
  
  /**
   * Transfers listeners from parent to augmented components.
   * This ensures that any listeners added to the parent view will still work.
   */
  private void transferListeners() {
    // These methods would need to be implemented if listeners need to be transferred
    // This is a placeholder for actual implementation
  }
  
  /**
   * Gets the color scheme manager from the parent.
   *
   * @return the color scheme manager
   */
  private ColorSchemeManager getColorSchemeManager() {
    // This should be implemented to access the parent's color scheme manager
    // For now, create a new one
    return new ColorSchemeManager();
  }
  
  /**
   * Sets the color scheme for this view.
   *
   * @param schemeName the name of the color scheme to use
   */
  @Override
  public void setColorScheme(String schemeName) {
    super.setColorScheme(schemeName);
    refresh(); // Ensure the augmented components are updated as well
  }
  
  /**
   * Highlights a card in the current player's hand.
   *
   * @param cardIndex the index of the card to highlight (0-based)
   */
  @Override
  public void highlightCard(int cardIndex) {
    super.highlightCard(cardIndex);
    augmentedHandPanel.highlightCard(cardIndex);
  }
  
  /**
   * Highlights a specific cell on the game board.
   *
   * @param row the row index of the cell to highlight
   * @param col the column index of the cell to highlight
   */
  @Override
  public void highlightCell(int row, int col) {
    super.highlightCell(row, col);
    augmentedBoardPanel.highlightCell(row, col);
  }
  
  /**
   * Refreshes the view to reflect the current state of the model.
   */
  @Override
  public void refresh() {
    super.refresh();
    augmentedBoardPanel.renderBoard();
    
    try {
      PlayerColors currentPlayer = augmentedModel.getCurrentPlayer();
      augmentedHandPanel.renderHand(currentPlayer);
    } catch (IllegalStateException e) {
      // Game might not be started yet
    }
    
    repaint();
  }
  
  /**
   * Clears any selections or highlights in the view.
   */
  @Override
  public void clearSelections() {
    super.clearSelections();
    augmentedBoardPanel.clearCellHighlights();
    augmentedHandPanel.clearCardHighlights();
  }
  
  /**
   * Manually sets the current player for the view.
   *
   * @param player the player to set as current
   */
  @Override
  public void simulatePlayerChange(PlayerColors player) {
    super.simulatePlayerChange(player);
    augmentedHandPanel.renderHand(player);
  }
}
