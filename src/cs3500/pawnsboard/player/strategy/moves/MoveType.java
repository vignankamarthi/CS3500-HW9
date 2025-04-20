package cs3500.pawnsboard.player.strategy.moves;

/**
 * Represents the type of move a player can make.
 */
public enum MoveType {
  /** A move that places a card on the board. */
  PLACE_CARD,
  /** A move that passes the player's turn. */
  PASS,
  /** Represents a situation where no move is possible. */
  EMPTY


}