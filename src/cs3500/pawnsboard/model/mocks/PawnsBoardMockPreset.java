package cs3500.pawnsboard.model.mocks;

import cs3500.pawnsboard.controller.listeners.ModelStatusListener;
import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mock implementation of the PawnsBoard interface that allows setting predetermined legal moves
 * and game states for testing strategies.
 * This mock can be configured to return specific values for certain moves to create controlled
 * testing scenarios for different strategy implementations.
 *
 * @param <C> the type of Card used in this game
 * @param <E> the type of Cell used in this game's board
 */
public class PawnsBoardMockPreset<C extends Card, E extends PawnsBoardCell<C>>
        implements PawnsBoard<C, E> {

  // Game state
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

  // Preset configurations
  private final Map<String, Boolean> legalMoves;
  private final Map<String, int[]> moveRowScoreChanges;
  private final Map<String, int[]> moveTotalScoreChanges;
  private final Map<String, Map<String, Boolean>> cellOwnershipChanges;
  private PawnsBoard<C, E> copyReturnValue;
  private PawnsBoard<C, E> presetSimulationResult;

  /**
   * Constructs a mock PawnsBoard with preset behaviors.
   */
  public PawnsBoardMockPreset() {
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

    // Initialize preset configurations
    this.legalMoves = new HashMap<>();
    this.moveRowScoreChanges = new HashMap<>();
    this.moveTotalScoreChanges = new HashMap<>();
    this.cellOwnershipChanges = new HashMap<>();
    this.copyReturnValue = null;
    this.presetSimulationResult = null;
  }

  /**
   * Sets up a basic 3x5 initial board state with pawns at the edges.
   * This creates a standard starting board with RED pawns in the first column
   * and BLUE pawns in the last column, all with count 1.
   *
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setupInitialBoard() {
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
   * Sets whether a specific move (card index, row, col) is legal.
   *
   * @param cardIndex the index of the card
   * @param row       the row coordinate
   * @param col       the column coordinate
   * @param isLegal   whether the move is legal
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setLegalMove(int cardIndex, int row, int col, boolean isLegal) {
    legalMoves.put(moveKey(cardIndex, row, col), isLegal);
    return this;
  }

  /**
   * Sets the row score changes after making a move.
   *
   * @param cardIndex      the index of the card
   * @param row            the row coordinate
   * @param col            the column coordinate
   * @param redScoreDelta  the change in RED's score
   * @param blueScoreDelta the change in BLUE's score
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setMoveRowScoreChanges(int cardIndex, int row, int col,
                                                           int redScoreDelta, int blueScoreDelta) {
    moveRowScoreChanges.put(moveKey(cardIndex, row, col), new int[]{redScoreDelta, blueScoreDelta});
    return this;
  }

  /**
   * Sets the total score changes after making a move.
   *
   * @param cardIndex      the index of the card
   * @param row            the row coordinate
   * @param col            the column coordinate
   * @param redScoreDelta  the change in RED's total score
   * @param blueScoreDelta the change in BLUE's total score
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setMoveTotalScoreChanges(int cardIndex, int row, int col,
                                                             int redScoreDelta,
                                                             int blueScoreDelta) {
    moveTotalScoreChanges.put(moveKey(cardIndex, row, col),
            new int[]{redScoreDelta, blueScoreDelta});
    return this;
  }

  /**
   * Sets cell ownership changes that would occur after making a move.
   *
   * @param cardIndex   the index of the card
   * @param row         the row coordinate
   * @param col         the column coordinate
   * @param cellChanges map from "row,col" to true/false (true if RED owns it, false if BLUE)
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setCellOwnershipChanges(int cardIndex, int row, int col,
                                                            Map<String, Boolean> cellChanges) {
    cellOwnershipChanges.put(moveKey(cardIndex, row, col), new HashMap<>(cellChanges));
    return this;
  }

  /**
   * Sets a preset PawnsBoard to return from copy() when simulating the result of a move.
   *
   * @param simulationResult the mock board to return from copy()
   * @return this mock for method chaining
   */
  public PawnsBoardMockPreset<C, E> setPresetSimulationResult(
          PawnsBoardMockPreset<PawnsBoardBaseCard, ?> simulationResult) {
    this.presetSimulationResult = (PawnsBoard<C, E>) simulationResult;
    return this;
  }

  /**
   * Helper method to create a key for move configurations.
   *
   * @param cardIndex the card index
   * @param row       the row index
   * @param col       the column index
   * @return the move key in the format "cardIndex,row,col"
   */
  private String moveKey(int cardIndex, int row, int col) {
    return cardIndex + "," + row + "," + col;
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


  // Setter methods for state

  /**
   * Sets the game started status.
   *
   * @param started boolean value indicating if the game has started
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setGameStarted(boolean started) {
    this.gameStarted = started;
    return this;
  }

  /**
   * Sets the game over status.
   *
   * @param over boolean value indicating if the game is over
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setGameOver(boolean over) {
    this.gameOver = over;
    return this;
  }

  /**
   * Sets the current player.
   *
   * @param player the PlayerColors enum value representing the current player
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setCurrentPlayer(PlayerColors player) {
    this.currentPlayer = player;
    return this;
  }

  /**
   * Sets the dimensions of the board.
   *
   * @param rows the number of rows in the board
   * @param cols the number of columns in the board
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setBoardDimensions(int rows, int cols) {
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
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setCellContent(int row, int col, CellContent content) {
    cellContents.put(cellKey(row, col), content);
    return this;
  }

  /**
   * Sets the owner of a specific cell.
   *
   * @param row   the row of the cell
   * @param col   the column of the cell
   * @param owner the PlayerColors enum value representing the owner of the cell
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setCellOwner(int row, int col, PlayerColors owner) {
    cellOwners.put(cellKey(row, col), owner);
    return this;
  }

  /**
   * Sets the pawn count for a specific cell.
   *
   * @param row   the row of the cell
   * @param col   the column of the cell
   * @param count the number of pawns to set for the cell
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setPawnCount(int row, int col, int count) {
    pawnCounts.put(cellKey(row, col), count);
    return this;
  }

  /**
   * Sets a card at a specific cell.
   *
   * @param row  the row of the cell
   * @param col  the column of the cell
   * @param card the card to place at the specified cell
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setCardAtCell(int row, int col, C card) {
    cellCards.put(cellKey(row, col), card);
    return this;
  }

  /**
   * Sets the scores for a specific row.
   *
   * @param row       the row to set scores for
   * @param redScore  the score for the red player in this row
   * @param blueScore the score for the blue player in this row
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setRowScores(int row, int redScore, int blueScore) {
    rowScores.put(row, new int[]{redScore, blueScore});
    return this;
  }

  /**
   * Sets the total score for both players.
   *
   * @param redTotal  the total score for the red player
   * @param blueTotal the total score for the blue player
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setTotalScore(int redTotal, int blueTotal) {
    this.totalScore = new int[]{redTotal, blueTotal};
    return this;
  }

  /**
   * Sets the winner of the game.
   *
   * @param winner the PlayerColors enum value representing the winner
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setWinner(PlayerColors winner) {
    this.winner = winner;
    return this;
  }

  /**
   * Sets the hand of cards for a specific player.
   *
   * @param player the PlayerColors enum value representing the player
   * @param hand   the list of cards to set as the player's hand
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setPlayerHand(PlayerColors player, List<C> hand) {
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
   * @return this PawnsBoardMockPreset instance for method chaining
   */
  public PawnsBoardMockPreset<C, E> setRemainingDeckSize(PlayerColors player, int deckSize) {
    if (player == PlayerColors.RED) {
      redDeckSize = deckSize;
    } else {
      blueDeckSize = deckSize;
    }
    return this;
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
    // Do nothing, this is a mock
  }

  @Override
  public boolean isGameOver() {
    return gameOver;
  }


  /**
   * Getter for the gameStartedVariable.
   *
   * @return the game started variable
   */
  public boolean getGameStarted() {
    return this.gameStarted;
  }

  @Override
  public PlayerColors getCurrentPlayer() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return currentPlayer;
  }

  @Override
  public void placeCard(int cardIndex, int row, int col)
          throws IllegalArgumentException, IllegalStateException, IllegalAccessException,
          IllegalOwnerException, IllegalCardException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over");
    }
    validateCoordinates(row, col);
    String key = moveKey(cardIndex, row, col);
    if (legalMoves.containsKey(key) && !legalMoves.get(key)) {
      throw new IllegalAccessException("Move is not legal");
    }
    if (moveRowScoreChanges.containsKey(key)) {
      int[] changes = moveRowScoreChanges.get(key);
      int[] currentScores = rowScores.getOrDefault(row, new int[]{0, 0});
      int newRedScore = currentScores[0] + changes[0];
      int newBlueScore = currentScores[1] + changes[1];
      rowScores.put(row, new int[]{newRedScore, newBlueScore});
    }
    if (moveTotalScoreChanges.containsKey(key)) {
      int[] changes = moveTotalScoreChanges.get(key);
      totalScore[0] += changes[0];
      totalScore[1] += changes[1];
    }
    if (cellOwnershipChanges.containsKey(key)) {
      Map<String, Boolean> changes = cellOwnershipChanges.get(key);
      for (Map.Entry<String, Boolean> entry : changes.entrySet()) {
        String cellKey = entry.getKey();
        Boolean isRed = entry.getValue();
        cellOwners.put(cellKey, isRed ? PlayerColors.RED : PlayerColors.BLUE);
      }
    }
    String cellKey = cellKey(row, col);
    cellContents.put(cellKey, CellContent.CARD);
    cellOwners.put(cellKey, currentPlayer);
    pawnCounts.put(cellKey, 0);

    if (currentPlayer == PlayerColors.RED && cardIndex < redHand.size()) {
      C card = redHand.remove(cardIndex);
      cellCards.put(cellKey, card);
    } else if (currentPlayer == PlayerColors.BLUE && cardIndex < blueHand.size()) {
      C card = blueHand.remove(cardIndex);
      cellCards.put(cellKey, card);
    }

    // Switch player
    currentPlayer = (currentPlayer == PlayerColors.RED) ? PlayerColors.BLUE : PlayerColors.RED;
  }

  @Override
  public void passTurn() throws IllegalStateException, IllegalOwnerException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over");
    }

    // Switch player
    currentPlayer = (currentPlayer == PlayerColors.RED) ? PlayerColors.BLUE : PlayerColors.RED;
  }

  @Override
  public int[] getBoardDimensions() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return new int[]{rows, columns};
  }

  @Override
  public CellContent getCellContent(int row, int col) {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    return cellContents.getOrDefault(cellKey(row, col), CellContent.EMPTY);
  }

  @Override
  public PlayerColors getCellOwner(int row, int col) {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    return cellOwners.get(cellKey(row, col));
  }

  @Override
  public int getPawnCount(int row, int col) {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    return pawnCounts.getOrDefault(cellKey(row, col), 0);
  }

  @Override
  public List<C> getPlayerHand(PlayerColors playerColors) {
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
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    return totalScore.clone();
  }

  @Override
  public PlayerColors getWinner() {
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
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    validateCoordinates(row, col);
    return cellCards.get(cellKey(row, col));
  }

  @Override
  public boolean isLegalMove(int cardIndex, int row, int col) {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over");
    }
    validateCoordinates(row, col);

    String key = moveKey(cardIndex, row, col);
    if (legalMoves.containsKey(key)) {
      return legalMoves.get(key);
    }

    // Default to checking if card index is valid
    List<C> hand = getPlayerHand(currentPlayer);
    return cardIndex >= 0 && cardIndex < hand.size();
  }

  @Override
  public PawnsBoard<C, E> copy() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }

    // Return preset simulation result if available
    if (presetSimulationResult != null) {
      return presetSimulationResult;
    }

    // Otherwise, return copy of this mock with same state
    if (copyReturnValue != null) {
      return copyReturnValue;
    }

    // Create a new copy with same state
    PawnsBoardMockPreset<C, E> copy = new PawnsBoardMockPreset<>();

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

    // Also copy preset configurations
    copy.legalMoves.putAll(this.legalMoves);
    copy.moveRowScoreChanges.putAll(this.moveRowScoreChanges);
    copy.moveTotalScoreChanges.putAll(this.moveTotalScoreChanges);

    for (Map.Entry<String, Map<String, Boolean>> entry : this.cellOwnershipChanges.entrySet()) {
      Map<String, Boolean> cellChanges = new HashMap<>(entry.getValue());
      copy.cellOwnershipChanges.put(entry.getKey(), cellChanges);
    }

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