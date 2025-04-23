package cs3500.pawnsboard.view.guicomponents;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * An augmented panel that displays a player's hand of cards with enhanced visuals.
 * This panel extends the functionality of CardHandPanel to support the display
 * of augmented cards with different influence types.
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class AugmentedCardHandPanel implements HandPanel {

  private final ReadOnlyPawnsBoard<?, ?> model;
  private final ColorSchemeManager colorSchemeManager;
  private final JPanel panel;
  private final JPanel cardsPanel;
  private final List<CardSelectionListener> listeners;

  private int highlightedCard = -1;
  private PlayerColors currentPlayer;
  private final int cardWidth = 150; // Slightly wider to accommodate augmented info
  private final int cardHeight = 240; // Slightly taller to accommodate augmented info
  private final int gridSize = 100; // Increased grid size

  /**
   * Constructs an augmented hand panel for the Pawns Board game.
   *
   * @param model the read-only game model to display
   * @param colorSchemeManager the color scheme manager to use
   * @throws IllegalArgumentException if model is not an AugmentedReadOnlyPawnsBoard
   */
  public AugmentedCardHandPanel(ReadOnlyPawnsBoard<?, ?> model, ColorSchemeManager 
          colorSchemeManager) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (colorSchemeManager == null) {
      throw new IllegalArgumentException("ColorSchemeManager cannot be null");
    }
    if (!(model instanceof AugmentedReadOnlyPawnsBoard)) {
      throw new IllegalArgumentException("Model must be an AugmentedReadOnlyPawnsBoard");
    }

    this.model = model;
    AugmentedReadOnlyPawnsBoard<?, ?> augmentedModel = (AugmentedReadOnlyPawnsBoard<?, ?>) model;
    this.colorSchemeManager = colorSchemeManager;
    this.listeners = new ArrayList<>();

    // Create card panel that will hold the cards
    cardsPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCards((Graphics2D) g);
      }
    };

    // Set panel properties
    cardsPanel.setBackground(Color.DARK_GRAY);
    cardsPanel.setPreferredSize(new Dimension(800, 300));
    // Increased height to show full augmented cards

    // Create scrollable container
    JScrollPane scrollPane = new JScrollPane(cardsPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

    // Enable autoscrolling to ensure cards are visible
    scrollPane.setAutoscrolls(true);

    // Create main panel
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(scrollPane);

    // Add mouse listener for card selection
    setupMouseListener();
  }

  /**
   * Sets up the mouse listener for card selection.
   */
  private void setupMouseListener() {
    cardsPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        try {
          List<?> hand = model.getPlayerHand(currentPlayer);

          // Calculate card index from mouse position
          int cardIndex = e.getX() / (cardWidth + 10);

          // Check if within valid range
          if (cardIndex >= 0 && cardIndex < hand.size()) {
            // Toggle highlight if clicking on the same card
            if (cardIndex == highlightedCard) {
              clearCardHighlights();
            } else {
              highlightCard(cardIndex);
              notifyListeners(cardIndex);
            }
          }
        } catch (IllegalStateException | IllegalArgumentException ex) {
          // Game may not be started yet or invalid player
        }
      }
    });
  }

  /**
   * Draws the cards in the player's hand.
   *
   * @param g2d the graphics context
   */
  private void drawCards(Graphics2D g2d) {
    try {
      List<?> hand = model.getPlayerHand(currentPlayer);

      // Set card panel size based on number of cards
      int totalWidth = (cardWidth + 10) * hand.size() + 10;
      cardsPanel.setPreferredSize(new Dimension(
              Math.max(totalWidth, panel.getWidth()),
              cardHeight + 20));

      // Draw player label
      g2d.setColor(Color.WHITE);
      g2d.setFont(new Font("Arial", Font.BOLD, 14));
      g2d.drawString("Player: " + currentPlayer + " (Augmented)", 10, 20);

      // Draw each card
      for (int i = 0; i < hand.size(); i++) {
        drawCard(g2d, i, (Card) hand.get(i));
      }
    } catch (IllegalStateException | IllegalArgumentException | ClassCastException e) {
      // Game may not be started yet or invalid player or casting issue
      drawEmptyHandMessage(g2d);
    }
  }

  /**
   * Draws a single card in the hand.
   *
   * @param g2d the graphics context
   * @param index the index of the card in the hand
   * @param card the card to draw
   */
  private void drawCard(Graphics2D g2d, int index, Card card) {
    // Calculate card position
    int x = 10 + index * (cardWidth + 10);
    int y = 30;

    // Create bounds for the card
    java.awt.Rectangle bounds = new java.awt.Rectangle(x, y, cardWidth, cardHeight);

    // Use AugmentedDrawingUtils to draw the card with special influence types
    AugmentedDrawingUtils.drawAugmentedCard(g2d, bounds, card, currentPlayer,
            index == highlightedCard,
            colorSchemeManager.getColorScheme());
  }

  /**
   * Draws a message when the hand is empty or cannot be displayed.
   *
   * @param g2d the graphics context
   */
  private void drawEmptyHandMessage(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    String message = "Augmented hand is empty or game hasn't started";
    g2d.drawString(message, 10, 50);
  }

  /**
   * Notifies all registered listeners of a card selection.
   *
   * @param cardIndex the index of the selected card
   */
  private void notifyListeners(int cardIndex) {
    for (CardSelectionListener listener : listeners) {
      listener.onCardSelected(cardIndex, currentPlayer);
    }
  }

  // HandPanel implementation

  @Override
  public void renderHand(PlayerColors player) {
    this.currentPlayer = player;
    if (panel != null) {
      cardsPanel.repaint();
    }
  }

  @Override
  public void highlightCard(int cardIndex) {
    this.highlightedCard = cardIndex;
    cardsPanel.repaint();
  }

  @Override
  public void clearCardHighlights() {
    this.highlightedCard = -1;
    cardsPanel.repaint();
  }

  @Override
  public void addCardSelectionListener(CardSelectionListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  @Override
  public JPanel getPanel() {
    return panel;
  }
}