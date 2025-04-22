package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;

/**
 * This interface extends ReadOnlyPawnsBoard to add methods specific to
 * the augmented version of the game that supports value modification.
 * It provides additional observation methods to query the modified values of cards.
 *
 * @param <C> the type of Card used in this game
 * @param <E> the type of Cell used in this game's board
 */
public interface ReadOnlyPawnsBoardAugmented<C extends Card, E extends PawnsBoardCell<C>> 
        extends ReadOnlyPawnsBoard<C, E> {
  
  /**
   * Gets the value modifier for a specific cell.
   * This is used to determine how much the value of a card has been modified.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the value modifier for the cell (positive for upgrades, negative for devaluations)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  int getCellValueModifier(int row, int col) 
          throws IllegalArgumentException, IllegalStateException;
  
  /**
   * Gets the effective value of a card in a specific cell, including any value modifiers.
   * This is the final value used for scoring.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the effective value of the card, or 0 if there is no card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  int getEffectiveCardValue(int row, int col)
          throws IllegalArgumentException, IllegalStateException;
}