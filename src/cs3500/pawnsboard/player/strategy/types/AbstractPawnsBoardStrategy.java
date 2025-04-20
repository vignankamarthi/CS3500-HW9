package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for Pawns Board game strategies.
 * Provides common functionality and utility methods for concrete strategy implementations.
 *
 * @param <C> the type of Card used in the game
 */
public abstract class AbstractPawnsBoardStrategy<C extends Card>
        implements PawnsBoardStrategy<C, PawnsBoardMove> {

  protected PlayerColors player;


  /**
   * Determines the next move based on the current game state.
   * This method must be implemented by concrete subclasses.
   *
   * @param model the current game state
   * @return an Optional containing the chosen move, or empty if no move was selected
   */
  @Override
  public abstract Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model);

  /**
   * Sets the player this strategy is playing for.
   * This allows the strategy to make decisions for a specific player,
   * which may be different from the current player in the model.
   * Note: This method is intended to be used for temporary strategy creation
   * during strategy composition or minimax evaluation. It modifies the current
   * strategy object and does not create a deep copy.
   *
   * @param player the player this strategy is playing for
   * @return this strategy for method chaining
   */
  public AbstractPawnsBoardStrategy<C> forPlayer(PlayerColors player) {
    this.player = player;
    return this;
  }


  /**
   * Initializes strategy context by extracting common game state information.
   *
   * @param model the current game state
   * @return a StrategyContext object containing game state information, or null if an error occurs
   */
  protected StrategyContext initializeContext(ReadOnlyPawnsBoard<C, ?> model) {
    try {
      PlayerColors currentPlayer = this.player != null ? this.player : model.getCurrentPlayer();
      List<C> hand = model.getPlayerHand(currentPlayer);
      int[] dimensions = model.getBoardDimensions();
      int rows = dimensions[0];
      int cols = dimensions[1];

      // Get the player index (0 for RED, 1 for BLUE)
      int playerIndex = (currentPlayer == PlayerColors.RED) ? 0 : 1;
      int opponentIndex = 1 - playerIndex;

      return new StrategyContext<>(currentPlayer, hand, rows, cols, playerIndex, opponentIndex);
    } catch (IllegalStateException e) {
      // Game has not started or is already over
      return null;
    }
  }


  /**
   * Creates a new move object with the given parameters.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row       the row coordinate
   * @param col       the column coordinate
   * @return a new PawnsBoardMove object
   */
  protected PawnsBoardMove createMove(int cardIndex, int row, int col) {
    return new PawnsBoardMove(cardIndex, row, col);
  }

  /**
   * Validates the game start state and returns an appropriate move if the game is not started.
   *
   * @param model the current game state
   * @return an Optional containing an empty or pass move if the game is not started or empty
   */
  protected Optional<PawnsBoardMove> validateGameStart(ReadOnlyPawnsBoard<C, ?> model) {
    if (!model.getGameStarted()) {
      return Optional.of(PawnsBoardMove.empty());
    }
    return Optional.empty();
  }

  /**
   * Inner class to hold common context information needed by strategies.
   */
  protected static class StrategyContext<C extends Card> {
    final PlayerColors currentPlayer;
    final List<C> hand;
    final int rows;
    final int cols;
    final int playerIndex;
    final int opponentIndex;

    /**
     * Constructs a new StrategyContext with the given parameters.
     *
     * @param currentPlayer the current player
     * @param hand          the current player's hand
     * @param rows          the number of rows on the board
     * @param cols          the number of columns on the board
     * @param playerIndex   the index of the current player (0 for RED, 1 for BLUE)
     * @param opponentIndex the index of the opponent (0 for RED, 1 for BLUE)
     */
    public StrategyContext(PlayerColors currentPlayer, List<C> hand, int rows, int cols,
                           int playerIndex, int opponentIndex) {
      this.currentPlayer = currentPlayer;
      this.hand = hand;
      this.rows = rows;
      this.cols = cols;
      this.playerIndex = playerIndex;
      this.opponentIndex = opponentIndex;
    }
  }
}
