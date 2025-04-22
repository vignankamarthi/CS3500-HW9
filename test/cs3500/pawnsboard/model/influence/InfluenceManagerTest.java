package cs3500.pawnsboard.model.influence;

import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for InfluenceManager.
 * This tests the functionality of the influence manager, which handles
 * different types of influences based on character codes.
 */
public class InfluenceManagerTest {

  private InfluenceManager influenceManager;
  private PawnsBoardAugmentedCell<PawnsBoardBaseCard> testCell;

  /**
   * Set up test fixtures before each test.
   */
  @Before
  public void setUp() {
    influenceManager = new InfluenceManager();
    testCell = new PawnsBoardAugmentedCell<>();
  }

  /**
   * Test constructor initializes with standard influence types.
   */
  @Test
  public void testConstructorInitializesStandardInfluences() {
    // Verify all standard influence types are registered
    Influence regular = influenceManager.getInfluence('I');
    Influence upgrading = influenceManager.getInfluence('U');
    Influence devaluing = influenceManager.getInfluence('D');
    Influence blank = influenceManager.getInfluence('X');
    Influence center = influenceManager.getInfluence('C');

    // Verify they are the correct types
    assertTrue("'I' should be a regular influence", regular.isRegular());
    assertTrue("'U' should be an upgrading influence", upgrading.isUpgrading());
    assertTrue("'D' should be a devaluing influence", devaluing.isDevaluing());
    assertFalse("'X' should not be any special influence type",
            blank.isRegular() || blank.isUpgrading() || blank.isDevaluing());
    assertFalse("'C' should not be any special influence type",
            center.isRegular() || center.isUpgrading() || center.isDevaluing());

    // Verify they return the correct char codes
    assertEquals("Regular influence should return 'I'", 'I', regular.toChar());
    assertEquals("Upgrading influence should return 'U'", 'U', upgrading.toChar());
    assertEquals("Devaluing influence should return 'D'", 'D', devaluing.toChar());
    assertEquals("Blank influence should return 'X'", 'X', blank.toChar());
    assertEquals("Center influence should return 'X'", 'X', center.toChar());
  }

  /**
   * Test registerInfluence with valid parameters.
   */
  @Test
  public void testRegisterInfluenceWithValidParameters() {
    // Create a custom influence for testing
    Influence customInfluence = new BlankInfluence() {
      @Override
      public char toChar() {
        return 'Z';
      }
    };

    // Register custom influence
    influenceManager.registerInfluence('Z', customInfluence);

    // Verify it was registered correctly
    Influence retrieved = influenceManager.getInfluence('Z');
    assertNotNull("Should retrieve registered influence", retrieved);
    assertEquals("Should retrieve the correct influence", 'Z', retrieved.toChar());
  }

  /**
   * Test registerInfluence with null influence throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRegisterInfluenceWithNullInfluence() {
    influenceManager.registerInfluence('N', null);
    fail("Should throw IllegalArgumentException for null influence");
  }

  /**
   * Test registerInfluence overrides existing influence.
   */
  @Test
  public void testRegisterInfluenceOverridesExisting() {
    // Create a custom influence for testing
    Influence customInfluence = new BlankInfluence() {
      @Override
      public char toChar() {
        return 'I'; // Same as regular influence
      }
    };

    // Register custom influence with same code as regular influence
    influenceManager.registerInfluence('I', customInfluence);

    // Verify it overrides the existing influence
    Influence retrieved = influenceManager.getInfluence('I');
    assertFalse("Should no longer be a regular influence", retrieved.isRegular());
    assertEquals("Should be the custom influence", customInfluence.toChar(), 
            retrieved.toChar());
  }

  /**
   * Test getInfluence with valid code.
   */
  @Test
  public void testGetInfluenceWithValidCode() {
    Influence influence = influenceManager.getInfluence('I');
    assertNotNull("Should retrieve registered influence", influence);
    assertTrue("Should retrieve regular influence", influence.isRegular());
  }

  /**
   * Test getInfluence with unregistered code throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetInfluenceWithUnregisteredCode() {
    influenceManager.getInfluence('Z');
    fail("Should throw IllegalArgumentException for unregistered code");
  }

  /**
   * Test applyInfluence delegates to the correct influence strategy.
   */
  @Test
  public void testApplyInfluenceDelegatesToCorrectStrategy() throws Exception {
    // Apply each type of influence and verify behavior

    // Regular influence should add a pawn to an empty cell
    boolean regularResult = influenceManager.applyInfluence('I', testCell, PlayerColors.RED);
    assertTrue("Regular influence should return true when successful", regularResult);
    assertEquals("Cell should now have a pawn", 1, testCell.getPawnCount());

    // Reset cell
    testCell = new PawnsBoardAugmentedCell<>();

    // Upgrading influence should increase value modifier
    boolean upgradingResult = influenceManager.applyInfluence('U', testCell,
            PlayerColors.RED);
    assertTrue("Upgrading influence should return true when successful", upgradingResult);
    assertEquals("Value modifier should increase", 1, 
            testCell.getValueModifier());

    // Reset cell
    testCell = new PawnsBoardAugmentedCell<>();

    // Devaluing influence should decrease value modifier
    boolean devaluingResult = influenceManager.applyInfluence('D', testCell, 
            PlayerColors.RED);
    assertTrue("Devaluing influence should return true when successful", devaluingResult);
    assertEquals("Value modifier should decrease", -1, 
            testCell.getValueModifier());

    // Reset cell
    testCell = new PawnsBoardAugmentedCell<>();

    // Blank influence should have no effect
    boolean blankResult = influenceManager.applyInfluence('X', testCell, PlayerColors.RED);
    assertFalse("Blank influence should return false", blankResult);
    assertEquals("Cell should remain empty", 0, testCell.getPawnCount());
    assertEquals("Value modifier should remain 0", 0, 
            testCell.getValueModifier());
  }

