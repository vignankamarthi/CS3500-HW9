package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for applying different types of influences to cells.
 * This class follows the Strategy Pattern to select and apply the appropriate
 * influence type based on a character code.
 */
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
    registerInfluence('X', new BlankInfluence());
    registerInfluence('C', new BlankInfluence()); // Center position is also a blank influence
  }
  
  /**
   * Registers an influence strategy with its character code.
   *
   * @param code the character code for the influence type
   * @param influence the influence strategy to register
   */
  public void registerInfluence(char code, Influence influence) {
    if (influence == null) {
      throw new IllegalArgumentException("Influence cannot be null");
    }
    influenceStrategies.put(code, influence);
  }
  
  /**
   * Gets an influence strategy for the given character code.
   *
   * @param code the character code of the influence
   * @return the corresponding influence strategy, or a BlankInfluence if not found
   */
  public Influence getInfluence(char code) {
    Influence influenceStrategy = influenceStrategies.get(code);

    if (influenceStrategy == null) {
      throw new IllegalArgumentException("Influence code " + code 
              + " not registered in influence manager");
    }
    return influenceStrategy;
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
  public <T extends PawnsBoardAugmentedCell<?>> boolean applyInfluence(char code, T cell, 
                                                                       PlayerColors currentPlayer) 
          throws Exception {
    Influence influence = getInfluence(code);
    return influence.applyInfluence(cell, currentPlayer);
  }
  
  /**
   * Creates an influence grid from a character grid.
   * This converts a grid of character codes to a grid of influence objects.
   *
   * @param charGrid the grid of character codes
   * @return a grid of influence objects
   * @throws IllegalArgumentException if the character grid is invalid
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
      if (charGrid[r] == null || charGrid[r].length != cols) {
        throw new IllegalArgumentException("Character grid must be rectangular");
      }
      
      for (int c = 0; c < cols; c++) {
        char code = charGrid[r][c];
        influenceGrid[r][c] = getInfluence(code);
      }
    }
    
    return influenceGrid;
  }
}