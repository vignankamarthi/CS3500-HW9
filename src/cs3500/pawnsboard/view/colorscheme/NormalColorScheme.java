package cs3500.pawnsboard.view.colorscheme;

import java.awt.Color;

/**
 * Normal color scheme implementation that provides the default colors for the Pawns Board game.
 * This maintains the original appearance of the game.
 */
public class NormalColorScheme implements ColorScheme {
  
  @Override
  public Color getBackgroundColor() {
    return Color.BLACK;
  }
  
  @Override
  public Color getCellBackground() {
    return Color.GRAY;
  }
  
  @Override
  public Color getHighlightedCell() {
    return Color.CYAN;
  }
  
  @Override
  public Color getRedPawnColor() {
    return Color.RED;
  }
  
  @Override
  public Color getBluePawnColor() {
    return Color.BLUE;
  }
  
  @Override
  public Color getPawnTextColor() {
    return Color.WHITE;
  }
  
  @Override
  public Color getRedScoreTextColor() {
    return this.getRedPawnColor();
  }
  
  @Override
  public Color getBlueScoreTextColor() {
    return this.getBluePawnColor();
  }
}