  /**
   * Test applyInfluence with unregistered code throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testApplyInfluenceWithUnregisteredCode() throws Exception {
    influenceManager.applyInfluence('Z', testCell, PlayerColors.RED);
    fail("Should throw IllegalArgumentException for unregistered code");
  }

  /**
   * Test createInfluenceGrid with valid parameters.
   */
  @Test
  public void testCreateInfluenceGridWithValidParameters() {
    char[][] charGrid = {
            {'I', 'U', 'D'},
            {'X', 'C', 'I'}
    };

    Influence[][] influenceGrid = influenceManager.createInfluenceGrid(charGrid);

    // Verify dimensions
    assertEquals("Grid should have 2 rows", 2, influenceGrid.length);
    assertEquals("Grid should have 3 columns", 3, influenceGrid[0].length);

    // Verify specific influence types
    assertTrue("(0,0) should be regular influence", influenceGrid[0][0].isRegular());
    assertTrue("(0,1) should be upgrading influence", influenceGrid[0][1].isUpgrading());
    assertTrue("(0,2) should be devaluing influence", influenceGrid[0][2].isDevaluing());
    assertEquals("(1,0) should be blank influence", 
            'X', influenceGrid[1][0].toChar());
    assertEquals("(1,1) should be center influence", 
            'X', influenceGrid[1][1].toChar());
    assertTrue("(1,2) should be regular influence", influenceGrid[1][2].isRegular());
  }

  /**
   * Test createInfluenceGrid with null grid throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateInfluenceGridWithNullGrid() {
    influenceManager.createInfluenceGrid(null);
    fail("Should throw IllegalArgumentException for null grid");
  }

  /**
   * Test createInfluenceGrid with empty grid throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateInfluenceGridWithEmptyGrid() {
    char[][] emptyGrid = new char[0][0];
    influenceManager.createInfluenceGrid(emptyGrid);
    fail("Should throw IllegalArgumentException for empty grid");
  }

  /**
   * Test createInfluenceGrid with non-rectangular grid throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateInfluenceGridWithNonRectangularGrid() {
    char[][] nonRectangularGrid = {
            {'I', 'U', 'D'},
            {'X', 'C'}
    };
    influenceManager.createInfluenceGrid(nonRectangularGrid);
    fail("Should throw IllegalArgumentException for non-rectangular grid");
  }

  /**
   * Test createInfluenceGrid with null row throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateInfluenceGridWithNullRow() {
    char[][] gridWithNullRow = new char[2][];
    gridWithNullRow[0] = new char[]{'I', 'U', 'D'};
    gridWithNullRow[1] = null;

    influenceManager.createInfluenceGrid(gridWithNullRow);
    fail("Should throw IllegalArgumentException for grid with null row");
  }

  /**
   * Test createInfluenceGrid with unregistered code throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateInfluenceGridWithUnregisteredCode() {
    char[][] gridWithUnregisteredCode = {
            {'I', 'U', 'D'},
            {'X', 'C', 'Z'}  // 'Z' is not registered
    };

    influenceManager.createInfluenceGrid(gridWithUnregisteredCode);
    fail("Should throw IllegalArgumentException for grid with unregistered code");
  }

  /**
   * Test applyInfluence passes the correct parameters to the influence strategy.
   */
  @Test
  public void testApplyInfluencePassesCorrectParameters() throws Exception {
    // Create a test influence that verifies parameters
    final PlayerColors[] capturedPlayer = new PlayerColors[1];
    final PawnsBoardAugmentedCell<?>[] capturedCell = new PawnsBoardAugmentedCell<?>[1];

    Influence testInfluence = new Influence() {
      @Override
      public boolean applyInfluence(PawnsBoardAugmentedCell<?> cell, PlayerColors currentPlayer) {
        capturedCell[0] = cell;
        capturedPlayer[0] = currentPlayer;
        return true;
      }

      @Override
      public boolean isRegular() {
        return false;
      }

      @Override
      public boolean isUpgrading() {
        return false;
      }

      @Override
      public boolean isDevaluing() {
        return false;
      }

      @Override
      public char toChar() {
        return 'T';
      }
    };

    // Register test influence
    influenceManager.registerInfluence('T', testInfluence);

    // Apply with specific parameters
    PawnsBoardAugmentedCell<PawnsBoardBaseCard> specificCell = new PawnsBoardAugmentedCell<>();
    PlayerColors specificPlayer = PlayerColors.BLUE;

    influenceManager.applyInfluence('T', specificCell, specificPlayer);

    // Verify parameters were passed correctly
    assertEquals("Should pass the correct cell", specificCell, capturedCell[0]);
    assertEquals("Should pass the correct player", specificPlayer, capturedPlayer[0]);
  }
}