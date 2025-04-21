package cs3500.pawnsboard.view;

import cs3500.pawnsboard.controller.listeners.CardSelectionListener;
import cs3500.pawnsboard.controller.listeners.CellSelectionListener;
import cs3500.pawnsboard.controller.listeners.KeyboardActionListener;
import cs3500.pawnsboard.model.enumerations.PlayerColors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the PawnsBoardGUIViewMock class.
 *
 * <p>This comprehensive test class verifies the behavior of the PawnsBoardGUIViewMock,
 * a mock implementation of the PawnsBoardGUIView interface used for testing GUI interactions
 * in the Pawns Board game. There are also tests for the Color Scheme class's integrated 
 * methods in here as well.</p>
 *
 * <p>The test suite covers various aspects of the mock view, including:
 * <ul>
 *   <li>Initial state verification</li>
 *   <li>Card and cell highlighting mechanisms</li>
 *   <li>Player change simulation</li>
 *   <li>View refresh and selection clearing</li>
 *   <li>Visibility state tracking</li>
 *   <li>Listener registration and management</li>
 *   <li>Stub method implementations</li>
 * </ul>
 * </p>
 *
 * <p>The mock view is designed to capture and verify state changes without
 * actually rendering a graphical interface, making it ideal for unit testing
 * view-related interactions.</p>
 *
 * @see PawnsBoardGUIViewMock
 * @see PawnsBoardGUIView
 */
public class PawnsBoardGUIViewMockTest {

  /**
   * The mock view being tested.
   */
  private PawnsBoardGUIViewMock mockView;

  /**
   * Sets up a fresh mock view before each test method.
   *
   * <p>This method ensures that each test starts with a clean,
   * unmodified mock view instance.</p>
   */
  @Before
  public void setUp() {
    mockView = new PawnsBoardGUIViewMock();
  }

  /**
   * Tests the initial state of the mock view.
   *
   * <p>Verifies that a newly created mock view has:
   * <ul>
   *   <li>No highlighted card (index -1)</li>
   *   <li>No highlighted cell (row and column -1)</li>
   *   <li>No simulated player (null)</li>
   *   <li>Not refreshed</li>
   *   <li>No selections cleared</li>
   *   <li>Not visible</li>
   * </ul>
   * </p>
   */
  @Test
  public void testInitialState() {
    assertEquals("Initial highlighted card should be -1", -1,
            mockView.getHighlightedCardIndex());
    assertEquals("Initial highlighted row should be -1", -1,
            mockView.getHighlightedRow());
    assertEquals("Initial highlighted col should be -1", -1,
            mockView.getHighlightedCol());
    assertNull("Initial simulated player should be null",
            mockView.getSimulatedPlayer());
    assertFalse("Initial refreshed state should be false",
            mockView.wasRefreshed());
    assertFalse("Initial selections cleared state should be false",
            mockView.wereSelectionsCleared());
    assertFalse("Initial visibility should be false", mockView.isVisible());
  }

  /**
   * Tests the card highlighting functionality.
   *
   * <p>Ensures that when a card is highlighted:
   * <ul>
   *   <li>The highlighted card index is correctly updated</li>
   * </ul>
   * </p>
   */
  @Test
  public void testHighlightCard() {
    mockView.highlightCard(3);
    assertEquals("Highlighted card index should be updated", 3,
            mockView.getHighlightedCardIndex());
  }

  /**
   * Tests the cell highlighting functionality.
   *
   * <p>Verifies that when a cell is highlighted:
   * <ul>
   *   <li>The highlighted row is correctly updated</li>
   *   <li>The highlighted column is correctly updated</li>
   * </ul>
   * </p>
   */
  @Test
  public void testHighlightCell() {
    mockView.highlightCell(2, 4);
    assertEquals("Highlighted row should be updated", 2,
            mockView.getHighlightedRow());
    assertEquals("Highlighted col should be updated", 4,
            mockView.getHighlightedCol());
  }

