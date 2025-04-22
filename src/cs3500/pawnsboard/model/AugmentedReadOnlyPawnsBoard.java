package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;

/**
 * This interface extends the ReadOnlyPawnsBoard interface with additional methods
 * for accessing information about cell value modifiers and effective card values
 * in a Pawns Board game with augmented influence types (upgrading and devaluing).
 *
 * <p>The augmented Pawns Board game adds two new influence types:</p>
 * <ul>
 *   <li>Upgrading influence (U): Potentially Increases the value of a cell by 1</li>
 *   <li>Devaluing influence (D): Potentially Decreases the value of a cell by 1</li>
 * </ul>
 *
 * <p>These influences affect the effective value of cards placed in affected cells,
 * which in turn affects scoring. If a card's effective value reaches 0 or less,
 * the card is removed and replaced with pawns equal to the value of the original card's cost.</p>
 *
 * @param <C> the type of Card used in this game
 * @param <E> the type of Cell used in this game's board
 */
public interface AugmentedReadOnlyPawnsBoard<C extends Card, E extends PawnsBoardCell<C>> 
        extends ReadOnlyPawnsBoard<C, E> {
  
  /**
   * Gets the value modifier for a specific cell.
   * This represents the cumulative effect of upgrading and devaluing influences
   * that have affected this cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the value modifier for the cell (positive for upgrades, negative for devaluations)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  int getCellValueModifier(int row, int col) 
          throws IllegalArgumentException, IllegalStateException;
  
  /**
   * Gets the effective value of a card in a specific cell, including any value modifiers.
   * The effective value is the original card value plus any value modifiers.
   * For scoring purposes, the effective value is never less than 0.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the effective value of the card, or 0 if there is no card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  int getEffectiveCardValue(int row, int col)
          throws IllegalArgumentException, IllegalStateException;
}
