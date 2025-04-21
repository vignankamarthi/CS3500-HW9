package cs3500.pawnsboard.view.colorscheme;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test suite for the HighContrastColorScheme class.
 * This class tests that the HighContrastColorScheme provides the correct colors
 * for the high contrast appearance of the Pawns Board game.
 */
public class HighContrastColorSchemeTest {

  private HighContrastColorScheme colorScheme;

  /**
   * Sets up a fresh HighContrastColorScheme instance before each test.
   */
  @Before
  public void setUp() {
    colorScheme = new HighContrastColorScheme();
  }

  /**
   * Tests that the getBackgroundColor method returns the correct color.
   */
  @Test
  public void testGetBackgroundColor() {
    assertEquals("Background color should be dark gray", 
            Color.DARK_GRAY, colorScheme.getBackgroundColor());
  }

  /**
   * Tests that the getCellBackground method returns the correct color.
   */
  @Test
  public void testGetCellBackground() {
    assertEquals("Cell background should be black", 
            Color.BLACK, colorScheme.getCellBackground());
  }

  /**
   * Tests that the getCellBorderColor method returns the correct color.
   */
  @Test
  public void testGetCellBorderColor() {
    assertEquals("Cell border should be white", 
            Color.WHITE, colorScheme.getCellBorderColor());
  }

  /**
   * Tests that the getHighlightedCell method returns the correct color.
   */
  @Test
  public void testGetHighlightedCell() {
    assertEquals("Highlighted cell should be yellow", 
            Color.YELLOW, colorScheme.getHighlightedCell());
  }

  /**
   * Tests that the getRedPawnColor method returns a bright red color.
   */
  @Test
  public void testGetRedPawnColor() {
    Color actual = colorScheme.getRedPawnColor();
    
    assertEquals("Red pawn red component should be 255", 
            255, actual.getRed());
    assertEquals("Red pawn green component should be 20", 
            20, actual.getGreen());
    assertEquals("Red pawn blue component should be 20", 
            20, actual.getBlue());
  }

  /**
   * Tests that the getBluePawnColor method returns a bright cyan color.
   */
  @Test
  public void testGetBluePawnColor() {
    Color actual = colorScheme.getBluePawnColor();
    
    assertEquals("Blue pawn red component should be 20", 
            20, actual.getRed());
    assertEquals("Blue pawn green component should be 200", 
            200, actual.getGreen());
    assertEquals("Blue pawn blue component should be 255", 
            255, actual.getBlue());
  }

  /**
   * Tests that the getPawnTextColor method returns the correct color.
   */
  @Test
  public void testGetPawnTextColor() {
    assertEquals("Pawn text color should be black", 
            Color.BLACK, colorScheme.getPawnTextColor());
  }

  /**
   * Tests that the getRedScoreTextColor method returns the correct color.
   */
  @Test
  public void testGetRedScoreTextColor() {
    assertEquals("Red score text color should be black", 
            Color.BLACK, colorScheme.getRedScoreTextColor());
  }

  /**
   * Tests that the getBlueScoreTextColor method returns the correct color.
   */
  @Test
  public void testGetBlueScoreTextColor() {
    assertEquals("Blue score text color should be black", 
            Color.BLACK, colorScheme.getBlueScoreTextColor());
  }

  /**
   * Tests that the getRedOwnedCellColor method returns the correct color.
   */
  @Test
  public void testGetRedOwnedCellColor() {
    assertEquals("Red owned cell color should match red pawn color", 
            colorScheme.getRedPawnColor(), colorScheme.getRedOwnedCellColor());
  }

  /**
   * Tests that the getBlueOwnedCellColor method returns the correct color.
   */
  @Test
  public void testGetBlueOwnedCellColor() {
    assertEquals("Blue owned cell color should match blue pawn color", 
            colorScheme.getBluePawnColor(), colorScheme.getBlueOwnedCellColor());
  }

  /**
   * Tests that high contrast colors provide better contrast than normal colors.
   */
  @Test
  public void testContrastIsHigherThanNormal() {
    NormalColorScheme normalScheme = new NormalColorScheme();
    
    // Check contrast between background and pawn colors
    assertNotEquals("High contrast red pawn color should differ from normal", 
            normalScheme.getRedPawnColor(), colorScheme.getRedPawnColor());
    
    assertNotEquals("High contrast blue pawn color should differ from normal", 
            normalScheme.getBluePawnColor(), colorScheme.getBluePawnColor());
    
    // Check text colors for contrast
    assertNotEquals("High contrast pawn text color should differ from normal", 
            normalScheme.getPawnTextColor(), colorScheme.getPawnTextColor());
  }

  /**
   * Tests the consistency between color methods that should return the same color.
   */
  @Test
  public void testColorConsistency() {
    // Verify that certain colors are consistent with each other
    assertEquals("Red owned cell color should be the same instance as red pawn color", 
            colorScheme.getRedPawnColor(), colorScheme.getRedOwnedCellColor());
    
    assertEquals("Blue owned cell color should be the same instance as blue pawn color", 
            colorScheme.getBluePawnColor(), colorScheme.getBlueOwnedCellColor());
  }
}
