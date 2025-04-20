package cs3500.pawnsboard.player;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.model.exceptions.IllegalAccessException;
import cs3500.pawnsboard.model.exceptions.IllegalCardException;
import cs3500.pawnsboard.model.exceptions.IllegalOwnerException;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.moves.MoveType;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;
import cs3500.pawnsboard.player.strategy.types.FillFirstStrategy;

import java.util.Optional;

/**
 * Implementation of the Player interface for AI players in the PawnsBoard game.
 * This class represents a computer player that makes decisions automatically
 * using a strategy to determine the best move.
 *
 * @param <C> the type of Card used in the game
 */
public class AIPlayer<C extends Card> implements Player<C> {
  private final PlayerColors playerColor;
  private PawnsBoardStrategy<C, PawnsBoardMove> strategy;

  /**
   * Constructs an AI player with the specified color and a default strategy.
   *
   * @param playerColor the color (RED or BLUE) assigned to this player
   * @throws IllegalArgumentException if playerColor is null
   */
  public AIPlayer(PlayerColors playerColor) {
    this(playerColor, new FillFirstStrategy<>());
  }

  /**
   * Constructs an AI player with the specified color and strategy.
   *
   * @param playerColor the color (RED or BLUE) assigned to this player
   * @param strategy    the strategy this AI player will use
   * @throws IllegalArgumentException if playerColor is null
   */
  public AIPlayer(PlayerColors playerColor, PawnsBoardStrategy<C, PawnsBoardMove> strategy) {
    if (playerColor == null) {
      throw new IllegalArgumentException("Player color cannot be null");
    }
    this.playerColor = playerColor;
    this.strategy = strategy;
  }

  /**
   * Takes a turn in the game based on AI decision-making using the configured strategy.
   * This method will use the strategy to determine the best move and execute it.
   * If the strategy cannot determine a valid move, the AI will pass its turn.
   *
   * @param model the current state of the game
   * @throws IllegalStateException if the game hasn't been started or is already over
   * @throws IllegalOwnerException if it's not this player's turn
   */
  @Override
  public void takeTurn(PawnsBoard<C, ?> model) throws IllegalStateException, IllegalOwnerException {
    try {
      if (!isMyTurn(model)) {
        throw new IllegalOwnerException("Not " + playerColor + "'s turn");
      }

      // Use the strategy to choose the best move
      // We can safely shift models to ReadOnlyPawnsBoard since PawnsBoard extends it
      Optional<PawnsBoardMove> move = strategy.chooseMove(model);

      if (!move.isPresent() || move.get().getMoveType() == MoveType.PASS
              || move.get().getMoveType() == MoveType.EMPTY) {
        // If no move was found or strategy decided to pass, pass the turn
        model.passTurn();
        return;
      }

      // Otherwise place the card as determined by the strategy
      PawnsBoardMove cardMove = move.get();
      int cardIndex = cardMove.getCardIndex();
      int row = cardMove.getRow();
      int col = cardMove.getCol();

      // Place the card on the board
      model.placeCard(cardIndex, row, col);

    } catch (IllegalStateException | IllegalOwnerException e) {
      // Automatically capture the error
      receiveInvalidMoveMessage(e.getMessage());
      // Re-throw to allow the controller to handle it if needed
      throw e;
    } catch (IllegalAccessException | IllegalCardException e) {
      // If there's a problem with the card or cell, capture the error
      receiveInvalidMoveMessage(e.getMessage());
      // Try to pass the turn instead
      try {
        model.passTurn();
      } catch (IllegalStateException | IllegalOwnerException ex) {
        // If passing also fails, capture this error too
        receiveInvalidMoveMessage(ex.getMessage());
        throw ex;
      }
    }
  }

