package cs3500.pawnsboard.model.enumerations;

import cs3500.pawnsboard.model.PawnsBoard;

/**
 * Represents the possible contents of a cell on the {@link PawnsBoard}.
 * A cell can be empty, contain pawns, or contain a card.
 */
public enum CellContent {
  EMPTY, PAWNS, CARD
}
