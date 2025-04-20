package cs3500.pawnsboard.player.strategy.moves;

/**
 * Implementation of the Move interface for the Pawns Board game.
 * Represents a specific move with a card index and target position,
 * or special moves like passing or an empty move.
 */
public class PawnsBoardMove implements Move {
  /** The type of move being made. */
  private final MoveType moveType;

  /** The index of the card in the player's hand. */
  private final int cardIndex;

  /** The row coordinate for card placement. */
  private final int row;

  /** The column coordinate for card placement. */
  private final int col;

  /**
   * Constructs a move for placing a card.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row the row coordinate where the card should be placed
   * @param col the column coordinate where the card should be placed
   */
  public PawnsBoardMove(int cardIndex, int row, int col) {
    this.moveType = MoveType.PLACE_CARD;
    this.cardIndex = cardIndex;
    this.row = row;
    this.col = col;
  }

  /**
   * Creates a move that passes the player's turn.
   *
   * @return a Pass move
   */
  public static PawnsBoardMove pass() {
    return new PawnsBoardMove(MoveType.PASS);
  }

  /**
   * Creates an empty move representing no possible action.
   *
   * @return an Empty move
   */
  public static PawnsBoardMove empty() {
    return new PawnsBoardMove(MoveType.EMPTY);
  }

  /**
   * Private constructor for special move types.
   *
   * @param moveType the type of special move
   */
  private PawnsBoardMove(MoveType moveType) {
    this.moveType = moveType;
    this.cardIndex = -1;
    this.row = -1;
    this.col = -1;
  }

  /**
   * Retrieves the type of move.
   *
   * @return the move type, which can be PLACE_CARD, PASS, or EMPTY
   */
  @Override
  public MoveType getMoveType() {
    return moveType;
  }

  /**
   * Retrieves the index of the card in the player's hand.
   *
   * @return the card index, or -1 if not a card placement move
   */
  @Override
  public int getCardIndex() {
    return cardIndex;
  }

  /**
   * Retrieves the row coordinate for card placement.
   *
   * @return the row index, or -1 if not a card placement move
   */
  @Override
  public int getRow() {
    return row;
  }

  /**
   * Retrieves the column coordinate for card placement.
   *
   * @return the column index, or -1 if not a card placement move
   */
  @Override
  public int getCol() {
    return col;
  }

  /**
   * Returns a String representation of this move.
   * @return a String representation of this move.
   */
  @Override
  public String toString() {
    switch (moveType) {
      case PLACE_CARD:
        return String.format("Move: Card %d at (%d, %d)", cardIndex, row, col);
      case PASS:
        return "Move: Pass Turn";
      case EMPTY:
        return "Move: No Move Available";
      default:
        return "Unknown Move";
    }
  }


}