  /**
   * Places a card from the player's hand onto the board.
   * For AI players, this method is typically called by takeTurn() after
   * the strategy has selected a move.
   *
   * @param model     the current state of the game
   * @param cardIndex the index of the card in the player's hand
   * @param row       the row where the card should be placed
   * @param col       the column where the card should be placed
   * @throws IllegalStateException  if the game hasn't been started or is already over
   * @throws IllegalAccessException if the cell doesn't have enough pawns for the card's cost
   * @throws IllegalOwnerException  if the pawns in the cell aren't owned by this player
   * @throws IllegalCardException   if the card index is invalid
   */
  @Override
  public void placeCard(PawnsBoard<C, ?> model, int cardIndex, int row, int col)
          throws IllegalStateException, IllegalAccessException, IllegalOwnerException,
          IllegalCardException {
    try {
      if (!isMyTurn(model)) {
        throw new IllegalOwnerException("Not " + playerColor + "'s turn");
      }

      // First verify this is a legal move according to the model
      if (!model.isLegalMove(cardIndex, row, col)) {
        throw new IllegalAccessException("Proposed move is not legal");
      }

      // Delegate to the model to place the card
      model.placeCard(cardIndex, row, col);
    } catch (IllegalStateException | IllegalAccessException |
             IllegalOwnerException | IllegalCardException e) {
      // Automatically capture the error
      receiveInvalidMoveMessage(e.getMessage());
      // Re-throw to allow the controller to handle it if needed
      throw e;
    }
  }

  /**
   * Passes the player's turn.
   * For AI players, this method is typically called by takeTurn() when
   * the strategy cannot find a valid move or explicitly chooses to pass.
   *
   * @param model the current state of the game
   * @throws IllegalStateException if the game hasn't been started or is already over
   * @throws IllegalOwnerException if it's not this player's turn
   */
  @Override
  public void passTurn(PawnsBoard<C, ?> model) throws IllegalStateException, IllegalOwnerException {
    try {
      if (!isMyTurn(model)) {
        throw new IllegalOwnerException("Not " + playerColor + "'s turn");
      }

      // Delegate to the model to pass the turn
      model.passTurn();
    } catch (IllegalStateException | IllegalOwnerException e) {
      // Automatically capture the error
      receiveInvalidMoveMessage(e.getMessage());
      // Re-throw to allow the controller to handle it if needed
      throw e;
    }
  }

  /**
   * Gets the color (RED or BLUE) associated with this player.
   *
   * @return the player's color
   */
  @Override
  public PlayerColors getPlayerColor() {
    return playerColor;
  }

  /**
   * Checks if it's this player's turn.
   *
   * @param model the current state of the game
   * @return true if it's this player's turn, false otherwise
   * @throws IllegalStateException if the game hasn't been started
   */
  @Override
  public boolean isMyTurn(PawnsBoard<C, ?> model) throws IllegalStateException {
    return model.getCurrentPlayer() == playerColor;
  }

  /**
   * Provides feedback to the AI about an invalid move.
   * Currently, logs the message for debugging purposes. Future enhancements
   * could incorporate this feedback to improve strategy decisions.
   *
   * @param message the error message describing why the move was invalid
   */
  @Override
  public void receiveInvalidMoveMessage(String message) {
    // Log the error message for debugging
    System.err.println("AI received invalid move: " + message);
    // Future enhancement: could store this information to improve strategy
  }

  /**
   * Notifies the AI that the game has ended.
   * Currently just logs the result for debugging purposes. Future enhancements
   * could incorporate this result for strategy improvement.
   *
   * @param model    the final state of the game
   * @param isWinner true if this player won, false if they lost or tied
   */
  @Override
  public void notifyGameEnd(PawnsBoard<C, ?> model, boolean isWinner) {
    // Log the game result for debugging
    System.out.println("AI player " + playerColor +
            (isWinner ? " won the game!" : " did not win the game."));

    // Display final scores
    int[] scores = model.getTotalScore();
    System.out.println("Final scores - RED: " + scores[0] + ", BLUE: " + scores[1]);

    // Future enhancement: could use this information to improve strategy
  }

  /**
   * Returns a string representation of the AI player.
   *
   * @return a string describing the player and their color
   */
  @Override
  public String toString() {
    return "AI Player (" + playerColor + ")";
  }

  /**
   * Gets the strategy this AI player is using for decision-making.
   *
   * @return the strategy used by this AI player
   */
  public PawnsBoardStrategy<C, PawnsBoardMove> getStrategy() {
    return strategy;
  }

  /**
   * Sets a new strategy for this AI player.
   *
   * @param strategy the new strategy to use
   * @throws IllegalArgumentException if strategy is null
   */
  public void setStrategy(PawnsBoardStrategy<C, PawnsBoardMove> strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("Strategy cannot be null");
    }
    this.strategy = strategy;
  }

  /**
   * Checks if this player is a human player.
   * Always returns false for instances of AIPlayer.
   *
   * @return false, indicating this is not a human player
   */
  public boolean isHuman() {
    return false;
  }
}