package cs3500.pawnsboard.view.colorscheme;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

/**
 * Test suite for the ColorSchemeManager class.
 * This class tests the functionality of the ColorSchemeManager, which is responsible
 * for managing different color schemes in the Pawns Board game.
 */
public class ColorSchemeManagerTest {

  private ColorSchemeManager manager;

  /**
   * Sets up a fresh ColorSchemeManager instance before each test.
   */
  @Before
  public void setUp() {
    manager = new ColorSchemeManager();
  }

  /**
   * Tests that the constructor initializes with default color schemes.
   */
  @Test
  public void testConstructorInitializesDefaultSchemes() {
    // Normal scheme should be registered by default
    assertNotNull("Default normal scheme should be registered",
            manager.getColorScheme());
    assertEquals("Default scheme should be 'normal'",
            "normal", manager.getCurrentSchemeName());

    // Available schemes should include normal and high_contrast
    String[] schemes = manager.getAvailableSchemeNames();
    assertEquals("Should have 2 default schemes", 2, schemes.length);
    
    // Check both default schemes are available
    boolean hasNormal = false;
    boolean hasHighContrast = false;
    
    for (String scheme : schemes) {
      if (scheme.equals("normal")) {
        hasNormal = true;
      } else if (scheme.equals("high_contrast")) {
        hasHighContrast = true;
      }
    }
    
    assertTrue("Default schemes should include 'normal'", hasNormal);
    assertTrue("Default schemes should include 'high_contrast'", hasHighContrast);
  }

  /**
   * Tests registering a new color scheme.
   */
  @Test
  public void testRegisterColorScheme() {
    // Create a mock color scheme
    ColorScheme mockScheme = new MockColorScheme();
    
    // Register it
    manager.registerColorScheme("mock_scheme", mockScheme);
    
    // Check it was registered
    String[] schemes = manager.getAvailableSchemeNames();
    boolean hasMockScheme = false;
    
    for (String scheme : schemes) {
      if (scheme.equals("mock_scheme")) {
        hasMockScheme = true;
        break;
      }
    }
    
    assertTrue("Registered scheme should be available", hasMockScheme);
    
    // Set it as current and verify
    manager.setColorScheme("mock_scheme");
    assertEquals("Current scheme should be 'mock_scheme'",
            "mock_scheme", manager.getCurrentSchemeName());
    assertSame("Current scheme object should be our mock", 
            mockScheme, manager.getColorScheme());
  }

  /**
   * Tests that registering a null scheme name throws an exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRegisterColorSchemeWithNullName() {
    manager.registerColorScheme(null, new MockColorScheme());
  }

  /**
   * Tests that registering a null scheme throws an exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRegisterNullColorScheme() {
    manager.registerColorScheme("null_scheme", null);
  }

  /**
   * Tests setting a color scheme by name.
   */
  @Test
  public void testSetColorScheme() {
    // Verify initial state
    assertEquals("Initial scheme should be 'normal'",
            "normal", manager.getCurrentSchemeName());
    
    // Change to high contrast
    manager.setColorScheme("high_contrast");
    
    // Verify it changed
    assertEquals("Current scheme should be 'high_contrast'",
            "high_contrast", manager.getCurrentSchemeName());
    
    // Verify the scheme is an instance of HighContrastColorScheme
    assertTrue("Current scheme should be a HighContrastColorScheme",
            manager.getColorScheme() instanceof HighContrastColorScheme);
    
    // Change back to normal
    manager.setColorScheme("normal");
    
    // Verify it changed back
    assertEquals("Current scheme should be 'normal'",
            "normal", manager.getCurrentSchemeName());
    
    // Verify the scheme is an instance of NormalColorScheme
    assertTrue("Current scheme should be a NormalColorScheme",
            manager.getColorScheme() instanceof NormalColorScheme);
  }

  /**
   * Tests that setting a nonexistent color scheme throws an exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetNonexistentColorScheme() {
    manager.setColorScheme("nonexistent_scheme");
  }

  /**
   * Tests getting the current color scheme.
   */
  @Test
  public void testGetColorScheme() {
    ColorScheme scheme = manager.getColorScheme();
    assertNotNull("Color scheme should not be null", scheme);
    assertTrue("Default scheme should be a NormalColorScheme",
            scheme instanceof NormalColorScheme);
    
    // Switch to high contrast
    manager.setColorScheme("high_contrast");
    scheme = manager.getColorScheme();
    
    assertNotNull("Color scheme should not be null", scheme);
    assertTrue("Scheme should now be a HighContrastColorScheme",
            scheme instanceof HighContrastColorScheme);
  }

  /**
   * Tests getting the current scheme name.
   */
  @Test
  public void testGetCurrentSchemeName() {
    assertEquals("Initial scheme name should be 'normal'",
            "normal", manager.getCurrentSchemeName());
    
    // Switch to high contrast
    manager.setColorScheme("high_contrast");
    assertEquals("Scheme name should be 'high_contrast'",
            "high_contrast", manager.getCurrentSchemeName());
  }

  /**
   * Tests getting available scheme names.
   */
  @Test
  public void testGetAvailableSchemeNames() {
    String[] schemes = manager.getAvailableSchemeNames();
    assertEquals("Should have 2 default schemes", 2, schemes.length);
    
    // Add a new scheme
    manager.registerColorScheme("test_scheme", new MockColorScheme());
    
    // Get updated list
    schemes = manager.getAvailableSchemeNames();
    assertEquals("Should now have 3 schemes", 3, schemes.length);
    
    // Check all expected schemes are in the array
    boolean hasNormal = false;
    boolean hasHighContrast = false;
    boolean hasTestScheme = false;
    
    for (String scheme : schemes) {
      if (scheme.equals("normal")) {
        hasNormal = true;
      } else if (scheme.equals("high_contrast")) {
        hasHighContrast = true;
      } else if (scheme.equals("test_scheme")) {
        hasTestScheme = true;
      }
    }
    
    assertTrue("Schemes should include 'normal'", hasNormal);
    assertTrue("Schemes should include 'high_contrast'", hasHighContrast);
    assertTrue("Schemes should include 'test_scheme'", hasTestScheme);
  }

  /**
   * A mock implementation of ColorScheme for testing.
   */
  private static class MockColorScheme implements ColorScheme {
    @Override
    public Color getBackgroundColor() {
      return Color.PINK;
    }

    @Override
    public Color getCellBackground() {
      return Color.PINK;
    }

    @Override
    public Color getCellBorderColor() {
      return Color.PINK;
    }

    @Override
    public Color getHighlightedCell() {
      return Color.PINK;
    }

    @Override
    public Color getRedPawnColor() {
      return Color.PINK;
    }

    @Override
    public Color getBluePawnColor() {
      return Color.PINK;
    }

    @Override
    public Color getPawnTextColor() {
      return Color.PINK;
    }

    @Override
    public Color getRedScoreTextColor() {
      return Color.PINK;
    }

    @Override
    public Color getBlueScoreTextColor() {
      return Color.PINK;
    }

    @Override
    public Color getRedOwnedCellColor() {
      return Color.PINK;
    }

    @Override
    public Color getBlueOwnedCellColor() {
      return Color.PINK;
    }
  }
}
