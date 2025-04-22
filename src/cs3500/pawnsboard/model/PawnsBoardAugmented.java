package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.cards.deckbuilder.DeckBuilder;
import cs3500.pawnsboard.model.cell.PawnsBoardAugmentedCell;
import cs3500.pawnsboard.model.cell.PawnsBoardCell;
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
 * An enhanced implementation of the Pawns Board game that supports
 * different influence types (regular, upgrading, devaluing).
 * This class extends AbstractPawnsBoard to reuse common functionality
 * while adding the ability to handle augmented cards and cells.
 * 
 * <p>Key differences from PawnsBoardBase:</p>
 * <ul>
 *   <li>Uses PawnsBoardAugmentedCell to support value modifications</li>
 *   <li>Supports PawnsBoardAugmentedCard with different influence types</li>
 *   <li>Uses InfluenceManager to apply different influence strategies</li>
 *   <li>Row scores consider value modifiers on cells</li>
 * </ul>
 *
 * @param <C> the type of Card used in this game (must extend PawnsBoardBaseCard)
 */
public class PawnsBoardAugmented<C extends PawnsBoardBaseCard> 
        extends AbstractPawnsBoard<C, PawnsBoardAugmentedCell<C>>
        implements ReadOnlyPawnsBoardAugmented<C, PawnsBoardAugmentedCell<C>> {
  
  // Board state - a 2D grid of augmented cells
  private List<List<PawnsBoardAugmentedCell<C>>> board;
  
  // Influence manager for applying different influence types
  private final InfluenceManager influenceManager;
  
  /**
   * Constructs a PawnsBoardAugmented with the specified deck builder and influence manager.
   *
   * @param deckBuilder the deck builder to use for card reading
   * @param influenceManager the influence manager to use for applying influences
   * @throws IllegalArgumentException if deckBuilder or influenceManager is null
   */
  public PawnsBoardAugmented(DeckBuilder<C> deckBuilder, InfluenceManager influenceManager) {
    super(deckBuilder);
    
    if (influenceManager == null) {
      throw new IllegalArgumentException("Influence manager cannot be null");
    }
    
    this.influenceManager = influenceManager;
  }
  
  /**
   * Constructs a PawnsBoardAugmented with a default influence manager.
   * The deck builder must be provided by concrete subclasses.
   *
   * @param deckBuilder the deck builder to use for card reading
   * @throws IllegalArgumentException if deckBuilder is null
   */
  public PawnsBoardAugmented(DeckBuilder<C> deckBuilder) {
    super(deckBuilder);
    this.influenceManager = new InfluenceManager();
  }
  
  /**
   * Creates an empty board with the specified dimensions using augmented cells.
   *
   * @param rows the number of rows
   * @param cols the number of columns
   * @return a List of Lists containing empty augmented cells
   */
  private List<List<PawnsBoardAugmentedCell<C>>> createEmptyBoard(int rows, int cols) {
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
   * Initializes and starts a new game with the specified parameters.
   * Sets up the board with rows and columns, initializes player decks from the deck configuration
   * files, deals cards to each player's hand, and sets the first player.
   *
   * @param rows               the number of rows on the board
   * @param cols               the number of columns on the board
   * @param redDeckConfigPath  path to the RED player's deck configuration file
   * @param blueDeckConfigPath path to the BLUE player's deck configuration file
   * @param startingHandSize   the number of cards each player starts with
   * @throws IllegalArgumentException          if any of the dimensional parameters are invalid
   * @throws IllegalArgumentException          if the starting hand size is too large
   * @throws InvalidDeckConfigurationException if deck configuration is invalid or cannot be read
   */
  @Override
  public void startGame(int rows, int cols, String redDeckConfigPath,
                        String blueDeckConfigPath, int startingHandSize)
          throws IllegalArgumentException, InvalidDeckConfigurationException {
    
    // Validate dimensions (reuse superclass validation logic)
    super.startGame(rows, cols, redDeckConfigPath, blueDeckConfigPath, startingHandSize);
    
    // Create the board with augmented cells
    board = createEmptyBoard(rows, cols);
    
    // Initialize the board with pawns (similar to base implementation)
    initializeStartingBoard();
  }
  
  /**
   * Initializes the starting board with pawns in the first and last columns.
   * The first column contains RED pawns, and the last column contains BLUE pawns.
   */
  private void initializeStartingBoard() {
    try {
      // Add RED pawns to first column
      for (int r = 0; r < rows; r++) {
        board.get(r).get(0).addPawn(PlayerColors.RED);
      }
      
      // Add BLUE pawns to last column
      for (int r = 0; r < rows; r++) {
        board.get(r).get(columns - 1).addPawn(PlayerColors.BLUE);
      }
    } catch (Exception e) {
      // This should never happen during initialization with empty cells
      throw new IllegalStateException("Error initializing board: " + e.getMessage());
    }
  }
  
  /**
   * Places a card from the current player's hand onto the specified cell.
   * The cell must contain enough pawns owned by the current player to cover the card's cost.
   * After placement, the card's influence will be applied to the board according to the
   * card's influence types.
   *
   * @param cardIndex the index of the card in the current player's hand
   * @param row       the row index where the card will be placed
   * @param col       the column index where the card will be placed
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started or is already over
   * @throws IllegalAccessException   if the cell doesn't contain enough pawns for the card's cost
   * @throws IllegalOwnerException    if the pawns in the cell aren't owned by the current player
   * @throws IllegalCardException     if the card is not in the current player's hand
   */
  @Override
  public void placeCard(int cardIndex, int row, int col)
          throws IllegalArgumentException, IllegalStateException, IllegalAccessException,
          IllegalOwnerException, IllegalCardException {
    try {
      validateGameInProgress();
      validateCoordinates(row, col);
      
      // Get current player's hand
      List<C> currentHand = getCurrentPlayerHand();
      
      // Validate card index
      if (cardIndex < 0 || cardIndex >= currentHand.size()) {
        throw new IllegalCardException("Invalid card index: " + cardIndex);
      }
      
      // Get the card to place
      C cardToPlace = currentHand.get(cardIndex);
      
      // Get the target cell
      PawnsBoardAugmentedCell<C> targetCell = board.get(row).get(col);
      
      // Check if cell has pawns
      if (targetCell.getContent() != CellContent.PAWNS) {
        throw new IllegalAccessException("Cell does not contain pawns");
      }
      
      // Check if pawns are owned by the current player
      if (targetCell.getOwner() != currentPlayerColors) {
        throw new IllegalOwnerException("Pawns in cell are not owned by current player");
      }
      
      // Check if there are enough pawns
      if (targetCell.getPawnCount() < cardToPlace.getCost()) {
        throw new IllegalAccessException(
                "Not enough pawns in cell. Required: " + cardToPlace.getCost()
                        + ", Available: " + targetCell.getPawnCount());
      }
      
      // Place the card
      targetCell.setCard(cardToPlace, currentPlayerColors);
      
      // Apply card influence
      applyCardInfluence(cardToPlace, row, col);
      
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
   * Applies the influence of a card to surrounding cells based on the card's influence grid.
   * For augmented cards, applies different influence types (regular, upgrading, devaluing).
   * For standard cards, uses regular influence only.
   *
   * @param card the card whose influence is being applied
   * @param row  the row where the card was placed
   * @param col  the column where the card was placed
   */
  private void applyCardInfluence(C card, int row, int col) {
    // Default to regular influence grid
    boolean[][] influenceGrid = card.getInfluenceGrid();
    
    // Center position in the influence grid
    int centerRow = 2;
    int centerCol = 2;
    
    // For blue player, mirror the influence grid horizontally
    boolean mirrorForBlue = !isPlayerRed(currentPlayerColors);
    
    // Check if this is an augmented card
    if (card instanceof PawnsBoardAugmentedCard) {
      // Handle augmented card with different influence types
      PawnsBoardAugmentedCard augmentedCard = (PawnsBoardAugmentedCard) card;
      applyAugmentedCardInfluence(augmentedCard, row, col, mirrorForBlue);
    } else {
      // For standard cards, apply regular influence only
      applyStandardCardInfluence(influenceGrid, row, col, mirrorForBlue);
    }
  }
  
  /**
   * Applies standard card influence to surrounding cells.
   * This is used for non-augmented cards that only have regular influence.
   *
   * @param influenceGrid the boolean influence grid
   * @param row the row where the card was placed
   * @param col the column where the card was placed
   * @param mirror whether to mirror the grid horizontally (for BLUE player)
   */
  private void applyStandardCardInfluence(boolean[][] influenceGrid, int row, int col, boolean mirror) {
    int centerRow = 2;
    int centerCol = 2;
    
    // Apply influence to each cell in the grid
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        // Skip if this is the center cell
        if (r == centerRow && c == centerCol) {
          continue;
        }
        
        // Get source grid position (mirrored if needed)
        int sourceCol = mirror ? 4 - c : c;
        
        // Skip if no influence
        if (!influenceGrid[r][sourceCol]) {
          continue;
        }
        
        // Calculate target cell coordinates
        int targetRow = row + (r - centerRow);
        int targetCol = col + (c - centerCol);
        
        // Skip if outside board
        if (targetRow < 0 || targetRow >= rows || targetCol < 0 || targetCol >= columns) {
          continue;
        }
        
        // Apply regular influence to the cell
        try {
          PawnsBoardAugmentedCell<C> targetCell = board.get(targetRow).get(targetCol);
          influenceManager.applyInfluence('I', targetCell, currentPlayerColors);
        } catch (Exception e) {
          // Skip this cell if influence application fails
        }
      }
    }
  }
  
  /**
   * Applies augmented card influence to surrounding cells.
   * This handles different influence types (regular, upgrading, devaluing).
   *
   * @param card the augmented card
   * @param row the row where the card was placed
   * @param col the column where the card was placed
   * @param mirror whether to mirror the grid horizontally (for BLUE player)
   */
  private void applyAugmentedCardInfluence(PawnsBoardAugmentedCard card, int row, int col, boolean mirror) {
    int centerRow = 2;
    int centerCol = 2;
    
    // Get the augmented influence grid
    Influence[][] influenceGrid = card.getAugmentedInfluenceGrid();
    
    // Apply influence to each cell in the grid
    for (int r = 0; r < 5; r++) {
      for (int c = 0; c < 5; c++) {
        // Skip if this is the center cell
        if (r == centerRow && c == centerCol) {
          continue;
        }
        
        // Get source grid position (mirrored if needed)
        int sourceCol = mirror ? 4 - c : c;
        
        // Get the influence for this position
        Influence influence = influenceGrid[r][sourceCol];
        
        // Skip if no influence
        if (influence == null) {
          continue;
        }
        
        // Calculate target cell coordinates
        int targetRow = row + (r - centerRow);
        int targetCol = col + (c - centerCol);
        
        // Skip if outside board
        if (targetRow < 0 || targetRow >= rows || targetCol < 0 || targetCol >= columns) {
          continue;
        }
        
        // Apply the specific influence to the cell
        try {
          PawnsBoardAugmentedCell<C> targetCell = board.get(targetRow).get(targetCol);
          influence.applyInfluence(targetCell, currentPlayerColors);
        } catch (Exception e) {
          // Skip this cell if influence application fails
        }
      }
    }
  }

  /**
   * Gets the dimensions of the board.
   *
   * @return an array where the elements represent the of dimension of the board.
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public int[] getBoardDimensions() throws IllegalStateException {
    return new int[] {rows, columns};
  }

  /**
   * Gets the content type of the given cell position on the board.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return a {@link CellContent} indicating whether the cell is empty, contains pawns, or a card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  public CellContent getCellContent(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return board.get(row).get(col).getContent();
  }
  
  /**
   * Gets the owner of a cell's contents (pawns or card).
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the {@link PlayerColors} who owns the cell's contents, or null if the cell is empty
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  public PlayerColors getCellOwner(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return board.get(row).get(col).getOwner();
  }
  
  /**
   * Gets the number of pawns in a specified cell.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the number of pawns in the cell, or 0 if the cell is empty or contains a card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  public int getPawnCount(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return board.get(row).get(col).getPawnCount();
  }
  
  /**
   * Gets the card at the specified cell position.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the card at the specified position, or null if the cell doesn't contain a card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  @Override
  public C getCardAtCell(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    
    if (getCellContent(row, col) != CellContent.CARD) {
      return null;
    }
    
    return board.get(row).get(col).getCard();
  }
  
  /**
   * Gets the row scores for both players for a specific row.
   * For augmented cells, the card values are adjusted based on value modifiers.
   *
   * @param row the row index to calculate scores for
   * @return an array where the first element is Red's score for the row and the second is Blue's
   * @throws IllegalArgumentException if the row is out of bounds
   * @throws IllegalStateException    if the game hasn't been started
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
      PawnsBoardAugmentedCell<C> cell = board.get(row).get(col);
      
      if (cell.getContent() == CellContent.CARD) {
        if (isPlayerRed(cell.getOwner())) {
          redScore += cell.getEffectiveCardValue();
        } else {
          blueScore += cell.getEffectiveCardValue();
        }
      }
    }
    
    return new int[]{redScore, blueScore};
  }
  
  /**
   * Gets the value modifier for a specific cell.
   * This is a new method specific to the augmented model.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the value modifier for the cell (positive for upgrades, negative for devaluations)
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  public int getCellValueModifier(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return board.get(row).get(col).getValueModifier();
  }
  
  /**
   * Gets the effective value of a card in a specific cell, including any value modifiers.
   * This is a new method specific to the augmented model.
   *
   * @param row the row index of the cell
   * @param col the column index of the cell
   * @return the effective value of the card, or 0 if there is no card
   * @throws IllegalArgumentException if the coordinates are invalid
   * @throws IllegalStateException    if the game hasn't been started
   */
  public int getEffectiveCardValue(int row, int col)
          throws IllegalArgumentException, IllegalStateException {
    validateGameStarted();
    validateCoordinates(row, col);
    return board.get(row).get(col).getEffectiveCardValue();
  }
  
  /**
   * Creates a deep copy of the current game board state.
   * This is useful for AI players to simulate moves without affecting the actual game.
   *
   * @return a new PawnsBoard instance with the same state as this board
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public PawnsBoard<C, PawnsBoardAugmentedCell<C>> copy() throws IllegalStateException {
    validateGameStarted();
    
    // Create a new augmented board with the same influence manager
    PawnsBoardAugmented<C> copy = new PawnsBoardAugmented<>(null, this.influenceManager) {
      // Anonymous subclass that doesn't require a deck builder for copying
      @Override
      public void startGame(int rows, int cols, String redDeckConfigPath,
                           String blueDeckConfigPath, int startingHandSize) {
        // Override to avoid using deck builder during copy
      }
    };
    
    // Copy basic game state
    copy.gameStarted = this.gameStarted;
    copy.gameOver = this.gameOver;
    copy.currentPlayerColors = this.currentPlayerColors;
    copy.lastPlayerPassed = this.lastPlayerPassed;
    copy.startingHandSize = this.startingHandSize;
    copy.rows = this.rows;
    copy.columns = this.columns;
    
    // Deep copy of the board
    copy.board = new ArrayList<>(rows);
    for (int r = 0; r < rows; r++) {
      List<PawnsBoardAugmentedCell<C>> rowCopy = new ArrayList<>(columns);
      for (int c = 0; c < columns; c++) {
        PawnsBoardAugmentedCell<C> originalCell = this.board.get(r).get(c);
        PawnsBoardAugmentedCell<C> cellCopy = new PawnsBoardAugmentedCell<>();
        
        // Copy cell state
        if (originalCell.getContent() == CellContent.PAWNS) {
          // Add pawns to the cell
          for (int i = 0; i < originalCell.getPawnCount(); i++) {
            try {
              cellCopy.addPawn(originalCell.getOwner());
            } catch (Exception e) {
              throw new IllegalStateException("Error copying pawn: " + e.getMessage());
            }
          }
        } else if (originalCell.getContent() == CellContent.CARD) {
          // Set card on the cell
          cellCopy.setCard(originalCell.getCard(), originalCell.getOwner());
          
          // Copy value modifier
          int modifier = originalCell.getValueModifier();
          if (modifier > 0) {
            cellCopy.upgrade(modifier);
          } else if (modifier < 0) {
            cellCopy.devalue(-modifier);
          }
        }
        
        rowCopy.add(cellCopy);
      }
      copy.board.add(rowCopy);
    }
    
    // Deep copy of decks and hands
    copy.redDeck = new ArrayList<>(this.redDeck);
    copy.blueDeck = new ArrayList<>(this.blueDeck);
    copy.redHand = new ArrayList<>(this.redHand);
    copy.blueHand = new ArrayList<>(this.blueHand);
    
    return copy;
  }
}