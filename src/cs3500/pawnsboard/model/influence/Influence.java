package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

/**
 * Interface for different types of influence that can be applied to a cell.
 * This follows the Strategy Pattern to allow different influence behaviors.
 */
public interface Influence {
  
  /**
   * Applies the influence to a cell.
   *
   * @param cell the cell to apply influence to
   * @param currentPlayer the player who is applying the influence
   * @return true if the influence was successfully applied, false otherwise
   * @throws Exception if there is an issue applying the influence
   */
  boolean applyInfluence(PawnsBoardCell<?> cell, PlayerColors currentPlayer) throws Exception;
  
  /**
   * Checks if this influence is a regular influence (adds/converts pawns).
   *
   * @return true if this is a regular influence, false otherwise
   */
  boolean isRegular();
  
  /**
   * Checks if this influence is an upgrading influence (increases cell value).
   *
   * @return true if this is an upgrading influence, false otherwise
   */
  boolean isUpgrading();
  
  /**
   * Checks if this influence is a devaluing influence (decreases cell value).
   *
   * @return true if this is a devaluing influence, false otherwise
   */
  boolean isDevaluing();
  
  /**
   * Gets a character representation of this influence type.
   * 'I' for regular influence, 'U' for upgrading, 'D' for devaluing.
   *
   * @return the character representing this influence type
   */
  char toChar();
}