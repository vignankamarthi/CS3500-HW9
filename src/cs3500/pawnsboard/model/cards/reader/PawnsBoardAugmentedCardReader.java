package cs3500.pawnsboard.model.cards.reader;

import cs3500.pawnsboard.model.cards.PawnsBoardAugmentedCard;
import cs3500.pawnsboard.model.cards.factory.CardFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implementation of {@link CardReader} that reads {@link PawnsBoardAugmentedCard}s 
 * from configuration files for the augmented Pawns Board game.
 * Supports enhanced influence types (regular, upgrading, devaluing).
 */
public class PawnsBoardAugmentedCardReader<C extends PawnsBoardAugmentedCard> 
        implements CardReader<C> {
  
  private final CardFactory<C> cardFactory;
  
  /**
   * Constructs a PawnsBoardAugmentedCardReader with the specified card factory.
   *
   * @param cardFactory factory for creating {@link PawnsBoardAugmentedCard} objects
   * @throws IllegalArgumentException if cardFactory is null
   */
  public PawnsBoardAugmentedCardReader(CardFactory<C> cardFactory) {
    if (cardFactory == null) {
      throw new IllegalArgumentException("Card factory cannot be null");
    }
    this.cardFactory = cardFactory;
  }
  
  /**
   * Reads cards from a file and returns them as a list.
   * The file should contain card definitions with mixed influence types.
   *
   * @param filePath path to the card configuration file
   * @return a list of {@link PawnsBoardAugmentedCard}s read from the file
   * @throws IllegalArgumentException if the file cannot be read or has invalid format
   */
  @Override
  public List<C> readCards(String filePath) throws IllegalArgumentException {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("Card file path cannot be null or empty");
    }
    List<C> cards = new ArrayList<>();
    File file = new File(filePath);
    
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        // Process each card
        cards.add(readSingleCard(scanner));
      }
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found: " + filePath);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error reading card file: " + e.getMessage());
    }
    
    return cards;
  }
  
  /**
   * Reads a single card from the scanner.
   * Enhanced to support mixed influence types (I, U, D, X, C).
   *
   * @param scanner the scanner to read from
   * @return a {@link PawnsBoardAugmentedCard} object
   * @throws IllegalArgumentException if card format is invalid
   */
  private C readSingleCard(Scanner scanner) throws IllegalArgumentException {
    // Read card header
    if (!scanner.hasNextLine()) {
      throw new IllegalArgumentException("Unexpected end of file");
    }
    
    String headerLine = scanner.nextLine();
    String[] headerParts = headerLine.split(" ");
    
    if (headerParts.length != 3) {
      throw new IllegalArgumentException("Invalid card header format: " + headerLine);
    }
    
    String name = headerParts[0];
    int cost = parseAndValidateCost(headerParts[1], headerLine);
    int value = parseAndValidateValue(headerParts[2], headerLine);
    
    // Read and validate influence grid with mixed influence types
    char[][] influenceGrid = readInfluenceGrid(scanner);
    
    // Use the augmented card factory to create the card
    return cardFactory.createPawnsBoardCard(name, cost, value, influenceGrid);

  }
  
  /**
   * Parses and validates the card cost from a string.
   *
   * @param costStr the cost as a string
   * @param headerLine the original header line for error reporting
   * @return the parsed cost
   * @throws IllegalArgumentException if the cost is invalid
   */
  private int parseAndValidateCost(String costStr, String headerLine) {
    int cost;
    try {
      cost = Integer.parseInt(costStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid cost object type in card header: " + headerLine);
    }
    
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Card cost must be between 1 and 3, got: " + cost);
    }
    
    return cost;
  }
  
  /**
   * Parses and validates the card value from a string.
   *
   * @param valueStr the value as a string
   * @param headerLine the original header line for error reporting
   * @return the parsed value
   * @throws IllegalArgumentException if the value is invalid
   */
  private int parseAndValidateValue(String valueStr, String headerLine) {
    int value;
    try {
      value = Integer.parseInt(valueStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid value object type in card header: " + headerLine);
    }
    
    if (value <= 0) {
      throw new IllegalArgumentException("Card value must be positive, got: " + value);
    }
    
    return value;
  }
  
  /**
   * Reads the influence grid from the scanner.
   * Enhanced to support mixed influence types (I, U, D, X, C).
   *
   * @param scanner the scanner to read from
   * @return the 5x5 influence grid
   * @throws IllegalArgumentException if the grid is invalid
   */
  private char[][] readInfluenceGrid(Scanner scanner) {
    char[][] influenceGrid = new char[5][5];
    boolean foundCardPosition = false;
    
    for (int row = 0; row < 5; row++) {
      if (!scanner.hasNextLine()) {
        throw new IllegalArgumentException("Unexpected end of file while reading influence grid");
      }
      
      String gridLine = scanner.nextLine();
      if (gridLine.length() != 5) {
        throw new IllegalArgumentException(
            "Influence grid line must have exactly 5 characters, got: " + gridLine);
      }
      
      for (int col = 0; col < 5; col++) {
        char c = gridLine.charAt(col);
        validateGridCharacter(c);
        
        if (c == 'C') {
          foundCardPosition = validateCardPosition(row, col, foundCardPosition);
        }
        
        influenceGrid[row][col] = c;
      }
    }
    
    if (!foundCardPosition) {
      throw new IllegalArgumentException("No card position (C) found in influence grid");
    }
    
    return influenceGrid;
  }
  
  /**
   * Validates a character in the influence grid.
   * Enhanced to support mixed influence types (I, U, D, X, C).
   *
   * @param c the character to validate
   * @throws IllegalArgumentException if the character is invalid
   */
  private void validateGridCharacter(char c) {
    if (c != 'X' && c != 'I' && c != 'U' && c != 'D' && c != 'C') {
      throw new IllegalArgumentException(
          "Invalid character in influence grid: " + c + ", expected X, I, U, D, or C");
    }
  }
  
  /**
   * Validates the card position in the influence grid.
   *
   * @param row the row of the position
   * @param col the column of the position
   * @param foundCardPosition whether a card position has already been found
   * @return true, indicating a card position has been found
   * @throws IllegalArgumentException if the position is invalid
   */
  private boolean validateCardPosition(int row, int col, boolean foundCardPosition) {
    if (foundCardPosition) {
      throw new IllegalArgumentException("Multiple card positions (C) found in influence grid");
    }
    if (row != 2 || col != 2) {
      throw new IllegalArgumentException(
          "Card position (C) must be in the center at (2,2), found at (" + row + "," + col + ")");
    }
    return true;
  }
}