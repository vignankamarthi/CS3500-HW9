package cs3500.pawnsboard.model.mocks;

import cs3500.pawnsboard.controller.listeners.ModelStatusListener;
import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mock implementation of the PawnsBoard interface that tracks method calls for testing
 * strategies.
 * Records which coordinates are checked, which card indices are used, and the order of operations.
 * This allows verification of strategy search patterns and behavior.
 *
 * @param <C> the type of Card used in this game
 * @param <E> the type of Cell used in this game's board
 */
public class PawnsBoardMockTracker<C extends Card, E extends PawnsBoardCell<C>>
        implements PawnsBoard<C, E> {

  // Game state - similar to other mocks
  private boolean gameStarted;
  private boolean gameOver;
  private PlayerColors currentPlayer;
  private int rows;
  private int columns;
  private final Map<String, CellContent> cellContents;
  private final Map<String, PlayerColors> cellOwners;
  private final Map<String, Integer> pawnCounts;
  private final Map<String, C> cellCards;
  private final Map<Integer, int[]> rowScores;
  private final List<C> redHand;
  private final List<C> blueHand;
  private int[] totalScore;
  private PlayerColors winner;
  private int redDeckSize;
  private int blueDeckSize;

  // Tracking variables
  private final List<String> methodCallLog;
  private final List<int[]> cellsCheckedInOrder;
  private final List<Integer> cardIndicesCheckedInOrder;
  private final Map<String, Integer> methodCallCounts;
  private boolean returnAllLegalMoves;
  private List<int[]> legalMoveCoordinates;


  /**
   * Constructs a mock PawnsBoard that tracks method calls.
   */
  public PawnsBoardMockTracker() {
    this.gameStarted = false;
    this.gameOver = false;
    this.currentPlayer = PlayerColors.RED;
    this.rows = 0;
    this.columns = 0;
    this.cellContents = new HashMap<>();
    this.cellOwners = new HashMap<>();
    this.pawnCounts = new HashMap<>();
    this.cellCards = new HashMap<>();
    this.rowScores = new HashMap<>();
    this.redHand = new ArrayList<>();
    this.blueHand = new ArrayList<>();
    this.totalScore = new int[]{0, 0};
    this.winner = null;
    this.redDeckSize = 0;
    this.blueDeckSize = 0;

    // Initialize tracking variables
    this.methodCallLog = new ArrayList<>();
    this.cellsCheckedInOrder = new ArrayList<>();
    this.cardIndicesCheckedInOrder = new ArrayList<>();
    this.methodCallCounts = new HashMap<>();
    this.returnAllLegalMoves = true;
    this.legalMoveCoordinates = new ArrayList<>();

  }

  /**
   * Sets up a basic 3x5 initial board state with pawns at the edges.
   * This creates a standard starting board with RED pawns in the first column
   * and BLUE pawns in the last column, all with count 1.
   *
   * @return this mock for method chaining
   */
  public PawnsBoardMockTracker<C, E> setupInitialBoard() {
    this.gameStarted = true;
    this.gameOver = false;
    this.currentPlayer = PlayerColors.RED;
    this.rows = 3;
    this.columns = 5;

    // Clear previous state
    this.cellContents.clear();
    this.cellOwners.clear();
    this.pawnCounts.clear();

    // Set up RED pawns in first column
    for (int r = 0; r < 3; r++) {
      setCellContent(r, 0, CellContent.PAWNS);
      setCellOwner(r, 0, PlayerColors.RED);
      setPawnCount(r, 0, 1);
    }

    // Set up BLUE pawns in last column
    for (int r = 0; r < 3; r++) {
      setCellContent(r, 4, CellContent.PAWNS);
      setCellOwner(r, 4, PlayerColors.BLUE);
      setPawnCount(r, 4, 1);
    }

    // Set row scores to 0
    for (int r = 0; r < 3; r++) {
      setRowScores(r, 0, 0);
    }

    setTotalScore(0, 0);

    return this;
  }

  /**
   * Sets which coordinates should be considered legal for isLegalMove.
   * If empty, all coordinates will be considered illegal unless returnAllLegalMoves is true.
   *
   * @param coordinates array of [row, col, cardIndex] arrays
   * @return this mock for method chaining
   */
  public PawnsBoardMockTracker<C, E> setLegalMoveCoordinates(List<int[]> coordinates) {
    this.legalMoveCoordinates = new ArrayList<>(coordinates);
    return this;
  }

  /**
   * Sets whether isLegalMove should return true for all coordinates.
   *
   * @param returnAll if true, all coordinates will be considered legal
   * @return this mock for method chaining
   */
  public PawnsBoardMockTracker<C, E> setReturnAllLegalMoves(boolean returnAll) {
    this.returnAllLegalMoves = returnAll;
    return this;
  }

  /**
   * Get the isGameStarted variable.
   */
  public boolean getGameStarted() {
    return gameStarted;
  }

  /**
   * Gets the log of method calls in order.
   *
   * @return the method call log
   */
  public List<String> getMethodCallLog() {
    return new ArrayList<>(methodCallLog);
  }

  /**
   * Gets the cells that were checked in order.
   *
   * @return list of [row, col] arrays in the order they were checked
   */
  public List<int[]> getCellsCheckedInOrder() {
    return new ArrayList<>(cellsCheckedInOrder);
  }

  /**
   * Gets the card indices that were checked in order.
   *
   * @return list of card indices in the order they were checked
   */
  public List<Integer> getCardIndicesCheckedInOrder() {
    return new ArrayList<>(cardIndicesCheckedInOrder);
  }

  /**
   * Gets the number of times a method was called.
   *
   * @param methodName the name of the method
   * @return the number of times the method was called
   */
  public int getMethodCallCount(String methodName) {
    return methodCallCounts.getOrDefault(methodName, 0);
  }

  /**
   * Clears all tracking data.
   *
   * @return this mock for method chaining
   */
  public PawnsBoardMockTracker<C, E> clearTracking() {
    methodCallLog.clear();
    cellsCheckedInOrder.clear();
    cardIndicesCheckedInOrder.clear();
    methodCallCounts.clear();
    return this;
  }


  /**
   * Helper method to log a method call.
   *
   * @param methodName the name of the method
   * @param args       the arguments to the method, joined by commas
   */
  private void logMethodCall(String methodName, String args) {
    String logEntry = methodName + "(" + args + ")";
    methodCallLog.add(logEntry);
    methodCallCounts.put(methodName, methodCallCounts.getOrDefault(methodName, 0) + 1);
  }

  // Setter methods for state

  /**
   * Sets the game started status.
   *
   * @param started boolean value indicating if the game has started
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setGameStarted(boolean started) {
    this.gameStarted = started;
    return this;
  }

  /**
   * Sets the game over status.
   *
   * @param over boolean value indicating if the game is over
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setGameOver(boolean over) {
    this.gameOver = over;
    return this;
  }

  /**
   * Sets the current player.
   *
   * @param player the PlayerColors enum value representing the current player
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setCurrentPlayer(PlayerColors player) {
    this.currentPlayer = player;
    return this;
  }

  /**
   * Sets the dimensions of the board.
   *
   * @param rows the number of rows in the board
   * @param cols the number of columns in the board
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setBoardDimensions(int rows, int cols) {
    this.rows = rows;
    this.columns = cols;
    return this;
  }

  /**
   * Sets the content of a specific cell.
   *
   * @param row     the row of the cell
   * @param col     the column of the cell
   * @param content the cell content desired to set
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setCellContent(int row, int col, CellContent content) {
    cellContents.put(cellKey(row, col), content);
    return this;
  }

  /**
   * Sets the owner of a specific cell.
   *
   * @param row   the row of the cell
   * @param col   the column of the cell
   * @param owner the PlayerColors enum value representing the owner of the cell
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setCellOwner(int row, int col, PlayerColors owner) {
    cellOwners.put(cellKey(row, col), owner);
    return this;
  }

  /**
   * Sets the pawn count for a specific cell.
   *
   * @param row   the row of the cell
   * @param col   the column of the cell
   * @param count the number of pawns to set for the cell
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setPawnCount(int row, int col, int count) {
    pawnCounts.put(cellKey(row, col), count);
    return this;
  }

  /**
   * Sets a card at a specific cell.
   *
   * @param row  the row of the cell
   * @param col  the column of the cell
   * @param card the card to place at the specified cell
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setCardAtCell(int row, int col, C card) {
    cellCards.put(cellKey(row, col), card);
    return this;
  }

  /**
   * Sets the scores for a specific row.
   *
   * @param row       the row to set scores for
   * @param redScore  the score for the red player in this row
   * @param blueScore the score for the blue player in this row
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setRowScores(int row, int redScore, int blueScore) {
    rowScores.put(row, new int[]{redScore, blueScore});
    return this;
  }

  /**
   * Sets the total score for both players.
   *
   * @param redTotal  the total score for the red player
   * @param blueTotal the total score for the blue player
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setTotalScore(int redTotal, int blueTotal) {
    this.totalScore = new int[]{redTotal, blueTotal};
    return this;
  }

  /**
   * Sets the winner of the game.
   *
   * @param winner the PlayerColors enum value representing the winner
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setWinner(PlayerColors winner) {
    this.winner = winner;
    return this;
  }

  /**
   * Sets the hand of cards for a specific player.
   *
   * @param player the PlayerColors enum value representing the player
   * @param hand   the list of cards to set as the player's hand
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setPlayerHand(PlayerColors player, List<C> hand) {
    if (player == PlayerColors.RED) {
      redHand.clear();
      redHand.addAll(hand);
    } else {
      blueHand.clear();
      blueHand.addAll(hand);
    }
    return this;
  }

  /**
   * Sets the remaining deck size for a specific player.
   *
   * @param player   the PlayerColors enum value representing the player
   * @param deckSize the number of cards remaining in the player's deck
   * @return this PawnsBoardMockTracker instance for method chaining
   */
  public PawnsBoardMockTracker<C, E> setRemainingDeckSize(PlayerColors player, int deckSize) {
    if (player == PlayerColors.RED) {
      redDeckSize = deckSize;
    } else {
      blueDeckSize = deckSize;
    }
    return this;
  }

  /**
   * Helper method to create a key for the cell maps.
   *
   * @param row the row index
   * @param col the column index
   * @return the cell key in the format "row,col"
   */
  private String cellKey(int row, int col) {
    return row + "," + col;
  }

  // PawnsBoard interface implementations

  /**
   * Registers a listener for model status events.
   *
   * @param listener the listener to add
   */
  @Override
  public void addModelStatusListener(ModelStatusListener listener) {
    throw new UnsupportedOperationException("THis mock is meant to test strategic logic, not " +
            "listeners.");
  }

  /**
   * Unregisters a listener for model status events.
   *
   * @param listener the listener to remove
   */
  @Override
  public void removeModelStatusListener(ModelStatusListener listener) {
    throw new UnsupportedOperationException("THis mock is meant to test strategic logic, not " +
            "listeners.");
  }

  @Override
  public void startGame(int rows, int cols, String redDeckConfigPath,
                        String blueDeckConfigPath, int startingHandSize)
          throws IllegalArgumentException, InvalidDeckConfigurationException {
    logMethodCall("startGame", rows + "," + cols + "," + redDeckConfigPath + ","
            + blueDeckConfigPath + "," + startingHandSize);
    // Do nothing, this is a mock
  }

  @Override
  public boolean isGameOver() {
    logMethodCall("isGameOver", "");
    return gameOver;
  }

  @Override
  public PlayerColors getCurrentPlayer() {
    logMethodCall("getCurrentPlayer", "");
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return currentPlayer;
  }

  @Override
  public void placeCard(int cardIndex, int row, int col) {
    logMethodCall("placeCard", cardIndex + "," + row + "," + col);
    // Do nothing, this is a mock
  }

  @Override
  public void passTurn() {
    logMethodCall("passTurn", "");
    // Do nothing, this is a mock
  }

  @Override
  public int[] getBoardDimensions() {
    logMethodCall("getBoardDimensions", "");
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return new int[]{rows, columns};
  }

  @Override
  public CellContent getCellContent(int row, int col) {
    logMethodCall("getCellContent", row + "," + col);
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    cellsCheckedInOrder.add(new int[]{row, col});
    return cellContents.getOrDefault(cellKey(row, col), CellContent.EMPTY);
  }

  @Override
  public PlayerColors getCellOwner(int row, int col) {
    logMethodCall("getCellOwner", row + "," + col);
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    cellsCheckedInOrder.add(new int[]{row, col});
    return cellOwners.get(cellKey(row, col));
  }

  @Override
  public int getPawnCount(int row, int col) {
    logMethodCall("getPawnCount", row + "," + col);
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    cellsCheckedInOrder.add(new int[]{row, col});
    return pawnCounts.getOrDefault(cellKey(row, col), 0);
  }

  @Override
  public List<C> getPlayerHand(PlayerColors playerColors) {
    logMethodCall("getPlayerHand", playerColors.toString());
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (playerColors == null) {
      throw new IllegalArgumentException("PlayerColors cannot be null");
    }
    return new ArrayList<>(playerColors == PlayerColors.RED ? redHand : blueHand);
  }

  @Override
  public int getRemainingDeckSize(PlayerColors playerColors) {
    logMethodCall("getRemainingDeckSize", playerColors.toString());
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (playerColors == null) {
      throw new IllegalArgumentException("PlayerColors cannot be null");
    }
    return playerColors == PlayerColors.RED ? redDeckSize : blueDeckSize;
  }

  @Override
  public int[] getRowScores(int row) {
    logMethodCall("getRowScores", Integer.toString(row));
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("Row index out of bounds: " + row);
    }
    return rowScores.getOrDefault(row, new int[]{0, 0});
  }

  @Override
  public int[] getTotalScore() {
    logMethodCall("getTotalScore", "");
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return totalScore.clone();
  }

  @Override
  public PlayerColors getWinner() {
    logMethodCall("getWinner", "");
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (!gameOver) {
      throw new IllegalStateException("Game is not over yet");
    }
    return winner;
  }

  @Override
  public C getCardAtCell(int row, int col) {
    logMethodCall("getCardAtCell", row + "," + col);
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    cellsCheckedInOrder.add(new int[]{row, col});
    return cellCards.get(cellKey(row, col));
  }

  @Override
  public boolean isLegalMove(int cardIndex, int row, int col) {
    logMethodCall("isLegalMove", cardIndex + "," + row + "," + col);
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over");
    }
    validateCoordinates(row, col);

    cellsCheckedInOrder.add(new int[]{row, col});
    cardIndicesCheckedInOrder.add(cardIndex);

    if (returnAllLegalMoves) {
      List<C> hand = getPlayerHand(currentPlayer);
      return cardIndex >= 0 && cardIndex < hand.size();
    } else {
      // Check if this coordinate is in the list of legal moves
      for (int[] move : legalMoveCoordinates) {
        if (move.length == 3 && move[0] == row && move[1] == col && move[2] == cardIndex) {
          return true;
        }
      }
      return false;
    }
  }

  @Override
  public PawnsBoard<C, E> copy() {
    logMethodCall("copy", "");
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }

    // Create a copy with the same state
    PawnsBoardMockTracker<C, E> copy = new PawnsBoardMockTracker<>();

    // Copy game state
    copy.gameStarted = this.gameStarted;
    copy.gameOver = this.gameOver;
    copy.currentPlayer = this.currentPlayer;
    copy.rows = this.rows;
    copy.columns = this.columns;
    copy.cellContents.putAll(this.cellContents);
    copy.cellOwners.putAll(this.cellOwners);
    copy.pawnCounts.putAll(this.pawnCounts);
    copy.cellCards.putAll(this.cellCards);
    copy.rowScores.putAll(this.rowScores);
    copy.redHand.addAll(this.redHand);
    copy.blueHand.addAll(this.blueHand);
    copy.totalScore = this.totalScore.clone();
    copy.winner = this.winner;
    copy.redDeckSize = this.redDeckSize;
    copy.blueDeckSize = this.blueDeckSize;

    // Copy configuration but NOT tracking data
    copy.returnAllLegalMoves = this.returnAllLegalMoves;
    copy.legalMoveCoordinates = new ArrayList<>(this.legalMoveCoordinates);

    // Clear all tracking collections in the copy
    copy.clearTracking();

    return copy;
  }

  /**
   * Validates that the given coordinates are within the board bounds.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  private void validateCoordinates(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= columns) {
      throw new IllegalArgumentException(
              "Invalid coordinates: (" + row + ", " + col + ")");
    }
  }
}