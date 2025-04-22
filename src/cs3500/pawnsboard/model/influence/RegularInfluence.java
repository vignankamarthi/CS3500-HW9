package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Implementation of the regular influence type.
 * This influence adds pawns to empty cells, increases pawn count for owned pawns,
 * and changes ownership of opponent's pawns.
 */
public class RegularInfluence implements Influence {
  
  private static final int MAX_PAWNS = 3;
  
  /**
   * Applies regular influence to a cell.
   * - If the cell is empty, adds a pawn of the current player
   * - If the cell has pawns owned by the current player, increases pawn count (up to MAX_PAWNS)
   * - If the cell has pawns owned by the opponent, changes ownership to the current player
   * - If the cell has a card, no effect is applied
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false for cells with cards
   * @throws Exception if there is an issue applying the influence
   */
  @Override
  public boolean applyInfluence(PawnsBoardCell<?> cell, PlayerColors currentPlayer) 
          throws Exception {
    if (currentPlayer == null) {
      throw new IllegalArgumentException("Current player cannot be null");
    }
    
    switch (cell.getContent()) {
      case EMPTY:
        // Add a pawn of the current player
        cell.addPawn(currentPlayer);
        return true;
        
      case PAWNS:
        if (cell.getOwner() == currentPlayer) {
          // Increase pawn count for current player (up to MAX_PAWNS)
          if (cell.getPawnCount() < MAX_PAWNS) {
            cell.addPawn(currentPlayer);
            return true;
          }
        } else if (cell.getOwner() != null) {
          // Convert ownership of pawns to current player
          cell.changeOwnership(currentPlayer);
          return true;
        }
        return false;
        
      case CARD:
        // No effect on cells with cards
        return false;
        
      default:
        // Should never happen
        throw new IllegalStateException("Unknown cell content type");
    }
  }
  
  @Override
  public boolean isRegular() {
    return true;
  }
  
  @Override
  public boolean isUpgrading() {
    return false;
  }
  
  @Override
  public boolean isDevaluing() {
    return false;
  }
  
  @Override
  public char toChar() {
    return 'I';
  }
}