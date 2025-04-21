package cs3500.pawnsboard.view.colorscheme;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for color schemes in the Pawns Board game.
 * Follows the Strategy pattern to allow switching between different color schemes at runtime.
 */
public class ColorSchemeManager {

  private final Map<String, ColorScheme> availableSchemes;
  private ColorScheme currentScheme;
  private String currentSchemeName;

  /**
   * Constructs a new ColorSchemeManager with the default color schemes.
   */
  public ColorSchemeManager() {
    this.availableSchemes = new HashMap<>();

    // Register default schemes
    registerColorScheme("normal", new NormalColorScheme());
    registerColorScheme("high_contrast", new HighContrastColorScheme());

    // Set default scheme
    setColorScheme("normal");
  }

  /**
   * Registers a new color scheme with the given name.
   *
   * @param name the name to register the scheme under
   * @param scheme the color scheme to register
   * @throws IllegalArgumentException if name or scheme is null
   */
  public void registerColorScheme(String name, ColorScheme scheme) {
    if (name == null || scheme == null) {
      throw new IllegalArgumentException("Scheme name and scheme cannot be null");
    }
    availableSchemes.put(name, scheme);
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
   * Gets the name of the current color scheme.
   *
   * @return the name of the current color scheme
   */
  public String getCurrentSchemeName() {
    return currentSchemeName;
  }

  /**
   * Sets the current color scheme by name.
   *
   * @param name the name of the color scheme to use
   * @throws IllegalArgumentException if no scheme with the given name exists
   */
  public void setColorScheme(String name) {
    if (!availableSchemes.containsKey(name)) {
      throw new IllegalArgumentException("No color scheme registered with name: " + name
      + ". Available schemes: " + availableSchemes.keySet());
    }
    currentScheme = availableSchemes.get(name);
    currentSchemeName = name;
  }

  /**
   * Gets the names of all registered color schemes.
   *
   * @return array of color scheme names
   */
  public String[] getAvailableSchemeNames() {
    return availableSchemes.keySet().toArray(new String[0]);
  }
}