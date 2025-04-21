package cs3500.pawnsboard.view.colorscheme;

import java.awt.Color;

/**
 * High contrast color scheme implementation that provides colors with higher contrast
 * for better accessibility in the Pawns Board game.
 * This implementation focuses on making the board more accessible.
 */
//TODO: Test this class
public class HighContrastColorScheme implements ColorScheme {
  
  @Override
  public Color getCellBackground() {
    return Color.BLACK;
  }
  
  @Override
  public Color getHighlightedCell() {
    return Color.YELLOW;
  }
  
  @Override
  public Color getRedPawnColor() {
    return new Color(255, 50, 50); // Bright red
  }
  
  @Override
  public Color getBluePawnColor() {
    return new Color(50, 200, 255); // Bright cyan
  }
  
  @Override
  public Color getPawnTextColor() {
    return Color.BLACK;
  }
  
  @Override
  public Color getRedScoreTextColor() {
    return this.getRedPawnColor(); // Bright red
  }
  
  @Override
  public Color getBlueScoreTextColor() {
    return this.getBluePawnColor();// Bright cyan
  }
}
