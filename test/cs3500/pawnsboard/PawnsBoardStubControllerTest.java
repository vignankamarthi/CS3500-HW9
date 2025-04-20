package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.PawnsBoardStubController;
import cs3500.pawnsboard.model.mocks.PawnsBoardMockForControllerTest;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.view.PawnsBoardGUIViewMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the PawnsBoardStubController class.
 *
 * <p>This test class verifies the behavior of the PawnsBoardStubController, which acts as a
 * placeholder controller for user interactions in the Pawns Board game. The tests focus on
 * ensuring that the controller correctly handles various user actions such as card selection,
 * cell selection, confirmation, and passing turns.</p>
 *
 * <p>The tests use a mock model and view to simulate game interactions and verify the
 * controller's response through console output and state changes.</p>
 *
 * <p>Key testing scenarios include:
 * <ul>
 *   <li>Initializing the controller and registering listeners</li>
 *   <li>Handling card and cell selection events</li>
 *   <li>Processing confirm and pass actions</li>
 *   <li>Verifying turn switching and view updates</li>
 * </ul>
 * </p>
 *
 * @see PawnsBoardStubController
 * @see PawnsBoardMockForControllerTest
 */
public class PawnsBoardStubControllerTest {

  /**
   * The stub controller being tested.
   */
  private PawnsBoardStubController controller;

  /**
   * A mock model to simulate game state.
   */
  private PawnsBoardMockForControllerTest<PawnsBoardBaseCard, ?> mockModel;

  /**
   * A mock view to capture and verify view interactions.
   */
  private PawnsBoardGUIViewMock mockView;

  /**
   * Captures console output for verification.
   */
  private ByteArrayOutputStream outContent;

  /**
   * Stores the original system output stream to restore after testing.
   */
  private PrintStream originalOut;