  /**
   * Tests the player change simulation functionality.
   *
   * <p>Ensures that the simulated player can be updated correctly.</p>
   */
  @Test
  public void testSimulatePlayerChange() {
    mockView.simulatePlayerChange(PlayerColors.BLUE);
    assertEquals("Simulated player should be updated", PlayerColors.BLUE,
            mockView.getSimulatedPlayer());
  }

  /**
   * Tests the view refresh mechanism.
   *
   * <p>Verifies that calling refresh updates the view's state.</p>
   */
  @Test
  public void testRefresh() {
    mockView.refresh();
    assertTrue("Refreshed state should be updated", mockView.wasRefreshed());
  }

  /**
   * Tests the selection clearing functionality.
   *
   * <p>Ensures that when selections are cleared:
   * <ul>
   *   <li>Highlighted card is reset to -1</li>
   *   <li>Highlighted row is reset to -1</li>
   *   <li>Highlighted column is reset to -1</li>
   *   <li>Selections cleared state is updated</li>
   * </ul>
   * </p>
   */
  @Test
  public void testClearSelections() {
    // First set some selections
    mockView.highlightCard(3);
    mockView.highlightCell(2, 4);

    // Then clear them
    mockView.clearSelections();

    assertEquals("Highlighted card should be reset", -1,
            mockView.getHighlightedCardIndex());
    assertEquals("Highlighted row should be reset", -1,
            mockView.getHighlightedRow());
    assertEquals("Highlighted col should be reset", -1,
            mockView.getHighlightedCol());
    assertTrue("Selections cleared state should be updated",
            mockView.wereSelectionsCleared());
  }

  /**
   * Tests the visibility setting functionality.
   *
   * <p>Verifies that the view's visibility can be updated.</p>
   */
  @Test
  public void testSetVisible() {
    mockView.setVisible(true);
    assertTrue("Visibility should be updated", mockView.isVisible());
  }

  /**
   * Tests the toString method implementation.
   *
   * <p>Ensures that the mock view returns the expected string representation.</p>
   */
  @Test
  public void testToString() {
    assertEquals("toString should return expected value", "Mock View",
            mockView.toString());
  }

  /**
   * Tests the renderGameState method without a header.
   *
   * <p>Verifies that the method returns the expected default game state string.</p>
   */
  @Test
  public void testRenderGameState() {
    assertEquals("renderGameState should return expected value",
            "Mock Game State", mockView.renderGameState());
  }

  /**
   * Tests the renderGameState method with a header.
   *
   * <p>Ensures that the method correctly prepends a header to the game state string.</p>
   */
  @Test
  public void testRenderGameStateWithHeader() {
    assertEquals("renderGameState with header should return expected value",
            "--- Test Header ---\nMock Game State",
            mockView.renderGameState("Test Header"));
  }

  /**
   * Tests listener registration functionality.
   *
   * <p>Verifies that different types of listeners can be registered and
   * correctly tracked by the mock view:
   * <ul>
   *   <li>Card selection listeners</li>
   *   <li>Cell selection listeners</li>
   *   <li>Keyboard action listeners</li>
   * </ul>
   * </p>
   */
  @Test
  public void testListenerRegistration() {
    // Create mock listeners
    CardSelectionListener cardListener = new MockCardSelectionListener();
    CellSelectionListener cellListener = new MockCellSelectionListener();
    KeyboardActionListener keyListener = new MockKeyboardActionListener();

    // Register listeners
    mockView.addCardSelectionListener(cardListener);
    mockView.addCellSelectionListener(cellListener);
    mockView.addKeyboardActionListener(keyListener);

    // Verify registration
    assertTrue("Card listener should be registered",
            mockView.hasCardSelectionListener(cardListener));
    assertTrue("Cell listener should be registered",
            mockView.hasCellSelectionListener(cellListener));
    assertTrue("Keyboard listener should be registered",
            mockView.hasKeyboardActionListener(keyListener));
  }

  /**
   * Mock implementation of CardSelectionListener for testing purposes.
   *
   * <p>Provides a stub implementation that does nothing when a card is selected.</p>
   */
  private class MockCardSelectionListener implements CardSelectionListener {
    @Override
    public void onCardSelected(int cardIndex, PlayerColors player) {
      // Do nothing, just a stub for testing
    }
  }

