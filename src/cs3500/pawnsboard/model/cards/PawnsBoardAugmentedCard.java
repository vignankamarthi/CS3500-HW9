package cs3500.pawnsboard.model.cards;

import cs3500.pawnsboard.model.influence.BlankInfluence;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.model.influence.InfluenceManager;

import java.util.Arrays;

/**
 * An augmented card implementation that supports different influence types.
 * This class implements Card directly and provides the ability to have different
 * influence types (regular, upgrading, devaluing) at different positions in the influence grid.
 */
public class PawnsBoardAugmentedCard implements Card {
  
  private final String name;
  private final int cost;
  private final int value;
  private final Influence[][] influenceGrid;
  
  /**
   * Constructs a PawnsBoardAugmentedCard with the specified attributes.
   *
   * @param name          the name of the card
   * @param cost          the cost of the card (1-3 pawns)
   * @param value         the value score of the card
   * @param influenceGrid the influence grid as a 2D array of Influence objects
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public PawnsBoardAugmentedCard(String name, int cost, int value, Influence[][] influenceGrid) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be null or empty");
    }
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Card cost must be between 1 and 3");
    }
    if (value < 1) {
      throw new IllegalArgumentException("Card value must be positive");
    }
    if (influenceGrid == null || influenceGrid.length != 5 
        || Arrays.stream(influenceGrid).anyMatch(row -> row == null 
            || row.length != 5)) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 grid");
    }
    
    this.name = name;
    this.cost = cost;
    this.value = value;
    
    // Create a defensive copy of the influence grid and ensure no null values
    this.influenceGrid = new Influence[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        this.influenceGrid[i][j] = (influenceGrid[i][j] != null) ? 
            influenceGrid[i][j] : new BlankInfluence();
      }
    }
  }
  
  /**
   * Gets the name of the card.
   *
   * @return the card name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Gets the cost of the card (1-3 pawns).
   *
   * @return the card cost
   */
  @Override
  public int getCost() {
    return cost;
  }

  /**
   * Gets the value score of the card.
   *
   * @return the value score
   */
  @Override
  public int getValue() {
    return value;
  }

  /**
   * Gets the influence grid as a 2D boolean array.
   * True indicates a cell has influence, false indicates no influence.
   *
   * @return the influence grid
   */
  @Override
  public boolean[][] getInfluenceGrid() {
    throw new UnsupportedOperationException("Augmented cards only contains influence grid");
  }
  
  /**
   * Gets the influence grid as a 2D array of Influence objects.
   *
   * @return a copy of the influence grid
   */
  public Influence[][] getAugmentedInfluenceGrid() {
    Influence[][] copy = new Influence[5][5];
    
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        copy[i][j] = influenceGrid[i][j];
      }
    }
    
    return copy;
  }
  
  /**
   * Gets the influence grid as a 2D char array.
   * Each influence type is represented by its character code:
   * 'I' for regular influence, 'U' for upgrading, 'D' for devaluing,
   * 'X' for no influence, 'C' for the card position.
   *
   * @return the influence grid as characters
   */
  @Override
  public char[][] getInfluenceGridAsChars() {
    char[][] charGrid = new char[5][5];
    
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (row == 2 && col == 2) {
          charGrid[row][col] = 'C'; // Card position
        } else {
          charGrid[row][col] = influenceGrid[row][col].toChar();
        }
      }
    }
    
    return charGrid;
  }
  
  /**
   * Static factory method to create an augmented card from a character grid.
   *
   * @param name          the name of the card
   * @param cost          the cost of the card (1-3 pawns)
   * @param value         the value score of the card
   * @param charGrid      the influence grid as a 2D char array
   * @param manager       the influence manager to use for creating influences
   * @return              a new PawnsBoardAugmentedCard
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public static PawnsBoardAugmentedCard fromCharGrid(
          String name, int cost, int value, char[][] charGrid, InfluenceManager manager) {
    
    if (charGrid == null || charGrid.length != 5
        || Arrays.stream(charGrid).anyMatch(row -> row == null || row.length != 5)) {
      throw new IllegalArgumentException("Character grid must be a 5x5 grid");
    }
    
    if (manager == null) {
      throw new IllegalArgumentException("Influence manager cannot be null");
    }
    
    // Convert character grid to influence grid
    Influence[][] influenceGrid = manager.createInfluenceGrid(charGrid);
    
    return new PawnsBoardAugmentedCard(name, cost, value, influenceGrid);
  }
  
  /**
   * Determines if this card is equal to another object.
   * Cards are equal if they have the same name, cost, value score, and influence grid.
   *
   * @param o the object to compare with
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    
    PawnsBoardAugmentedCard card = (PawnsBoardAugmentedCard) o;
    
    if (cost != card.cost) {
      return false;
    }
    if (value != card.value) {
      return false;
    }
    if (!name.equals(card.name)) {
      return false;
    }
    
    // Compare influence grids
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        Influence thisInfluence = influenceGrid[i][j];
        Influence otherInfluence = card.influenceGrid[i][j];
        
        if (thisInfluence.toChar() != otherInfluence.toChar()) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  /**
   * Returns a hash code value for the card.
   * Consistent with equals: equal cards must have equal hash codes.
   *
   * @return a hash code value for this card
   */
  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + cost;
    result = 31 * result + value;
    
    // Add influence grid to hash
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        result = 31 * result + influenceGrid[i][j].toChar();
      }
    }
    
    return result;
  }
  
  /**
   * Returns a String representation of the card with useful information.
   * 
   * @return a String representation of the card
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(" (Cost: ").append(cost)
      .append(", Value: ").append(value).append(")\n");
    
    // Append influence grid
    char[][] charGrid = getInfluenceGridAsChars();
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        sb.append(charGrid[row][col]);
      }
      sb.append('\n');
    }
    
    return sb.toString();
  }
}