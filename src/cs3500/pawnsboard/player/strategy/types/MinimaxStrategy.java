package cs3500.pawnsboard.player.strategy.types;

import cs3500.pawnsboard.model.PawnsBoard;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoard;
import cs3500.pawnsboard.model.cards.Card;
import cs3500.pawnsboard.model.enumerations.CellContent;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.moves.Move;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A strategy that uses the minimax principle to select moves.
 * This is a FALLIBLE strategy that attempts to minimize the opponent's maximum possible move.
 *
 * <p>The strategy works as follows:
 * <ol>
 *   <li>For each possible move, simulate making that move</li>
 *   <li>Use the opponent's strategy to find their best response</li>
 *   <li>Evaluate the board state after the opponent's response</li>
 *   <li>Choose the move that leads to the worst outcome for the opponent</li>
 * </ol>
 * </p>
 *
 * <p>This strategy may return an empty Optional if no legal moves exist or if all moves
 * would lead to strong opponent responses.</p>
 *
 * @param <C> the type of Card used in the game
 */
public class MinimaxStrategy<C extends Card> extends AbstractPawnsBoardStrategy<C> {

  private final PawnsBoardStrategy<C, ? extends Move> opponentStrategy;

  /**
   * Constructs a new MinimaxStrategy with the specified opponent strategy.
   *
   * @param opponentStrategy the strategy to use for simulating opponent moves
   * @throws IllegalArgumentException if opponentStrategy is null
   */
  public MinimaxStrategy(PawnsBoardStrategy<C, ? extends Move> opponentStrategy) {
    if (opponentStrategy == null) {
      throw new IllegalArgumentException("Opponent strategy cannot be null");
    }
    this.opponentStrategy = opponentStrategy;
  }

  /**
   * Determines the next move based on the current game state.
   * Selects the move that minimizes the opponent's maximum possible response.
   *
   * @param model the current game state
   * @return an Optional containing the best move, or empty if no legal move exists
   */
  @Override
  public Optional<PawnsBoardMove> chooseMove(ReadOnlyPawnsBoard<C, ?> model) {
    validateGameStart(model);
    // Initialize context with common game state information
    StrategyContext<C> context = initializeContext(model);

    // If context is null, the game isn't in a valid state for moves
    if (context == null) {
      return Optional.of(PawnsBoardMove.empty());
    }

    Optional<PawnsBoardMove> ourBestMove = Optional.empty();
    int minimizedOpponentsScore = Integer.MAX_VALUE; // Lower scores are better for us (worse
    // for opponent)

    // Track all legal moves
    List<PawnsBoardMove> legalMoves = findAllLegalMoves(model, context);

    // If no legal moves exist, return empty move
    if (legalMoves.isEmpty()) {
      return Optional.of(PawnsBoardMove.empty());
    }

    // Evaluate each move
    for (PawnsBoardMove move : legalMoves) {
      int evaluation = evaluateMove(model, move);

      // A lower evaluation means the opponent has worse options
      if (evaluation < minimizedOpponentsScore) {
        minimizedOpponentsScore = evaluation;
        ourBestMove = Optional.of(move);
      }
    }

    // If all moves lead to the same opponent response quality, just pick the first one
    if (!ourBestMove.isPresent() && !legalMoves.isEmpty()) {
      ourBestMove = Optional.of(legalMoves.get(0));
    }

    return ourBestMove;
  }

  /**
   * Finds all legal moves for the current player.
   *
   * @param model the current game state
   * @param context the strategy context
   * @return a list of all legal moves
   */
  private List<PawnsBoardMove> findAllLegalMoves(ReadOnlyPawnsBoard<C, ?> model,
                                                 StrategyContext<C> context) {
    List<PawnsBoardMove> legalMoves = new ArrayList<>();

    for (int cardIndex = 0; cardIndex < context.hand.size(); cardIndex++) {
      for (int row = 0; row < context.rows; row++) {
        for (int col = 0; col < context.cols; col++) {
          if (model.isLegalMove(cardIndex, row, col)) {
            legalMoves.add(createMove(cardIndex, row, col));
          }
        }
      }
    }

    return legalMoves;
  }



