package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.cards.Card;

/**
 * Text-based view for the Augmented Pawns Board game.
 * Extends the base textual view to support value modifiers.
 * Renders the game state with the following enhanced format:
 * - Empty cells: "____" (four underscores)
 * - Cells with pawns: Number of pawns + owner letter + value modifier (e.g., "1r+1", "2b-2", "3r__")
 * - Empty cells with value modifiers: Two underscores + value modifier (e.g., "__+1", "__-2")
 * - Cells with cards: Owner letter + card value + value modifier (e.g., "R2__", "B3-1")
 *
 * <p>Each row also shows the row scores for RED and BLUE players.</p>
 *
 * @param <C> the type of Card used in the game
 */
public class PawnsBoardAugmentedTextualView<C extends Card> extends PawnsBoardTextualView<C> {

  private final AugmentedReadOnlyPawnsBoard<C, ?> augmentedModel;

  /**
   * Constructs an augmented text view with the specified model.
   *
   * @param model the augmented game model to display
   * @throws IllegalArgumentException if model is null
   * @throws IllegalArgumentException if model is not an AugmentedReadOnlyPawnsBoard
   */
  public PawnsBoardAugmentedTextualView(ReadOnlyPawnsBoard<C, ?> model) {
    super(model);

    if (!(model instanceof AugmentedReadOnlyPawnsBoard)) {
      throw new IllegalArgumentException("Model must be an AugmentedReadOnlyPawnsBoard");
    }

    this.augmentedModel = (AugmentedReadOnlyPawnsBoard<C, ?>) model;
  }

  /**
   * Renders a single cell based on its content, including value modifiers.
   * Uses a 4-character format:
   * - Empty cells: "____"
   * - Cells with pawns: Number of pawns + owner letter + value modifier
   * - Empty cells with modifiers: Two underscores + value modifier
   * - Cells with cards: Owner letter + card value + value modifier
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return a string representation of the cell with modifiers
   */
  protected String renderCell(int row, int col) {
    try {
      CellContent content = augmentedModel.getCellContent(row, col);
      int valueModifier = augmentedModel.getCellValueModifier(row, col);

      switch (content) {
        case EMPTY:
          // Empty cell: "____" or "__+1" or "__-2"
          if (valueModifier == 0) {
            return "____";
          } else {
            return String.format("__%s%d",
                    valueModifier > 0 ? "+" : "-",
                    Math.abs(valueModifier));
          }

        case PAWNS:
          // Pawns: "1r__" or "2b+1" or "3r-2"
          int pawnCount = augmentedModel.getPawnCount(row, col);
          PlayerColors owner = augmentedModel.getCellOwner(row, col);
          String ownerChar = (owner == PlayerColors.RED) ? "r" : "b";

          if (valueModifier == 0) {
            return pawnCount + ownerChar + "__";
          } else {
            return String.format("%d%s%s%d",
                    pawnCount,
                    ownerChar,
                    valueModifier > 0 ? "+" : "-",
                    Math.abs(valueModifier));
          }

        case CARD:
          // Card: "R2__" or "B3-1"
          PlayerColors cardOwner = augmentedModel.getCellOwner(row, col);
          C card = augmentedModel.getCardAtCell(row, col);
          int value = card != null ? card.getValue() : 0;
          String prefix = (cardOwner == PlayerColors.RED) ? "R" : "B";

          // For cards, we only show negative modifiers since positives are added to the value
          if (valueModifier >= 0) {
            return prefix + value + "__";
          } else {
            return String.format("%s%d-%d",
                    prefix,
                    value,
                    Math.abs(valueModifier));
          }

        default:
          return "____"; // Fallback
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      return "____"; // Error case
    }
  }
}