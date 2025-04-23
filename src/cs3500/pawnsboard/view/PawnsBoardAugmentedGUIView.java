package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.AugmentedReadOnlyPawnsBoard;
import cs3500.pawnsboard.view.guicomponents.AugmentedGameBoardPanel;
import cs3500.pawnsboard.view.guicomponents.AugmentedCardHandPanel;

/**
 * Interface for an augmented graphical view of the Pawns Board game.
 * This interface extends the base PawnsBoardGUIView with methods specific to
 * the augmented game mechanics, such as displaying value modifiers and different influence types.
 */
public interface PawnsBoardAugmentedGUIView extends PawnsBoardGUIView {
  
  /**
   * Gets the augmented model this view is using.
   *
   * @return the augmented model
   */
  AugmentedReadOnlyPawnsBoard<?, ?> getAugmentedModel();
  
  /**
   * Updates the display of a cell's value modifier.
   * This allows the controller to request updates to the visual representation
   * of value modifiers when they change.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @param valueModifier the new value modifier
   */
  void updateCellValueModifier(int row, int col, int valueModifier);
  
  /**
   * Toggles the display of different influence types in the view.
   * When enabled, different colors will be used for different influence types
   * (regular, upgrading, devaluing).
   *
   * @param showInfluenceTypes true to show different influence types, false to use a single color
   */
  void displayInfluenceTypes(boolean showInfluenceTypes);
  
  /**
   * Gets the augmented board panel used by this view.
   * This is useful for testing or for components that need direct access to the panel.
   *
   * @return the augmented board panel
   */
  AugmentedGameBoardPanel getAugmentedBoardPanel();
  
  /**
   * Gets the augmented hand panel used by this view.
   * This is useful for testing or for components that need direct access to the panel.
   *
   * @return the augmented hand panel
   */
  AugmentedCardHandPanel getAugmentedHandPanel();
}