  /**
   * Evaluates a move by simulating it and finding the opponent's best response.
   *
   * @param model the current game state
   * @param move the move to evaluate
   * @return a score representing the quality of the opponent's best response
   *         (lower is better for us, worse for opponent)
   */
  private int evaluateMove(ReadOnlyPawnsBoard<C, ?> model, PawnsBoardMove move) {
    try {
      // Create a copy of the model to simulate our move
      PawnsBoard<C, ?> modelCopy = model.copy();

      // Make our move
      modelCopy.placeCard(move.getCardIndex(), move.getRow(), move.getCol());

      // Get the opponent's color
      PlayerColors opponent = modelCopy.getCurrentPlayer();

      // Find opponent's best response using the strategy directly
      Optional<? extends Move> opponentMove = opponentStrategy.chooseMove(modelCopy);

      // If opponent has no moves, that's great for us (lowest possible score)
      if (!opponentMove.isPresent()) {
        return Integer.MIN_VALUE;
      }

      // Simulate opponent's move
      PawnsBoard<C, ?> afterOpponentMove = modelCopy.copy();
      Move oppMove = opponentMove.get();
      afterOpponentMove.placeCard(oppMove.getCardIndex(), oppMove.getRow(), oppMove.getCol());

      // Evaluate the resulting board state (higher score is better for opponent)
      return evaluateGameState(afterOpponentMove);

    } catch (Exception e) {
      // If simulation fails, assume a neutral evaluation
      return 0;
    }
  }

  /**
   * Evaluates the quality of a game state for the opponent.
   * Higher scores indicate a better position for the opponent.
   *
   * @param model the game state to evaluate
   * @return a score representing the quality of the position for the opponent
   */
  private int evaluateGameState(ReadOnlyPawnsBoard<C, ?> model) {
    try {
      // Get current player (which will be the opponent after our move)
      PlayerColors opponent = model.getCurrentPlayer();

      // Get the indices for the players
      int opponentIndex = (opponent == PlayerColors.RED) ? 0 : 1;
      int ourIndex = 1 - opponentIndex;

      // Get total scores
      int[] scores = model.getTotalScore();
      int opponentScore = scores[opponentIndex];
      int ourScore = scores[ourIndex];

      // Calculate score advantage (positive means opponent is ahead)
      int scoreDifference = opponentScore - ourScore;

      // Count cells controlled by each player
      int opponentCells = countPlayerCells(model, opponent);
      int ourCells = countPlayerCells(model, opponent == PlayerColors.RED
              ? PlayerColors.BLUE : PlayerColors.RED);

      // Calculate cell advantage (positive means opponent controls more cells)
      int cellDifference = opponentCells - ourCells;

      // Weight score difference more heavily than cell difference
      return scoreDifference * 3 + cellDifference;

    } catch (Exception e) {
      // If evaluation fails, assume a neutral value
      return 0;
    }
  }

  /**
   * Counts the number of cells controlled by the specified player.
   * A cell is controlled if it contains pawns or a card owned by the player.
   *
   * @param model the game state
   * @param player the player to count cells for
   * @return the number of cells controlled by the player
   */
  private int countPlayerCells(ReadOnlyPawnsBoard<C, ?> model, PlayerColors player) {
    int count = 0;
    int[] dimensions = model.getBoardDimensions();

    for (int row = 0; row < dimensions[0]; row++) {
      for (int col = 0; col < dimensions[1]; col++) {
        CellContent content = model.getCellContent(row, col);

        // Skip empty cells
        if (content == CellContent.EMPTY) {
          continue;
        }

        // Count cells owned by the player
        PlayerColors owner = model.getCellOwner(row, col);
        if (owner == player) {
          count++;
        }
      }
    }

    return count;
  }
}