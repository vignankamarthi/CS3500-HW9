package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of the devaluing influence type.
 * This influence decreases the value of cells with cards.
 * If a card's value becomes 0 or less, the card is removed and replaced with pawns.
 */
public class DevaluingInfluence implements Influence {
  
  /**
   * Applies devaluing influence to a cell.
   * If the cell contains a card, decreases its value by 1.
   * If the cell's effective value becomes 0 or less, the card is removed
   * and replaced with pawns equal to its cost.
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false otherwise
   * @throws Exception if there is an issue applying the influence
   */
  @Override
  public boolean applyInfluence(PawnsBoardAugmentedCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    PawnsBoardAugmentedCell<?> augmentedCell = cell;
    
    // Decrease value by 1 regardless of cell content
    // This is preserved for future cards that may be placed here
    augmentedCell.devalue(1);
    
    // Card removal (if necessary) is handled in the devalue method of PawnsBoardAugmentedCell
    return true;
  }
  
  @Override
  public boolean isRegular() {
    return false;
  }
  
  @Override
  public boolean isUpgrading() {
    return false;
  }
  
  @Override
  public boolean isDevaluing() {
    return true;
  }
  
  @Override
  public char toChar() {
    return 'D';
  }
}