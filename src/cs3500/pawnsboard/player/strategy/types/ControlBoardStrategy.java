package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.moves.Move;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A strategy that selects moves to maximize the number of cells controlled by the player.
 * This is a FALLIBLE strategy that seeks to give the current player ownership of the most cells.
 *
 * <p>The strategy works as follows:
 * <ol>
 *   <li>For each possible move (card and position), simulate playing the card</li>
 *   <li>Count how many cells the player would control after each move</li>
 *   <li>Select the move that results in the most cells under player control</li>
 *   <li>In case of ties, select the uppermost-leftmost position (row first, then column)</li>
 *   <li>If multiple cards tie for the best position, choose the leftmost card in hand</li>
 * </ol>
 * </p>
 *
 * <p>This strategy may return an empty Optional if no legal moves exist or
 * if all moves would result in fewer cells under player control.</p>
 *
 * @param <C> the type of Card used in the game
 */
public class ControlBoardStrategy<C extends Card> extends AbstractPawnsBoardStrategy<C> {

  /**
   * Determines the next move based on the current game state.
   * Selects the move that maximizes the number of cells under the player's control.
   *
   * @param model the current game state
   * @return an Optional containing the best move, or empty if no legal move exists
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    validateGameStart(model);
    StrategyContext<C> context = initializeContext(model);
    if (context == null) {
      return Optional.of(PawnsBoardMove.empty());
    }
    int initialCellCount = countPlayerCells(model, context.currentPlayer);
    List<MoveResult> candidateMoves = new ArrayList<>();

    for (int cardIndex = 0; cardIndex < context.hand.size(); cardIndex++) {
      for (int row = 0; row < context.rows; row++) {
        for (int col = 0; col < context.cols; col++) {
          if (model.isLegalMove(cardIndex, row, col)) {
            try {
              PawnsBoard<C, ?> simulationModel = model.copy();
              simulationModel.placeCard(cardIndex, row, col);

              int cellCount = countPlayerCells(simulationModel, context.currentPlayer);

              if (cellCount > initialCellCount) {
                initialCellCount = cellCount;
                candidateMoves.clear();
                candidateMoves.add(new MoveResult(cardIndex, row, col, cellCount));
              }
              else if (cellCount == initialCellCount) {
                candidateMoves.add(new MoveResult(cardIndex, row, col, cellCount));
              }
            } catch (Exception e) {
              // Ignore simulation failures
            }
          }
        }
      }
    }
    if (!candidateMoves.isEmpty()) {
      candidateMoves.sort(Comparator
              .comparingInt(MoveResult::getRow)
              .thenComparingInt(MoveResult::getCol)
              .thenComparingInt(MoveResult::getCardIndex));

      MoveResult optimalMove = candidateMoves.get(0);
      return Optional.of(new PawnsBoardMove(
              optimalMove.getCardIndex(),
              optimalMove.getRow(),
              optimalMove.getCol()));
    }
    return Optional.of(PawnsBoardMove.empty());
  }

  /**
   * Counts the number of cells controlled by the specified player.
   * A cell is controlled if it contains pawns or a card owned by the player.
   *
   * @param model the game state
   * @param player the player to count cells for
   * @return the number of cells controlled by the player
   */
  private int countPlayerCells(ReadOnlyPawnsBoard<C, ?> model, PlayerColors player) {
    int count = 0;
    int[] dimensions = model.getBoardDimensions();

    for (int row = 0; row < dimensions[0]; row++) {
      for (int col = 0; col < dimensions[1]; col++) {
        CellContent content = model.getCellContent(row, col);

        if (content == CellContent.EMPTY) {
          continue;
        }

        PlayerColors owner = model.getCellOwner(row, col);
        if (owner == player) {
          count++;
        }
      }
    }

    return count;
  }

  /**
   * Internal class to represent a move result with additional strategic information.
   * Implements the Move interface to provide compatibility with the strategy framework.
   */
  private static class MoveResult implements Move {
    /** Index of the card to be played. */
    private final int cardIndex;

    /** Row coordinate for card placement. */
    private final int row;

    /** Column coordinate for card placement. */
    private final int col;

    /** Number of cells controlled by this move. */
    private final int cellCount;

    /**
     * Constructs a new MoveResult with strategic move information.
     *
     * @param cardIndex the index of the card
     * @param row the row coordinate
     * @param col the column coordinate
     * @param cellCount the number of cells controlled after making this move
     */
    public MoveResult(int cardIndex, int row, int col, int cellCount) {
      this.cardIndex = cardIndex;
      this.row = row;
      this.col = col;
      this.cellCount = cellCount;
    }

    /**
     * Gets the type of move, which is always PLACE_CARD for this strategy.
     *
     * @return the move type (PLACE_CARD)
     */
    @Override
    public MoveType getMoveType() {
      return MoveType.PLACE_CARD;
    }

    /**
     * Gets the index of the card to be played.
     *
     * @return the card index
     */
    @Override
    public int getCardIndex() {
      return cardIndex;
    }

    /**
     * Gets the row coordinate for card placement.
     *
     * @return the row index
     */
    @Override
    public int getRow() {
      return row;
    }

    /**
     * Gets the column coordinate for card placement.
     *
     * @return the column index
     */
    @Override
    public int getCol() {
      return col;
    }

    /**
     * Gets the number of cells controlled by this move.
     *
     * @return the cell count
     */
    public int getCellCount() {
      return cellCount;
    }
  }
}