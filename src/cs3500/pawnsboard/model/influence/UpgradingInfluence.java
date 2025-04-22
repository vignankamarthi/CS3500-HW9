package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of the upgrading influence type.
 * This influence increases the value of cells with cards owned by the player.
 * It has no effect on empty cells or cells with pawns.
 * 
 * <p>Note: The value modification is implemented in the augmented cell class,
 * which will handle the actual value increase.</p>
 *
 * TODO: Complete implementation after PawnsBoardAugmentedCell is created to properly handle the value modification logic.
 */
// TODO: Test this class
public class UpgradingInfluence implements Influence {
  
  /**
   * Applies upgrading influence to a cell.
   * This only has an effect on cells with cards owned by the current player.
   * The implementation will depend on the cell supporting value modification.
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
      // Only upgrade cards owned by the current player
      if (cell.getOwner() == currentPlayer) {
        // The actual increase is handled by the augmented cell class
        // This will be implemented when we create PawnsBoardAugmentedCell
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