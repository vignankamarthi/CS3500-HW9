package cs3500.pawnsboard.model.cards;

import java.util.Arrays;
import cs3500.pawnsboard.model.PawnsBoard;


/**
 * Cards for the {@link PawnsBoard} game that have a defined name, cost between 1 and 3 pawns,
 * a positive value, and an influence grid showing which cells are influenced and which are
 * not. Implements the {@link Card} interface.
 */
public class PawnsBoardBaseCard implements Card {
  private final String name;
  private final int cost;
  private final int value;
  private final boolean[][] influenceGrid;
  
  /**
   * Constructs a PawnsBoardBaseCard with the specified attributes.
   *
   * @param name          the name of the PawnsBoardBaseCard
   * @param cost          the cost of the PawnsBoardBaseCard (1-3 pawns)
   * @param value         the value score of the PawnsBoardBaseCard
   * @param influenceGrid the influence grid as a 2D boolean array
   * @throws IllegalArgumentException if the PawnsBoardBaseCard name is null or empty
   * @throws IllegalArgumentException if the PawnsBoardBaseCard cost is not between 1 and 3
   * @throws IllegalArgumentException the PawnsBoardBaseCard value is not positive
   * @throws IllegalArgumentException is the influence grid is not a 5x5 grid
   */
  public PawnsBoardBaseCard(String name, int cost, int value, boolean[][] influenceGrid) {
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
        || Arrays.stream(influenceGrid).anyMatch(row -> row.length != 5)) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 grid");
    }
    
    this.name = name;
    this.cost = cost;
    this.value = value;
    this.influenceGrid = gridCopy(influenceGrid);
  }
  
  /**
   * Creates a perfect copy of a 2D boolean array.
   *
   * @param original the original array to copy
   * @return a perfect copy of the array
   */
  private boolean[][] gridCopy(boolean[][] original) {
    boolean[][] copy = new boolean[original.length][];
    for (int i = 0; i < original.length; i++) {
      copy[i] = Arrays.copyOf(original[i], original[i].length);
    }
    return copy;
  }

  /**
   * Gets the name of the PawnsBoardBaseCard.
   *
   * @return the PawnsBoardBaseCard name
   */
  @Override
  public String getName() {
    return name;
  }


  /**
   * Gets the cost of the PawnsBoardBaseCard (1-3 pawns).
   *
   * @return the PawnsBoardBaseCard cost
   */
  @Override
  public int getCost() {
    return cost;
  }

  /**
   * Gets the value score of the PawnsBoardBaseCard.
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
    return gridCopy(influenceGrid);
  }

  /**
   * Gets the influence grid as a 2D char array.
   * 'I' indicates a cell has influence, 'X' indicates no influence,
   * 'C' indicates the PawnsBoardBaseCard position.
   *
   * @return the influence grid as chars
   */
  @Override
  public char[][] getInfluenceGridAsChars() {
    char[][] charGrid = new char[5][5];
    
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (row == 2 && col == 2) {
          charGrid[row][col] = 'C'; // PawnsBoardBaseCard position
        } else if (influenceGrid[row][col]) {
          charGrid[row][col] = 'I'; // Has influence
        } else {
          charGrid[row][col] = 'X'; // No influence
        }
      }
    }
    
    return charGrid;
  }


  /**
   * Determines if this PawnsBoardBaseCard is equal to another object.
   * PawnsBoardBaseCard are equal if they have the same name, cost, value score,
   * and influence grid.
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
    
    PawnsBoardBaseCard card = (PawnsBoardBaseCard) o;
    
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
    for (int i = 0; i < influenceGrid.length; i++) {
      if (!Arrays.equals(influenceGrid[i], card.influenceGrid[i])) {
        return false;
      }
    }
    
    return true;
  }


  /**
   * Returns a hash code value for the PawnsBoardBaseCard.
   * Consistent with equals: equal PawnsBoardBaseCards must have equal hash codes.
   * Robust implementation of hashing is necessary to ensure that PawnsBoardBaseCards follows
   * the specified equality in the assignment instructions.
   *
   * @return a hash code value for this PawnsBoardBaseCard
   */
  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + cost;
    result = 31 * result + value;
    
    // Add influence grid to hash
    for (boolean[] row : influenceGrid) {
      result = 31 * result + Arrays.hashCode(row);
    }
    
    return result;
  }

  /**
   * Returns a String representation of the PawnsBoardBaseCard with useful information
   * about the PawnsBoardBaseCard and for better readability.
   * @return a String representation of the PawnsBoardBaseCard with useful information
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(" (Cost: ").append(cost).append(", Value: ").append(value).append(")\n");
    
    // Append influence grid
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (row == 2 && col == 2) {
          sb.append('C');
        } else if (influenceGrid[row][col]) {
          sb.append('I');
        } else {
          sb.append('X');
        }
      }
      sb.append('\n');
    }
    
    return sb.toString();
  }
}