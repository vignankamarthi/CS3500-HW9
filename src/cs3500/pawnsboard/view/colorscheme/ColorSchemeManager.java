package cs3500.pawnsboard.view.colorscheme;

/**
 * Manager class for color schemes in the Pawns Board game.
 * Follows the Strategy pattern to allow switching between different color schemes at runtime.
 */
public class ColorSchemeManager {
  
  private static final ColorScheme NORMAL_SCHEME = new NormalColorScheme();
  private static final ColorScheme HIGH_CONTRAST_SCHEME = new HighContrastColorScheme();
  
  private ColorScheme currentScheme;
  private boolean highContrastMode;
  
  /**
   * Constructs a new ColorSchemeManager with the default color scheme.
   */
  public ColorSchemeManager() {
    this.currentScheme = NORMAL_SCHEME;
    this.highContrastMode = false;
  }
  
  /**
   * Gets the current color scheme.
   *
   * @return the current color scheme
   */
  public ColorScheme getColorScheme() {
    return currentScheme;
  }
  
  /**
   * Sets whether high contrast mode is enabled.
   *
   * @param enabled true to enable high contrast mode, false for normal mode
   */
  public void setHighContrastMode(boolean enabled) {
    this.highContrastMode = enabled;
    this.currentScheme = enabled ? HIGH_CONTRAST_SCHEME : NORMAL_SCHEME;
  }
  
  /**
   * Checks if high contrast mode is currently enabled.
   *
   * @return true if high contrast mode is enabled, false otherwise
   */
  public boolean isHighContrastMode() {
    return highContrastMode;
  }
}
