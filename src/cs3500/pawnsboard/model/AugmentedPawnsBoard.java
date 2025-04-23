package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;

/**
 * This interface extends PawnsBoard with additional functionality for an augmented version
 * of the Pawns Board game that supports new influence types:
 * upgrading and devaluing.
 *
 * <p>The augmented Pawns Board game adds these capabilities:</p>
 * <ul>
 *   <li>Upgrading influence: Potentially Increases the value of a cell by 1</li>
 *   <li>Devaluing influence: Potentially Decreases the value of a cell by 1</li>
 *   <li>Card removal when a card's effective value reaches 0 or less</li>
 * </ul>
 *
 * @param <C> the type of Card used in this game
 * @param <E> the type of Cell used in this game's board
 */
public interface AugmentedPawnsBoard<C extends Card, E extends PawnsBoardCell<C>> 
        extends PawnsBoard<C, E>, AugmentedReadOnlyPawnsBoard<C, E> {
  
  /**
   * Applies an upgrading influence to a cell.
   * This increases the value of any card in this cell by 1.
   * This influence persists even if the cell is empty or contains pawns.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @param amount the amount to increase the value by (typically 1)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   */
  void upgradeCell(int row, int col, int amount)
          throws IllegalArgumentException, IllegalStateException;
  
  /**
   * Applies a devaluing influence to a cell.
   * This decreases the value of any card in this cell by the specified amount.
   * If a card's effective value becomes 0 or less, the card is removed and replaced with pawns.
   * This influence persists even if the cell is empty or contains pawns.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @param amount the amount to decrease the value by (typically 1)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   */
  void devalueCell(int row, int col, int amount)
          throws IllegalArgumentException, IllegalStateException;
  
  /**
   * Removes a card from a cell and replaces it with pawns.
   * The number of pawns is equal to the ORIGINAL card's cost (up to a maximum of 3).
   * The pawns belong to the player who owns the card.
   * This is used when a card's effective value becomes zero or less.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   * @throws IllegalAccessException if the cell doesn't contain a card
   */
  void removeCardAndRestorePawns(int row, int col)
          throws IllegalArgumentException, IllegalStateException, IllegalAccessException;
  
  /**
   * Checks if a card needs to be removed due to devaluation.
   * This happens when a card's effective value becomes 0 or less.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return true if the card needs to be removed, false otherwise
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  boolean shouldRemoveCard(int row, int col)
          throws IllegalArgumentException, IllegalStateException;
  
  /**
   * Gets the cell at the specified position.
   * This is a helper method for testing and demonstrations.
   *
   * @param row the row index
   * @param col the column index
   * @return the cell at the specified position
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  E getCell(int row, int col)
          throws IllegalArgumentException, IllegalStateException;
}
