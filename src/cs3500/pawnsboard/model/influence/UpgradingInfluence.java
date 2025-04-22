package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of the upgrading influence type.
 * This influence increases the value of cells with cards.
 * It has no effect on empty cells or cells with pawns.
 * Value modifiers are composite and apply regardless of card ownership.
 */
public class UpgradingInfluence implements Influence {
  
  /**
   * Applies upgrading influence to a cell.
   * This only has an effect on cells with cards.
   * No ownership checks are performed as value modifiers are composite.
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false otherwise
   * @throws Exception if there is an issue applying the influence
   */
  @Override
  public boolean applyInfluence(PawnsBoardCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    // We will only handle cells with cards
    if (cell.getContent() == CellContent.CARD) {
      // No ownership check - upgrade all cards
      if (cell instanceof PawnsBoardAugmentedCell) {
        PawnsBoardAugmentedCell<?> augmentedCell = (PawnsBoardAugmentedCell<?>) cell;
        augmentedCell.upgrade(1); // Increase value by 1
        return true;
      }
    }
    
    // No effect on other cell types
    return false;
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