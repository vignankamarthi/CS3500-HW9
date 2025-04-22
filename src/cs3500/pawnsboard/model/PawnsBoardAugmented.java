package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.deckbuilder.DeckBuilder;
import cs3500.pawnsboard.model.cards.deckbuilder.PawnsBoardAugmentedDeckBuilder;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.model.exceptions.InvalidDeckConfigurationException;
import cs3500.pawnsboard.model.influence.Influence;
import cs3500.pawnsboard.model.influence.InfluenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AugmentedPawnsBoard interface for a rectangular board with
 * support for upgrading and devaluing influences.
 * 
 * <p>This class extends AbstractPawnsBoard and implements AugmentedPawnsBoard,
 * providing all the functionality of the base game along with the new influence types.
 * It uses PawnsBoardAugmentedCell to track value modifiers and PawnsBoardAugmentedCard
 * to represent cards with different influence types.</p>
 *
 * <p>When a devaluing influence reduces a card's effective value to 0 or less,
 * the card is removed and replaced with pawns equal to its cost (up to 3).</p>
 *
 * @param <C> the type of Card used in this game
 */
//TODO: Create base testing file
//TODO: Create integration tests for model (mock textual view - to be made)
public class PawnsBoardAugmented<C extends PawnsBoardAugmentedCard> 
        extends AbstractPawnsBoard<C, PawnsBoardAugmentedCell<C>>
        implements AugmentedPawnsBoard<C, PawnsBoardAugmentedCell<C>> {

  // Board with augmented cells that track value modifiers (0-based index)
  private List<List<PawnsBoardAugmentedCell<C>>> augmentedBoard;
  
  // Influence manager for handling different influence types
  private final InfluenceManager influenceManager;
  
  // Deck builder for reading augmented cards
  private final DeckBuilder<C> deckBuilder;

  /**
   * Constructs a PawnsBoardAugmented with the specified deck builder and influence manager.
   *
   * @param deckBuilder the deck builder to use for reading cards
   * @param influenceManager the influence manager to use for applying influences
   * @throws IllegalArgumentException if deckBuilder or influenceManager is null
   */
  public PawnsBoardAugmented(DeckBuilder<C> deckBuilder, InfluenceManager influenceManager) {
    if (deckBuilder == null) {
      throw new IllegalArgumentException("Deck builder cannot be null");
    }
    if (influenceManager == null) {
      throw new IllegalArgumentException("Influence manager cannot be null");
    }
    this.deckBuilder = deckBuilder;
    this.influenceManager = influenceManager;
  }

  /**
   * Constructs a PawnsBoardAugmented with default deck builder and influence manager.
   */
  public PawnsBoardAugmented() {
    this.influenceManager = new InfluenceManager();
    this.deckBuilder = (DeckBuilder<C>) new PawnsBoardAugmentedDeckBuilder(influenceManager);
  }

  /**
   * Initializes and starts a new game with the specified parameters.
   * Sets up the board with rows and columns, initializes player decks from the configuration files,
   * and deals cards to each player's hand.
   *
   * @param rows               the number of rows on the board
   * @param cols               the number of columns on the board
   * @param redDeckConfigPath  path to the RED player's deck configuration file
   * @param blueDeckConfigPath path to the BLUE player's deck configuration file
   * @param startingHandSize   the number of cards each player starts with
   * @throws IllegalArgumentException          if any of the dimensional parameters are invalid
   * @throws InvalidDeckConfigurationException if deck configuration is invalid or cannot be read
   */
  @Override
  public void startGame(int rows, int cols, String redDeckConfigPath,
                        String blueDeckConfigPath, int startingHandSize)
          throws IllegalArgumentException, InvalidDeckConfigurationException {
    // Validate dimensions
    validateBoardDimensions(rows, cols);

    this.startingHandSize = startingHandSize;
    // DOCUMENTED INVARIANT: Board dimensions (rows and columns) are set here and 
    // never modified elsewhere, maintaining the invariant that board dimensions 
    // remain fixed after initialization
    this.rows = rows;
    this.columns = cols;
    this.augmentedBoard = createEmptyAugmentedBoard(rows, cols);

    // Read decks from separate files for RED and BLUE players
    List<C> redDeckCards = deckBuilder.createDeck(redDeckConfigPath, false);
    List<C> blueDeckCards = deckBuilder.createDeck(blueDeckConfigPath, false);

    redDeck = redDeckCards;
    blueDeck = blueDeckCards;

    int minDeckSize = rows * cols;
    if (redDeck.size() < minDeckSize || blueDeck.size() < minDeckSize) {
      throw new InvalidDeckConfigurationException(
              "Deck size must be at least " + minDeckSize + " cards");
    }

    if (startingHandSize > redDeck.size() / 3) {
      throw new IllegalArgumentException(
              "Starting hand size cannot exceed one third of the deck size");
    }

    redHand = new ArrayList<>();
    blueHand = new ArrayList<>();

    for (int i = 0; i < startingHandSize; i++) {
      redHand.add(redDeck.remove(0));
      blueHand.add(blueDeck.remove(0));
    }

    initializeStartingBoard();

    currentPlayerColors = PlayerColors.RED;
    gameStarted = true;
    gameOver = false;
    lastPlayerPassed = false;

    // Notify listeners about the initial turn
    notifyTurnChange(currentPlayerColors);
  }

  /**
   * Creates an empty board with augmented cells that can track value modifiers.
   *
   * @param rows the number of rows
   * @param cols the number of columns
   * @return a List of Lists containing empty augmented cells
   */
  private List<List<PawnsBoardAugmentedCell<C>>> createEmptyAugmentedBoard(int rows, int cols) {
    List<List<PawnsBoardAugmentedCell<C>>> newBoard = new ArrayList<>(rows);

    for (int r = 0; r < rows; r++) {
      List<PawnsBoardAugmentedCell<C>> row = new ArrayList<>(cols);
      for (int c = 0; c < cols; c++) {
        row.add(new PawnsBoardAugmentedCell<>());
      }
      newBoard.add(row);
    }

    return newBoard;
  }

  /**
   * Validates the board dimensions according to the game's requirements.
   *
   * @param rows the number of rows
   * @param cols the number of columns
   * @throws IllegalArgumentException if dimensions are invalid
   */
  private void validateBoardDimensions(int rows, int cols) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Number of rows must be positive");
    }

    if (cols <= 1) {
      throw new IllegalArgumentException("Number of columns must be greater than 1");
    }

    if (cols % 2 == 0) {
      throw new IllegalArgumentException("Number of columns must be odd");
    }
  }

  /**
   * Initializes the starting board with pawns in the first and last columns.
   * RED pawns are placed in the first column, and BLUE pawns in the last column.
   */
  private void initializeStartingBoard() {
    try {
      // Add RED pawns to first column
      for (int r = 0; r < rows; r++) {
        augmentedBoard.get(r).get(0).addPawn(PlayerColors.RED);
      }

      // Add BLUE pawns to last column
      for (int r = 0; r < rows; r++) {
        augmentedBoard.get(r).get(columns - 1).addPawn(PlayerColors.BLUE);
      }
    } catch (Exception e) {
      // This should never happen during initialization with empty cells
      throw new IllegalStateException("Error initializing board: " + e.getMessage());
    }
  }

  /**
   * Places a card from the current player's hand onto the specified cell.
   * After placement, applies influences from the card to surrounding cells.
   * Supports different influence types (regular, upgrading, devaluing) based on the card.
   *
   * @param cardIndex the index of the card in the current player's hand
   * @param row the row index where the card will be placed
   * @param col the column index where the card will be placed
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   * @throws IllegalAccessException if the cell doesn't have enough pawns for the card's cost
   * @throws IllegalOwnerException if the pawns in the cell aren't owned by the current player
   * @throws IllegalCardException if the card is not in the current player's hand
   */
  @Override
  public void placeCard(int cardIndex, int row, int col)
          throws IllegalArgumentException, IllegalStateException, IllegalAccessException,
          IllegalOwnerException, IllegalCardException {
    try {
      validateGameInProgress();
      validateCoordinates(row, col);
      List<C> currentHand = getCurrentPlayerHand();
      if (cardIndex < 0 || cardIndex >= currentHand.size()) {
        throw new IllegalCardException("Invalid card index: " + cardIndex);
      }
      C cardToPlace = currentHand.get(cardIndex);
      PawnsBoardAugmentedCell<C> targetCell = augmentedBoard.get(row).get(col);
      if (targetCell.getContent() != CellContent.PAWNS) {
        throw new IllegalAccessException("Cell does not contain pawns");
      }
      if (targetCell.getOwner() != currentPlayerColors) {
        throw new IllegalOwnerException("Pawns in cell are not owned by current player");
      }
      if (targetCell.getPawnCount() < cardToPlace.getCost()) {
        throw new IllegalAccessException(
                "Not enough pawns in cell. Required: " + cardToPlace.getCost()
                        + ", Available: " + targetCell.getPawnCount());
      }
      
      // Place the card
      targetCell.setCard(cardToPlace, currentPlayerColors);
      
      // Apply influences from the card to surrounding cells
      applyCardInfluences(cardToPlace, row, col);
      
      // Remove the card from hand
      currentHand.remove(cardIndex);
      
      // Reset pass flag and switch players
      lastPlayerPassed = false;
      switchPlayer(); // This will notify about turn change
      
      // Draw a card for the new current player at the start of their turn
      drawCard();
    } catch (IllegalArgumentException e) {
      notifyInvalidMove("Invalid coordinates: " + e.getMessage());
      throw e;
    } catch (IllegalStateException e) {
      notifyInvalidMove("Game state error: " + e.getMessage());
      throw e;
    } catch (IllegalAccessException e) {
      notifyInvalidMove("Access error: " + e.getMessage());
      throw e;
    } catch (IllegalOwnerException e) {
      notifyInvalidMove("Ownership error: " + e.getMessage());
      throw e;
    } catch (IllegalCardException e) {
      notifyInvalidMove("Card error: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Applies influences from a card to surrounding cells.
   * Handles all influence types (regular, upgrading, devaluing) based on the card's grid.
   *
   * @param card the card whose influences are being applied
   * @param row the row where the card was placed
   * @param col the column where the card was placed
   */
  private void applyCardInfluences(C card, int row, int col) {
    Influence[][] influenceGrid = card.getAugmentedInfluenceGrid();

    // Center position in the influence grid
    int centerRow = 2;
    int centerCol = 2;

    // Apply influence to each cell in the influence grid
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        // Skip if this is the center cell (where the card is placed)
        if (r == centerRow && c == centerCol) {
          continue;
        }

        // Calculate target cell coordinates on the game board
        int targetRow = row + (r - centerRow);
        int targetCol = col + (c - centerCol);

        // Skip if target cell is outside the board
        if (targetRow < 0 || targetRow >= rows || targetCol < 0 || targetCol >= columns) {
          continue;
        }

        try {
          // Get the influence type at this position
          Influence influence = influenceGrid[r][c];
          
          // Apply the influence to the target cell
          PawnsBoardAugmentedCell<C> targetCell = augmentedBoard.get(targetRow).get(targetCol);
          influence.applyInfluence(targetCell, currentPlayerColors);
          
          // Check if the cell has a card that needs to be removed due to devaluation
          if (shouldRemoveCard(targetRow, targetCol)) {
            removeCardAndRestorePawns(targetRow, targetCol);
          }
        } catch (Exception e) {
          // Log error but continue with other influences
          System.err.println("Error applying influence at (" + targetRow + "," + targetCol 
                  + "): " + e.getMessage());
        }
      }
    }
  }

  /**
   * Gets the value modifier for a specific cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the value modifier (positive for upgrades, negative for devaluations)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int getCellValueModifier(int row, int col) 
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return augmentedBoard.get(row).get(col).getValueModifier();
  }

  /**
   * Gets the effective value of a card in a specific cell, including any value modifiers.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the effective value of the card, or 0 if there is no card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int getEffectiveCardValue(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return augmentedBoard.get(row).get(col).getEffectiveCardValue();
  }

  /**
   * Applies an upgrading influence to a cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @param amount the amount to increase the value by
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   */
  @Override
  public void upgradeCell(int row, int col, int amount)
          throws IllegalArgumentException, IllegalStateException {
    validateGameInProgress();
    validateCoordinates(row, col);
    augmentedBoard.get(row).get(col).upgrade(amount);
  }

  /**
   * Applies a devaluing influence to a cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @param amount the amount to decrease the value by
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   */
  @Override
  public void devalueCell(int row, int col, int amount)
          throws IllegalArgumentException, IllegalStateException {
    validateGameInProgress();
    validateCoordinates(row, col);
    augmentedBoard.get(row).get(col).devalue(amount);
    
    // Check if a card needs to be removed
    if (shouldRemoveCard(row, col)) {
      try {
        removeCardAndRestorePawns(row, col);
      } catch (IllegalAccessException e) {
        // This should never happen since we checked shouldRemoveCard first
        System.err.println("Unexpected error removing card: " + e.getMessage());
      }
    }
  }

  /**
   * Removes a card from a cell and replaces it with pawns.
   * The number of pawns is equal to the ORIGINAL card's cost (up to a maximum of 3).
   * The pawns belong to the player who owned the card.
   * This is used when a card's effective value becomes 0 or less.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started or is already over
   * @throws IllegalAccessException if the cell doesn't contain a card
   */
  @Override
  public void removeCardAndRestorePawns(int row, int col)
          throws IllegalArgumentException, IllegalStateException, IllegalAccessException {
    validateGameInProgress();
    validateCoordinates(row, col);
    
    PawnsBoardAugmentedCell<C> cell = augmentedBoard.get(row).get(col);
    
    if (cell.getContent() != CellContent.CARD) {
      throw new IllegalAccessException("Cannot remove card from cell without a card");
    }
    
    // Get card details before removing it
    C card = cell.getCard();
    PlayerColors owner = cell.getOwner();
    int cardCost = card.getCost();
    
    // Reset the cell and restore pawns
    cell.restorePawnsAfterCardRemoval(cardCost, owner);
  }

  /**
   * Checks if a card needs to be removed due to devaluation.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return true if the card needs to be removed, false otherwise
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public boolean shouldRemoveCard(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    
    PawnsBoardAugmentedCell<C> cell = augmentedBoard.get(row).get(col);
    
    // Only cards can be removed
    if (cell.getContent() != CellContent.CARD) {
      return false;
    }
    
    // Check if effective value is 0 or less
    int effectiveValue = cell.getEffectiveCardValue();
    return effectiveValue <= 0;
  }

  /**
   * Gets the content type of a cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the cell content (EMPTY, PAWNS, or CARD)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public CellContent getCellContent(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return augmentedBoard.get(row).get(col).getContent();
  }

  /**
   * Gets the owner of a cell's contents.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the player who owns the cell contents, or null if the cell is empty
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public PlayerColors getCellOwner(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return augmentedBoard.get(row).get(col).getOwner();
  }

  /**
   * Gets the number of pawns in a cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the number of pawns, or 0 if the cell is empty or contains a card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int getPawnCount(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return augmentedBoard.get(row).get(col).getPawnCount();
  }

  /**
   * Gets the dimensions of the board.
   *
   * @return an array where the first element is the number of rows and the second is the number of columns
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int[] getBoardDimensions() throws IllegalStateException {
    validateGameStarted();
    return new int[]{rows, columns};
  }

  /**
   * Gets the card at a specific cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the card, or null if the cell doesn't contain a card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public C getCardAtCell(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    
    PawnsBoardAugmentedCell<C> cell = augmentedBoard.get(row).get(col);
    
    if (cell.getContent() != CellContent.CARD) {
      return null;
    }
    
    return cell.getCard();
  }



  /**
   * Gets the row scores for both players for a specific row.
   * Takes into account value modifiers when calculating scores.
   *
   * @param row the row index
   * @return an array with RED's score (index 0) and BLUE's score (index 1)
   * @throws IllegalArgumentException if the row is out of bounds
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int[] getRowScores(int row) throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();

    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("Row index out of bounds: " + row);
    }

    int redScore = 0;
    int blueScore = 0;

    for (int col = 0; col < columns; col++) {
      PawnsBoardAugmentedCell<C> cell = augmentedBoard.get(row).get(col);

      if (cell.getContent() == CellContent.CARD) {
        if (isPlayerRed(cell.getOwner())) {
          // Use effective value (including modifiers) instead of original value
          redScore += Math.max(0, cell.getEffectiveCardValue());
        } else {
          // Use effective value (including modifiers) instead of original value
          blueScore += Math.max(0, cell.getEffectiveCardValue());
        }
      }
    }

    return new int[]{redScore, blueScore};
  }

  /**
   * Creates a deep copy of the current game state.
   *
   * @return a new PawnsBoardAugmented with the same state
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public PawnsBoardAugmented<C> copy() throws IllegalStateException {
    validateGameStarted();

    // Create a new board with the same influence manager and deck builder
    PawnsBoardAugmented<C> copy = new PawnsBoardAugmented<>(deckBuilder, influenceManager);

    // Copy basic game state
    copy.gameStarted = this.gameStarted;
    copy.gameOver = this.gameOver;
    copy.currentPlayerColors = this.currentPlayerColors;
    copy.lastPlayerPassed = this.lastPlayerPassed;
    copy.startingHandSize = this.startingHandSize;
    copy.rows = this.rows;
    copy.columns = this.columns;

    // Deep copy of the board
    copy.augmentedBoard = new ArrayList<>(rows);
    for (int r = 0; r < rows; r++) {
      List<PawnsBoardAugmentedCell<C>> rowCopy = new ArrayList<>(columns);
      for (int c = 0; c < columns; c++) {
        PawnsBoardAugmentedCell<C> originalCell = this.augmentedBoard.get(r).get(c);
        PawnsBoardAugmentedCell<C> cellCopy = new PawnsBoardAugmentedCell<>();

        // Copy cell state
        CellContent content = originalCell.getContent();
        PlayerColors owner = originalCell.getOwner();
        
        if (content == CellContent.PAWNS) {
          // Add pawns to the cell
          for (int i = 0; i < originalCell.getPawnCount(); i++) {
            try {
              cellCopy.addPawn(owner);
            } catch (Exception e) {
              throw new IllegalStateException("Error copying pawn: " + e.getMessage());
            }
          }
        } else if (content == CellContent.CARD) {
          // Set card on the cell
          cellCopy.setCard(originalCell.getCard(), owner);
          
          // Copy value modifier
          if (originalCell.getValueModifier() > 0) {
            cellCopy.upgrade(originalCell.getValueModifier());
          } else if (originalCell.getValueModifier() < 0) {
            cellCopy.devalue(-originalCell.getValueModifier());
          }
        } else if (originalCell.getValueModifier() != 0) {
          // Empty cell with value modifier
          if (originalCell.getValueModifier() > 0) {
            cellCopy.upgrade(originalCell.getValueModifier());
          } else {
            cellCopy.devalue(-originalCell.getValueModifier());
          }
        }

        rowCopy.add(cellCopy);
      }
      copy.augmentedBoard.add(rowCopy);
    }

    // Deep copy of decks and hands
    copy.redDeck = new ArrayList<>(this.redDeck);
    copy.blueDeck = new ArrayList<>(this.blueDeck);
    copy.redHand = new ArrayList<>(this.redHand);
    copy.blueHand = new ArrayList<>(this.blueHand);

    return copy;
  }
}
