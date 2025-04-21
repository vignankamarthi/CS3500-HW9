package cs3500.pawnsboard.view.colorscheme;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.assertEquals;


/**
 * Test suite for the NormalColorScheme class.
 * This class tests that the NormalColorScheme provides the correct colors
 * for the standard appearance of the Pawns Board game.
 */
public class NormalColorSchemeTest {

  private NormalColorScheme colorScheme;

  /**
   * Sets up a fresh NormalColorScheme instance before each test.
   */
  @Before
  public void setUp() {
    colorScheme = new NormalColorScheme();
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
    assertEquals("Cell background should be gray", 
            Color.GRAY, colorScheme.getCellBackground());
  }

  /**
   * Tests that the getCellBorderColor method returns the correct color.
   */
  @Test
  public void testGetCellBorderColor() {
    assertEquals("Cell border should be black", 
            Color.BLACK, colorScheme.getCellBorderColor());
  }

  /**
   * Tests that the getHighlightedCell method returns the correct color.
   */
  @Test
  public void testGetHighlightedCell() {
    assertEquals("Highlighted cell should be cyan", 
            Color.CYAN, colorScheme.getHighlightedCell());
  }

  /**
   * Tests that the getRedPawnColor method returns the correct color.
   */
  @Test
  public void testGetRedPawnColor() {
    assertEquals("Red pawn color should be red", 
            Color.RED, colorScheme.getRedPawnColor());
  }

  /**
   * Tests that the getBluePawnColor method returns the correct color.
   */
  @Test
  public void testGetBluePawnColor() {
    assertEquals("Blue pawn color should be blue", 
            Color.BLUE, colorScheme.getBluePawnColor());
  }

  /**
   * Tests that the getPawnTextColor method returns the correct color.
   */
  @Test
  public void testGetPawnTextColor() {
    assertEquals("Pawn text color should be white", 
            Color.WHITE, colorScheme.getPawnTextColor());
  }

  /**
   * Tests that the getRedScoreTextColor method returns the correct color.
   */
  @Test
  public void testGetRedScoreTextColor() {
    assertEquals("Red score text color should match red pawn color", 
            colorScheme.getRedPawnColor(), colorScheme.getRedScoreTextColor());
  }

  /**
   * Tests that the getBlueScoreTextColor method returns the correct color.
   */
  @Test
  public void testGetBlueScoreTextColor() {
    assertEquals("Blue score text color should match blue pawn color", 
            colorScheme.getBluePawnColor(), colorScheme.getBlueScoreTextColor());
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
   * Tests the consistency between color methods that should return the same color.
   */
  @Test
  public void testColorConsistency() {
    // Verify that certain colors are consistent with each other
    assertEquals("Red score text color should be the same instance as red pawn color",
            colorScheme.getRedPawnColor(), colorScheme.getRedScoreTextColor());

    assertEquals("Blue score text color should be the same instance as blue pawn color", 
            colorScheme.getBluePawnColor(), colorScheme.getBlueScoreTextColor());

    assertEquals("Red owned cell color should be the same instance as red pawn color", 
            colorScheme.getRedPawnColor(), colorScheme.getRedOwnedCellColor());

    assertEquals("Blue owned cell color should be the same instance as blue pawn color", 
            colorScheme.getBluePawnColor(), colorScheme.getBlueOwnedCellColor());
  }
}
