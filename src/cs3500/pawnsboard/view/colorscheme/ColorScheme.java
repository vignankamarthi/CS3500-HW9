package cs3500.pawnsboard.view.colorscheme;

import java.awt.Color;

/**
 * Interface that defines the color scheme for the Pawns Board game.
 * This allows for different visual themes to be implemented and switched at runtime,
 * following the Strategy pattern. This interface focuses only on board cell colors 
 * and score text colors.
 */
public interface ColorScheme {
  
  /**
   * Gets the background color for the board panel.
   *
   * @return the background color
   */
  Color getBackgroundColor();
  
  /**
   * Gets the color for cell backgrounds.
   *
   * @return the cell background color
   */
  Color getCellBackground();
  
  /**
   * Gets the color for highlighted cells.
   *
   * @return the highlighted cell color
   */
  Color getHighlightedCell();
  
  /**
   * Gets the color for the Red player's pawns.
   *
   * @return the Red pawn color
   */
  Color getRedPawnColor();
  
  /**
   * Gets the color for the Blue player's pawns.
   *
   * @return the Blue pawn color
   */
  Color getBluePawnColor();
  
  /**
   * Gets the color for text on pawns.
   *
   * @return the pawn text color
   */
  Color getPawnTextColor();
  
  /**
   * Gets the color for Red player's score text.
   *
   * @return the Red score text color
   */
  Color getRedScoreTextColor();
  
  /**
   * Gets the color for Blue player's score text.
   *
   * @return the Blue score text color
   */
  Color getBlueScoreTextColor();
  
  /**
   * Gets the color for cell borders.
   *
   * @return the cell border color
   */
  Color getCellBorderColor();
  
  /**
   * Gets the background color for cells owned by the Red player.
   *
   * @return the Red-owned cell background color
   */
  Color getRedOwnedCellColor();
  
  /**
   * Gets the background color for cells owned by the Blue player.
   *
   * @return the Blue-owned cell background color
   */
  Color getBlueOwnedCellColor();
  
  /**
   * Gets the color for text on card cells.
   *
   * @return the card cell text color
   */
  Color getCardTextColor();
  
  /**
   * Gets the color for upgrading influence cells.
   * This represents the 'U' influence type in the augmented game.
   *
   * @return the upgrading influence color
   */
  Color getUpgradingInfluenceColor();
  
  /**
   * Gets the color for devaluing influence cells.
   * This represents the 'D' influence type in the augmented game.
   *
   * @return the devaluing influence color
   */
  Color getDevaluingInfluenceColor();
  
  /**
   * Gets the color for displaying value modifiers.
   * This is used to show the +/- values on cells.
   *
   * @return the value modifier text color
   */
  Color getValueModifierColor();
}
