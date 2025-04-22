package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of the upgrading influence type.
 * This influence increases the value of cells with cards.
 * Upgrades are preserved even for empty cells or cells with pawns,
 * affecting future cards that may be placed there.
 */
//TODO: Test this class
public class UpgradingInfluence implements Influence {
  
  /**
   * Applies upgrading influence to a cell.
   * Increases the value of any card in the cell by 1.
   * For empty cells or cells with pawns, the upgrade is preserved for future cards.
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false otherwise
   * @throws Exception if there is an issue applying the influence
   */
  @Override
  public boolean applyInfluence(PawnsBoardAugmentedCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    // We can assume we're working with augmented cells in this context
    PawnsBoardAugmentedCell<?> augmentedCell = cell;
    
    // Apply upgrade regardless of cell content
    // This ensures future cards placed in this cell will also receive the upgrade
    augmentedCell.upgrade(1);
    return true;
  }
  
  @Override
  public boolean isRegular() {
    return false;
  }
  
  @Override
  public boolean isUpgrading() {
    return true;
  }
  
  @Override
  public boolean isDevaluing() {
    return false;
  }
  
  @Override
  public char toChar() {
    return 'U';
  }
}