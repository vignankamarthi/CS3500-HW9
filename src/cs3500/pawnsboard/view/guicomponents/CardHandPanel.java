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
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.colorscheme.ColorSchemeManager;

/**
 * A panel that displays a player's hand of cards.
 * This panel is responsible for rendering cards with their names,
 * costs, values, and influence grids.
 *
 *
 * <p>There is no explicit testing file for this class
 * as testing GUI/drawing related classes happens in the form of a
 * main testing class and screenshots provided. </p>
 */
public class CardHandPanel implements HandPanel {
  
  private final ReadOnlyPawnsBoard<?, ?> model;
  private final JPanel panel;
  private final JPanel cardsPanel;
  private final List<CardSelectionListener> listeners;
  
  private int highlightedCard = -1;
  private PlayerColors currentPlayer;
  private final int cardWidth = 140; // Increased card width
  private final int cardHeight = 230; // Increased card height for better visibility
  private final int gridSize = 100; // Increased grid size
  
  /**
   * Constructs a hand panel for the Pawns Board game.
   *
   * @param model the read-only game model to display
   * @param colorSchemeManager the color scheme manager to use
   */
  public CardHandPanel(ReadOnlyPawnsBoard<?, ?> model, ColorSchemeManager colorSchemeManager) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (colorSchemeManager == null) {
      throw new IllegalArgumentException("ColorSchemeManager cannot be null");
    }

    this.model = model;
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
    cardsPanel.setPreferredSize(new Dimension(800, 280));
    // Increased height to show full cards
    
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
      g2d.drawString("Player: " + currentPlayer, 10, 20);
      
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
    
    // Use DrawingUtils to draw the card
    DrawingUtils.drawCard(g2d, bounds, card, currentPlayer, index == highlightedCard);
  }
  
  /**
   * Draws the influence grid for a card.
   *
   * @param g2d the graphics context
   * @param x the x coordinate to start drawing
   * @param y the y coordinate to start drawing
   * @param card the card containing the influence grid
   */
  private void drawInfluenceGrid(Graphics2D g2d, int x, int y, Card card) {
    // Use DrawingUtils to draw the influence grid
    DrawingUtils.drawInfluenceGrid(g2d, x, y, card, gridSize, currentPlayer);
  }
  
  /**
   * Draws a message when the hand is empty or cannot be displayed.
   *
   * @param g2d the graphics context
   */
  private void drawEmptyHandMessage(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    String message = "Hand is empty or game hasn't started";
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
