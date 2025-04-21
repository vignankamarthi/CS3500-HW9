package cs3500.pawnsboard.view.colorscheme;

import java.awt.Color;

/**
 * High contrast color scheme implementation that provides colors with higher contrast
 * for better accessibility in the Pawns Board game.
 * This implementation focuses on making the board more accessible.
 */
public class HighContrastColorScheme implements ColorScheme {
  
  @Override
  public Color getBackgroundColor() {
    return Color.DARK_GRAY;
  }
  
  @Override
  public Color getCellBackground() {
    return Color.BLACK;
  }

  @Override
  public Color getCellBorderColor() {
    return Color.WHITE;
  }
  
  @Override
  public Color getHighlightedCell() {
    return Color.YELLOW;
  }
  
  @Override
  public Color getRedPawnColor() {
    return new Color(255, 20, 20); // Bright red
  }
  
  @Override
  public Color getBluePawnColor() {
    return new Color(20, 200, 255); // Bright cyan
  }
  
  @Override
  public Color getPawnTextColor() {
    return Color.BLACK;
  }
  
  @Override
  public Color getRedScoreTextColor() {
    return Color.BLACK;
  }
  
  @Override
  public Color getBlueScoreTextColor() {
    return Color.BLACK;
  }
  
  @Override
  public Color getRedOwnedCellColor() {
    return this.getRedPawnColor();
  }
  
  @Override
  public Color getBlueOwnedCellColor() {
    return this.getBluePawnColor(); 
  }
}
