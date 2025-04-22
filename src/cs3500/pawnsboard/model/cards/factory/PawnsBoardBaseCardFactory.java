package cs3500.pawnsboard.model.cards.factory;

import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;

/**
 * Implementation of {@link CardFactory} for {@link PawnsBoardBaseCard}s in the Pawns Board game.
 * Creates card objects based on provided parameters using a method chaining approach.
 */
public class PawnsBoardBaseCardFactory implements CardFactory<PawnsBoardBaseCard> {
  
  /**
   * Creates a card with the specified parameters.
   *
   * @param name          the name of the card
   * @param cost          the cost of the card (1-3)
   * @param value         the value score of the card
   * @param influenceGrid the 5x5 influence grid for the card
   * @return a new {@link PawnsBoardBaseCard} instance
   */
  @Override
  public PawnsBoardBaseCard createPawnsBoardCard(String name, int cost, int value,
                                                 char[][] influenceGrid) {
    return new CardBuilder()
        .withName(name)
        .withCost(cost)
        .withValue(value)
        .withInfluenceGrid(convertInfluenceGrid(influenceGrid))
        .build();
  }
  
  /**
   * Converts the char grid to a boolean grid where true indicates influence.
   *
   * @param charGrid the char grid to convert
   * @return the boolean influence grid
   */
  private boolean[][] convertInfluenceGrid(char[][] charGrid) {
    // First validate the grid
    if (charGrid == null || charGrid.length != 5) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 grid");
    }

    for (int i = 0; i < charGrid.length; i++) {
      if (charGrid[i] == null || charGrid[i].length != 5) {
        throw new IllegalArgumentException("Influence grid must be a 5x5 grid");
      }
    }

    // Then process it
    boolean[][] influence = new boolean[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        influence[row][col] = charGrid[row][col] == 'I';
      }
    }

    return influence;
  }
  
  /**
   * Builder class for creating PawnsBoardBaseCard instances.
   * Allows for method chaining for better readability.
   */
  private static class CardBuilder {
    private String name;
    private int cost;
    private int value;
    private boolean[][] influenceGrid;
    
    /**
     * Sets the PawnsBoardBaseCard name.
     *
     * @param name the name of the card
     * @return this builder for method chaining
     */
    public CardBuilder withName(String name) {
      this.name = name;
      return this;
    }
    
    /**
     * Sets the PawnsBoardBaseCard cost.
     *
     * @param cost the cost of the card
     * @return this builder for method chaining
     */
    public CardBuilder withCost(int cost) {
      this.cost = cost;
      return this;
    }
    
    /**
     * Sets the PawnsBoardBaseCard value.
     *
     * @param value the value of the card
     * @return this builder for method chaining
     */
    public CardBuilder withValue(int value) {
      this.value = value;
      return this;
    }
    
    /**
     * Sets the PawnsBoardBaseCard influence grid.
     *
     * @param influenceGrid the influence grid
     * @return this builder for method chaining
     */
    public CardBuilder withInfluenceGrid(boolean[][] influenceGrid) {
      this.influenceGrid = influenceGrid;
      return this;
    }
    
    /**
     * Builds a new PawnsBoardBaseCard instance.
     *
     * @return the new {@link PawnsBoardBaseCard}
     */
    public PawnsBoardBaseCard build() {
      return new PawnsBoardBaseCard(name, cost, value, influenceGrid);
    }
  }
}