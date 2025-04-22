package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for applying different types of influences to cells.
 * This class follows the Strategy Pattern to select and apply the appropriate
 * influence type based on a character code.
 */
// TODO: Test this class
public class InfluenceManager {
  
  private final Map<Character, Influence> influenceStrategies;
  
  /**
   * Constructs an InfluenceManager with the standard influence strategies.
   */
  public InfluenceManager() {
    influenceStrategies = new HashMap<>();

    // Register standard influence types
    registerInfluence('I', new RegularInfluence());
    registerInfluence('U', new UpgradingInfluence());
    registerInfluence('D', new DevaluingInfluence());
  }
  
  /**
   * Registers an influence strategy with its character code.
   *
   * @param code the character code for the influence type
   * @param influence the influence strategy to register
   */
  public void registerInfluence(char code, Influence influence) {
    influenceStrategies.put(code, influence);
  }
  
  /**
   * Gets an influence strategy for the given character code.
   *
   * @param code the character code of the influence
   * @return the corresponding influence strategy, or null if not found
   */
  public Influence getInfluence(char code) {
    return influenceStrategies.get(code);
  }
  
  /**
   * Applies an influence to a cell based on the character code.
   *
   * @param code the character code of the influence to apply
   * @param cell the cell to apply the influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false otherwise
   * @throws IllegalArgumentException if the influence code is not registered
   * @throws Exception if there is an issue applying the influence
   */
  public boolean applyInfluence(char code, PawnsBoardCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    Influence influence = getInfluence(code);
    
    if (influence == null) {
      throw new IllegalArgumentException("Unknown influence code: " + code);
    }
    
    return influence.applyInfluence(cell, currentPlayer);
  }
  
  /**
   * Creates an influence grid from a character grid.
   * This converts a grid of character codes to a grid of influence objects.
   *
   * @param charGrid the grid of character codes
   * @return a grid of influence objects
   * @throws IllegalArgumentException if any character code is not registered
   */
  public Influence[][] createInfluenceGrid(char[][] charGrid) {
    if (charGrid == null) {
      throw new IllegalArgumentException("Character grid cannot be null");
    }
    
    int rows = charGrid.length;
    if (rows == 0) {
      throw new IllegalArgumentException("Character grid cannot be empty");
    }
    
    int cols = charGrid[0].length;
    Influence[][] influenceGrid = new Influence[rows][cols];
    
    for (int r = 0; r < rows; r++) {
      if (charGrid[r].length != cols) {
        throw new IllegalArgumentException("Character grid must be square (5x5)");
      }
      
      for (int c = 0; c < cols; c++) {
        char code = charGrid[r][c];
        Influence influence = getInfluence(code);
        
        if (influence == null && code != 'X' && code != 'C') {
          throw new IllegalArgumentException("Unknown influence code: " + code);
        }
        
        influenceGrid[r][c] = influence;
      }
    }
    
    return influenceGrid;
  }
  
}