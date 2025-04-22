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
  public boolean applyInfluence(PawnsBoardCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    if (cell.getContent() == CellContent.CARD) {
      if (cell instanceof PawnsBoardAugmentedCell) {
        PawnsBoardAugmentedCell<?> augmentedCell = (PawnsBoardAugmentedCell<?>) cell;
        
        // Store card info before devaluing in case we need to restore pawns
        Card cardInCell = cell.getCard();
        PlayerColors cardOwner = cell.getOwner();
        int cardCost = (cardInCell != null) ? cardInCell.getCost() : 0;
        
        // Decrease value by 1
        augmentedCell.devalue(1);
        
        // Card removal is handled in the devalue method of PawnsBoardAugmentedCell
        return true;
      }
    } else if (cell.getContent() == CellContent.PAWNS) {
      // No effect on cells with pawns, but influence is preserved for future cards
      if (cell instanceof PawnsBoardAugmentedCell) {
        PawnsBoardAugmentedCell<?> augmentedCell = (PawnsBoardAugmentedCell<?>) cell;
        augmentedCell.devalue(1);
        return true;
      }
    } else if (cell.getContent() == CellContent.EMPTY) {
      // For empty cells, we still apply the devaluation for future cards
      if (cell instanceof PawnsBoardAugmentedCell) {
        PawnsBoardAugmentedCell<?> augmentedCell = (PawnsBoardAugmentedCell<?>) cell;
        augmentedCell.devalue(1);
        return true;
      }
    }
    
    // No effect on other cell types or non-augmented cells
    return false;
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