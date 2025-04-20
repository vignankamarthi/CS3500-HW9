package cs3500.pawnsboard.player.strategy.moves;

/**
 * Represents a move in the Pawns Board game.
 * Supports different types of moves including card placement, passing, and empty moves.
 */
public interface Move {

  /**
   * Gets the type of move.
   *
   * @return the move type indicating how the player is acting
   */
  MoveType getMoveType();

  /**
   * Gets the index of the card in the player's hand to be played.
   *
   * @return the card index, or -1 if not a card placement move
   */
  int getCardIndex();

  /**
   * Gets the row coordinate where the card should be placed.
   *
   * @return the row index, or -1 if not a card placement move
   */
  int getRow();

  /**
   * Gets the column coordinate where the card should be placed.
   *
   * @return the column index, or -1 if not a card placement move
   */
  int getCol();

  /**
   * Returns a formatted String value of this move.
   */
  String toString();
}