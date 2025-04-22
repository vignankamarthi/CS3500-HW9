package cs3500.pawnsboard.model.cards;

import cs3500.pawnsboard.model.influence.BlankInfluence;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.model.influence.InfluenceManager;

import java.util.Arrays;

/**
 * An augmented card implementation that supports different influence types.
 * This class extends PawnsBoardBaseCard and adds the ability to have different
 * influence types (regular, upgrading, devaluing) at different positions in the influence grid.
 */
public class PawnsBoardAugmentedCard extends PawnsBoardBaseCard {
  
  private final Influence[][] influenceGrid;
  
  /**
   * Constructs a PawnsBoardAugmentedCard with the specified attributes.
   *
   * @param name            the name of the card
   * @param cost            the cost of the card (1-3 pawns)
   * @param value           the value score of the card
   * @param influenceGrid   the influence grid as a 2D array of Influence objects
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public PawnsBoardAugmentedCard(String name, int cost, int value, Influence[][] influenceGrid) {
    super(name, cost, value, convertToBoolean(influenceGrid));
    
    if (influenceGrid == null || influenceGrid.length != 5
        || Arrays.stream(influenceGrid).anyMatch(
                row -> row == null || row.length != 5)) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 grid of influence types");
    }
    
    // Create a defensive copy of the influence grid
    this.influenceGrid = new Influence[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        this.influenceGrid[i][j] = influenceGrid[i][j];
      }
    }
  }
  
  /**
   * Converts an influence grid to a boolean grid for the base card.
   * Any non-blank influence is considered to have influence (true).
   *
   * @param influenceGrid the influence grid to convert
   * @return the boolean influence grid
   */
  private static boolean[][] convertToBoolean(Influence[][] influenceGrid) {
    if (influenceGrid == null || influenceGrid.length != 5
        || Arrays.stream(influenceGrid).anyMatch(
                row -> row == null || row.length != 5)) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 grid");
    }
    
    boolean[][] booleanGrid = new boolean[5][5];
    
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        // An influence is active if it's not a blank influence and not null
        if (influenceGrid[i][j] != null && 
            influenceGrid[i][j].toChar() != 'X' &&
            influenceGrid[i][j].toChar() != 'C') {
          booleanGrid[i][j] = true;
        } else {
          booleanGrid[i][j] = false;
        }
      }
    }
    
    return booleanGrid;
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
        } else if (influenceGrid[row][col] != null) {
          charGrid[row][col] = influenceGrid[row][col].toChar();
        } else {
          charGrid[row][col] = 'X'; // No influence
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
   * Returns a String representation of the augmented card with useful information.
   * 
   * @return a String representation of the augmented card
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName()).append(" (Cost: ").append(getCost())
      .append(", Value: ").append(getValue()).append(")\n");
    
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