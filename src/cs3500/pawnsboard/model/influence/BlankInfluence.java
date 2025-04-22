package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of blank influence that has no effect on cells.
 * This is used to represent 'X' in the influence grid, indicating no influence.
 */
public class BlankInfluence implements Influence {
  
  /**
   * Applies blank influence to a cell, which has no effect.
   * Always returns false since no influence is applied.
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return false, since no influence is applied
   * @throws Exception if there is an issue applying the influence
   */
  @Override
  public <T extends PawnsBoardAugmentedCell<?>> boolean applyInfluence(T cell, 
                                                                       PlayerColors currentPlayer) {
    // No effect on any cell type
    return false;
  }
  
  /**
   * Checks if this influence is a regular influence.
   *
   * @return false, this is not a regular influence
   */
  @Override
  public boolean isRegular() {
    return false;
  }
  
  /**
   * Checks if this influence is an upgrading influence.
   *
   * @return false, this is not an upgrading influence
   */
  @Override
  public boolean isUpgrading() {
    return false;
  }
  
  /**
   * Checks if this influence is a devaluing influence.
   *
   * @return false, this is not a devaluing influence
   */
  @Override
  public boolean isDevaluing() {
    return false;
  }
  
  /**
   * Gets a character representation of this influence type.
   *
   * @return 'X' for blank influence
   */
  @Override
  public char toChar() {
    return 'X';
  }
}