  /**
   * Mock implementation of CellSelectionListener for testing purposes.
   *
   * <p>Provides a stub implementation that does nothing when a cell is selected.</p>
   */
  private class MockCellSelectionListener implements CellSelectionListener {
    @Override
    public void onCellSelected(int row, int col) {
      // Do nothing, just a stub for testing
    }
  }

  /**
   * Mock implementation of KeyboardActionListener for testing purposes.
   *
   * <p>Provides stub implementations for confirm and pass actions.</p>
   */
  private class MockKeyboardActionListener implements KeyboardActionListener {
    @Override
    public void onConfirmAction() {
      // Do nothing, just a stub for testing
    }

    @Override
    public void onPassAction() {
      // Do nothing, just a stub for testing
    }
  }

  //===============================================
  // Tests for New Color Scheme Integration
  //===============================================

  /**
   * Tests the initial state of the color scheme.
   *
   * <p>Verifies that a newly created mock view starts with the normal color scheme.</p>
   */
  @Test
  public void testInitialColorScheme() {
    assertEquals("Initial color scheme should be 'normal'",
            "normal", mockView.getCurrentColorScheme());
    assertFalse("Initial color scheme should not be high contrast",
            mockView.isHighContrastMode());
  }

  /**
   * Tests setting a specific color scheme.
   *
   * <p>Verifies that the color scheme can be changed to a specific scheme by name.</p>
   */
  @Test
  public void testSetColorScheme() {
    mockView.setColorScheme("high_contrast");
    assertEquals("Color scheme should be updated",
            "high_contrast", mockView.getCurrentColorScheme());
    assertTrue("High contrast mode should be true",
            mockView.isHighContrastMode());
    
    mockView.setColorScheme("normal");
    assertEquals("Color scheme should be reverted to normal",
            "normal", mockView.getCurrentColorScheme());
    assertFalse("High contrast mode should be false",
            mockView.isHighContrastMode());
  }

  /**
   * Tests toggling between color schemes.
   *
   * <p>Verifies that the color scheme can be toggled between normal and high contrast.</p>
   */
  @Test
  public void testToggleColorScheme() {
    // Start with normal scheme
    assertEquals("Initial color scheme should be 'normal'",
            "normal", mockView.getCurrentColorScheme());
    
    // First toggle should switch to high contrast
    mockView.toggleColorScheme();
    assertEquals("Color scheme should be switched to high contrast",
            "high_contrast", mockView.getCurrentColorScheme());
    assertTrue("High contrast mode should be true",
            mockView.isHighContrastMode());
    
    // Second toggle should switch back to normal
    mockView.toggleColorScheme();
    assertEquals("Color scheme should be switched back to normal",
            "normal", mockView.getCurrentColorScheme());
    assertFalse("High contrast mode should be false",
            mockView.isHighContrastMode());
  }

  /**
   * Tests the isHighContrastMode method.
   *
   * <p>Verifies that isHighContrastMode correctly reports the current mode.</p>
   */
  @Test
  public void testIsHighContrastMode() {
    // Default should be false (normal mode)
    assertFalse("Default should not be high contrast mode",
            mockView.isHighContrastMode());
    
    // Set to high contrast and check
    mockView.setColorScheme("high_contrast");
    assertTrue("Should now be in high contrast mode",
            mockView.isHighContrastMode());
    
    // Set back to normal and check
    mockView.setColorScheme("normal");
    assertFalse("Should now be back in normal mode",
            mockView.isHighContrastMode());
  }

  /**
   * Tests the getCurrentColorScheme method.
   *
   * <p>Verifies that getCurrentColorScheme correctly returns the current scheme name.</p>
   */
  @Test
  public void testGetCurrentColorScheme() {
    // Default should be "normal"
    assertEquals("Default color scheme should be 'normal'",
            "normal", mockView.getCurrentColorScheme());
    
    // Set to high contrast and check
    mockView.setColorScheme("high_contrast");
    assertEquals("Current color scheme should be 'high_contrast'",
            "high_contrast", mockView.getCurrentColorScheme());
  }
}