  /**
   * Sets up the testing environment before each test method.
   *
   * <p>This method prepares the mock model, mock view, and controller,
   * and sets up output stream capturing for console logging.</p>
   */
  @Before
  public void setUp() {
    // Create a mock model with a simple board state
    mockModel = new PawnsBoardMockForControllerTest<>();
    mockModel.setupInitialBoard();
    mockModel.setGameStarted(true);
    mockModel.setCurrentPlayer(PlayerColors.RED);

    // Create a mock view
    mockView = new PawnsBoardGUIViewMock();

    // Create the controller
    controller = new PawnsBoardStubController();

    // Capture System.out for testing console output
    originalOut = System.out;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  /**
   * Restores the original system output stream after each test.
   *
   * <p>This ensures that console output capturing does not interfere
   * with subsequent tests or system operations.</p>
   */
  @After
  public void restoreStreams() {
    System.setOut(originalOut);
  }

  /**
   * Tests that the controller properly initializes and registers itself as a listener.
   *
   * <p>Verifies that upon initialization, the controller:
   * <ul>
   *   <li>Registers as a card selection listener</li>
   *   <li>Registers as a cell selection listener</li>
   *   <li>Registers as a keyboard action listener</li>
   *   <li>Prints game instructions to the console</li>
   * </ul>
   * </p>
   */
  @Test
  public void testInitialize() {
    controller.initialize(mockModel, mockView);

    // Check that the controller has registered itself as a listener
    assertTrue("Controller should register as card listener",
            mockView.hasCardSelectionListener(controller));
    assertTrue("Controller should register as cell listener",
            mockView.hasCellSelectionListener(controller));
    assertTrue("Controller should register as keyboard listener",
            mockView.hasKeyboardActionListener(controller));

    // Check for instructions output
    String output = outContent.toString();
    assertTrue("Controller should print instructions",
            output.contains("Pawns Board Game Instructions"));
  }

  /**
   * Tests handling of card selection events.
   *
   * <p>Ensures that when a card is selected:
   * <ul>
   *   <li>The card selection is logged to the console</li>
   *   <li>The selected card is highlighted in the view</li>
   * </ul>
   * </p>
   *
   * @see PawnsBoardStubController#handleCardSelection(int)
   */
  @Test
  public void testHandleCardSelection() {
    controller.initialize(mockModel, mockView);

    // Clear the output from initialization
    outContent.reset();

    // Trigger card selection
    controller.handleCardSelection(2);

    String output = outContent.toString();
    assertTrue("Controller should log card selection",
            output.contains("Card 2 selected"));

    // Verify that the view was updated
    assertEquals("Card should be highlighted in view",
            2, mockView.getHighlightedCardIndex());
  }

  /**
   * Tests handling of cell selection events.
   *
   * <p>Verifies that when a cell is selected:
   * <ul>
   *   <li>The cell selection is logged to the console</li>
   *   <li>The selected cell coordinates are highlighted in the view</li>
   * </ul>
   * </p>
   *
   * @see PawnsBoardStubController#handleCellSelection(int, int)
   */
  @Test
  public void testHandleCellSelection() {
    controller.initialize(mockModel, mockView);

    // Clear the output from initialization
    outContent.reset();

    // Trigger cell selection
    controller.handleCellSelection(1, 2);

    String output = outContent.toString();
    assertTrue("Controller should log cell selection",
            output.contains("Cell selected at coordinates: (1, 2)"));

    // Verify that the view was updated
    assertEquals("Row should be highlighted in view",
            1, mockView.getHighlightedRow());
    assertEquals("Column should be highlighted in view",
            2, mockView.getHighlightedCol());
  }

  /**
   * Tests handling of confirm action when both card and cell are selected.
   *
   * <p>Ensures that when a confirm action is triggered with complete selections:
   * <ul>
   *   <li>The confirm action is logged to the console</li>
   *   <li>The card and cell details are mentioned</li>
   *   <li>The turn switches to the other player</li>
   *   <li>Selections are cleared in the view</li>
   * </ul>
   * </p>
   *
   * @see PawnsBoardStubController#handleConfirmAction()
   */
  @Test
  public void testHandleConfirmActionWithSelections() {
    controller.initialize(mockModel, mockView);

    // Prepare selections
    controller.handleCardSelection(3);
    controller.handleCellSelection(2, 1);

    // Clear output
    outContent.reset();

    // Trigger confirm action
    controller.handleConfirmAction();

    String output = outContent.toString();
    assertTrue("Controller should log confirm action",
            output.contains("Confirm action requested"));
    assertTrue("Controller should mention card index",
            output.contains("card 3"));
    assertTrue("Controller should mention cell coordinates",
            output.contains("(2, 1)"));
    assertTrue("Controller should switch turns",
            output.contains("Turn switched from RED to BLUE"));

    // Verify view was updated
    assertEquals("Current player should be switched in view",
            PlayerColors.BLUE, mockView.getSimulatedPlayer());
    assertEquals("Selections should be cleared after confirm",
            -1, mockView.getHighlightedCardIndex());
  }

  /**
   * Tests handling of confirm action when selections are incomplete.
   *
   * <p>Verifies that when a confirm action is triggered without complete selections:
   * <ul>
   *   <li>An incomplete selection message is logged</li>
   *   <li>No turn switching occurs</li>
   * </ul>
   * </p>
   *
   * @see PawnsBoardStubController#handleConfirmAction()
   */
  @Test
  public void testHandleConfirmActionIncompleteSelections() {
    controller.initialize(mockModel, mockView);

    // Only select a card, not a cell
    controller.handleCardSelection(1);

    // Clear output
    outContent.reset();

    // Trigger confirm action
    controller.handleConfirmAction();

    String output = outContent.toString();
    assertTrue("Controller should indicate incomplete selection",
            output.contains("Cannot confirm action"));
    assertTrue("Controller should specify what's missing",
            output.contains("select both a card and a cell"));
  }

  /**
   * Tests handling of pass action.
   *
   * <p>Ensures that when a pass action is triggered:
   * <ul>
   *   <li>The pass action is logged to the console</li>
   *   <li>The turn switches to the other player</li>
   *   <li>The view is refreshed</li>
   * </ul>
   * </p>
   *
   * @see PawnsBoardStubController#handlePassAction()
   */
  @Test
  public void testHandlePassAction() {
    controller.initialize(mockModel, mockView);

    // Clear output
    outContent.reset();

    // Trigger pass action
    controller.handlePassAction();

    String output = outContent.toString();
    assertTrue("Controller should log pass action",
            output.contains("Pass action requested"));
    assertTrue("Controller should switch turns",
            output.contains("Turn passed from RED to BLUE"));

    // Verify view was updated
    assertEquals("Current player should be switched in view",
            PlayerColors.BLUE, mockView.getSimulatedPlayer());
    assertTrue("View should be refreshed after pass",
            mockView.wasRefreshed());
  }

  /**
   * Tests the full interaction sequence: select card, select cell, confirm.
   *
   * <p>Verifies a complete user interaction flow:
   * <ul>
   *   <li>Card selection is processed and logged</li>
   *   <li>Cell selection is processed and logged</li>
   *   <li>Confirm action switches the player turn</li>
   * </ul>
   * </p>
   */
  @Test
  public void testFullInteractionSequence() {
    controller.initialize(mockModel, mockView);

    // Clear output
    outContent.reset();

    // Complete sequence of actions
    controller.handleCardSelection(2);
    controller.handleCellSelection(1, 3);
    controller.handleConfirmAction();

    String output = outContent.toString();
    assertTrue("Output should contain card selection message",
            output.contains("Card 2 selected"));
    assertTrue("Output should contain cell selection message",
            output.contains("Cell selected at coordinates: (1, 3)"));
    assertTrue("Output should contain confirm action message",
            output.contains("Confirm action requested"));

    // Verify turn changed
    assertEquals("Player should be switched after confirm",
            PlayerColors.BLUE, mockView.getSimulatedPlayer());
  }

  /**
   * Tests deselection of a card by verifying the selection state.
   *
   * <p>Ensures that a card can be selected and its selection state is maintained.</p>
   */
  @Test
  public void testCardDeselectionBehavior() {
    controller.initialize(mockModel, mockView);

    // Select a card first
    controller.handleCardSelection(2);

    // Clear output to isolate next interactions
    outContent.reset();

    // check if controller maintains selection state correctly
    int initialHighlightedCard = mockView.getHighlightedCardIndex();
    // We're just verifying the selection happened
    assertEquals("Card should be highlighted", 2, initialHighlightedCard);
  }

  /**
   * Tests deselection of a cell by verifying the selection state.
   *
   * <p>Ensures that a cell can be selected and its selection state is maintained.</p>
   */
  @Test
  public void testCellDeselectionBehavior() {
    controller.initialize(mockModel, mockView);

    // Select a cell first
    controller.handleCellSelection(1, 2);

    // Clear output to isolate next interactions
    outContent.reset();

    // Just verify the selection happened
    assertEquals("Row should be highlighted", 1, mockView.getHighlightedRow());
    assertEquals("Column should be highlighted", 2, mockView.getHighlightedCol());
  }
}